@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.convention.plugin.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

android {
    namespace = libs.versions.packageName.get()

    defaultConfig {
        applicationId = libs.versions.packageName.get()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
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
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
}