package com.dingyi.myluaapp.ide.plugins


import com.dingyi.myluaapp.diagnostic.Logger
import com.dingyi.myluaapp.ide.ui.android.bundle.AndroidBundle
import com.dingyi.myluaapp.openapi.application.PathManager
import com.dingyi.myluaapp.openapi.extensions.PluginDescriptor
import com.dingyi.myluaapp.openapi.extensions.PluginId
import com.dingyi.myluaapp.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.util.ArrayUtil
import com.intellij.util.ArrayUtilRt
import com.intellij.util.ReflectionUtil
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.execution.ParametersListUtil
import com.intellij.util.graph.DFSTBuilder
import com.intellij.util.graph.GraphGenerator
import com.intellij.util.graph.InboundSemiGraph
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.Writer
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.ref.Reference
import java.lang.ref.SoftReference
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Arrays
import java.util.Collections
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ForkJoinPool
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.Collectors
import java.util.stream.Stream


// Prefer to use only JDK classes. Any post start-up functionality should be placed in PluginManager class.
object PluginManagerCore {
    @NonNls
    val META_INF: String = "META-INF/"
    val CORE_ID = PluginId.getId("com.dingyi.myluaapp")
    val CORE_PLUGIN_ID: String = "com.dingyi.myluaapp"
    val JAVA_PLUGIN_ID = PluginId.getId("com.dingyi.myluaapp.java")
    val JAVA_MODULE_ID: PluginId = PluginId.getId("com.dingyi.myluapp.modules.java")
    val PLUGIN_XML: String = "plugin.xml"
    val PLUGIN_XML_PATH: String = META_INF + PLUGIN_XML
    val ALL_MODULES_MARKER: PluginId = PluginId.getId("com.dingyi.myluapp.modules.all")
    val MODULE_DEPENDENCY_PREFIX: String = "com.dingyi.myluapp.module"
    val SPECIAL_IDEA_PLUGIN_ID: PluginId = PluginId.getId("MyLuaApp CORE")
    val PROPERTY_PLUGIN_PATH: String = "plugin.path"

    @NonNls
    val DISABLE: String = "disable"

    @NonNls
    val ENABLE: String = "enable"

    @NonNls
    val EDIT: String = "edit"
    private val IGNORE_DISABLED_PLUGINS: Boolean =
        java.lang.Boolean.getBoolean("idea.ignore.disabled.plugins")
    private val HAS_LOADED_CLASS_METHOD_TYPE: MethodType = MethodType.methodType(
        Boolean::class.javaPrimitiveType,
        String::class.java
    )
    private var brokenPluginVersions: Reference<Map<PluginId, Set<String?>>>? = null

    @Volatile
    private var ourPlugins: Array<PluginDescriptorImpl>

    @Volatile
    private var ourLoadedPlugins: List<PluginDescriptorImpl?>? = null
    private var ourPluginLoadingErrors: Map<PluginId, Exception>? = null
    private var ourAdditionalLayoutMap: Map<String, Array<String>> = emptyMap()

    @Volatile
    var isUnitTestMode: Boolean = java.lang.Boolean.getBoolean("idea.is.unit.test")

    @ApiStatus.Internal
    val usePluginClassLoader: Boolean =
        java.lang.Boolean.getBoolean("idea.from.sources.plugins.class.loader")

    @ApiStatus.Internal
    private val ourPluginErrors: MutableList<Supplier<out HtmlChunk>> = ArrayList()
    private var ourPluginsToDisable: Set<PluginId>? = null
    private var ourPluginsToEnable: Set<PluginId>? = null

    @ApiStatus.Internal
    var ourDisableNonBundledPlugins: Boolean = false

    /**
     * Bundled plugins that were updated.
     * When we update bundled plugin it becomes not bundled, so it is more difficult for analytics to use that data.
     */
    private var ourShadowedBundledPlugins: Set<PluginId>? = null
    var isRunningFromSources: Boolean? = null
        get() {
            var result: Boolean? = field
            if (result == null) {
                result = Files.isDirectory(
                    Paths.get(
                        PathManager.homePath.toString(),
                        Project.DIRECTORY_STORE_FOLDER
                    )
                )
                field = result
            }
            return result
        }
        private set

    @Volatile
    private var descriptorListFuture: CompletableFuture<DescriptorListLoadingContext?>? = null
    private var ourBuildNumber: Int? = 114514

    /**
     * Returns list of all available plugin descriptors (bundled and custom, include disabled ones). Use [.getLoadedPlugins]
     * if you need to get loaded plugins only.
     *
     *
     *
     * Do not call this method during bootstrap, should be called in a copy of PluginManager, loaded by PluginClassLoader.
     */
    val plugins: Array<Any>
        get() {
            val result: Array<PluginDescriptor> = ourPlugins
            if (result == null) {
                loadAndInitializePlugins(null, null)
                return ourPlugins
            }
            return result
        }
    val allPlugins: Collection<Any>
        get() = Arrays.asList(*ourPlugins)

    /**
     * Returns descriptors of plugins which are successfully loaded into IDE. The result is sorted in a way that if each plugin comes after
     * the plugins it depends on.
     */
    val loadedPlugins: List<Any?>
        get() = getLoadedPlugins(null)

    @ApiStatus.Internal
    fun getLoadedPlugins(coreClassLoader: ClassLoader?): List<PluginDescriptorImpl?> {
        val result: List<PluginDescriptorImpl?>? = ourLoadedPlugins
        if (result == null) {
            loadAndInitializePlugins(null, coreClassLoader)
            return (ourLoadedPlugins)!!
        }
        return result
    }

    @get:ApiStatus.Internal
    val andClearPluginLoadingErrors: List<HtmlChunk>
        get() {
            synchronized(ourPluginErrors) {
                val errors: List<HtmlChunk> =
                    ContainerUtil.map(
                        ourPluginErrors,
                        { obj: Supplier<out HtmlChunk> -> obj.get() })
                ourPluginErrors.clear()
                return errors
            }
        }

    private fun registerPluginErrors(errors: List<Supplier<out HtmlChunk>>) {
        synchronized(ourPluginErrors) {
            ourPluginErrors.addAll(
                errors
            )
        }
    }

    @ApiStatus.Internal
    fun arePluginsInitialized(): Boolean {
        return ourPlugins != null
    }

    @Synchronized
    fun doSetPlugins(value: Array<PluginDescriptorImpl>?) {
        ourPlugins = (value)!!
        ourLoadedPlugins = if (value == null) null else Collections.unmodifiableList(
            getOnlyEnabledPlugins(value)
        )
    }

    fun isDisabled(pluginId: PluginId): Boolean {
        return DisabledPluginsState.isDisabled(pluginId)
    }

    fun isBrokenPlugin(descriptor: PluginDescriptor): Boolean {
        val pluginId: PluginId? = descriptor.getPluginId()
        if (pluginId == null) {
            return true
        }
        val set: Set<String?>? = getBrokenPluginVersions().get(pluginId)
        return set != null && set.contains(descriptor.getVersion())
    }

    fun updateBrokenPlugins(brokenPlugins: Map<PluginId, Set<String?>>) {
        brokenPluginVersions = SoftReference(brokenPlugins)
        val updatedBrokenPluginFile: Path = updatedBrokenPluginFile
        try {
            DataOutputStream(
                BufferedOutputStream(
                    Files.newOutputStream(updatedBrokenPluginFile),
                    32000
                )
            ).use { out ->
                out.write(1)
                out.writeInt(brokenPlugins.size)
                for (entry: Map.Entry<PluginId, Set<String?>> in brokenPlugins.entries) {
                    out.writeUTF(entry.key.getIdString())
                    out.writeShort(entry.value.size)
                    for (s: String? in entry.value) {
                        out.writeUTF(s)
                    }
                }
            }
        } catch (ignore: NoSuchFileException) {
        } catch (e: IOException) {
            logger.error("Failed to read $updatedBrokenPluginFile", e)
        }
    }

