plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pharmacymanager"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.pharmacymanager"
        minSdk = 24
        targetSdk = 36
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
    buildFeatures{
        viewBinding = true
    }
    dataBinding{
        enable = true
    }
}

dependencies {
    // AndroidX UI & Support
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Volley (Network / API)
    implementation(libs.volley)

    // Lifecycle Components (ViewModel & LiveData)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)

    // Gson (For parsing JSON)
    implementation(libs.gson)
    implementation(libs.fragment)

    // Navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Material Design Components New Version
    implementation(libs.material.v1110)
    
    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Unit Testing
    testImplementation(libs.junit)

    // Android Instrumentation Tests
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}