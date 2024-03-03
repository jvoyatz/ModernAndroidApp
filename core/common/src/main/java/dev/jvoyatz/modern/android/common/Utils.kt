package dev.jvoyatz.modern.android.common


/**
 * Accepts a list of type [I] and by applying the [mapper] on each item of this list,
 * it returns finally a new list with the mapped type
 *
 * @param I the type of the input [List]
 * @param O the type of the output [List]
 * @param input the [input] that will be mapped into another type [O]
 * @param mapper contains the code that will transform type [I] to [O]
 * @return a new list of type [O]
 */
inline fun <I, O> mapList(input: List<I>?, mapper: (I) -> O): List<O> {
    return input?.map { mapper(it) } ?: emptyList()
}

/**
 * Same as the [mapList] above,
 * though defined as a receiver function on type [List] instances of type [I]
 *
 * @receiver List<I> types can invoke this method to map their items to [O]
 */
inline fun <I, O> List<I>?.mapList(mapper: (I) -> O): List<O> = mapList(this, mapper)