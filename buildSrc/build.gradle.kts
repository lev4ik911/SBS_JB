import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.3.71"
}

repositories {

    jcenter()
    google()
    maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("dev.icerock:mobile-multiplatform:0.4.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.71")
    implementation("com.android.tools.build:gradle:3.5.3")
    implementation(kotlin("stdlib-jdk8"))
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}