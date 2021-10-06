pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.android")) {
                useModule("com.android.tools.build:gradle:7.0.2")
            }
            if (requested.id.id.startsWith("org.jetbrains.kotlin")) {
                useVersion("1.5.21")
            }
//            if (requested.id.id.startsWith("dagger.hilt.android")) {
//                useModule("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
//            }
        }
    }
}

rootProject.name = "RSShcool2021-Android-task-5-Cats"
include (":app")