pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }

    // https://docs.gradle.org/current/userguide/platforms.html#sec:importing-catalog-from-file
    versionCatalogs {
        val ql by creating {
            from(files(File(rootDir, "./gradle/ql.versions.toml")))
        }
    }
}

rootProject.name = "llqqnt"
