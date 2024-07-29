package io.github.sgpublic

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class VersionInfo: DefaultTask() {
    override fun getGroup() = "liteloaderqqnt"

    private var cache: JsonObject? = null
    private val cacheFile = File(project.rootDir, "liteloaderqqnt.json")

    @get:Input
    abstract val token: Property<String>
    @get:Input
    abstract val dockerLinuxqqRepoHost: Property<String>

    @TaskAction
    fun execute() {
        info()
    }

    private fun info(): JsonObject = Git.open(project.rootDir).use { git ->
        val isInit = !cacheFile.exists()
        if (git.status().call().hasUncommittedChanges() && !isInit) {
            throw IllegalStateException("Please commit all the change before getting linuxqq version info!")
        }

        val content = JsonObject()

        val linuxqqVersion = NetJsonArray("https://${dockerLinuxqqRepoHost.getOrElse("gitlab.com")}/api/v4/projects/105/repository/tags")
            .get(0).asJsonObject.get("name").asString

        content.add("linuxqq.version", JsonPrimitive(linuxqqVersion))

        val llqqnt = NetJsonObject("https://api.github.com/repos/LiteLoaderQQNT/LiteLoaderQQNT/releases/latest")

        val llqqntVersion = llqqnt.get("tag_name").asString.let {
            return@let it.takeIf { it.startsWith("v") } ?: "v$it"
        }
        content.add("llqqnt.version", JsonPrimitive(llqqntVersion))

        val llqqntAsset0 = llqqnt.get("assets").asJsonArray
            .get(0).asJsonObject
        val llqqntUrl = llqqntAsset0.get("browser_download_url")
        content.add("llqqnt.url", llqqntUrl)

        content.add("llqqnt.file", JsonPrimitive("LiteLoaderQQNT-${llqqntVersion}.zip"))

        var dockerImageVersion = project.version.toString().toLong()
        content.add("dockerimage.version", JsonPrimitive(dockerImageVersion))

        if (content != readCache()) {
            dockerImageVersion += 1
            content.add("dockerimage.version", JsonPrimitive(dockerImageVersion))
            cacheFile.writeText(GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(content))
            this.cache = content

            if (!isInit) {
                git.add().addFilepattern("liteloaderqqnt.json").call()
                git.commit()
                    .setMessage("chore(linuxqq): update liteloaderqqnt $llqqntVersion, linuxqq $linuxqqVersion")
                    .setAuthor("updater", "updater@example.com")
                    .call()
                git.tag()
                    .setName("${linuxqqVersion}-${llqqntVersion}-${dockerImageVersion}")
                    .call()
                git.push()
                    .also {
                        if (token.orNull != null) {
                            it.setCredentialsProvider(UsernamePasswordCredentialsProvider(
                                "mhmzx", token.get()
                            ))
                        }
                    }
                    .setPushAll().setPushTags().call()
            }
        }

        return content
    }

    private fun readCache(): JsonObject? {
        return cacheFile.takeIf { it.exists() }?.let {
            return@let Gson().fromJson(it.reader(), JsonObject::class.java)
        }
    }

    operator fun get(key: String): Any {
        val cache = this.cache ?: readCache() ?: info()
        (cache.get(key) as JsonPrimitive).let {
            return when {
                it.isNumber -> it.asLong
                it.isBoolean -> it.isBoolean
                else -> it.asString
            }
        }
    }
}
