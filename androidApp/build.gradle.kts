plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.amontdevs.saturnwallpapers.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.amontdevs.saturnwallpapers"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.android.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildToolsVersion = "31.0.0"
}

dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.lifecycle.viewmodel)
    //implementation(libs.androidx.palette.ktx)
    implementation(libs.coil.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.date.time)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.lottie.compose)
    implementation(libs.navigation.compose)
    implementation(libs.work)


    debugImplementation(libs.compose.ui.tooling)

    //implementation(libs.saturn.sdk.android)
}