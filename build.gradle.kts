// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion = "1.5.31"

    repositories {
        google()
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion-RC")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt") version "1.18.1"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    ktlint {
        debug.apply { false }
    }
}

detekt {
    toolVersion = "1.18.1"
    config = files("config/detekt/detekt.yml")
    buildUponDefaultConfig = true
    failFast = true

    input = files("app/src/main/java", "app/src/main/kotlin")

    reports {
        html {
            enabled = true
            destination = file("app/build/detekt/detekt.html")
        }
    }
}
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "1.8"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