    fun getBrokenPluginVersions(): Map<PluginId, Set<String?>> {
        if (IGNORE_DISABLED_PLUGINS) {
            return emptyMap<PluginId, Set<String?>>()
        }
        var result: Map<PluginId, Set<String?>>? =
            if (brokenPluginVersions == null) null else brokenPluginVersions!!.get()
        if (result == null) {
            result = readBrokenPluginFile()
            brokenPluginVersions = SoftReference<Map<PluginId, Set<String?>>>(result)
        }
        return result
    }

    private fun readBrokenPluginFile(): Map<PluginId, Set<String?>> {
        val updatedBrokenPluginFile: Path = updatedBrokenPluginFile
        val brokenPluginsStorage: Path
        if (Files.exists(updatedBrokenPluginFile)) {
            brokenPluginsStorage = updatedBrokenPluginFile
        } else {
            brokenPluginsStorage = Paths.get(PathManager.getBinPath() + "/brokenPlugins.db")
        }
        try {
            DataInputStream(
                BufferedInputStream(
                    Files.newInputStream(brokenPluginsStorage),
                    32000
                )
            ).use { stream ->
                val version: Int = stream.readUnsignedByte()
                if (version != 1) {
                    logger.error("Unsupported version of " + brokenPluginsStorage + "(fileVersion=" + version + ", supportedVersion=1)")
                    return emptyMap<PluginId, Set<String?>>()
                }
                val count: Int = stream.readInt()
                val result: MutableMap<PluginId, Set<String?>> =
                    HashMap<PluginId, Set<String>>(count)
                for (i in 0 until count) {
                    val pluginId: PluginId = PluginId.getId(stream.readUTF())
                    val versions: Array<String?> =
                        arrayOfNulls(stream.readUnsignedShort())
                    for (j in versions.indices) {
                        versions.get(j) = stream.readUTF()
                    }
                    result.put(
                        pluginId,
                        if (versions.size == 1) setOf(versions.get(0)) else HashSet(
                            Arrays.asList(*versions)
                        )
                    )
                }
                return result
            }
        } catch (ignore: NoSuchFileException) {
        } catch (e: IOException) {
            logger.error("Failed to read $brokenPluginsStorage", e)
        }
        return emptyMap<PluginId, Set<String?>>()
    }

    @Throws(IOException::class)
    fun writePluginsList(ids: Collection<PluginId?>, writer: Writer) {
        val sortedIds: List<PluginId> = ArrayList<Any?>(ids)
        sortedIds.sort(null)
        for (id: PluginId in sortedIds) {
            writer.write(id.getIdString())
            writer.write('\n'.code)
        }
    }

    fun disablePlugin(id: PluginId): Boolean {
        return DisabledPluginsState.setEnabledState(setOf(id), false)
    }

    fun enablePlugin(id: PluginId): Boolean {
        return DisabledPluginsState.setEnabledState(setOf(id), true)
    }

    fun isModuleDependency(dependentPluginId: PluginId): Boolean {
        return dependentPluginId.getIdString().startsWith(MODULE_DEPENDENCY_PREFIX)
    }

    /**
     * This is an internal method, use [PluginException.createByClass] instead.
     */
    @ApiStatus.Internal
    fun createPluginException(
        errorMessage: String, cause: Throwable?,
        pluginClass: Class<*>
    ): PluginException {
        val classLoader: ClassLoader = pluginClass.classLoader
        val pluginId: PluginId?
        if (classLoader is PluginAwareClassLoader) {
            pluginId = (classLoader as PluginAwareClassLoader).getPluginId()
        } else {
            pluginId = getPluginByClassName(pluginClass.name)
        }
        return PluginException(errorMessage, cause, pluginId)
    }

    fun getPluginByClassName(className: String): PluginId? {
        val id: PluginId? = getPluginOrPlatformByClassName(className)
        return if ((id != null && !CORE_ID.equals(id))) id else null
    }

    fun getPluginOrPlatformByClassName(className: String): PluginId? {
        val result: PluginDescriptor? = getPluginDescriptorOrPlatformByClassName(className)
        return if (result == null) null else result.getPluginId()
    }

    @ApiStatus.Internal
    fun getPluginDescriptorOrPlatformByClassName(@NonNls className: String): PluginDescriptor? {
        val loadedPlugins: List<PluginDescriptorImpl?>? = ourLoadedPlugins
        if (((loadedPlugins == null) ||
                    className.startsWith("java.") ||
                    className.startsWith("javax.") ||
                    className.startsWith("kotlin.") ||
                    className.startsWith("groovy.") ||
                    !className.contains("."))
        ) {
            return null
        }
        var result: PluginDescriptor? = null
        for (o: PluginDescriptorImpl in loadedPlugins) {
            val classLoader: ClassLoader = o.getPluginClassLoader()
            if (!hasLoadedClass(className, classLoader)) {
                continue
            }
            result = o
            break
        }
        if (result == null) {
            return null
        }

        // return if the found plugin is not "core" or the package is obviously "core"
        if ((!CORE_ID.equals(result.getPluginId()) ||
                    className.startsWith("com.jetbrains.") || className.startsWith("org.jetbrains.") ||
                    className.startsWith("com.intellij.") || className.startsWith("org.intellij.") ||
                    className.startsWith("com.android.") ||
                    className.startsWith("git4idea.") || className.startsWith("org.angularjs."))
        ) {
            return result
        }

        // otherwise we need to check plugins with use-idea-classloader="true"
        var root: String? = null
        for (o: PluginDescriptorImpl in loadedPlugins) {
            if (!o.isUseIdeaClassLoader) {
                continue
            }
            if (root == null) {
                root = PathManager.getResourceRoot(
                    result.getPluginClassLoader(),
                    className.replace('.', '/') + ".class"
                )
                if (root == null) {
                    return null
                }
            }
            val path: Path = o.getPluginPath()
            if (root.startsWith(FileUtilRt.toSystemIndependentName(path.toString()))) {
                return o
            }
        }
        return null
    }

    private val updatedBrokenPluginFile: Path
        private get() = Paths.get(PathManager.getConfigPath()).resolve("updatedBrokenPlugins.db")

    private fun hasLoadedClass(className: String, loader: ClassLoader): Boolean {
        if (loader is UrlClassLoader) {
            return (loader as UrlClassLoader).hasLoadedClass(className)
        }

        // it can be an UrlClassLoader loaded by another class loader, so instanceof doesn't work
        var aClass: Class<*> = loader.javaClass
        if (aClass.isAnonymousClass || aClass.isMemberClass) {
            aClass = aClass.superclass
        }
        try {
            return MethodHandles.publicLookup()
                .findVirtual(aClass, "hasLoadedClass", HAS_LOADED_CLASS_METHOD_TYPE)
                .invoke(loader, className) as Boolean
        } catch (ignore: NoSuchMethodError) {
        } catch (ignore: IllegalAccessError) {
        } catch (ignore: IllegalAccessException) {
        } catch (e: Throwable) {
            logger.error(e)
        }
        return false
    }

    /**
     * In 191.* and earlier builds Java plugin was part of the platform, so any plugin installed in IntelliJ IDEA might be able to use its
     * classes without declaring explicit dependency on the Java module. This method is intended to add implicit dependency on the Java plugin
     * for such plugins to avoid breaking compatibility with them.
     */
    fun getImplicitDependency(
        descriptor: PluginDescriptorImpl,
        javaDepGetter: Supplier<PluginDescriptorImpl?>
    ): PluginDescriptorImpl? {
        // skip our plugins as expected to be up-to-date whether bundled or not
        if (descriptor.isBundled() || (VENDOR_JETBRAINS == descriptor.getVendor())) {
            return null
        }
        val pluginId: PluginId = descriptor.getPluginId()
        if (CORE_ID.equals(pluginId) ||
            JAVA_PLUGIN_ID.equals(pluginId)
        ) {
            return null
        }
        val javaDep: PluginDescriptorImpl? = javaDepGetter.get()
        if (javaDep == null) {
            return null
        }

        // If a plugin does not include any module dependency tags in its plugin.xml, it's assumed to be a legacy plugin
        // and is loaded only in IntelliJ IDEA, so it may use classes from Java plugin.
        return if (hasModuleDependencies(descriptor)) null else javaDep
    }

