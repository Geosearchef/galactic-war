import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    dependencies {
        classpath("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    }
}

plugins {
    kotlin("multiplatform") version "1.5.31"
    kotlin("plugin.serialization") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "com.faforever.galactic-war"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

val exposedVersion: String by project

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
        withJava()
    }
    js(LEGACY) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
//                testImplementation("io.mockk:mockk:1.12.3")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("com.sparkjava:spark-core:2.9.3")
                implementation("com.sparkjava:spark-template-velocity:2.7.1")

                implementation("org.slf4j:slf4j-simple:2.0.0-alpha7")

                implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
                implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.2")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

                implementation("mysql:mysql-connector-java:8.0.28")
                implementation("org.xerial:sqlite-jdbc:3.36.0.3")
            }
        }
        val jvmTest by getting {

        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

//                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:17.0.2-pre.206-kotlin-1.5.10")
//                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:17.0.2-pre.206-kotlin-1.5.10")
            }
        }
        val jsTest by getting {

        }
    }
}

application {
    mainClass.set("GWServerKt")
}

tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack") {
    outputFileName = "gw.js"
}

//tasks.named<Copy>("jvmProcessResources") {
//    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
//    from(jsBrowserDistribution)
//}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named("jsBrowserDistribution"))
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}


// We need to pack a fat jar to include the kotlin standard libraries
// Modifying the default task to do this causes an error due to a duplicate
// Use the shadow jar plugin instead!

//tasks.withType<Jar> {
//    manifest {
//        attributes(
//            mapOf(
//                "Main-Class" to application.mainClassName
//            )
//        )
//    }
//    // from: https://stackoverflow.com/questions/44197521/gradle-project-java-lang-noclassdeffounderror-kotlin-jvm-internal-intrinsics
//    from(sourceSets.main.get().output)
//
//    // need to get Koltin libs over into jar (!! this causes duplicate file)
//    dependsOn(configurations.runtimeClasspath)
////    from({
////        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
////    })
//}