package dev.jvoyatz.modern.android.common.utils

/**
 * Mapper, a generic interface that provides two method to implement
 *  a method [mapFrom] which is a receiver method on [From] instances and
 *  another one [mapTo] which is a received method on [To] instances
 *
 * @param From the origin type, which contains the data we want to map
 * @param To the destination type or the type to which our data will mapped to
 */
interface Mapper<From, To> {
    /**
     * Maps the content of [From] to [To] instances
     *
     * @return a new instance of [To]
     */
    fun From.mapFrom(): To

    /**
     * Maps instances of type [To] to [From]
     *
     * @return
     */
    fun To.mapTo(): From
}

/**
 * Accepts a list of type [I] and by applying the [mapSingle] on each item of this list,
 * it returns finally a new list with the mapped type
 *
 * @param I the type of the input [List]
 * @param O the type of the output [List]
 * @param input the [input] that will be mapped into another type [O]
 * @param mapSingle contains the code that will transform type [I] to [O]
 * @return a new list of type [O]
 */
inline fun <I, O> mapList(input: List<I>?, mapSingle: (I) -> O): List<O> {
    return input?.map { mapSingle(it) } ?: emptyList()
}