@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.convention.plugin.kotlin.jvm)
}

dependencies {
    implementation(libs.junit)
    implementation(libs.coroutines)
    implementation(libs.retrofit)
    implementation(libs.javax.inject)
}