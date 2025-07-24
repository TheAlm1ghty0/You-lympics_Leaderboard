// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) version "8.10.0" apply false
//    id("com.android.application") version "8.10.0" apply false // Change this version
    id("com.android.library") version "8.11.1" apply false    // Or this if it's a library project
    alias(libs.plugins.google.gms.google.services) apply false
}