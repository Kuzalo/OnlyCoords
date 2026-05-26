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
        // Version management for the mod-publish-plugin (applied per version in build.gradle.kts, without a version).
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
        // OnlyCoords targets (Mojang mappings on all via loom-back-compat). Each dedicated build
        // covers a Fabric range: 1.21.6→1.21.7, 1.21.8, 1.21.9→1.21.10, 1.21.11, 26.1→26.1.2.
        versions("1.21.6", "1.21.8", "1.21.9", "1.21.11", "26.1.2")
        vcsVersion = "1.21.8"
    }
}

rootProject.name = "OnlyCoords"
