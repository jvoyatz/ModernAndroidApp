@file:Suppress("unused")

package dev.jvoyatz.modern.android.common.result.wrapper

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException

/*
 * Created by John Voyatzis on 06/07/2021
 *                  Updated on 14/11/2023.
 *
 */

/**
 *
 * https://proandroiddev.com/resilient-use-cases-with-kotlin-result-coroutines-and-annotations-511df10e2e16
 *
 * Alternative for runCatching, catching throwable can be an issue
 * Read this as well https://stackoverflow.com/questions/2274102/difference-between-using-throwable-and-exception-in-a-try-catch/2274116#2274116
 *
 * By catching throwable cancellation exceptions cannot be caught
 *
 *
 * Handling cancellation
 * See https://github.com/Kotlin/kotlinx.coroutines/issues/1814.
 */
inline fun <R> resultWrapperOf(crossinline block: () -> R) =
    try {
        ResultWrapper.success(block())
    } catch (t: TimeoutCancellationException) {
        ResultWrapper.failure(t)
    } catch (c: CancellationException) {
        throw c
    } catch (e: Exception) {
        ResultWrapper.failure(e)
    }


/**
 *
 * https://proandroiddev.com/resilient-use-cases-with-kotlin-result-coroutines-and-annotations-511df10e2e16
 *
 * Acts on given type T
 *
 * Handling cancellation
 * See https://github.com/Kotlin/kotlinx.coroutines/issues/1814.
 */
fun <T, R> T.resultWrapperOf(block: T.() -> R) =
    try {
        ResultWrapper.success(block())
    } catch (t: TimeoutCancellationException) {
        ResultWrapper.failure(t)
    } catch (c: CancellationException) {
        throw c
    } catch (e: Exception) {
        ResultWrapper.failure(e)
    }

fun <T> Flow<T>.asResultWrapper(): Flow<ResultWrapper<T>> =
    this.map {
        resultWrapperOf { it }
    }


fun Throwable.parseException(): ResultWrapper.Failure =
    when (this) {
        is SocketTimeoutException -> ResultWrapper.Failure.TimeOutFailure
        is HttpException -> {
            ResultWrapper.Failure.HttpFailure(this.code(), this.message())
        }

        is IOException -> ResultWrapper.Failure.NetworkFailure
        else -> ResultWrapper.Failure.UnexpectedFailure(
            message = message ?: "Exception not handled"
        )
    }

/**
 * [resultWrapperOf]
 */
inline fun <R, T> ResultWrapper<T>.mapResult(crossinline transform: (value: T) -> R): ResultWrapper<R> {
    return when (this) {
        is ResultWrapper.Success -> resultWrapperOf { transform(data) }
        is ResultWrapper.Failure -> this
    }
}


