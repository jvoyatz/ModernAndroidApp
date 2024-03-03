package dev.jvoyatz.modern.android.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Defines a contract which gives the capability to access Kotlin language's [Dispatchers].
 * It's purpose is to make testing easier by not using the dispatchers explicitly throughout
 * the code written in this app.
 *
 * @see [Dispatchers.IO], [Dispatchers.Main], [Dispatchers.Default], [Dispatchers.Unconfined]
 */
interface AppDispatchers{
    val io: CoroutineDispatcher
        get() = Dispatchers.IO

    val main: CoroutineDispatcher
        get() = Dispatchers.Main

    val default: CoroutineDispatcher
        get() = Dispatchers.Default

    val unconfined: CoroutineDispatcher
        get() = Dispatchers.Unconfined
}

internal class AppDispatchersImpl @Inject constructor(): AppDispatchers