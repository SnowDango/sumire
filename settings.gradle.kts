pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "Sumire"
include(":app")
include(":ui")
include(":infla")
include(":data")
include(":presenter:playing")
include(":presenter:history")
include(":presenter:settings")
include(":repository")
include(":usecase")
include(":model")
include(":history")
