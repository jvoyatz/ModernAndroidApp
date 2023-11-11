import com.android.build.api.dsl.ApplicationExtension
import dev.jvoyatz.modern.android.TARGETSDK
import dev.jvoyatz.modern.android.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target){
            pluginManager.run {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = TARGETSDK
            }

        }
    }

}