    fun hasModuleDependencies(descriptor: PluginDescriptorImpl): Boolean {
        for (dependency: PluginDependency in descriptor.pluginDependencies) {
            val dependencyPluginId: PluginId = dependency.getPluginId()
            if ((JAVA_PLUGIN_ID.equals(dependencyPluginId) ||
                        JAVA_MODULE_ID.equals(dependencyPluginId) ||
                        isModuleDependency(dependencyPluginId))
            ) {
                return true
            }
        }
        return false
    }

    @Synchronized
    fun invalidatePlugins() {
        doSetPlugins(null)
        DisabledPluginsState.invalidate()
        ourShadowedBundledPlugins = null
    }

    private fun logPlugins(
        plugins: Array<PluginDescriptorImpl>,
        incompletePlugins: Collection<PluginDescriptorImpl>
    ) {
        val bundled: StringBuilder = StringBuilder()
        val disabled: StringBuilder = StringBuilder()
        val custom: StringBuilder = StringBuilder()
        val disabledPlugins: MutableSet<PluginId> = HashSet<PluginId>()
        for (descriptor: PluginDescriptor in plugins) {
            var target: StringBuilder
            val pluginId: PluginId = descriptor.getPluginId()
            if (!descriptor.isEnabled()) {
                if (!isDisabled(pluginId)) {
                    // plugin will be logged as part of "Problems found loading plugins"
                    continue
                }
                disabledPlugins.add(pluginId)
                target = disabled
            } else if (descriptor.isBundled() || SPECIAL_IDEA_PLUGIN_ID.equals(pluginId)) {
                target = bundled
            } else {
                target = custom
            }
            appendPlugin(descriptor, target)
        }
        for (plugin: PluginDescriptorImpl in incompletePlugins) {
            // log only explicitly disabled plugins
            val pluginId: PluginId = plugin.getPluginId()
            if (isDisabled(pluginId) &&
                !disabledPlugins.contains(pluginId)
            ) {
                appendPlugin(plugin, disabled)
            }
        }
        val logger: Logger = logger
        logger.info("Loaded bundled plugins: $bundled")
        if (custom.length > 0) {
            logger.info("Loaded custom plugins: $custom")
        }
        if (disabled.length > 0) {
            logger.info("Disabled plugins: $disabled")
        }
    }

    private fun appendPlugin(descriptor: PluginDescriptor, target: StringBuilder) {
        if (target.length > 0) {
            target.append(", ")
        }
        target.append(descriptor.getName())
        val version: String? = descriptor.getVersion()
        if (version != null) {
            target.append(" (").append(version).append(')')
        }
    }

    private fun prepareLoadingPluginsErrorMessage(
        pluginErrors: Map<PluginId, PluginLoadingError>,
        globalErrors: List<Supplier<String>>,
        actions: List<Supplier<HtmlChunk>>
    ) {
        ourPluginLoadingErrors = pluginErrors
        if (pluginErrors.isEmpty() && globalErrors.isEmpty()) {
            return
        }

        // log includes all messages, not only those which need to be reported to the user
        val logMessage: String = "Problems found loading plugins:\n  " +
                Stream.concat<String>(
                    globalErrors.stream()
                        .map({ obj: Supplier<String> -> obj.get() }),
                    pluginErrors.entries.stream()
                        .sorted(java.util.Map.Entry.comparingByKey<PluginId, PluginLoadingError>())
                        .map(java.util.function.Function<Map.Entry<PluginId, PluginLoadingError>, Any> { e: Map.Entry<PluginId, PluginLoadingError> -> e.value.getInternalMessage() })
                )
                    .collect(Collectors.joining("\n  "))
        if (isUnitTestMode || !java.awt.GraphicsEnvironment.isHeadless()) {
            val errorsList: List<Supplier<HtmlChunk>> = Stream.concat(
                globalErrors.stream().map({ message: Supplier<String> ->
                    Supplier {
                        HtmlChunk.text(
                            message.get()
                        )
                    }
                }),
                pluginErrors.entries.stream()
                    .sorted(java.util.Map.Entry.comparingByKey<PluginId, PluginLoadingError>()).map(
                        java.util.function.Function<Map.Entry<PluginId, PluginLoadingError>, PluginLoadingError> { java.util.Map.Entry.value })
                    .filter(PluginLoadingError::isNotifyUser)
                    .map(java.util.function.Function { error: PluginLoadingError ->
                        Supplier {
                            HtmlChunk.text(
                                error.getDetailedMessage()
                            )
                        }
                    })
            ).collect(Collectors.toList())
            if (!errorsList.isEmpty()) {
                registerPluginErrors(ContainerUtil.concat(errorsList, actions))
            }
            logger.warn(logMessage)
        } else {
            logger.error(logMessage)
        }
    }

    @NlsContexts.Label
    fun getShortLoadingErrorMessage(pluginDescriptor: PluginDescriptor): String? {
        val error: PluginLoadingError? =
            ourPluginLoadingErrors!!.get(pluginDescriptor.getPluginId())
        return if (error == null) null else error.getShortMessage()
    }

    fun getFirstDisabledDependency(pluginDescriptor: PluginDescriptor): PluginId? {
        val error: PluginLoadingError? =
            ourPluginLoadingErrors!!.get(pluginDescriptor.getPluginId())
        return if (error == null) null else error.disabledDependency
    }

    fun createPluginIdGraph(
        descriptors: Collection<PluginDescriptorImpl?>,
        idToDescriptorMap: Map<PluginId?, PluginDescriptorImpl?>,
        withOptional: Boolean
    ): CachingSemiGraph<PluginDescriptorImpl> {
        val hasAllModules: Boolean = idToDescriptorMap.containsKey(ALL_MODULES_MARKER)
        val javaDep: Supplier<PluginDescriptorImpl?> =
            Supplier<PluginDescriptorImpl?> {
                idToDescriptorMap.get(
                    JAVA_MODULE_ID
                )
            }
        val uniqueCheck: MutableSet<PluginDescriptorImpl> = HashSet<PluginDescriptorImpl>()
        val `in`: MutableMap<PluginDescriptorImpl, List<PluginDescriptorImpl>> =
            HashMap<PluginDescriptorImpl, List<PluginDescriptorImpl>>(descriptors.size)
        for (descriptor: PluginDescriptorImpl in descriptors) {
            val list: List<PluginDescriptorImpl> = getDirectDependencies(
                descriptor,
                idToDescriptorMap,
                withOptional,
                hasAllModules,
                javaDep,
                uniqueCheck
            )
            if (!list.isEmpty()) {
                `in`[descriptor] = list
            }
        }
        return CachingSemiGraph(descriptors, `in`)
    }

