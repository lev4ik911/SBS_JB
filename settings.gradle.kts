/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

pluginManagement {
    repositories {
        jcenter()
        google()
        mavenCentral()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://jetbrains.bintray.com/kotlin-native-dependencies") }
        maven { url = uri("https://maven.fabric.io/public") }
        maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
    resolutionStrategy.eachPlugin {
        // part of plugins defined in Deps.Plugins, part in buildSrc/build.gradle.kts.kts
        if (requested.id.id == "com.squareup.sqldelight") {
            useModule("com.squareup.sqldelight:gradle-plugin:1.3.0")
        } else {
            val module = Deps.plugins[requested.id.id] ?: return@eachPlugin

            useModule(module)
        }
    }
}

enableFeaturePreview("GRADLE_METADATA")

include(":android-app", ":mpp-library", ":mvvmbase")

//listOf(
//    Modules.MultiPlatform.domain,
//    Modules.MultiPlatform.Feature.config,
//    Modules.MultiPlatform.Feature.list
//).forEach { include(it.name) }
