package io.github.sgpublic

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import de.undercouch.gradle.tasks.download.DownloadAction
import java.io.File

open class QQNTDownload: DefaultTask() {
    private val action: DownloadAction by lazy {
        DownloadAction(project, this)
    }
    private val qqntjsPattern = "var params= (.*?);".toRegex()

    @TaskAction
    fun download() {
        val dir = project.layout.buildDirectory.dir("qqnt").get().asFile
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val qqntParams = NetJsonObject(
            "https://cdn-go.cn/qq-web/im.qq.com_new/latest/rainbow/linuxQQDownload.js"
        ) { js ->
            qqntjsPattern.find(js)?.value?.let {
                return@let it.substring(12, it.length - 1)
            } ?: throw IllegalStateException("Cannot get qqnt version name.")
        }

        val qqntVersionName = qqntParams.get("version").asString
        QQNT_VERSION_NAME = qqntVersionName
        val qqntUrl = when (commandLine("dpkg --print-architecture")) {
            "amd64" -> qqntParams.get("x64DownloadUrl").asJsonObject.get("deb")
            "arm64" -> qqntParams.get("armDownloadUrl").asJsonObject.get("deb")
            else -> throw IllegalArgumentException("Unsupported arch!")
        }.asString

        logger.info("Downloading QQNT $qqntVersionName ...")

        action.src(qqntUrl)
        action.dest(File(dir, "$qqntVersionName.deb"))
        action.overwrite(false)

        action.execute()
    }

    companion object {
        var QQNT_VERSION_NAME: String? = null
    }
}
