// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlin_version = "1.5.31"

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version-RC")
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt:1.18.1")
    id("org.jlleitschuh.gradle.ktlint:9.2.1")
}

subprojects {
    plugins {
        id("org.jlleitschuh.gradle.ktlint") apply false
    }
    ktlint {
        debug.set(false)
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
tasks.detekt.jvmTarget = "1.8"

task.register("clean", Delete: class) {
    delete(rootProject.buildDir)
}
