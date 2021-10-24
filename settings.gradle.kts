dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {

        //TODO Определиться где указывать репозитории, settings, build или buildsrc/build (не используется, но есть в проекте)
        google()
        mavenCentral()
    }
}
//TODO Убрать build.gradle.work build.gradle.bak .editorconfig .idea
rootProject.name = "RSShcool2021-Android-task-5-Cats"
include(":app")
