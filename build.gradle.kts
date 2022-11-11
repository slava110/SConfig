import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    antlr
    application
}

group = "com.slava_110.sconfig"
version = "1.0"

application {
    mainClass.set("com.slava_110.sconfig.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.11.1")
    implementation("org.antlr:antlr4-runtime:4.11.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("com.charleskorn.kaml:kaml:0.49.0")

    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = listOf("-visitor")
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}