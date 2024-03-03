@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.convention.plugin.android.library)
}

android {
    namespace = "${libs.versions.packageName.get()}.network"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    packaging {
        resources.excludes.add("META-INF/*")
    }

}

dependencies {
    implementation(libs.bundles.networking)
    implementation(libs.guava)
    implementation(libs.bundles.test)
}