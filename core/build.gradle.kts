plugins {
    java
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`
}

val voice by sourceSets.creating

configurations {
    getByName("voiceImplementation") {
        extendsFrom(implementation.get())
    }
}

dependencies {
    api(projects.common)
    api(projects.rest)
    api(projects.gateway)
    "voiceApi"(projects.core)
    "voiceApi"(projects.voice)

    implementation(libs.bundles.common)

    api(libs.kord.cache.api)
    api(libs.kord.cache.map)

    samplesImplementation(libs.slf4j.simple)
    testImplementation(libs.mockk)
    testImplementation(libs.bundles.test.implementation)
    testRuntimeOnly(libs.bundles.test.runtime)
}

val fatJar = task("fatJar", type = org.gradle.jvm.tasks.Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveBaseName.set("kord-selfbot-${project.name}")
    destinationDirectory.set(file("${projectDir}/../result/").also { if (!it.exists()) it.mkdir() })
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
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

java {
    registerFeature("voice") {
        usingSourceSet(voice)
        withJavadocJar()
        withSourcesJar()
        capability("dev.jombi.kordsb", "core-voice", version as String)
    }
}
