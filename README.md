# SBS Initiative project

![sbs-version](https://img.shields.io/badge/SBS-0.1.0-red)

This repository contains clients part of [SBS Initiative project](https://github.com/aleks-zhigar/SBS).

## Table of Contents

- [Dependencies](#dependencies)
- [Repository structure](#repository-structure)

- Base: ![kotlin-version](https://img.shields.io/badge/Kotlin-1.3.70-blue) ![jvm-version](https://img.shields.io/badge/JVM-1.8+11-orange) ![gradle+gradlew-version](https://img.shields.io/badge/Gradle+Wrapper-6.5-blue) 

## Dependencies

###mpp-library
  - [`Ktor`](https://ktor.io/) as framework;
  - [`moko-mvvm`](https://github.com/icerockdev/moko-mvvm);
  - [`moko-permissions`](https://github.com/icerockdev/moko-permissions);
  - [`SQLDelight`](https://github.com/cashapp/sqldelight/);

###android-app
  - [`Koin`](https://github.com/InsertKoinIO/koin/) as DI;
  - [`recycler-fast-scroll`](https://github.com/FutureMind/recycler-fast-scroll/);
  - [`shimmer-android`](https://github.com/facebook/shimmer-android)
  - [`Android loading animations`](https://github.com/ybq/Android-SpinKit)
  - [`A fast circular ImageView perfect for profile images`](https://github.com/hdodenhof/CircleImageView)
  - [`Image Cropping Library for Android`](https://github.com/Yalantis/uCrop)
  - [`Glide`](https://github.com/bumptech/glide)
  - [`Material Dialogs`](https://github.com/afollestad/material-dialogs)
  - [`FancyToast-Android`](https://github.com/Shashank02051997/FancyToast-Android)
  - [`MaterialSearchView`](https://github.com/MiguelCatalan/MaterialSearchView)
  - [`Stepper-Touch`](https://github.com/DanielMartinus/Stepper-Touch)

###ios-app

## Repository structure

`mpp-library` - common part code; based on:

`mvvmbase` - common code for the Android app;

`android-app` - client for Android

`ios-app` - client for iOS

