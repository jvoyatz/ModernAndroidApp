package dev.jvoyatz.modern.android.network.utils

import com.google.common.reflect.TypeToken
import java.lang.reflect.Type

/**
 * https://stackoverflow.com/questions/30005110/how-does-gson-typetoken-work
 * https://helw.net/2017/11/09/runtime-generics-in-an-erasure-world/
 *
 * what you should already know ?
 *      generics in java are a compile time concept to help enforce type safety
 *
 * and what happens next?
 *      during compilation, type erasure kicks in, resulting in the underlying bytecode being free of any generics information
 *
 * however, how can I get information of this generic type at runtime?
 *      by using TypeToken
 */
inline fun <reified T> typeOf(): Type = object : TypeToken<T>() {}.type

/**
 * Opens and returns the content found in the given file
 *
 * Always provide a `/` at the beginning of the fileName arg
 */
internal fun Any.getResFileContent(filename: String): String {
    return this::class.java.getResourceAsStream(filename)!!.bufferedReader().use { br ->
        br.readText()
    }
}

inline fun <reified T> Any.isTypeOf(): Boolean {
    return T::class.java.isAssignableFrom(this.javaClass)
}