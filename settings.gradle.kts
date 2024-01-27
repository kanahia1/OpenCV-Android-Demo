pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OpenCV Demo"
include(":app")
include(":opencv")
project(":opencv").projectDir = File("C:\\Users\\Kanahia OG\\Downloads\\opencv-4.9.0-android-sdk\\OpenCV-android-sdk\\sdk")
 