package dev.jvoyatz.modern.android.network.moshi

import dev.jvoyatz.modern.android.network.config.model.ApiResponse
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import retrofit2.http.GET

internal interface BoredApi {
    @GET("/activity")
    suspend fun getRandomActivity(): ApiResponse<BoredActivity, BoredActivityError>

    @GET("/activity")
    suspend fun getRandomActivityInvalid(): ApiResponse<BoredActivityInvalid, BoredActivityErrorInvalid>

    @GET("/activity")
    fun getDeferredRandomActivity(): Deferred<ApiResponse<BoredActivity, BoredActivityError>>

    @GET("/activity")
    fun getDeferredRandomActivityInvalid(): Deferred<ApiResponse<BoredActivityInvalid, BoredActivityErrorInvalid>>

    @GET("/activity")
    suspend fun getTestUnit(): ApiResponse<Unit, BoredActivityError>

    @GET("/activity")
    fun getDeferredTestUnit(): Deferred<ApiResponse<Unit, BoredActivityErrorInvalid>>
}

internal class BoredApiDataSource(
    private val api: BoredApi
) {
    fun getRandomActivity() = runBlocking {
        api.getRandomActivity()
    }

    fun getRandomActivityInvalid() = runBlocking {
        api.getRandomActivityInvalid()
    }

    fun getDeferredRandomActivity() = runBlocking {
        api.getDeferredRandomActivity().await()
    }

    fun getDeferredRandomActivityInvalid() = runBlocking {
        api.getDeferredRandomActivityInvalid().await()
    }

    fun getTestUnit() = runBlocking {
        api.getTestUnit()
    }

    fun getDeferredTestUnit() = runBlocking {
        api.getDeferredTestUnit().await()
    }
}
