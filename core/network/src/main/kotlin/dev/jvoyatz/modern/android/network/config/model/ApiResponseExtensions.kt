package dev.jvoyatz.modern.android.network.config.model

import dev.jvoyatz.modern.android.network.config.model.ApiResponse.ApiError
import dev.jvoyatz.modern.android.network.config.model.ApiResponse.ApiSuccess
import dev.jvoyatz.modern.android.network.config.model.ApiResponse.HttpError
import dev.jvoyatz.modern.android.network.config.model.ApiResponse.NetworkError
import dev.jvoyatz.modern.android.network.config.model.ApiResponse.UnexpectedError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Extension functions on [dev.jvoyatz.modern.android.network.config.model.ApiResponse] types
 */

/**
 * Checks if the receiver object is an instance of [ApiSuccess]
 *
 * @return boolean, true or false
 */
fun <S, E> ApiResponse<S, E>.isSuccess() = this is ApiSuccess

/**
 * Checks if the receiver object is an instance of [ApiSuccess] and its body is of [Unit] type
 *
 * @return boolean, true or false
 */
fun <S, E> ApiResponse<S, E>.isSuccessEmpty() = (this is ApiSuccess) && this.body === Unit

/**
 * Checks if the receiver object is an instance of [ApiError]
 *
 * @return boolean, true or false
 */
fun <S, E> ApiResponse<S, E>.isError() = this is ApiError

/**
 * Casts the receiver object to [ApiSuccess] in case this
 * is feasible, meaning that the receiver is an instance of [ApiSuccess]
 * otherwise it returns null
 */
fun <S, E> ApiResponse<S, E>.asSuccess(): ApiSuccess<S, E>? {
    if (this is ApiSuccess) {
        return this
    }
    return null
}

/**
 * Casts the receiver object to [ApiError] in case this
 * is feasible, meaning that the receiver is an instance of [ApiError]
 * otherwise it returns null
 */
fun <S, E> ApiResponse<S, E>.asError(): ApiError<S, E>? {
    if (this is ApiError) {
        return this
    }
    return null
}

/**
 * Casts the receiver object to [HttpError] in case this
 * is feasible, meaning that the receiver is an instance of [HttpError]
 * otherwise it returns null
 */
fun <S, E> ApiResponse<S, E>.asHttpError(): HttpError<S, E>? {
    if (this is HttpError) {
        return this
    }
    return null
}

/**
 * Casts the receiver object to [NetworkError] in case this
 * is feasible, meaning that the receiver is an instance of [NetworkError]
 * otherwise it returns null
 */
fun <S, E> ApiResponse<S, E>.asNetworkError(): NetworkError<S, E>? {
    if (this is NetworkError) {
        return this
    }
    return null
}

/**
 * Casts the receiver object to [UnexpectedError] in case this
 * is feasible, meaning that the receiver is an instance of [UnexpectedError]
 * otherwise it returns null
 */
fun <S, E> ApiResponse<S, E>.asUnexpectedError(): UnexpectedError<S, E>? {
    if (this is UnexpectedError) {
        return this
    }
    return null
}

/**
 * Applies the given function, in case the receiver object
 * is an instance of [ApiSuccess]
 *
 * @param onExecute, a lambda block tha executes a certain action
 */
inline fun <S, E> ApiResponse<S, E>.onSuccess(
    crossinline onExecute: S.() -> Unit
): ApiResponse<S, E> = apply {
    if (this is ApiSuccess) {
        onExecute(this.body)
    }
}


/**
 * Applies the given function, in case the receiver object
 * is an instance of [ApiSuccess], however it firstly maps
 * the type [S] to [V]
 *
 * @param mapper, a lambda tha takes as input the type [S] and  transforms it into [V]
 * @param onExecute, a lambda block tha executes a certain action
 */
inline fun <S, E, V> ApiResponse<S, E>.onSuccess(
    mapper: S.() -> V,
    crossinline onExecute: V.() -> Unit
): ApiResponse<S, E> = apply {
    if (this is ApiSuccess) {
        onExecute(body.mapper())
    }
}

/**
 * A suspending function that applies the given function, in case the receiver object
 * is an instance of [ApiSuccess]
 *
 * @param onExecute, a lambda block tha executes a certain action
 */
suspend inline fun <S, E> ApiResponse<S, E>.onSuspendedSuccess(
    crossinline onExecute: suspend S.() -> Unit
): ApiResponse<S, E> = apply {
    if (this is ApiSuccess) {
        onExecute(this.body)
    }
}

/**
 * A suspending function Applies the given function, in case the receiver object
 * is an instance of [ApiSuccess], however it firstly maps
 * the type [S] to [V]
 *
 * @param mapper, a lambda tha takes as input the type [S] and  transforms it into [V]
 * @param onExecute, a lambda block tha executes a certain action
 */
suspend inline fun <S, E, V> ApiResponse<S, E>.onSuspendedSuccess(
    mapper: (S) -> V,
    crossinline onExecute: suspend V.() -> Unit
): ApiResponse<S, E> = apply {
    if (this is ApiSuccess) {
        onExecute(mapper(this.body))
    }
}

