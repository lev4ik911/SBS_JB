
object Versions {
    object Android {
        const val compileSdk = 28
        const val targetSdk = 28
        const val minSdk = 21
    }

    const val kotlin = "1.3.70"

    private const val mokoResources = "0.9.0"
    private const val mokoNetwork = "0.6.0"
    private const val mokoUnits = "0.3.1" // temporary dev version
    private const val SQLDelight = "1.3.0"

    object Plugins {
        const val kotlin = Versions.kotlin
        const val serialization = Versions.kotlin
        const val androidExt = Versions.kotlin
        const val mokoResources = Versions.mokoResources
        const val mokoNetwork = Versions.mokoNetwork
        const val mokoUnits = Versions.mokoUnits
        const val SQLDelight = Versions.SQLDelight

    }

    object Libs {
        object Android {
            const val kotlinStdLib = Versions.kotlin
            const val appCompat = "1.1.0"
            const val material = "1.1.0"
            const val constraintLayout = "1.1.3"
            const val lifecycle = "2.0.0"
            const val recyclerView = "1.0.0"
        }

        object MultiPlatform {
            const val kotlinStdLib = Versions.kotlin
            const val SQLDelight = Versions.SQLDelight
            const val coroutines = "1.3.5"
            const val serialization = "0.20.0"
            const val ktorClient = "1.3.2"
            const val ktorClientLogging = ktorClient

            const val mokoParcelize = "0.3.0"
            const val mokoTime = "0.3.0"
            const val mokoGraphics = "0.3.0"
            const val mokoMvvm = "0.6.0"
            const val mokoResources = Versions.mokoResources
            const val mokoNetwork = Versions.mokoNetwork
            const val mokoFields = "0.3.0"
            const val mokoPermissions = "0.5.0"
            const val mokoMedia = "0.4.0"
            const val mokoUnits = Versions.mokoUnits

            const val napier = "1.3.0"
            const val settings = "0.5.1"
        }
    }
}