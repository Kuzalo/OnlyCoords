plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.8"

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    swaps["mod_version"] = "\"${property("mod.version")}\";"
    swaps["minecraft"] = "\"${node.metadata.version}\";"
    constants["release"] = property("mod.id") != "template"
    dependencies["fapi"] = node.project.property("deps.fabric_api") as String

    replacements {
        // These pairs map 26.1.x's renamed Mojang classes to the legacy names the source is written
        // in. loom-back-compat reads them to expose 26.1.x's renamed classes (GuiGraphicsExtractor,
        // graphics.text(...)) under the legacy names (GuiGraphics, graphics.drawString) the source uses.
        // Note: the resource-id class (ResourceLocation -> Identifier, renamed at MC 1.21.11) is handled
        // per-version in the sources via Stonecutter //? blocks, not here, because 1.21.11 (< 26.1) needs
        // the new name natively while 26.1.x exposes both via loom-back-compat.
        string(current.parsed < "1.21.11") {
            replace("Identifier", "ResourceLocation")
        }
        string(current.parsed < "26.1") {
            replace("GuiGraphicsExtractor", "GuiGraphics")
            replace("graphics.text(", "graphics.drawString(")
        }

        string(current.parsed >= "26.1") {
            replace("classTweaker v1 named", "classTweaker v1 official")
        }
    }
}

// Aggregation task: publishes ALL versions at once.
// Each version subproject (:1.21.8, :26.1.2) has its own publishMods task (mod-publish-plugin
// applied per version in build.gradle.kts). We chain them here.
// Run later, after configuring the tokens (see build.gradle.kts): ./gradlew chiseledPublishMods
// NB: if you add a version in settings.gradle.kts, add it below too.
tasks.register("chiseledPublishMods") {
    group = "publishing"
    description = "Publishes all Minecraft versions (Modrinth / CurseForge / GitHub)."
    dependsOn(
        ":1.21.6:publishMods",
        ":1.21.8:publishMods",
        ":1.21.9:publishMods",
        ":1.21.11:publishMods",
        ":26.1.2:publishMods",
    )
}
