// ─────────────────────────────────────────────────────────────────────────────
// PUBLISHING (Modrinth / CurseForge / GitHub Releases) — prepared but INACTIVE.
// To publish later:
//   1. Create the project on Modrinth and CurseForge, note their project IDs.
//   2. Replace the "TODO_*" placeholders in the publishMods block (below):
//      Modrinth projectId, CurseForge projectId, GitHub repository (owner/repo).
//   3. Create the 3 secrets in GitHub (Settings → Secrets and variables → Actions):
//        - MODRINTH_TOKEN    (Modrinth Personal Access Token)
//        - CURSEFORGE_TOKEN  (CurseForge API key)
//        - GITHUB_TOKEN      → provided AUTOMATICALLY by GitHub Actions, nothing to create.
//   4. Re-enable .github/workflows/publish.yml then push a "v*" tag (e.g. v1.0.0).
// Without these tokens, "publishMods" / "chiseledPublishMods" publishes nothing.
// ─────────────────────────────────────────────────────────────────────────────
plugins {
    // This plugin applies the correct loom variant based on the Minecraft version
    id("dev.kikugie.loom-back-compat")
    // Multi-platform publishing — see the publishMods block and the comment above.
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
    // YACL bundles quilt-parsers (json/gson) as JIJ; required at dev runtime (see dependencies block).
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
    // Full Fabric API: YACL/ModMenu (and our mod) depend on the "fabric-api" umbrella + several modules
    // (fabric-resource-loader-v0, fabric-screen-api-v1, the right keybind module per version, ...).
    // The individual modules (fapi(...)) do not provide the umbrella → runtime resolution fails.
    val fabricApiVersion: String = sc.properties["deps.fabric_api"]
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")

    // Config GUI: YACL (via the Modrinth maven, already declared) + ModMenu (TerraformersMC maven), per-version versions.
    val yaclVersion: String = sc.properties["deps.yacl"]
    val modmenuVersion: String = sc.properties["deps.modmenu"]
    modImplementation("maven.modrinth:yacl:$yaclVersion")
    modImplementation("com.terraformersmc:modmenu:$modmenuVersion")

    // YACL bundles quilt-parsers (json + gson 0.2.1) as JIJ. In production, Fabric loads these nested jars
    // from YACL's jar; but in dev, loom does NOT put the JIJ on the runtime classpath
    // -> NoClassDefFoundError: org.quiltmc.parsers.json.gson.GsonReader crash on YACL startup.
    // They are JIJ fabric mods (fabric.mod.json present, verified via jar tf) -> modRuntimeOnly.
    // Identical versions on YACL 1.21.8 (3.8.2) and 26.1.2 (3.9.3) -> global declaration.
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

// Multi-platform publishing — see the comment at the top of the file to enable it.
// As long as the tokens (MODRINTH_TOKEN / CURSEFORGE_TOKEN / GITHUB_TOKEN) are not defined,
// providers.environmentVariable(...) stays empty → no publishing happens (no-op task).
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