@file:Suppress("unused")

package dev.jvoyatz.modern.android.common.result.data

/*
 * Created by John Voyatzis on 10/09/2020
 *                  Updated on 14/11/2023.
 *
 */

/**
 * Simple class that holds the result of an operation/task.
 *
 * It can have two states, see [Success] or [Error] and its sub-states.
 */
sealed interface DataResult<out T> {
    class Success<T>(val data: T?) : DataResult<T>

    sealed interface Error : DataResult<Nothing> {
        data class ApiError(val message: String? = null) : Error
        data class GeneralError(val message: String? = null) : Error
        data class DbError(val message: String? = null) : Error
    }
}