package io.github.sgpublic

import de.undercouch.gradle.tasks.download.DownloadAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class LiteLoaderDownload: DefaultTask() {
    private val action: DownloadAction by lazy {
        DownloadAction(project, this)
    }
    @TaskAction
    fun download() {
        val dir = project.layout.buildDirectory.dir("ll").get().asFile
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val ll = NetJsonObject("https://api.github.com/repos/LiteLoaderQQNT/LiteLoaderQQNT/releases/latest")

        val llVersionName = ll.get("tag_name").asString
        LL_VERSION_NAME = llVersionName
        val llUrl = ll.get("assets").asJsonArray[0]
            .asJsonObject.get("browser_download_url").asString

        logger.info("Downloading LiteLoaderQQNT $llVersionName from $llUrl")

        action.src(llUrl)
        action.dest(File(dir, "$llVersionName.zip"))
        action.overwrite(false)

        action.execute()
    }

    companion object {
        var LL_VERSION_NAME: String? = null
    }
}