    private fun getDirectDependencies(
        rootDescriptor: PluginDescriptorImpl,
        idToDescriptorMap: Map<PluginId?, PluginDescriptorImpl?>,
        withOptional: Boolean,
        hasAllModules: Boolean,
        javaDep: Supplier<PluginDescriptorImpl?>,
        uniqueCheck: MutableSet<PluginDescriptorImpl>
    ): List<PluginDescriptorImpl> {
        val dependencies: List<PluginDependency> = rootDescriptor.pluginDependencies
        val implicitDep: PluginDescriptorImpl? =
            if (hasAllModules) getImplicitDependency(rootDescriptor, javaDep) else null
        var capacity: Int = dependencies.size + rootDescriptor.incompatibilities.size()
        if (!withOptional) {
            for (dependency: PluginDependency in dependencies) {
                if (dependency.isOptional()) {
                    capacity--
                }
            }
        }
        if (capacity == 0) {
            return if (implicitDep == null) emptyList<PluginDescriptorImpl>() else listOf<PluginDescriptorImpl>(
                implicitDep
            )
        }
        uniqueCheck.clear()
        val plugins: MutableList<PluginDescriptorImpl> =
            ArrayList<PluginDescriptorImpl>(capacity + (if (implicitDep == null) 0 else 1))
        if (implicitDep != null) {
            if (rootDescriptor === implicitDep) {
                logger.error("Plugin $rootDescriptor depends on self")
            } else {
                uniqueCheck.add(implicitDep)
                plugins.add(implicitDep)
            }
        }
        for (dependency: PluginDependency in dependencies) {
            if (!withOptional && dependency.isOptional()) {
                continue
            }

            // check for missing optional dependency
            val dep: PluginDescriptorImpl? = idToDescriptorMap.get(dependency.getPluginId())
            // if 'dep' refers to a module we need to check the real plugin containing this module only if it's still enabled,
            // otherwise the graph will be inconsistent
            if (dep == null) {
                continue
            }

            // ultimate plugin it is combined plugin, where some included XML can define dependency on ultimate explicitly and for now not clear,
            // can be such requirements removed or not
            if (rootDescriptor === dep) {
                if (!CORE_ID.equals(rootDescriptor.getPluginId())) {
                    logger.error("Plugin $rootDescriptor depends on self")
                }
            } else if (uniqueCheck.add(dep)) {
                plugins.add(dep)
            }
        }
        for (moduleId: PluginId in rootDescriptor.incompatibilities) {
            val dep: PluginDescriptorImpl? = idToDescriptorMap.get(moduleId)
            if (dep != null && uniqueCheck.add(dep)) {
                plugins.add(dep)
            }
        }
        return plugins
    }

    private fun checkPluginCycles(
        descriptors: List<PluginDescriptorImpl?>,
        idToDescriptorMap: Map<PluginId?, PluginDescriptorImpl?>,
        errors: MutableList<Supplier<String>>
    ) {
        val graph: CachingSemiGraph<PluginDescriptorImpl> =
            createPluginIdGraph(descriptors, idToDescriptorMap, true)
        val builder: DFSTBuilder<PluginDescriptorImpl> =
            DFSTBuilder(GraphGenerator.generate<Any>(graph))
        if (builder.isAcyclic()) {
            return
        }
        for (component: Collection<PluginDescriptorImpl> in builder.getComponents()) {
            if (component.size < 2) {
                continue
            }
            for (descriptor: PluginDescriptor in component) {
                descriptor.setEnabled(false)
            }
            val pluginsString: String = component.stream()
                .map(java.util.function.Function<PluginDescriptorImpl, Any> { it: PluginDescriptorImpl -> ("'" + it.getName()).toString() + "'" })
                .collect(
                    Collectors.joining(", ")
                )
            errors.add(
                message(
                    "plugin.loading.error.plugins.cannot.be.loaded.because.they.form.a.dependency.cycle",
                    pluginsString
                )
            )
            val detailedMessage: StringBuilder = StringBuilder()
            val pluginToString: java.util.function.Function<PluginDescriptorImpl, String> =
                java.util.function.Function<PluginDescriptorImpl, String> { plugin: PluginDescriptorImpl ->
                    (("id = " + plugin.getPluginId()
                        .getIdString()).toString() + " (" + plugin.getName()).toString() + ")"
                }
            detailedMessage.append("Detected plugin dependencies cycle details (only related dependencies are included):\n")
            component.stream()
                .map(java.util.function.Function<PluginDescriptorImpl, Pair<Any, String?>> { p: PluginDescriptorImpl ->
                    Pair.create(
                        p,
                        pluginToString.apply(p)
                    )
                })
                .sorted(
                    Comparator.comparing(
                        { p: Pair<Any, String?> -> p.second },
                        java.lang.String.CASE_INSENSITIVE_ORDER
                    )
                )
                .forEach({ p: Pair<Any, String?> ->
                    detailedMessage.append("  ").append(p.getSecond()).append(" depends on:\n")
                    ContainerUtil.toCollection(Iterable<T> {
                        graph.getIn(
                            p.first
                        )
                    })
                        .stream()
                        .filter(Predicate<T> { dep: T -> component.contains(dep) })
                        .map(pluginToString)
                        .sorted(java.lang.String.CASE_INSENSITIVE_ORDER)
                        .forEach(Consumer<R> { dep: R? ->
                            detailedMessage.append("    ").append(dep).append("\n")
                        })
                })
            logger.info(detailedMessage.toString())
        }
    }

    private fun prepareLoadingPluginsErrorMessage(
        disabledIds: Map<PluginId, String>,
        disabledRequiredIds: Set<PluginId>,
        idMap: Map<PluginId?, PluginDescriptor?>,
        pluginErrors: Map<PluginId, PluginLoadingError>,
        globalErrors: List<Supplier<String>>
    ) {
        val actions: MutableList<Supplier<HtmlChunk>> = ArrayList()
        if (!disabledIds.isEmpty()) {
            val nameToDisable: @NlsSafe String?
            if (disabledIds.size == 1) {
                val id: PluginId = disabledIds.keys.iterator().next()
                nameToDisable =
                    if (idMap.containsKey(id)) idMap.get(id).getName() else id.getIdString()
            } else {
                nameToDisable = null
            }
            actions.add(Supplier {
                HtmlChunk.link(
                    DISABLE,
                    CoreBundle.message(
                        "link.text.disable.plugin.or.plugins",
                        nameToDisable,
                        if (nameToDisable != null) 0 else 1
                    )
                )
            })
            if (!disabledRequiredIds.isEmpty()) {
                val nameToEnable: String? = if (disabledRequiredIds.size == 1 && idMap.containsKey(
                        disabledRequiredIds.iterator().next()
                    )
                ) idMap.get(disabledRequiredIds.iterator().next()).getName() else null
                actions.add(Supplier {
                    HtmlChunk
                        .link(
                            ENABLE,
                            CoreBundle.message(
                                "link.text.enable.plugin.or.plugins",
                                nameToEnable,
                                if (nameToEnable != null) 0 else 1
                            )
                        )
                })
            }
            actions.add(Supplier {
                HtmlChunk.link(
                    EDIT,
                    CoreBundle.message("link.text.open.plugin.manager")
                )
            })
        }
        prepareLoadingPluginsErrorMessage(pluginErrors, globalErrors, actions)
    }

    @ApiStatus.Internal
    @Synchronized
    fun onEnable(enabled: Boolean): Boolean {
        val pluginIds: Set<PluginId>? = if (enabled) ourPluginsToEnable else ourPluginsToDisable
        ourPluginsToEnable = null
        ourPluginsToDisable = null
        val applied: Boolean = pluginIds != null
        if (applied) {
            val pluginIdMap: Map<PluginId?, PluginDescriptorImpl?> = buildPluginIdMap()
            for (pluginId: PluginId? in pluginIds) {
                val descriptor: PluginDescriptor? = pluginIdMap.get(pluginId)
                if (descriptor != null) {
                    descriptor.setEnabled(enabled)
                }
            }
            DisabledPluginsState.setEnabledState(pluginIds, enabled)
        }
        return applied
    }

    // separate method to avoid exposing of DescriptorListLoadingContext class
    fun scheduleDescriptorLoading() {
        orScheduleLoading
    }

    @get:Synchronized
    private val orScheduleLoading: CompletableFuture<Any?>
        private get() {
            var future: CompletableFuture<DescriptorListLoadingContext?>? = descriptorListFuture
            if (future != null) {
                return future
            }
            future = CompletableFuture.supplyAsync<DescriptorListLoadingContext?>(
                Supplier<DescriptorListLoadingContext?> {
                    val activity: Activity =
                        StartUpMeasurer.startActivity(
                            "plugin descriptor loading",
                            ActivityCategory.DEFAULT
                        )
                    val context: DescriptorListLoadingContext =
                        PluginDescriptorLoader.loadDescriptors(
                            isUnitTestMode,
                            isRunningFromSources
                        )
                    activity.end()
                    context
                }, ForkJoinPool.commonPool()
            )
            descriptorListFuture = future
            return (future)!!
        }

