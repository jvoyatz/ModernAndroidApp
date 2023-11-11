# Android Plugins

This directory `buildLog` fefines and offers Android plugins, which are used to 
keep single source of truth for common module configurations.

## Notes

#### buildSrc
* in case of multi module apps, we need to create & maintain multiple gradle files
    * this can be error prone
    * not the best option
    * how to handle dependencies and their versions?
* a directory, which contains build info/build logic
* we use it along with kotlin-dsl
    * the code of this directory
    * is compiled, tested and it is placed in the class path of our build script -- [src](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html#sec:build_sources)
* **drawabacks**
    *  invalidating the whole project for any change within buildSrc
    * solution composite builds
    * https://docs.gradle.org/current/userguide/composite_builds.html


#### Convention plugins

* Gradle convention plugin?
* tool for reusing build configuration
* set of conventions for project/modules and apply them wherever needed

```
  Convention plugins apply conventions to a build. They do this by applying ecosystem plugins such as AGP and KGP, 
  and then configuring those plugins according to the convention used by your build. A simple example may help illustrate 
  the change from the client (feature engineer) perspective. Consider this un-conventional build script:
  
  apply plugin: 'com.android.library'
  apply plugin: 'kotlin-android'

  android {
    // lots of boilerplate
  }

  tasks.withType(KotlinCompile).configureEach {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11
    }
  }
  
  A convention plugin allows us to define configurations, or conventions, for builds that we re-use across a project.

  A convention is represented by a Gradle script or a Plugin. 
  They will live in a build logic module that will register plugins with Gradle. The module is then applied via your pluginManagement .
```


* what do we gain by using them ?
    * common config between projects/modules
    * maintenance
    * readability
    * easier building

* Inside buildLogic thre is a convention module, which defines a set of plugins that are used by modules to configure themselves
* Additionally there some *Kotlin* files that contain logic for  configuring feature/app/lib modules



Sources
* [Now in Android](https://github.com/android/nowinandroid/blob/main/build-logic/README.md)
* [https://developer.squareup.com/blog/herding-elephants/](https://developer.squareup.com/blog/herding-elephants/)
* [https://github.com/jjohannes/idiomatic-gradle](https://github.com/jjohannes/idiomatic-gradle)
