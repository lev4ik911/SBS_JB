import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.gradle.build-scan") version("2.1")
    kotlin("jvm")
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://kotlin.bintray.com/kotlin") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://dl.bintray.com/icerockdev/moko") }
        maven { url = uri("https://kotlin.bintray.com/ktor") }
        maven { url = uri("https://dl.bintray.com/aakira/maven") }
        maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }
    }

    // workaround for https://youtrack.jetbrains.com/issue/KT-27170
//    configurations.create("compileClasspath")
}

//buildScan {
//    termsOfServiceUrl = "https://gradle.com/terms-of-service"
//    termsOfServiceAgree = "yes"
//}

//tasks.register("clean", Delete::class).configure {
//    delete(rootProject.buildDir)
//}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.squareup.sqldelight:gradle-plugin:1.3.0")
}
repositories {
    mavenCentral()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}