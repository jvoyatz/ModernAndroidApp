package dev.jvoyatz.modern.android

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.getByType


private const val LIBS = "libs"
private const val PACKAGE_NAME = "packageName"
private const val VERSION_NAME = "versionName"
private const val VERSION_CODE = "versionCode"
private const val TARGET_SDK = "targetSdk"
private const val MIN_SDK = "minSdk"
private const val COMPILE_SDK = "compileSdk"
private const val TEST_RUNNER = "androidTestInstrumentation"

val Project.libs
    get() = extensions.getByType<VersionCatalogsExtension>().named(LIBS)

val Project.COMPILE_SDK
    get() = libs.findVersion(dev.jvoyatz.modern.android.COMPILE_SDK).get().toString().toInt()

val Project.TARGETSDK
    get() = libs.findVersion(TARGET_SDK).get().toString().toInt()

val Project.MINSDK
    get() = libs.findVersion(MIN_SDK).get().toString().toInt()

val Project.PKG_NAME
    get() = libs.findVersion(PACKAGE_NAME).get().toString()