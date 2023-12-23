package dev.jvoyatz.modern.android.network.models

import dev.jvoyatz.modern.android.network.models.ApiResponse.*


/**
 * Overloading invoke operator to get the successful body otherwise null
 *
 * @param S the success body type
 * @param E the error body type
 *
 * Usage:
 *  val response = call.getSomething()
 *  response() ?: "null response"
 */
operator fun <S, E> ApiResponse<S, E>.invoke(): S? =
    if (this is ApiSuccess) body else null
