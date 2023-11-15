package dev.jvoyatz.modern.android.common.result.wrapper

/*
 * Created by John Voyatzis on 06/07/2021
 *                  Updated on 14/11/2023.
 *
 */

/**
 * Sealed interface with two possible states
 *  1) Success
 *  2) Error
 *
 *  Acts as a wrapper for data.
 *
 *  Can be used as well to wrap the result of given a block
 *  into a instance of this class.
 */
sealed interface ResultWrapper<out T> {
    data class Success<T>(val data: T) : ResultWrapper<T>

    sealed interface Failure : ResultWrapper<Nothing> {
        data class GeneralFailure(val error: String) : Failure
        data object NetworkFailure : Failure
        data object TimeOutFailure : Failure
        data class HttpFailure(val code: Int, val message: String) : Failure
        data class UnexpectedFailure(val message: String = "unknown exception") : Failure
    }


    companion object {
        /**
         * Creates a Success instance
         */
        fun <T> success(data: T): Success<T> = Success(data)

        /**
         * Creates an Error instance
         */
        fun failure(throwable: Throwable?): Failure =
            throwable?.parseException() ?: Failure.GeneralFailure(
                "unknown error"
            )
    }
}