
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("com.squareup.sqldelight")
    id("kotlin-android-extensions") //version "1.3.70"
    //  id("kotlinx-serialization") //version "1.3.70"
    kotlin("plugin.serialization") version "1.3.70"
    id("dev.icerock.mobile.multiplatform")
    id("dev.icerock.mobile.multiplatform-resources")
}
androidExtensions {
    isExperimental = true
}
android {
    compileSdkVersion(28)

    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isDebuggable = true
            //   applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildToolsVersion = "30.0.0 rc4"
}
sqldelight {
    database("SBSDB") {
        packageName = "by.iba.sbs"
        //  name = "sbsdb"
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
    Deps.Libs.MultiPlatform.ktorClientLogging,
    Deps.Libs.MultiPlatform.ktorClientJson,
    Deps.Libs.MultiPlatform.ktorClientSerialization
)
//val mppModules = listOf(
//    Modules.MultiPlatform.domain,
//    Modules.MultiPlatform.Feature.config,
//    Modules.MultiPlatform.Feature.list
//)

setupFramework(
    exports = mppLibs + ktorLibs//+ mppModules
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
apply(from = "https://raw.githubusercontent.com/JakeWharton/SdkSearch/master/gradle/projectDependencyGraph.gradle")