pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
    plugins {
        // Gestion de version du mod-publish-plugin (appliqué par version dans build.gradle.kts, sans version).
        id("me.modmuss50.mod-publish-plugin") version "1.1.0"
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.4"
    id("dev.kikugie.loom-back-compat") version "0.3"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
    create(rootProject) {
        // See https://stonecutter.kikugie.dev/wiki/start/#choosing-minecraft-versions
        // Cibles OnlyCoords : 1.21.8 et 26.1.2 (Mojang mappings sur les deux via loom-back-compat).
        versions("1.21.8", "26.1.2")
        vcsVersion = "1.21.8"
    }
}

rootProject.name = "OnlyCoords"
