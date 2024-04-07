import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import io.github.sgpublic.QQNTDownload
import io.github.sgpublic.LiteLoaderDownload
import io.github.sgpublic.aptInstall

plugins {
    alias(ql.plugins.docker.api)
    alias(ql.plugins.release.github)
}

group = "io.github.sgpublic"
version = "1"

val DEBIAN_BOOKWORN_DATE: String by project

tasks {
    val tag = "mhmzx/qqnt-llonebot"

    val downloadLatestQQNT by creating(QQNTDownload::class)

    val downloadLatestLiteLoader by creating(LiteLoaderDownload::class)

    val dockerCreateBookwormDockerfile by creating(Dockerfile::class) {
        dependsOn(downloadLatestQQNT, downloadLatestLiteLoader)
        doFirst {
            delete(layout.buildDirectory.file("docker-bookworm"))
            copy {
                from("./src/main/docker/")
                include("*.sh")
                into(layout.buildDirectory.dir("docker-bookworm"))
            }
            copy {
                from(layout.buildDirectory.dir("adb/platform-tools"))
                into(layout.buildDirectory.dir("docker-bookworm/adb"))
            }
        }
        group = "docker"
        destFile = layout.buildDirectory.file("docker-bookworm/Dockerfile")
        from("debian:bookworm-$DEBIAN_BOOKWORN_DATE")
        workingDir("/tmp")
        copyFile("./*.sh", "/")
        copyFile("./*.deb", "/tmp")
        runCommand(listOf(
            "apt-get update",
            aptInstall("tasksel"),
            aptInstall("/tmp/${QQNTDownload.QQNT_VERSION_NAME}.deb"),
            "tasksel install desktop gnome-desktop",
            "useradd -m -u 1000 qqnt",
            "apt-get clean",
            "rm -f /tmp/${QQNTDownload.QQNT_VERSION_NAME}.deb",
        ).joinToString(" &&\\\n "))
        volume("/home/qqnt/.cache/QQ")
        entryPoint("bash", "/docker-entrypoint.sh")
    }
    val dockerBuildBookwormImage by creating(DockerBuildImage::class) {
        group = "docker"
        dependsOn(dockerCreateBookwormDockerfile)
        inputDir = layout.buildDirectory.dir("docker-bookworm")
        dockerFile = dockerCreateBookwormDockerfile.destFile
        images.add("$tag:${QQNTDownload.QQNT_VERSION_NAME}-${LiteLoaderDownload.LL_VERSION_NAME}-$version")
        images.add("$tag:latest")
        noCache = true
    }

    val dockerPushBuildBookImageOfficial by creating(DockerPushImage::class) {
        group = "docker"
        dependsOn(dockerBuildBookwormImage)
        images.add("$tag:${QQNTDownload.QQNT_VERSION_NAME}-${LiteLoaderDownload.LL_VERSION_NAME}-$version")
        images.add("$tag:latest")
    }

    val dockerPushImageOfficial by creating {
        group = "docker"
        dependsOn(dockerPushBuildBookImageOfficial)
    }
}

fun findEnv(name: String): String {
    return findProperty(name)?.toString()?.takeIf { it.isNotBlank() }
            ?: System.getenv(name.replace(".", "_").uppercase())
}

docker {
    registryCredentials {
        username = findEnv("publishing.docker.username")
        password = findEnv("publishing.docker.password")
    }
}

githubRelease {
    token(findEnv("publishing.github.token"))
    owner = "sgpublic"
    repo = "poetry-docker"
    tagName = "$version"
    releaseName = "$version"
    overwrite = true
}