    /**
     * Think twice before use and get approve from core team. Returns enabled plugins only.
     */
    @get:ApiStatus.Internal
    val enabledPluginRawList: CompletableFuture<List<Any>>
        get() = orScheduleLoading.thenApply(
            java.util.function.Function<DescriptorListLoadingContext?, List<PluginDescriptorImpl>> { it: DescriptorListLoadingContext? -> it.result.getEnabledPlugins() })

    @ApiStatus.Internal
    fun initPlugins(coreClassLoader: ClassLoader): CompletionStage<List<PluginDescriptorImpl>> {
        var future: CompletableFuture<DescriptorListLoadingContext?>? = descriptorListFuture
        if (future == null) {
            future = CompletableFuture.completedFuture(null)
        }
        return future.thenApply<List<PluginDescriptorImpl>>(java.util.function.Function<DescriptorListLoadingContext, List<PluginDescriptorImpl?>?> { context: DescriptorListLoadingContext? ->
            loadAndInitializePlugins(context, coreClassLoader)
            ourLoadedPlugins
        })
    }

    private fun loadAdditionalLayoutMap(): Map<String, Array<String>> {
        val fileWithLayout: Path? = if (usePluginClassLoader) Paths.get(
            PathManager.getSystemPath(),
            PlatformUtils.getPlatformPrefix() + ".txt"
        ) else null
        if (fileWithLayout == null || !Files.exists(fileWithLayout)) {
            return emptyMap()
        }
        val additionalLayoutMap: MutableMap<String, Array<String>> = LinkedHashMap()
        try {
            Files.newBufferedReader(fileWithLayout).use { bufferedReader ->
                var line: String
                while ((bufferedReader.readLine().also { line = it }) != null) {
                    val parameters: List<String> =
                        ParametersListUtil.parse(line.trim { it <= ' ' })
                    if (parameters.size < 2) {
                        continue
                    }
                    additionalLayoutMap.put(
                        parameters.get(0),
                        ArrayUtilRt.toStringArray(
                            parameters.subList(
                                1,
                                parameters.size
                            )
                        )
                    )
                }
            }
        } catch (ignored: Exception) {
        }
        return additionalLayoutMap
    }

    /**
     * not used by plugin manager - only for dynamic plugin reloading.
     * Building plugin graph and using `getInList` as it is done for regular loading is not required - all that magic and checks
     * are not required here because only regular plugins maybe dynamically reloaded.
     */
    @ApiStatus.Internal
    fun createClassLoaderConfiguratorForDynamicPlugin(pluginDescriptor: PluginDescriptorImpl): ClassLoaderConfigurator {
        val idMap: Map<PluginId?, PluginDescriptorImpl?> = buildPluginIdMap(
            ContainerUtil.concat(
                getLoadedPlugins(null), listOf(pluginDescriptor)
            )
        )
        return ClassLoaderConfigurator(
            true,
            PluginManagerCore::class.java.classLoader, idMap, ourAdditionalLayoutMap
        )
    }

    // no need to log error - ApplicationInfo is required in production in any case, so, will be logged if really needed
    val buildNumber: BuildNumber
        get() {
            var result: BuildNumber? = ourBuildNumber
            if (result == null) {
                result = BuildNumber.fromPluginsCompatibleBuild()
                if (result == null) {
                    if (isUnitTestMode) {
                        result = BuildNumber.currentVersion()
                    } else {
                        try {
                            result = ApplicationInfoImpl.getShadowInstance().getApiVersionAsNumber()
                        } catch (ignore: RuntimeException) {
                            // no need to log error - ApplicationInfo is required in production in any case, so, will be logged if really needed
                            result = BuildNumber.currentVersion()
                        }
                    }
                }
                ourBuildNumber = result
            }
            return result
        }

    private fun disableIncompatiblePlugins(
        descriptors: List<PluginDescriptorImpl?>,
        idMap: Map<PluginId?, PluginDescriptorImpl?>,
        errors: MutableMap<PluginId, PluginLoadingError>
    ) {
        val isNonBundledPluginDisabled: Boolean = ourDisableNonBundledPlugins
        if (isNonBundledPluginDisabled) {
            logger.info("Running with disableThirdPartyPlugins argument, third-party plugins will be disabled")
        }
        val selectedIds: String? = System.getProperty("idea.load.plugins.id")
        val selectedCategory: String? = System.getProperty("idea.load.plugins.category")
        val coreDescriptor: PluginDescriptorImpl? = idMap.get(CORE_ID)
        var explicitlyEnabled: MutableSet<PluginDescriptorImpl?>? = null
        if (selectedIds != null) {
            val set: MutableSet<PluginId> = HashSet<PluginId>()
            for (it: String in selectedIds.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()) {
                set.add(PluginId.getId(it))
            }
            set.addAll(ApplicationInfoImpl.getShadowInstance().getEssentialPluginsIds())
            explicitlyEnabled = LinkedHashSet<PluginDescriptorImpl>(set.size)
            for (id: PluginId in set) {
                val descriptor: PluginDescriptorImpl? = idMap.get(id)
                if (descriptor != null) {
                    explicitlyEnabled!!.add(descriptor)
                }
            }
        } else if (selectedCategory != null) {
            explicitlyEnabled = LinkedHashSet<PluginDescriptorImpl?>()
            for (descriptor: PluginDescriptorImpl in descriptors) {
                if ((selectedCategory == descriptor.getCategory())) {
                    explicitlyEnabled!!.add(descriptor)
                }
            }
        }
        if (explicitlyEnabled != null) {
            // add all required dependencies
            val finalExplicitlyEnabled: MutableSet<PluginDescriptorImpl?> = explicitlyEnabled
            val depProcessed: MutableSet<PluginDescriptor?> = HashSet<PluginDescriptor?>()
            for (descriptor: PluginDescriptorImpl in ArrayList<Any?>(explicitlyEnabled)) {
                processAllDependencies(descriptor, false, idMap, depProcessed,
                    BiFunction<PluginId, PluginDescriptorImpl?, FileVisitResult> { id: PluginId?, dependency: PluginDescriptorImpl? ->
                        finalExplicitlyEnabled.add(dependency)
                        FileVisitResult.CONTINUE
                    })
            }
        }
        val brokenPluginVersions: Map<PluginId, Set<String?>> = getBrokenPluginVersions()
        val shouldLoadPlugins: Boolean =
            java.lang.Boolean.parseBoolean(System.getProperty("idea.load.plugins", "true"))
        for (descriptor: PluginDescriptorImpl in descriptors) {
            if (descriptor === coreDescriptor) {
                continue
            }
            val set: Set<String?>? = brokenPluginVersions.get(descriptor.getPluginId())
            if (set != null && set.contains(descriptor.getVersion())) {
                descriptor.setEnabled(false)
                errors[descriptor.getPluginId()] = PluginLoadingError(
                    descriptor,
                    message(
                        "plugin.loading.error.long.marked.as.broken",
                        descriptor.getName(),
                        descriptor.getVersion()
                    ),
                    message("plugin.loading.error.short.marked.as.broken")
                )
            } else if (explicitlyEnabled != null) {
                if (!explicitlyEnabled.contains(descriptor)) {
                    descriptor.setEnabled(false)
                    logger.info(
                        (("Plugin '" + descriptor.getName()).toString() + "' " +
                                (if (selectedIds != null) "is not in 'idea.load.plugins.id' system property" else "category doesn't match 'idea.load.plugins.category' system property"))
                    )
                }
            } else if (!shouldLoadPlugins) {
                descriptor.setEnabled(false)
                errors[descriptor.getPluginId()] = PluginLoadingError(
                    descriptor,
                    message(
                        "plugin.loading.error.long.plugin.loading.disabled",
                        descriptor.getName()
                    ),
                    message("plugin.loading.error.short.plugin.loading.disabled")
                )
            } else if (isNonBundledPluginDisabled && !descriptor.isBundled()) {
                descriptor.setEnabled(false)
                errors[descriptor.getPluginId()] = PluginLoadingError(
                    descriptor,
                    message(
                        "plugin.loading.error.long.custom.plugin.loading.disabled",
                        descriptor.getName()
                    ),
                    message("plugin.loading.error.short.custom.plugin.loading.disabled"),
                    false,
                    null
                )
            }
        }
    }

