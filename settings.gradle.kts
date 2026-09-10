pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
    }
}

rootProject.name = "LifeForgeOS"

include(":common")
include(":androidApp")
include(":windowsApp")

// Feature modules (to be added as needed)
// include(":features:gym")
// include(":features:nutrition")
// include(":features:habits")
// include(":features:recovery")
// include(":features:books")
// include(":features:courses")
// include(":features:entertainment")
// include(":features:notes")
// include(":features:journal")
// include(":features:goals")
// include(":features:timers")
// include(":features:statistics")