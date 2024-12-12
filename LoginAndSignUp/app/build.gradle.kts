plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.loginandsignup"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.loginandsignup"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation ("androidx.core:core:1.13.1")
    implementation ("androidx.appcompat:appcompat:1.4.1")
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.google.firebase:firebase-auth:22.1.0")

    // CameraX Dependencies
    implementation("androidx.camera:camera-core:1.2.0-rc01")
    implementation("androidx.camera:camera-camera2:1.2.0-rc01")
    implementation("androidx.camera:camera-lifecycle:1.2.0-rc01")
    implementation("androidx.camera:camera-video:1.2.0-rc01")
    implementation("androidx.camera:camera-view:1.2.0-rc01")
    implementation("androidx.camera:camera-extensions:1.2.0-rc01")
    implementation(libs.androidx.core.splashscreen)


    // Retrofit for network requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Kotlin Coroutines for asynchronous programming
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    // Optional: OkHttp Logging Interceptor for debugging network requests
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")


    implementation ("com.github.zeeshan5422:Searchable-Spinner-Kotlin:v1.0")


    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation ("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.14.2")
    implementation(kotlin("script-runtime"))

}

