// De dónde se descargan los plugins de Gradle (Android, Kotlin, Compose).
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// De dónde se descargan las librerías de la app (AndroidX, Compose, y luego Firebase).
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LoteriaPC01"
include(":app")
