plugins {
    `kord-module`
    `kord-sampled-module`
    `kord-publishing`
}

dependencies {
    api(projects.common)
    implementation(libs.bundles.common)

    api(libs.ktor.client.json)
    api(libs.ktor.client.websockets)
    api(libs.bundles.ktor.client.serialization)
    api(libs.ktor.client.cio)


    testImplementation(libs.bundles.test.implementation)
    testRuntimeOnly(libs.bundles.test.runtime)
}

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
