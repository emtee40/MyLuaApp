package com.dingyi.myluaapp.ui.settings

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.dingyi.myluaapp.R
import com.dingyi.myluaapp.base.BaseFragment
import com.dingyi.myluaapp.common.ktx.*
import com.dingyi.myluaapp.databinding.FragmentSettingsBinding
import com.dingyi.myluaapp.ui.GeneralActivity
import com.hjq.language.MultiLanguages
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.*
import de.Maxr1998.modernpreferences.preferences.choice.SelectionItem
import java.util.*
import kotlin.concurrent.thread

class SettingsFragment : BaseFragment<FragmentSettingsBinding, MainViewModel>() {

    override fun getViewModelClass(): Class<MainViewModel> {
        return getJavaClass()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {

            val targetMethod = it.getString("method") ?: "main"

            val methodStringArg = it.getString("arg") ?: ""


            val methodInstance = if (methodStringArg.isEmpty()) {
                getJavaClass<SettingsFragment>()
                    .getMethod(targetMethod)
            } else {
                getJavaClass<SettingsFragment>()
                    .getMethod(targetMethod, getJavaClass<String>())
            }

            val screen = if (methodStringArg.isEmpty()) methodInstance.invoke(this) else
                methodInstance.invoke(this, methodStringArg)

            if (screen is PreferenceScreen) {
                val activity = requireActivity()
                if (activity is AppCompatActivity) {
                    activity.supportActionBar?.apply {
                        title = screen.titleRes.getString()
                        setDisplayHomeAsUpEnabled(true)
                    }
                }

                val preferencesAdapter = PreferencesAdapter(screen)
                viewBinding.list.apply {

                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    adapter = preferencesAdapter
                }

                preferencesAdapter.setRootScreen(screen)
            }
        }

    }


    override fun getViewBindingInstance(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }


    private fun getLanguageSelectionItem(): List<SelectionItem> {
        val settingsApplicationLanguageEntry = requireContext()
            .getStringArray(R.array.settings_application_language_entry)

        val settingsApplicationLanguageValue = requireContext()
            .getStringArray(R.array.settings_application_language_entry_value)

        return settingsApplicationLanguageEntry.mapIndexed { index, s ->
            SelectionItem(
                key = settingsApplicationLanguageValue[index],
                title = s,
                summary = null
            )
        }

    }

    private fun checkLanguage(language: String) {
        val restart =
            when (language) {
                "default" -> MultiLanguages.setSystemLanguage(requireContext())
                "chinese" -> MultiLanguages.setAppLanguage(requireContext(), Locale.CHINESE)
                "english" -> MultiLanguages.setAppLanguage(requireContext(), Locale.ENGLISH)
                else -> false
            }



        if (restart) {
            thread {
                requireActivity().runOnUiThread {
                    R.string.settings_editor_language_restart_toast
                        .getString()
                        .showToast()
                }
                Thread.sleep(500)
                android.os.Process.killProcess(android.os.Process.myPid())    //获取PID
            }
        }
    }

    fun application() = screen(requireActivity()) {
        titleRes = R.string.settings_application_category


        singleChoice(
            key = "language",
            items = getLanguageSelectionItem()
        ) {
            titleRes = R.string.settings_application_language_title
            summaryRes = R.string.settings_application_language_summary
            icon = iconRes(R.drawable.ic_twotone_translate_24)

            onSelectionChange {
                checkLanguage(it)
                true
            }

        }
    }


    fun editor() = screen(requireActivity()) {
        titleRes = R.string.settings_editor_category

        categoryHeader("settings_editor_function_category") {
            titleRes = R.string.settings_editor_function_category
        }

        editText("symbol") {
            titleRes = R.string.settings_editor_symbol_bar_category
        }

        categoryHeader("settings_editor_appearance_category") {
            titleRes = R.string.settings_editor_appearance_category
        }

        pref("font_set") {
            titleRes = R.string.settings_editor_font_category
            summaryRes = R.string.settings_editor_font_summary
            icon = iconRes(R.drawable.ic_twotone_translate_24)
            onClick {
                startActivityForResult(
                    Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        type = "font/ttf"
                    },
                    REQUEST_FONT_SET_CODE
                )
                true
            }
        }

        switch("magnifier_set") {
            titleRes = R.string.settings_editor_magnifier_category
            summaryRes = R.string.settings_editor_magnifier_summary
            icon = iconRes(R.drawable.ic_twotone_search_24)

        }


    }


    /**
     * The main function will return preferences dsl of the settings fragment
     */
    fun main() = screen(requireActivity()) {
        titleRes = R.string.settings_main_title

        //application
        pref("application") {
            titleRes = R.string.settings_application_category
            summaryRes = R.string.settings_application_summary
            icon = iconRes(R.drawable.ic_twotone_palette_24)
            onClick {
                startSettings("application")
                true
            }
        }

        //editor
        pref("editor") {
            titleRes = R.string.settings_editor_category
            summaryRes = R.string.settings_editor_summary
            icon = iconRes(R.drawable.ic_twotone_keyboard_24)
            onClick {
                startSettings("editor")
                true
            }
        }

        //build
        pref("build") {
            icon = iconRes(R.drawable.ic_twotone_build_24)
            summaryRes = R.string.settings_build_summary
            titleRes =R.string.settings_build_category
        }

        //plugin
        pref("plugin") {
            icon = iconRes(R.drawable.ic_twotone_memory_24)
            summaryRes = R.string.settings_plugin_summary
            titleRes = R.string.settings_plugin_category
        }

        //about
        pref("about") {
            titleRes = R.string.settings_about_category
        }
    }


    private fun startSettings(method: String, arg: String? = null) {
        requireActivity().startActivity<GeneralActivity> {
            putExtra("type", getJavaClass<SettingsFragment>().name)
            putExtra("arg",
                Bundle().apply {
                    putString("method", method)
                    putString("arg", arg)
                }
            )
        }

    }


    private fun iconRes(
        iconRes: Int,
        tintRes: Int = R.attr.theme_hintTextColor
    ): Drawable? {
        val drawable = AppCompatResources
            .getDrawable(requireContext(), iconRes)

        if (tintRes != 0) {
            val color = requireContext().getAttributeColor(tintRes)
            drawable?.setTint(color)
        }
        return drawable
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_FONT_SET_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let {
                        requireContext().contentResolver.openInputStream(it)?.use { input ->
                            runCatching {
                                (Paths.fontsDir + "/default.ttf").toFile().run {
                                    if (!exists()) {
                                        createNewFile()
                                    }
                                    outputStream()
                                }.use { output ->
                                    input.copyTo(output)
                                }
                            }.isSuccess
                        }
                    }?.let {
                        if (it) R.string.settings_editor_font_toast_successful
                        else R.string.settings_editor_font_toast_fail
                    }?.getString()?.showToast()
                }
            }
        }


    }

    companion object {
        const val REQUEST_FONT_SET_CODE = 1
    }
}