plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.o7solutions.meetnote"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.o7solutions.meetnote"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//    lottie
    implementation("com.airbnb.android:lottie:6.3.0")// Use latest version


//    Room database
    implementation("androidx.room:room-runtime:2.7.2")
    kapt("androidx.room:room-compiler:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")


//    View Pager
//    implementation 'com.google.android.material:material:1.x.x' // Use the latest stable version
    implementation("androidx.viewpager2:viewpager2:1.0.0") // Or the latest stable version
    implementation("com.google.android.material:material:1.12.0") // Or the latest stable version
    implementation("androidx.fragment:fragment-ktx:1.8.8") // For Kotlin extensions for Fragments
}