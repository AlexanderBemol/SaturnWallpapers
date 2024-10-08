plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.kmp.compiler)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.date.time)
            implementation(libs.koin.core)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.negotation)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.noarg)
            implementation(libs.room.runtime)
            implementation(libs.serialization)
            implementation(libs.sqlite.bundled)
        }
        commonTest.dependencies {
            implementation(project(":shared"))
            implementation(libs.junit)
            implementation(libs.koin.testing)
        }
    }

    dependencies {
        ksp(libs.room.compiler)
    }
    task("testClasses")
}

android {
    namespace = "com.amontdevs.saturnwallpapers"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
dependencies {
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.contentpager)
}

room {
    schemaDirectory("$projectDir/schemas")
}