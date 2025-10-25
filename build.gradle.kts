import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.internal.JavaConfigurationVariantMapping
import org.gradle.api.plugins.internal.JvmPluginsHelper
import java.net.URI

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "2.2.20"
    kotlin("kapt") version "2.2.20"
    id("com.gradleup.shadow") version "9.2.2"
    id("net.kyori.blossom") version "2.2.0"
    id("org.jetbrains.dokka") version "2.1.0"
    id("org.jetbrains.dokka-javadoc") version "2.1.0"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.3" // IntelliJ + Blossom integration
    id("org.ajoberstar.grgit.service") version "5.3.3"
}

group = "org.mythicmc"
version = "1.0.0-alpha.0${getVersionMetadata()}"
description = "A collection of useful tools and utilities for Paper and Velocity plugins."

repositories {
    mavenCentral()
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    kapt("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
}

java {
    withDokkaJar(project, project.tasks.dokkaGeneratePublicationHtml)
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
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
    relocate("kotlin", "org.mythicmc.foundation.shadow.kotlin")
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

dokka {
    dokkaSourceSets.main {
        moduleName.set("Foundation")
        includes.from("Module.md")
        suppressedFiles.from(
            "src/main/kotlin/org/mythicmc/foundation/paper",
            "src/main/kotlin/org/mythicmc/foundation/velocity"
        )
        sourceLink {
            localDirectory.set(file("src/main/kotlin"))
            remoteUrl.set(URI("https://github.com/mythicmc/Foundation/tree/master/src/main/kotlin"))
            remoteLineSuffix.set("#L")
        }
    }
}

tasks.register<Jar>("dokkaGenerateHtmlJar") {
    dependsOn(tasks.dokkaGeneratePublicationHtml)
    from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-docs")
}

tasks.register<Jar>("dokkaGenerateJavadocJar") {
    dependsOn(tasks.dokkaGeneratePublicationJavadoc)
    from(tasks.dokkaGeneratePublicationJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

/**
 * Taken from: https://github.com/Kotlin/dokka/issues/558
 * @param project [Project] this extension belongs to
 * @param artifactTask that produces the Documentation output files
 *
 * @see org.gradle.api.plugins.internal.DefaultJavaPluginExtension.withJavadocJar
 * @see org.gradle.jvm.component.internal.DefaultJvmSoftwareComponent.withJavadocJar
 * @see org.gradle.api.plugins.jvm.internal.DefaultJvmFeature.withJavadocJar
 */
fun JavaPluginExtension.withDokkaJar(project: Project, artifactTask: TaskProvider<out Task>) {
    val javadoc = this.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val kdocVariant = JvmPluginsHelper.createDocumentationVariantWithArtifact(
        javadoc.javadocElementsConfigurationName,
        null,
        DocsType.JAVADOC,
        emptySet(),
        javadoc.javadocJarTaskName,
        artifactTask,
        project as ProjectInternal,
    )
    val java = project.components.getByName<AdhocComponentWithVariants>("java")
    java.addVariantsFromConfiguration(kdocVariant, JavaConfigurationVariantMapping("runtime", true))
}

publishing {
    repositories {
        maven {
            name = "mythicmcReleases"
            url = uri("https://maven.mythicmc.org/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.mythicmc"
            artifactId = "foundation"
            version = project.version.toString()
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name = project.name
                description = project.description
                url = "https://github.com/mythicmc/Foundation"
                // properties = mapOf("myProp" to "value", "prop.with.dots" to "anotherValue")
                licenses {
                    license {
                        name = "GPL-3.0-only"
                        url = "https://spdx.org/licenses/GPL-3.0-only.html"
                    }
                }
                developers {
                    developer {
                        id = "retrixe"
                        name = "Ibrahim Ansari"
                        email = "ibu2@mythicmc.org"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/mythicmc/Foundation.git"
                    developerConnection = "scm:git:ssh://github.com/mythicmc/Foundation.git"
                    url = "https://github.com/mythicmc/Foundation/"
                }
            }
        }
    }
}
