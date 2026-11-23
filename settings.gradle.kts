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

rootProject.name = "RusDiliPro"
include(
    ":app",
    ":core:common",
    ":core:ui",
    ":core:database",
    ":core:network",
    ":feature:home",
    ":feature:book",
    ":feature:quiz",
    ":feature:dictionary",
    ":feature:news"
)
