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
        maven("https://www.jitpack.io")
        maven("https://developer.huawei.com/repo/")
        maven("https://developer.hihonor.com/repo/")
    }
}

rootProject.name = "LinkYou"
include(":app")
include(":core")
include(":opensource")
include(":network")
include(":database")
