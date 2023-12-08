plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id ("kotlin-kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.connectify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.connectify"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures{
        dataBinding = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}


dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.compose.foundation:foundation-desktop:1.6.0-beta01")
    implementation("com.google.android.gms:play-services-cast-framework:21.3.0")
    implementation("com.android.support:support-annotations:28.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    // Storage
    implementation ("com.google.firebase:firebase-storage-ktx")


    // Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    kapt ("com.github.bumptech.glide:compiler:4.16.0")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.11.0"))

    // define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation ("com.github.bumptech.glide:okhttp3-integration:4.7.1")

    //coroutines for firebase
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")

    implementation("com.firebaseui:firebase-ui-database:8.0.0")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.0")

    implementation("com.google.firebase:firebase-storage")

    implementation("com.google.android.material:material:1.10.0")

    implementation("androidx.camera:camera-core:1.4.0-alpha02")
    implementation("androidx.camera:camera-camera2:1.4.0-alpha02")
    implementation("androidx.camera:camera-lifecycle:1.4.0-alpha02")
    implementation("androidx.camera:camera-view:1.4.0-alpha02")

    implementation("androidx.activity:activity-ktx:1.8.1")
    implementation("com.github.dhaval2404:imagepicker:2.1")


}