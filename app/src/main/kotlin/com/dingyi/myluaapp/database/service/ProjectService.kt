package com.dingyi.myluaapp.database.service

import com.dingyi.myluaapp.bean.ProjectInfo
import com.dingyi.myluaapp.database.bean.CodeFile
import com.dingyi.myluaapp.database.bean.ProjectConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litepal.LitePal
import org.litepal.extension.find

/**
 * @author: dingyi
 * @date: 2021/8/8 18:33
 * @description:
 **/
object ProjectService {

    suspend fun queryCodeFile(path: String) = withContext(Dispatchers.IO) {
        LitePal.where("filePath = ?", path)
            .find<CodeFile>()
            .run {
                getOrNull(0)
            }
    }


    suspend fun queryProjectConfig(info: ProjectInfo) = withContext(Dispatchers.IO) {
        val path = info.path
        LitePal.where("projectPath = ?", path)
            .find<ProjectConfig>(true)
            .run {
                if (size > 0) {
                    get(0)
                } else {
                    val config = ProjectConfig()
                    config.apply {
                        id = 0
                        this.lastOpenDir = path
                        this.projectPath = path
                    }
                    val file = CodeFile().apply {
                        id = 0
                        projectConfig = config
                        openSelectionColumn=0
                        openSelectionLine=0
                        this.filePath = "$path/app/src/main/assets/main.lua"
                    }
                    config.openFiles.add(file)
                    withContext(Dispatchers.IO) {
                        file.save()
                        config.apply { save() }
                    }
                }
            }
    }


    suspend fun queryProjectConfig(path: String) = withContext(Dispatchers.IO) {
        LitePal.where("projectPath = ?", path)
            .find<ProjectConfig>(true)[0]
    }

}