/**
 * Applies the given function, in case the receiver object
 * is an instance of [ApiError]
 *
 * @param onExecute, a lambda block tha executes a certain action
 */
inline fun <S, E> ApiResponse<S, E>.onError(
    crossinline onExecute: ApiError<S, E>.() -> Unit
): ApiResponse<S, E> = apply {
    if (this is ApiError) {
        onExecute(this)
    }
}

/**
 * A suspending function that applies the given function, in case the receiver object
 * is an instance of [ApiError]
 *
 * @param onExecute, a lambda block tha executes a certain action
 */
suspend inline fun <S, E> ApiResponse<S, E>.onSuspendedError(
    crossinline onExecute: suspend ApiError<S, E>.() -> Unit
): ApiResponse<S, E> = apply {
    if (this is ApiError) {
        onExecute(this)
    }
}

/**
 * Applies the given function, in case the receiver object
 * is an instance of [HttpError]
 *
 * @param onExecute, a lambda block tha executes a certain action
 */
inline fun <S, E> ApiResponse<S, E>.onHttpError(
    crossinline onExecute: HttpError<S, E>.() -> Unit
): ApiResponse<S, E> = apply {
    if (this is HttpError) {
        onExecute(this)
    }
}

/**
 * A suspending function that applies the given function, in case the receiver object
 * is an instance of [HttpError]
 *
 * @param onExecute, a lambda block tha executes a certain action
 */
suspend inline fun <S, E> ApiResponse<S, E>.onSuspendedHttpError(
    crossinline onExecute: suspend HttpError<S, E>.() -> Unit
): ApiResponse<S, E> = apply {
    if (this is HttpError) {
        onExecute(this)
    }
}

/**
 * Applies the given function, in case the receiver object
 * is an instance of [NetworkError]
 *
 * @param onExecute, a lambda block tha executes a certain action
 */
inline fun <S, E> ApiResponse<S, E>.onNetworkError(
    crossinline onExecute: NetworkError<S, E>.() -> Unit
): ApiResponse<S, E> = apply {
    if (this is NetworkError) {
        onExecute(this)
    }
}

/**
 * A suspending function that applies the given function, in case the receiver object
 * is an instance of [NetworkError]
 *
 * @param onExecute, a lambda block tha executes a certain action
 */
suspend inline fun <S, E> ApiResponse<S, E>.onSuspendedNetworkError(
    crossinline onExecute: suspend NetworkError<S, E>.() -> Unit
): ApiResponse<S, E> = apply {
    if (this is NetworkError) {
        onExecute(this)
    }
}


/**
 * Applies the given function, in case the receiver object
 * is an instance of [NetworkError]
 *
 * @param onExecute, a lambda block tha executes a certain action
 */
inline fun <S, E> ApiResponse<S, E>.onUnknownError(
    crossinline onExecute: UnexpectedError<S, E>.() -> Unit
): ApiResponse<S, E> = apply {
    if (this is UnexpectedError) {
        onExecute(this)
    }
}

/**
 * A suspending function that applies the given function, in case the receiver object
 * is an instance of [UnexpectedError]
 *
 * @param onExecute, a lambda block tha executes a certain action
 */
suspend inline fun <S, E> ApiResponse<S, E>.onSuspendedUnknownError(
    crossinline onExecute: suspend UnexpectedError<S, E>.() -> Unit
): ApiResponse<S, E> = apply {
    if (this is UnexpectedError) {
        onExecute(this)
    }
}

/**
 * Wraps the current receiver [ApiResponse] object into a cold
 * flow of type [S], in case it is an instance of [ApiSuccess]
 */
fun <S, E> ApiResponse<S, E>.toFlow(): Flow<S> {
    return if (this is ApiSuccess) {
        flowOf(this.body)
    } else {
        emptyFlow()
    }
}

/**
 * Wraps the current receiver [ApiResponse] object into a cold
 * flow of type [S], in case it is an instance of [ApiSuccess]
 */
inline fun <S, E, R> ApiResponse<S, E>.toFlow(
    crossinline mapper: S.() -> R
): Flow<R> {
    return if (this is ApiSuccess) {
        flowOf(this.body.mapper())
    } else {
        emptyFlow()
    }
}

/**
 * Wraps the current receiver [ApiResponse] object into a cold
 * flow of type [S], in case it is an instance of [ApiSuccess]
 */
suspend inline fun <S, E, R> ApiResponse<S, E>.toSuspendFlow(
    crossinline mapper: suspend S.() -> R
): Flow<R> {
    return if (this is ApiSuccess) {
        flowOf(this.body.mapper())
    } else {
        emptyFlow()
    }
}

/**
 * Checks if the receiver object is an instance of [ApiSuccess] and returns its body
 * otherwise it returns null
 */
fun <S, E> ApiResponse<S, E>.getOrNull(): S? {
    return when (this) {
        is ApiSuccess -> this.body
        else -> null
    }
}

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

/**
 * Checks what type is the current instance of ApiResponse
 * and in case it is an instance of ApiError, then returns
 * a string describing the error
 */
fun <S, E> ApiResponse<S, E>.extractError(): String? {
    return if (this is ApiSuccess) return null
    else {
        asError()?.error() ?: ERROR_UNKNOWN
    }
}