    fun isCompatible(descriptor: PluginDescriptor): Boolean {
        return !isIncompatible(descriptor)
    }

    fun isCompatible(descriptor: PluginDescriptor, buildNumber: BuildNumber?): Boolean {
        return !isIncompatible(descriptor, buildNumber)
    }

    fun isIncompatible(descriptor: PluginDescriptor): Boolean {
        return isIncompatible(descriptor, buildNumber)
    }

    fun isIncompatible(descriptor: PluginDescriptor, buildNumber: BuildNumber?): Boolean {
        var buildNumber: BuildNumber? = buildNumber
        if (buildNumber == null) {
            buildNumber = PluginManagerCore.buildNumber
        }
        return checkBuildNumberCompatibility(descriptor, buildNumber) != null
    }

    fun checkBuildNumberCompatibility(
        descriptor: PluginDescriptor,
        ideBuildNumber: Int
    ): RuntimeException? {

        val descriptorSdkVersion = descriptor.minSdkVersion

        if (descriptorSdkVersion < ideBuildNumber) {
            return RuntimeException(
                message(
                    com.dingyi.myluaapp.ide.core.R.string.plugin_loading_error_long_incompatible,
                    descriptor.name, descriptor.version, descriptor.minSdkVersion, ideBuildNumber
                ).get()
            )
        }

    }

    private fun checkEssentialPluginsAreAvailable(idMap: Map<PluginId?, PluginDescriptorImpl?>) {
        val required: List<PluginId> =
            ApplicationInfoImpl.getShadowInstance().getEssentialPluginsIds()
        var missing: MutableList<String?>? = null
        for (id: PluginId in required) {
            val descriptor: PluginDescriptorImpl? = idMap.get(id)
            if (descriptor == null || !descriptor.isEnabled()) {
                if (missing == null) {
                    missing = ArrayList()
                }
                missing.add(id.getIdString())
            }
        }
        if (missing != null) {
            throw EssentialPluginMissingException(missing)
        }
    }

    fun initializePlugins(
        context: DescriptorListLoadingContext,
        coreLoader: ClassLoader,
        checkEssentialPlugins: Boolean
    ): PluginManagerState {
        val loadingResult: PluginLoadingResult = context.result
        val pluginErrors: MutableMap<PluginId, PluginLoadingError> =
            HashMap<Any?, Any?>(loadingResult.`getPluginErrors$intellij_platform_core_impl`())
        val globalErrors: MutableList<Supplier<String>> = loadingResult.getGlobalErrors()
        if (loadingResult.duplicateModuleMap != null) {
            for (entry: Map.Entry<PluginId?, List<PluginDescriptorImpl?>> in loadingResult.duplicateModuleMap.entrySet()) {
                globalErrors.add(Supplier {
                    CoreBundle.message(
                        "plugin.loading.error.module.declared.by.multiple.plugins", entry.key,
                        entry.value.stream().map<Any>(PluginDescriptorImpl::toString)
                            .collect(Collectors.joining("\n  "))
                    )
                })
            }
        }
        val idMap: Map<PluginId?, PluginDescriptorImpl?> = loadingResult.idMap
        val coreDescriptor: PluginDescriptorImpl? = idMap.get(CORE_ID)
        if (checkEssentialPlugins && coreDescriptor == null) {
            throw EssentialPluginMissingException(
                listOf<String>(
                    CORE_ID.toString() + " (platform prefix: " + System.getProperty(
                        PlatformUtils.PLATFORM_PREFIX_KEY
                    ) + ")"
                )
            )
        }
        val descriptors: List<PluginDescriptorImpl?> = loadingResult.getEnabledPlugins()
        disableIncompatiblePlugins(descriptors, idMap, pluginErrors)
        checkPluginCycles(descriptors, idMap, globalErrors)

        // topological sort based on required dependencies only
        val sortedRequired: Array<PluginDescriptorImpl> = getTopologicallySorted(
            createPluginIdGraph(descriptors, idMap, false)
        )
        val enabledPluginIds: MutableSet<PluginId> = LinkedHashSet<PluginId>()
        val enabledModuleIds: MutableSet<PluginId> = LinkedHashSet<PluginId>()
        val disabledIds: MutableMap<PluginId, String> = LinkedHashMap<PluginId, String>()
        val disabledRequiredIds: MutableSet<PluginId> = LinkedHashSet<PluginId>()
        for (descriptor: PluginDescriptorImpl in sortedRequired) {
            val wasEnabled: Boolean = descriptor.isEnabled()
            if (wasEnabled && computePluginEnabled(
                    descriptor,
                    enabledPluginIds,
                    enabledModuleIds,
                    idMap,
                    disabledRequiredIds,
                    context.disabledPlugins,
                    pluginErrors
                )
            ) {
                enabledPluginIds.add(descriptor.getPluginId())
                enabledModuleIds.addAll(descriptor.modules)
            } else {
                descriptor.setEnabled(false)
                if (wasEnabled) {
                    disabledIds[descriptor.getPluginId()] = descriptor.getName()
                }
            }
        }
        prepareLoadingPluginsErrorMessage(
            disabledIds,
            disabledRequiredIds,
            idMap,
            pluginErrors,
            globalErrors
        )

        // topological sort based on all (required and optional) dependencies
        val graph: CachingSemiGraph<PluginDescriptorImpl?> =
            createPluginIdGraph(Arrays.asList(*sortedRequired), idMap, true)
        val sortedAll: Array<PluginDescriptorImpl> = getTopologicallySorted(graph)
        val enabledPlugins: List<PluginDescriptorImpl> = getOnlyEnabledPlugins(sortedAll)
        for (plugin: PluginDescriptorImpl in enabledPlugins) {
            checkOptionalDescriptors(plugin.pluginDependencies, idMap)
        }
        val additionalLayoutMap: Map<String, Array<String>> = loadAdditionalLayoutMap()
        ourAdditionalLayoutMap = additionalLayoutMap
        val classLoaderConfigurator: ClassLoaderConfigurator = ClassLoaderConfigurator(
            context.usePluginClassLoader, coreLoader, idMap,
            additionalLayoutMap
        )
        enabledPlugins.forEach(classLoaderConfigurator::configure)
        if (checkEssentialPlugins) {
            checkEssentialPluginsAreAvailable(idMap)
        }
        val effectiveDisabledIds: Set<PluginId> =
            if (disabledIds.isEmpty()) emptySet<PluginId>() else HashSet<Any?>(disabledIds.keys)
        return PluginManagerState(
            sortedAll,
            enabledPlugins,
            disabledRequiredIds,
            effectiveDisabledIds,
            idMap
        )
    }

    private fun checkOptionalDescriptors(
        pluginDependencies: List<PluginDependency>,
        idMap: Map<PluginId?, PluginDescriptorImpl?>
    ) {
        for (dependency: PluginDependency in pluginDependencies) {
            val subDescriptor: PluginDescriptorImpl? = dependency.subDescriptor
            if (subDescriptor == null || dependency.isDisabledOrBroken) {
                continue
            }
            val dependencyDescriptor: PluginDescriptorImpl? =
                idMap.get(dependency.getPluginId())
            if (dependencyDescriptor == null || !dependencyDescriptor.isEnabled()) {
                dependency.isDisabledOrBroken = true
                continue
            }

            // check that plugin doesn't depend on unavailable plugin
            val childDependencies: List<PluginDependency> = subDescriptor.pluginDependencies
            if (!checkChildDeps(childDependencies, idMap)) {
                dependency.isDisabledOrBroken = true
            }
        }
    }

