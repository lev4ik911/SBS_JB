
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
  //  id("com.squareup.sqldelight")
    id("kotlin-android-extensions")
    id("kotlinx-serialization")
    id("dev.icerock.mobile.multiplatform")
    id("dev.icerock.mobile.multiplatform-resources")
}

android {
    compileSdkVersion(Versions.Android.compileSdk)

    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
    }
}

val mppLibs = listOf(
    Deps.Libs.MultiPlatform.settings,
    Deps.Libs.MultiPlatform.napier,
    Deps.Libs.MultiPlatform.SQLDelight,
    Deps.Libs.MultiPlatform.SQLDelightDriver,
    Deps.Libs.MultiPlatform.mokoParcelize,
    Deps.Libs.MultiPlatform.mokoNetwork,
    Deps.Libs.MultiPlatform.mokoResources,
    Deps.Libs.MultiPlatform.mokoMvvm,
    Deps.Libs.MultiPlatform.mokoUnits
)
val ktorLibs = listOf(
    Deps.Libs.MultiPlatform.ktorClient,
    Deps.Libs.MultiPlatform.ktorClientLogging
)
//val mppModules = listOf(
//    Modules.MultiPlatform.domain,
//    Modules.MultiPlatform.Feature.config,
//    Modules.MultiPlatform.Feature.list
//)

setupFramework(
    exports = mppLibs //+ mppModules
)

dependencies {
    mppLibrary(Deps.Libs.MultiPlatform.kotlinStdLib)
    mppLibrary(Deps.Libs.MultiPlatform.coroutines)
    mppLibrary(Deps.Libs.MultiPlatform.serialization)
    androidLibrary(Deps.Libs.Android.lifecycle)
    ktorLibs.forEach { mppLibrary(it) }
    mppLibs.forEach { mppLibrary(it) }
    // mppModules.forEach { mppModule(it) }
}

multiplatformResources {
    multiplatformResourcesPackage = "by.iba.sbs.library"
}

// dependencies graph generator
//apply(from = "https://raw.githubusercontent.com/JakeWharton/SdkSearch/master/gradle/projectDependencyGraph.gradle")