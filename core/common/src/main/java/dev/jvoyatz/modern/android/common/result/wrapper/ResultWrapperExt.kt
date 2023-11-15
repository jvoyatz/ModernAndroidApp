@file:Suppress("unused")

package dev.jvoyatz.modern.android.common.result.wrapper

/*
 * Created by John Voyatzis on 06/07/2021
 *                  Updated on 14/11/2023.
 *
 */

/**
 * Returns true if this instance is a type of [ResultWrapper.Success]
 */
val <T> ResultWrapper<T>.isSuccess: Boolean
    get() = this is ResultWrapper.Success


/**
 * Returns true if this instance is a type of [ResultWrapper.Failure]
 */
val <T> ResultWrapper<T>.isFailure: Boolean
    get() = this is ResultWrapper.Failure

/**
 * If this instance is a type of [ResultWrapper.Success] then it casts it
 * to this type, otherwise it returns null.
 */
fun <T> ResultWrapper<T>.asSuccess(): ResultWrapper.Success<T>? {
    return if (this is ResultWrapper.Success) {
        return this
    } else {
        null
    }
}

/**
 * If this instance is a type of [ResultWrapper.Failure] then it casts it
 * to this type, otherwise it returns null.
 */
fun <T> ResultWrapper<T>.asError(): ResultWrapper.Failure? {
    return if (this is ResultWrapper.Failure) {
        return this
    } else {
        null
    }
}

/**
 * Executes the given block in case of a [ResultWrapper.Success] instance, otherwise it does nothing
 */
inline fun <T> ResultWrapper<T>.onSuccess(crossinline action: (value: T) -> Unit): ResultWrapper<T> {
    if (isSuccess) action(asSuccess()!!.data)
    return this
}

/**
 * Executes the given block in case of a [ResultWrapper.Failure] instance, otherwise it does nothing
 */
inline fun <T> ResultWrapper<T>.onError(crossinline action: (value: ResultWrapper.Failure) -> Unit): ResultWrapper<T> {
    if (isFailure) asError()?.let {
        action(it)
    }
    return this
}

/**
 * Executes the given block in any case of a [ResultWrapper] instance
 */
inline fun <T> ResultWrapper<T>.onAny(crossinline action: () -> Unit): ResultWrapper<T> {
    action()
    return this
}

inline fun <T, R> ResultWrapper<T>.mapSuccess(mapper: T.() -> R): ResultWrapper<R> {
    return when (this) {
        is ResultWrapper.Success -> data.mapper().toResultDataSuccess()
        is ResultWrapper.Failure -> this
    }
}

/**
 * When ResultWrapper is Error, we map it to a new error using the given block
 *
 * @param failure transforms an existing error to a new error
 */
inline fun <T> ResultWrapper<T>.mapError(crossinline failure: ResultWrapper.Failure.() -> ResultWrapper.Failure): ResultWrapper<T> =
    when (this) {
        is ResultWrapper.Success -> this
        is ResultWrapper.Failure -> failure()
    }

/**
 * Wrap any non nullable type to a [ResultWrapper.Success] instance
 */
fun <T> T.toResultDataSuccess(): ResultWrapper<T> = ResultWrapper.success(this)

fun <T> ResultWrapper<T>.getOrNull(): T? = when (this) {
    is ResultWrapper.Success -> data
    else -> null
}


inline fun <R, T> ResultWrapper<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (ResultWrapper.Failure) -> R
): R = when (this) {
    is ResultWrapper.Success -> onSuccess(data)
    is ResultWrapper.Failure -> onFailure(this)
}
