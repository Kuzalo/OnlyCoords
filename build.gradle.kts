// ─────────────────────────────────────────────────────────────────────────────
// PUBLICATION (Modrinth / CurseForge / GitHub Releases) — préparée mais INACTIVE.
// Pour publier plus tard :
//   1. Crée le projet sur Modrinth et CurseForge, note leurs project IDs.
//   2. Remplace les placeholders "TODO_*" dans le bloc publishMods (plus bas) :
//      projectId Modrinth, projectId CurseForge, repository GitHub (owner/repo).
//   3. Crée les 3 secrets dans GitHub (Settings → Secrets and variables → Actions) :
//        - MODRINTH_TOKEN    (Personal Access Token Modrinth)
//        - CURSEFORGE_TOKEN  (clé API CurseForge)
//        - GITHUB_TOKEN      → fourni AUTOMATIQUEMENT par GitHub Actions, rien à créer.
//   4. Réactive .github/workflows/publish.yml puis pousse un tag « v* » (ex: v1.0.0).
// Sans ces tokens, « publishMods » / « chiseledPublishMods » ne publie rien.
// ─────────────────────────────────────────────────────────────────────────────
plugins {
    // This plugin applies the correct loom variant based on the Minecraft version
    id("dev.kikugie.loom-back-compat")
    // Publication multi-plateforme — voir le bloc publishMods et le commentaire ci-dessus.
    id("me.modmuss50.mod-publish-plugin")
}

// DO NOT set group = ...!
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

// This can be used for publishing on Modrinth and Curseforge
val compatibleVersions: List<String> = sc.properties.rawOrNull("mod", "mc_releases")
    ?.asList().orEmpty().map { it.toString() }

repositories {
    /**
     * Restricts dependency search of the given [groups] to the [maven URL][url],
     * improving the setup speed.
     */
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
    strictMaven("https://maven.terraformersmc.com/releases", "TerraformersMC", "com.terraformersmc")
    // YACL embarque quilt-parsers (json/gson) en JIJ ; requis au runtime dev (cf. bloc dependencies).
    strictMaven("https://maven.quiltmc.org/repository/release", "QuiltMC", "org.quiltmc.parsers")
}

dependencies {
    /**
     * Fetches only the required Fabric API modules to not waste time downloading all of them for each version.
     * @see <a href="https://github.com/FabricMC/fabric">List of Fabric API modules</a>
     */
    fun fapi(vararg modules: String) {
        for (it in modules) modImplementation(fabricApi.module(it, sc.properties["deps.fabric_api"]))
    }

    minecraft("com.mojang:minecraft:${sc.current.version}")
    // Applies Mojang Mappings on obfuscated versions
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    // Full Fabric API : YACL/ModMenu (et notre mod) dépendent de l'umbrella "fabric-api" + de plusieurs modules
    // (fabric-resource-loader-v0, fabric-screen-api-v1, le bon module keybind par version, …).
    // Les modules individuels (fapi(...)) ne fournissent pas l'umbrella → résolution runtime KO.
    val fabricApiVersion: String = sc.properties["deps.fabric_api"]
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")

    // GUI de config : YACL (via le maven Modrinth, déjà déclaré) + ModMenu (maven TerraformersMC), versions per-version.
    val yaclVersion: String = sc.properties["deps.yacl"]
    val modmenuVersion: String = sc.properties["deps.modmenu"]
    modImplementation("maven.modrinth:yacl:$yaclVersion")
    modImplementation("com.terraformersmc:modmenu:$modmenuVersion")

    // YACL embarque quilt-parsers (json + gson 0.2.1) en JIJ. En prod, Fabric charge ces jars imbriqués
    // depuis le jar de YACL ; mais en dev, loom ne place PAS les JIJ sur le classpath runtime
    // -> crash NoClassDefFoundError: org.quiltmc.parsers.json.gson.GsonReader au démarrage de YACL.
    // Ce sont des fabric mods JIJ (fabric.mod.json présent, vérifié via jar tf) -> modRuntimeOnly.
    // Versions identiques sur YACL 1.21.8 (3.8.2) et 26.1.2 (3.9.3) -> déclaration globale.
    modRuntimeOnly("org.quiltmc.parsers:json:0.2.1")
    modRuntimeOnly("org.quiltmc.parsers:gson:0.2.1")
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json") // Useful for interface injection
    accessWidenerPath = sc.process(
        rootProject.file("src/main/resources/onlycoords.ct"),
        "build/processed.ct"
    )

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        vmArgs("-Dmixin.debug.export=true") // Exports transformed classes for debugging
        runDir = "../../run" // Shares the run directory between versions
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

// Publication multi-plateforme — voir le commentaire en haut du fichier pour l'activer.
// Tant que les tokens (MODRINTH_TOKEN / CURSEFORGE_TOKEN / GITHUB_TOKEN) ne sont pas définis,
// providers.environmentVariable(...) reste vide → aucune publication n'a lieu (tâche no-op).
publishMods {
    file = loomx.modJar.flatMap { it.archiveFile }
    displayName = "${property("mod.name")} ${property("mod.version")} (MC ${sc.current.version})"
    version = project.version.toString()
    changelog = providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText.orElse("Initial release.")
    type = STABLE
    modLoaders.add("fabric")

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = "TODO_MODRINTH_ID" // TODO Kuzalo: replace with actual Modrinth project ID after creating the project
        minecraftVersions.addAll(compatibleVersions)
    }
    curseforge {
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        projectId = "TODO_CURSEFORGE_ID" // TODO Kuzalo: replace with actual CurseForge project ID after creating the project
        minecraftVersions.addAll(compatibleVersions)
    }
    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        repository = "TODO_KUZALO/OnlyCoords" // TODO Kuzalo: replace with your GitHub <owner>/<repo>
        commitish = "main"
    }
}

tasks {
    processResources {
        fun MutableMap<String, String>.register(key: String, property: String) {
            val value: String = sc.properties[property]
            inputs.property(key, value)
            set(key, value)
        }

        val props = buildMap {
            register("id", "mod.id")
            register("name", "mod.name")
            register("version", "mod.version")
            register("minecraft", "mod.mc_compat")
        }

        filesMatching("fabric.mod.json") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    // Builds the version into a shared folder in `build/libs/${mod version}/`
    register<Copy>("buildAndCollect") {
        group = "build"

        // loomx.mod(Sources)Jar returns the jar task for the applied loom variant
        from(loomx.modJar.map { it.archiveFile }, loomx.modSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}