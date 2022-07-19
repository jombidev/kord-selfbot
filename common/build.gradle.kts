buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`

    // see https://github.com/gmazzo/gradle-buildconfig-plugin
    id("com.github.gmazzo.buildconfig") version "3.1.0"
}

dependencies {
    api(libs.kotlinx.datetime)

    api(libs.bundles.common)
    testImplementation(libs.bundles.test.implementation)
    testRuntimeOnly(libs.bundles.test.runtime)
}

/*
This will generate a file named "BuildConfigGenerated.kt" that looks like:

package dev.jombi.kordsb.common

internal const val BUILD_CONFIG_GENERATED_LIBRARY_VERSION: String = "<version>"
internal const val BUILD_CONFIG_GENERATED_COMMIT_HASH: String = "<commit hash>"
internal const val BUILD_CONFIG_GENERATED_SHORT_COMMIT_HASH: String = "<short commit hash>"
*/

val fatJar = task("fatJar", type = org.gradle.jvm.tasks.Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveBaseName.set("kord-selfbot-${project.name}")
    destinationDirectory.set(file("${projectDir}/../result/").also { if (!it.exists()) it.mkdir() })
    with(tasks.jar.get() as CopySpec)
}
val srcJar = task("srcJarBuilder", type = org.gradle.jvm.tasks.Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveBaseName.set("kord-selfbot-${project.name}-source")
    destinationDirectory.set(file("${projectDir}/../result/").also { if (!it.exists()) it.mkdir() })
    with(tasks.sourcesJar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
    "sourcesJar" {
        dependsOn(srcJar)
    }
}

buildConfig {
    packageName("dev.jombi.kordsb.common")
    className("BuildConfigGenerated")
    //archiveBaseName.set("kord-selfbot-${project.name}-${project.version}")

    useKotlinOutput {
        topLevelConstants = true
        internalVisibility = true
    }

    buildConfigField("String", "BUILD_CONFIG_GENERATED_LIBRARY_VERSION", "\"${Library.version}\"")
    buildConfigField("String", "BUILD_CONFIG_GENERATED_COMMIT_HASH", "\"${Library.commitHash}\"")
    buildConfigField("String", "BUILD_CONFIG_GENERATED_SHORT_COMMIT_HASH", "\"${Library.shortCommitHash}\"")
}
