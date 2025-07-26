plugins {
    alias(libs.plugins.android.application) version "8.10.0"
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.Kohnqueror.you_lympics_leaderboard"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.Kohnqueror.you_lympics_leaderboard"
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
            applicationVariants.all {
                val variant = this
                variant.outputs.all {
                    val output = this
                    val newApkName = "Leaderboard.apk"
                    (output as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName = newApkName
                }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation (libs.gson)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
}