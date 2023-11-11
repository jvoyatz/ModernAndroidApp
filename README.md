# Modern Android App


## Content
- [test](#Resources)

**Intro**
TBA


## Resources

* Gradle
  * https://www.jrebel.com/blog/using-buildsrc-custom-logic-gradle-builds
  * https://quickbirdstudios.com/blog/gradle-kotlin-buildsrc-plugin-android/
  * https://proandroiddev.com/gradle-dependency-management-with-kotlin-94eed4df9a28
  * https://handstandsam.com/2018/02/11/kotlin-buildsrc-for-better-gradle-dependency-management/
  * https://proandroiddev.com/better-dependencies-management-using-buildsrc-kotlin-dsl-eda31cdb81bf
  * https://docs.gradle.org/current/userguide/composite_builds.html

## Notes



Why would you want to make binary plugins out of convention plugins
First, let’s answer this: what is a binary plugin?

A Gradle plugin that is resolved as a dependency rather than compiled from source is a binary plugin. Binary plugins are cool because the next best thing after a cached compilation task is one that doesn’t exist in the first place.

For most use cases, convention plugins will need to be updated very infrequently. This means that having each developer execute the plugin build as part of their development process is needlessly wasteful, and we can instead just distribute them as maven dependencies.

This also makes it significantly easier to share convention plugins between projects without resorting to historically painful solutions like Git submodules or just straight up copy-pasting.


