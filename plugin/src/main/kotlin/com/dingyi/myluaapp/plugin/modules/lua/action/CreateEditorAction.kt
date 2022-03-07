package com.dingyi.myluaapp.plugin.modules.lua.action

import com.dingyi.myluaapp.common.ktx.endsWith
import com.dingyi.myluaapp.plugin.api.Action
import com.dingyi.myluaapp.plugin.api.action.ActionArgument
import com.dingyi.myluaapp.plugin.api.editor.Editor
import com.dingyi.myluaapp.plugin.modules.lua.editor.LuaLanguage

class CreateEditorAction : Action<Unit> {
    override fun callAction(argument: ActionArgument): Unit? {
        argument.getArgument<Editor>(0)?.let { editor ->

            if (editor.getFile().name.endsWith("lua","aly")) {
                editor.setLanguage(LuaLanguage())
            }
        }

        return null

    }

    override val name: String
        get() = "CreateEditorAction"
    override val id: String
        get() = "com.dingyi.myluaapp.plugin.lua.action1"
}