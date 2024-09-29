package io.github.sgpublic

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class CreateTag: DefaultTask() {
    override fun getGroup(): String {
        return "publishing"
    }

    @get:Input
    abstract val token: Property<String>

    @TaskAction
    fun execute() {
        Git.open(project.rootDir).use { git ->
            val content = cacheFile.reader().use {
                Gson().fromJson(it, JsonObject::class.java)
            }
            val linuxqqVersion = content.get("linuxqq.version").asString
            val llqqntVersion = content.get("llqqnt.version").asString
            val dockerImageVersion = content.get("dockerimage.version").asInt
            git.add().addFilepattern("liteloaderqqnt.json").call()
            git.commit()
                .setMessage("chore(linuxqq): update liteloaderqqnt $llqqntVersion, linuxqq $linuxqqVersion")
                .setAuthor("updater", "updater@example.com")
                .call()
            git.tag()
                .setName("v${linuxqqVersion}-v${llqqntVersion}-${dockerImageVersion}")
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
}