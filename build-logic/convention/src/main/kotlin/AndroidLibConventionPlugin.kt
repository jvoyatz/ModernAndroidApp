import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import dev.jvoyatz.modern.android.TARGETSDK
import dev.jvoyatz.modern.android.configureKotlinAndroid
import dev.jvoyatz.modern.android.disableUnnecessaryAndroidTests
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidLibConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target){
            pluginManager.run {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = TARGETSDK
            }


            extensions.configure<LibraryAndroidComponentsExtension> {
                disableUnnecessaryAndroidTests(target)
            }
        }
    }
}