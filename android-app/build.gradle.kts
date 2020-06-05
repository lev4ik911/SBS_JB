plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("kotlinx-serialization")
    id("dev.icerock.mobile.multiplatform-units")
}

android {
    compileSdkVersion(28)

    dataBinding {
        isEnabled = true
    }

    dexOptions {
        javaMaxHeapSize = "2g"
    }

    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
        applicationId = "by.iba.sbs"
        versionCode = 1
        versionName = "0.1.0"

        vectorDrawables.useSupportLibrary = true

        val url = "https://newsapi.org/v2/"
        buildConfigField("String", "BASE_URL", "\"$url\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=org.mylibrary.OptInAnnotation"
    }

    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }
    buildToolsVersion = "30.0.0 rc4"
}

dependencies {
    implementation(Deps.Libs.Android.kotlinStdLib.name)

    implementation(Deps.Libs.Android.appCompat.name)
    implementation(Deps.Libs.Android.material.name)
    implementation(Deps.Libs.Android.constraintLayout.name)
    implementation(Deps.Libs.Android.recyclerView.name)

    implementation(Deps.Libs.MultiPlatform.napier.android!!)

    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta6")
    implementation("androidx.navigation:navigation-fragment:2.2.2")
    implementation("androidx.navigation:navigation-ui:2.2.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.2.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime")
    // Koin AndroidX Scope features
    implementation("org.koin:koin-androidx-scope:2.1.5")
// Koin AndroidX ViewModel features
    implementation("org.koin:koin-androidx-viewmodel:2.1.5")
// Koin AndroidX Experimental features
    implementation("org.koin:koin-androidx-ext:2.1.5")
    implementation("org.koin:koin-androidx-fragment:2.1.5")
    implementation("com.futuremind.recyclerfastscroll:fastscroll:0.2.5")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.github.ybq:Android-SpinKit:1.4.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.0.0")
    implementation("com.github.yalantis:ucrop:2.2.4")
    implementation("com.github.bumptech.glide:glide:4.11.0")
    implementation("com.afollestad.material-dialogs:core:3.3.0")
    implementation("com.afollestad.material-dialogs:input:3.3.0")
    implementation(project(":mpp-library"))
    implementation(project(":mvvmbase"))
}

multiplatformUnits {
    classesPackage = "by.iba.sbs"
    dataBindingPackage = "by.iba.sbs"
    layoutsSourceSet = "main"
}
