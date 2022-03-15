package com.dingyi.myluaapp.plugin.runtime.action

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.dingyi.myluaapp.common.ktx.convertObject
import com.dingyi.myluaapp.plugin.api.Action
import com.dingyi.myluaapp.plugin.api.action.ActionArgument
import com.dingyi.myluaapp.plugin.api.action.ActionKey
import com.dingyi.myluaapp.plugin.api.action.ActionService
import com.dingyi.myluaapp.plugin.api.context.PluginContext

class ActionService(private val pluginContext: PluginContext) : ActionService {

    private val allAction = mutableMapOf<ActionKey, MutableList<Class<*>>>()

    private val allForwardAction =
        mutableMapOf<ActionKey, MutableList<(ActionArgument) -> ActionArgument>>()

    override fun createActionArgument(): ActionArgument {
        return DefaultActionArgument()
    }

    override fun <T : Action<*>> registerAction(actionClass: Class<T>, key: ActionKey) {
        val keyAction = allAction.getOrDefault(key, mutableListOf())
        if (key.isRepeat().not() && keyAction.isNotEmpty()) {
            if (keyAction[0] != actionClass) {
                error("The key can not repeat add")
            }
        }

        if (!keyAction.contains(actionClass))
            keyAction.add(actionClass)

        allAction[key] = keyAction
    }

    override fun <T : Action<*>> registerAction(
        actionClass: Class<T>,
        lifecycle: Lifecycle,
        key: ActionKey
    ) {
        registerAction(actionClass, key)
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    unregisterAction(actionClass.convertObject(), key)
                    source.lifecycle.removeObserver(this)
                }
            }
        })
    }

    override fun unregisterAction(actionClass: Class<Action<*>>, key: ActionKey) {
        val keyAction = allAction.getOrDefault(key, mutableListOf())
        keyAction.remove(actionClass)
        allAction[key] = keyAction
    }


    override fun clearAction(key: ActionKey) {
        allAction[key]?.clear()
    }

    override fun <T> callAction(actionArgument: ActionArgument, key: ActionKey): T? {
        val list = allAction[key]
        val targetActionArgument = forwardActionArgument(actionArgument, key)
        if (list != null) {
            for (action in list) {
                val result = (action.newInstance() as Action<*>).callAction(targetActionArgument)
                if (result != null) {
                    return result as T
                }
            }
        }
        return null

    }

    override fun registerForwardArgument(
        key: ActionKey,
        block: (ActionArgument) -> ActionArgument
    ) {
        val keyAction = allForwardAction.getOrDefault(key, mutableListOf())
        keyAction.add(block)
        allForwardAction[key] = keyAction
    }

    override fun registerForwardArgument(
        vararg key: ActionKey,
        block: (ActionArgument) -> ActionArgument
    ) {
        key.forEach {
            registerForwardArgument(it, block)
        }
    }

    override fun forwardActionArgument(
        actionArgument: ActionArgument,
        key: ActionKey
    ): ActionArgument {
        val keyAction = allForwardAction.getOrDefault(key, mutableListOf())

        allForwardAction[key] = keyAction

        return keyAction
            .fold(actionArgument) { acc: ActionArgument, function: (ActionArgument) -> ActionArgument ->
                function.invoke(acc)
            }
    }

    override fun unregisterForwardArgument(
        vararg key: ActionKey,
        block: (ActionArgument) -> ActionArgument
    ) {
        key.forEach {
            unregisterForwardArgument(it, block)
        }
    }

    override fun unregisterForwardArgument(
        key: ActionKey,
        block: (ActionArgument) -> ActionArgument
    ) {
        val keyAction = allForwardAction.getOrDefault(key, mutableListOf())
        keyAction.remove(block)
        allForwardAction[key] = keyAction
    }

    override fun registerForwardArgument(
        vararg key: ActionKey,
        lifecycle: Lifecycle,
        block: (ActionArgument) -> ActionArgument
    ) {
        key.forEach {
            registerForwardArgument(it, lifecycle, block)
        }
    }

    override fun registerForwardArgument(
        key: ActionKey,
        lifecycle: Lifecycle,
        block: (ActionArgument) -> ActionArgument
    ) {
        registerForwardArgument(key, block)
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    unregisterForwardArgument(key, block)
                    source.lifecycle.removeObserver(this)
                }
            }
        })
    }

    private inner class DefaultActionArgument : ActionArgument {

        private val argument = mutableListOf<Any?>()

        override fun <T> getArgument(i: Int): T? {
            return argument[i] as T?
        }

        override fun addArgument(arg: Any?): ActionArgument {
            argument.add(arg)
            return this
        }

        override fun clear() {
            argument.clear()
        }

        override fun getPluginContext(): PluginContext {
            return pluginContext
        }

    }

}