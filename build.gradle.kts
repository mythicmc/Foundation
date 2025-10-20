import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    // TODO: If you don't want Kotlin support, remove the kotlin plugins and src/main/kotlin folder.
    //       Else if you want Kotlin only, remove the src/main/java folder. You can also mix and
    //       match (if you want to do that, you certainly know how to, it's not rocket science).
    kotlin("jvm") version "2.2.20"
    kotlin("kapt") version "2.2.20"
    id("com.gradleup.shadow") version "9.2.2"
    id("net.kyori.blossom") version "2.2.0"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.3" // IntelliJ + Blossom integration
    id("org.ajoberstar.grgit.service") version "5.3.3"
}

group = "org.mythicmc"
version = "1.0.0-alpha.0${getVersionMetadata()}"
// TODO: Set the description.
description = ""

repositories {
    mavenCentral()
    // TODO: Uncomment what you need and remove the rest (complete the dependencies block first?).
    // For Paper and/or Velocity: maven(url = "https://repo.papermc.io/repository/maven-public/")
    // For Spigot and/or Bungee: maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    // For Spigot and/or Bungee: maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    // TODO: Uncomment the dependencies you need and remove the rest.
    // NOTE: Paper and Spigot APIs are mutually exclusive!
    /* NOTE:
        You cannot bundle Adventure for use on Bungee/Spigot AND use Paper/Velocity's native
        Adventure support in the same project due to the shadow JAR relocating your class
        references! If you only use Paper/Velocity native Adventure a few times, you could
        use reflection and reference classes as Class.forName(n + "et.kyori.adventure").. lol
    */
    // Adventure for Spigot and Bungee: implementation("net.kyori:adventure-api:4.14.0")
    // Needed for Spigot plugins: implementation("net.kyori:adventure-platform-bukkit:4.3.0")
    // Needed for Bungee plugins: implementation("net.kyori:adventure-platform-bungeecord:4.3.0")

    // TODO: If removing Velocity, remove the `velocity` and `java-templates` folders in `src/`
    // Velocity: compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    // Velocity (for Java users!): annotationProcessor("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    // Velocity (for Kotlin users!): kapt("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    // TODO: If removing BungeeCord, remove `bungee.yml` in `src/`
    // Bungee: compileOnly("net.md-5:bungeecord-api:1.16-R0.5-SNAPSHOT")
    // TODO: If removing Spigot/Paper, remove `plugin.yml` in `src/`, else pick one of these:
    // Spigot: compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    // Paper 1.16 and older: compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    // Paper 1.17 and newer: compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly(fileTree(mapOf("dir" to "lib", "includes" to listOf("*.jar"))))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

sourceSets {
    main {
        blossom {
            resources {
                property("version", project.version.toString())
                property("description", project.description ?: "")
            }
            javaSources {
                property("version", project.version.toString())
                property("description", project.description ?: "")
            }
        }
    }
}

tasks.getByName<ShadowJar>("shadowJar") {
    minimize()
    archiveClassifier.set("")
    // TODO: Remove if not using Adventure or Kotlin, also, reminder to relocate bundled dependencies!
    relocate("net.kyori", "org.mythicmc.templateplugin.shadow.net.kyori")
    relocate("kotlin", "org.mythicmc.templateplugin.shadow.kotlin")
}

fun getVersionMetadata(): String {
    if (project.hasProperty("skipVersionMetadata")) return ""

    val grgit = try { grgitService.service.orNull?.grgit } catch (_: Exception) { null }
    if (grgit != null) {
        val head = grgit.head() ?: return "+unknown" // No head, fresh git repo
        var id = head.abbreviatedId
        val tag = grgit.tag.list().find { head.id == it.commit.id }

        // If we're on a tag and the tree is clean, don't put any metadata
        if (tag != null && grgit.status().isClean) {
            return ""
        }
        // Flag the build if the tree isn't clean
        if (!grgit.status().isClean) {
            id += "-dirty"
        }

        return "+git.$id"
    }

    return "+unknown"
}
