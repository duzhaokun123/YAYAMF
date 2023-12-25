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
        mavenLocal {
            content {
                includeGroup("io.github.duzhaokun123")
            }
        }
        google()
        mavenCentral()
        maven("https://api.xposed.info")
    }
}

rootProject.name = "YAYAMF"
include(":app")
include(":android-stub")