    // multiple dependency condition is not supported, so,
    // jsp-javaee.xml depends on com.intellij.javaee.web, and included file in turn define jsp-css.xml that depends on com.intellij.css
    // that's why nesting level is more than one
    private fun checkChildDeps(
        childDependencies: List<PluginDependency>,
        idMap: Map<PluginId?, PluginDescriptorImpl?>
    ): Boolean {
        for (dependency: PluginDependency in childDependencies) {
            if (dependency.isDisabledOrBroken) {
                if (dependency.isOptional()) {
                    continue
                }
                return false
            }
            val dependentDescriptor: PluginDescriptorImpl? = idMap.get(dependency.getPluginId())
            if (dependentDescriptor == null || !dependentDescriptor.isEnabled()) {
                dependency.isDisabledOrBroken = true
                if (dependency.isOptional()) {
                    continue
                }
                return false
            }
            if (dependency.subDescriptor != null) {
                val list: List<PluginDependency> = dependency.subDescriptor.pluginDependencies
                if (!checkChildDeps(list, idMap)) {
                    dependency.isDisabledOrBroken = true
                    if (dependency.isOptional()) {
                        continue
                    }
                    return false
                }
            }
        }
        return true
    }

    fun getTopologicallySorted(graph: InboundSemiGraph<PluginDescriptorImpl?>): Array<PluginDescriptorImpl> {
        val requiredOnlyGraph: DFSTBuilder<PluginDescriptorImpl> = DFSTBuilder<Node>(
            GraphGenerator.generate(graph)
        )
        val sortedRequired: Array<PluginDescriptorImpl> =
            graph.getNodes().toArray(arrayOfNulls<PluginDescriptorImpl>(0))
        val comparator: Comparator<PluginDescriptorImpl> = requiredOnlyGraph.comparator()
        // there is circular reference between core and implementation-detail plugin, as not all such plugins extracted from core,
        // so, ensure that core plugin is always first (otherwise not possible to register actions - parent group not defined)
        Arrays.sort(sortedRequired,
            Comparator<T> { o1: T, o2: T ->
                if (CORE_ID.equals(o1.getPluginId())) -1 else if (CORE_ID.equals(
                        o2.getPluginId()
                    )
                ) 1 else comparator.compare(o1, o2)
            })
        return sortedRequired
    }

    @ApiStatus.Internal
    fun buildPluginIdMap(descriptors: List<PluginDescriptorImpl?>): Map<PluginId?, PluginDescriptorImpl?> {
        val idMap: MutableMap<PluginId?, PluginDescriptorImpl?> =
            HashMap<PluginId, PluginDescriptorImpl>(descriptors.size)
        var duplicateMap: MutableMap<PluginId, MutableList<PluginDescriptorImpl>>? = null
        for (descriptor: PluginDescriptorImpl in descriptors) {
            var newDuplicateMap: MutableMap<PluginId, MutableList<PluginDescriptorImpl>>? =
                checkAndPut(descriptor, descriptor.getPluginId(), idMap, duplicateMap)
            if (newDuplicateMap != null) {
                duplicateMap = newDuplicateMap
                continue
            }
            for (module: PluginId in descriptor.modules) {
                newDuplicateMap = checkAndPut(descriptor, module, idMap, duplicateMap)
                if (newDuplicateMap != null) {
                    duplicateMap = newDuplicateMap
                }
            }
        }
        return idMap
    }

    private fun checkAndPut(
        descriptor: PluginDescriptorImpl,
        id: PluginId,
        idMap: MutableMap<PluginId?, PluginDescriptorImpl?>,
        duplicateMap: MutableMap<PluginId, MutableList<PluginDescriptorImpl>>?
    ): MutableMap<PluginId, MutableList<PluginDescriptorImpl>>? {
        var duplicateMap: MutableMap<PluginId, MutableList<PluginDescriptorImpl>>? =
            duplicateMap
        if (duplicateMap != null) {
            val duplicates: MutableList<PluginDescriptorImpl>? = duplicateMap.get(id)
            if (duplicates != null) {
                duplicates.add(descriptor)
                return duplicateMap
            }
        }
        val existingDescriptor: PluginDescriptorImpl? = idMap.put(id, descriptor)
        if (existingDescriptor == null) {
            return null
        }

        // if duplicated, both are removed
        idMap.remove(id)
        if (duplicateMap == null) {
            duplicateMap = LinkedHashMap<PluginId, MutableList<PluginDescriptorImpl>>()
        }
        val list: MutableList<PluginDescriptorImpl> = ArrayList<PluginDescriptorImpl>()
        list.add(existingDescriptor)
        list.add(descriptor)
        duplicateMap!![id] = list
        return duplicateMap
    }

    private fun computePluginEnabled(
        descriptor: PluginDescriptorImpl,
        loadedPluginIds: Set<PluginId>,
        loadedModuleIds: Set<PluginId>,
        idMap: Map<PluginId?, PluginDescriptorImpl?>,
        disabledRequiredIds: MutableSet<PluginId>,
        disabledPlugins: Set<PluginId>,
        errors: MutableMap<PluginId, PluginLoadingError>
    ): Boolean {
        if (CORE_ID.equals(descriptor.getPluginId())) {
            return true
        }
        val notifyUser: Boolean = !descriptor.isImplementationDetail()
        var result: Boolean = true
        for (incompatibleId: PluginId in descriptor.incompatibilities) {
            if (!loadedModuleIds.contains(incompatibleId) || disabledPlugins.contains(incompatibleId)) {
                continue
            }
            result = false
            val presentableName: String = incompatibleId.getIdString()
            errors[descriptor.getPluginId()] = PluginLoadingError(
                descriptor,
                message(
                    "plugin.loading.error.long.ide.contains.conflicting.module",
                    descriptor.getName(), presentableName
                ),
                message(
                    "plugin.loading.error.short.ide.contains.conflicting.module",
                    presentableName
                ),
                notifyUser,
                null
            )
        }
        for (dependency: PluginDependency in descriptor.pluginDependencies) {
            val depId: PluginId = dependency.getPluginId()
            if (dependency.isOptional() || loadedPluginIds.contains(depId) || loadedModuleIds.contains(
                    depId
                )
            ) {
                continue
            }
            result = false
            val dep: PluginDescriptor? = idMap.get(depId)
            if (dep != null && disabledPlugins.contains(depId)) {
                // broken/incompatible plugins can be updated, add them anyway
                disabledRequiredIds.add(dep.getPluginId())
            }
            val depName: String? = if (dep == null) null else dep.getName()
            if (depName == null) {
                val depPresentableId: @NlsSafe String? = depId.getIdString()
                if (errors.containsKey(depId)) {
                    errors[descriptor.getPluginId()] = PluginLoadingError(
                        descriptor,
                        message(
                            "plugin.loading.error.long.depends.on.failed.to.load.plugin",
                            descriptor.getName(),
                            (depPresentableId)!!
                        ),
                        message(
                            "plugin.loading.error.short.depends.on.failed.to.load.plugin",
                            (depPresentableId)
                        ), notifyUser,
                        null
                    )
                } else {
                    errors[descriptor.getPluginId()] = PluginLoadingError(
                        descriptor,
                        message(
                            "plugin.loading.error.long.depends.on.not.installed.plugin",
                            descriptor.getName(),
                            (depPresentableId)!!
                        ),
                        message(
                            "plugin.loading.error.short.depends.on.not.installed.plugin",
                            (depPresentableId)
                        ),
                        notifyUser,
                        null
                    )
                }
            } else {
                errors[descriptor.getPluginId()] = PluginLoadingError(
                    descriptor,
                    message(
                        "plugin.loading.error.long.depends.on.disabled.plugin",
                        descriptor.getName(), depName
                    ),
                    message(
                        "plugin.loading.error.short.depends.on.disabled.plugin",
                        depName
                    ),
                    notifyUser,
                    dep.getPluginId()
                )
            }
        }
        return result
    }

    @Nls
    private fun message(
        key: String,
        vararg params: Any
    ): Supplier<String> {
        return object : Supplier<String> {
            override fun get(): String {
                return CoreBundle.message(key, params)
            }
        }
    }

    @Nls
    private fun message(
        key: Int,
        vararg params: Any
    ): Supplier<String> {
        return Supplier { AndroidBundle.coreBundle.message(key, params) }
    }


