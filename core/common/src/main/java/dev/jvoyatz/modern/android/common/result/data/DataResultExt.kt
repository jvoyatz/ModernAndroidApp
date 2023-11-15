@file:Suppress("unused")

package dev.jvoyatz.modern.android.common.result.data

/*
 * Created by John Voyatzis on 10/09/2020
 *                  Updated on 14/11/2023.
 *
 */

//Extension functions for DataResult

/**
 * Executes the action block in case the current instance is Success
 *
 * @param action is a lambda that executes a certain job
 */
inline fun <T> DataResult<T>.onSuccess(crossinline action: (T?) -> Unit): DataResult<T> =
    when (this) {
        is DataResult.Success -> apply { action(data) }
        is DataResult.Error -> this
    }


/**
 * Executes the action block in case the current instance is Error
 *
 * * @param action is a lambda that executes a certain job
 */
inline fun <T> DataResult<T>.onError(crossinline action: (DataResult.Error) -> Unit): DataResult<T> =
    when (this) {
        is DataResult.Error -> apply { action(this) }
        is DataResult.Success -> this
    }

/**
 * Executes the given action without checking the current type
 *
 * @param action is a lambda that executes a certain job
 */
inline fun <T> DataResult<T>.onAny(crossinline action: () -> Unit): DataResult<T> =
    apply { action() }

/**
 * Maps the data held in the Success type into a new type
 *
 * @param mapper is a lambda that executes a certain job
 */
inline fun <T, R> DataResult<T>.mapSuccess(crossinline mapper: T.() -> R) = when (this) {
    is DataResult.Success -> data?.mapper().toDataResultSuccess()
    is DataResult.Error -> this
}

/**
 * When DataResult is an Error, we map it to a new error using the given block
 *
 * @param error transforms an existing error to a new error
 */
inline fun <T> DataResult<T>.mapError(crossinline error: DataResult.Error.() -> DataResult.Error): DataResult<T> =
    when (this) {
        is DataResult.Success -> this
        is DataResult.Error -> error()
    }

/**
 * Wraps the given type into a DataResult object
 */
fun <T> T?.toDataResultSuccess() = DataResult.Success(this)