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
        // Source écrite en Mojang 26.1.2 (Identifier, GuiGraphicsExtractor, graphics.text) ;
        // rétro-mapping vers les noms Mojang 1.21.8 pour les versions < 26.1.
        string(current.parsed < "26.1") {
            replace("Identifier", "ResourceLocation")
            replace("GuiGraphicsExtractor", "GuiGraphics")
            replace("graphics.text(", "graphics.drawString(")
        }

        string(current.parsed >= "26.1") {
            replace("classTweaker v1 named", "classTweaker v1 official")
        }
    }
}

// Tâche d'agrégation : publie TOUTES les versions d'un coup.
// Chaque sous-projet de version (:1.21.8, :26.1.2) a sa propre tâche publishMods (mod-publish-plugin
// appliqué par version dans build.gradle.kts). On les enchaîne ici.
// À lancer plus tard, après configuration des tokens (cf. build.gradle.kts) : ./gradlew chiseledPublishMods
// NB : si tu ajoutes une version dans settings.gradle.kts, ajoute-la aussi ci-dessous.
tasks.register("chiseledPublishMods") {
    group = "publishing"
    description = "Publie toutes les versions Minecraft (Modrinth / CurseForge / GitHub)."
    dependsOn(":1.21.8:publishMods", ":26.1.2:publishMods")
}
