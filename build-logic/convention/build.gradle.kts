import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "${libs.versions.packageName}.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
//here we register the custom plugins that will be used in project's modules
    plugins {
        register("androidApplication") {
            id = "dev.jvoyatz.modern.android.application"
            implementationClass = "AndroidAppConventionPlugin"
        }

        register("androidLibrary") {
            id = "dev.jvoyatz.modern.android.library"
            implementationClass = "AndroidLibConventionPlugin"
        }

        register("jvmLibrary") {
            id = "dev.jvoyatz.modern.android.library.jvm"
            implementationClass = "KotlinJvmConventionPlugin"
        }
    }
}