    @Synchronized
    private fun loadAndInitializePlugins(
        context: DescriptorListLoadingContext?,
        coreLoader: ClassLoader?
    ) {
        var context: DescriptorListLoadingContext? = context
        var coreLoader: ClassLoader? = coreLoader
        if (coreLoader == null) {
            val callerClass: Class<*>? = ReflectionUtil.findCallerClass(1)
            assert(callerClass != null)
            coreLoader = callerClass!!.classLoader
        }
        try {
            if (context == null) {
                context = PluginDescriptorLoader.loadDescriptors(
                    isUnitTestMode,
                    isRunningFromSources
                )
            }
            val activity: Activity =
                StartUpMeasurer.startActivity("plugin initialization", ActivityCategory.DEFAULT)
            val initResult: PluginManagerState = initializePlugins(
                context,
                (coreLoader)!!, !isUnitTestMode
            )
            ourPlugins = initResult.sortedPlugins
            val result: PluginLoadingResult = context.result
            if (!result.incompletePlugins.isEmpty()) {
                val oldSize: Int = initResult.sortedPlugins.length
                val all: Array<PluginDescriptorImpl> = Arrays.copyOf(
                    initResult.sortedPlugins,
                    oldSize + result.incompletePlugins.size()
                )
                ArrayUtil.copy(result.incompletePlugins.values(), all, oldSize)
                ourPlugins = all
            }
            ourPluginsToDisable = initResult.effectiveDisabledIds
            ourPluginsToEnable = initResult.disabledRequiredIds
            ourLoadedPlugins = initResult.sortedEnabledPlugins
            ourShadowedBundledPlugins = result.shadowedBundledIds
            activity.end()
            activity.setDescription("plugin count: " + ourLoadedPlugins!!.size)
            logPlugins(initResult.sortedPlugins, result.incompletePlugins.values())
        } catch (e: RuntimeException) {
            logger.error(e)
            throw e
        }
    }

    // do not use class reference here
    val logger: Logger
        get() {
            // do not use class reference here
            return Logger.getInstance("#com.dingyi.myluaapp.ide.plugins.PluginManager")
        }

    fun getPlugin(id: PluginId?): PluginDescriptor? {
        if (id != null) {
            for (plugin: PluginDescriptor in plugins) {
                if (id.equals(plugin.getPluginId())) {
                    return plugin
                }
            }
        }
        return null
    }

    fun findPluginByModuleDependency(id: PluginId): PluginDescriptor? {
        for (descriptor: PluginDescriptorImpl in ourPlugins) {
            if (descriptor.modules.contains(id)) {
                return descriptor
            }
        }
        return null
    }

    fun isPluginInstalled(id: PluginId?): Boolean {
        return getPlugin(id) != null
    }

    @ApiStatus.Internal
    fun buildPluginIdMap(): Map<PluginId?, PluginDescriptorImpl?> {
        LoadingState.COMPONENTS_REGISTERED.checkOccurred()
        return buildPluginIdMap(Arrays.asList(*ourPlugins))
    }

    /**
     * You must not use this method in cycle, in this case use [.processAllDependencies] instead
     * (to reuse result of [.buildPluginIdMap]).
     *
     * [FileVisitResult.SKIP_SIBLINGS] is not supported.
     *
     * Returns `false` if processing was terminated because of [FileVisitResult.TERMINATE], and `true` otherwise.
     */
    @ApiStatus.Internal
    fun processAllDependencies(
        rootDescriptor: PluginDescriptorImpl,
        withOptionalDeps: Boolean,
        consumer: java.util.function.Function<in PluginDescriptor?, FileVisitResult>
    ): Boolean {
        return processAllDependencies(
            rootDescriptor,
            withOptionalDeps,
            buildPluginIdMap(),
            consumer
        )
    }

    @ApiStatus.Internal
    fun processAllDependencies(
        rootDescriptor: PluginDescriptorImpl,
        withOptionalDeps: Boolean,
        idToMap: Map<PluginId?, PluginDescriptorImpl?>,
        consumer: java.util.function.Function<in PluginDescriptor?, FileVisitResult>
    ): Boolean {
        return processAllDependencies(rootDescriptor,
            withOptionalDeps,
            idToMap,
            HashSet<PluginDescriptor?>(),
            BiFunction<PluginId, PluginDescriptorImpl?, FileVisitResult> { id: PluginId?, descriptor: PluginDescriptorImpl? ->
                if (descriptor != null) consumer.apply(
                    descriptor
                ) else FileVisitResult.SKIP_SUBTREE
            })
    }

    @ApiStatus.Internal
    fun processAllDependencies(
        rootDescriptor: PluginDescriptorImpl,
        withOptionalDeps: Boolean,
        idToMap: Map<PluginId?, PluginDescriptorImpl?>,
        consumer: BiFunction<PluginId, PluginDescriptorImpl?, FileVisitResult>
    ): Boolean {
        return processAllDependencies(
            rootDescriptor,
            withOptionalDeps,
            idToMap,
            HashSet<PluginDescriptor?>(),
            consumer
        )
    }

    @ApiStatus.Internal
    private fun processAllDependencies(
        rootDescriptor: PluginDescriptorImpl,
        withOptionalDeps: Boolean,
        idToMap: Map<PluginId?, PluginDescriptorImpl?>,
        depProcessed: MutableSet<PluginDescriptor?>,
        consumer: BiFunction<PluginId, PluginDescriptorImpl?, FileVisitResult>
    ): Boolean {
        for (dependency: PluginDependency in rootDescriptor.pluginDependencies) {
            if (!withOptionalDeps && dependency.isOptional()) {
                continue
            }
            val descriptor: PluginDescriptorImpl? = idToMap.get(dependency.getPluginId())
            val pluginId: PluginId =
                if (descriptor == null) dependency.getPluginId() else descriptor.getPluginId()
            when (consumer.apply(pluginId, descriptor)) {
                FileVisitResult.TERMINATE -> return false
                FileVisitResult.CONTINUE -> if (descriptor != null && depProcessed.add(descriptor)) {
                    processAllDependencies(
                        descriptor,
                        withOptionalDeps,
                        idToMap,
                        depProcessed,
                        consumer
                    )
                }

                FileVisitResult.SKIP_SUBTREE -> {}
                FileVisitResult.SKIP_SIBLINGS -> throw UnsupportedOperationException("FileVisitResult.SKIP_SIBLINGS is not supported")
            }
        }
        return true
    }

    private fun getOnlyEnabledPlugins(sortedAll: Array<PluginDescriptorImpl>): List<PluginDescriptorImpl> {
        val enabledPlugins: MutableList<PluginDescriptorImpl> =
            ArrayList<PluginDescriptorImpl>(sortedAll.size)
        for (descriptor: PluginDescriptorImpl in sortedAll) {
            if (descriptor.isEnabled()) {
                enabledPlugins.add(descriptor)
            }
        }
        return enabledPlugins
    }

    @Synchronized
    fun isUpdatedBundledPlugin(plugin: PluginDescriptor): Boolean {
        return ourShadowedBundledPlugins != null && ourShadowedBundledPlugins!!.contains(plugin.getPluginId())
    }
    //<editor-fold desc="Deprecated stuff.">

    @Deprecated("Use {@link #isDisabled(PluginId)} ")
    fun isDisabled(pluginId: String): Boolean {
        return isDisabled(PluginId.getId(pluginId))
    }

    @Deprecated("Use {@link #disablePlugin(PluginId)} ")
    fun disablePlugin(id: String): Boolean {
        return disablePlugin(PluginId.getId(id))
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2021.3")
    @Deprecated("Use {@link #enablePlugin(PluginId)} ")
    fun enablePlugin(id: String): Boolean {
        return enablePlugin(PluginId.getId(id))
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2021.2")
    @Deprecated("Use {@link DisabledPluginsState#addDisablePluginListener} directly")
    fun addDisablePluginListener(listener: Runnable) {
        DisabledPluginsState.addDisablePluginListener(listener)
    } //</editor-fold>

    class EssentialPluginMissingException internal constructor(val pluginIds: List<String?>) :
        RuntimeException(
            "Missing essential plugins: " + java.lang.String.join(
                ", ",
                pluginIds
            )
        )
}