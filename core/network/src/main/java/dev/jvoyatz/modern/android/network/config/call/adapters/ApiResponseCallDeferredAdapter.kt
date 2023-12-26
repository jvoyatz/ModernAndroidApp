package dev.jvoyatz.modern.android.network.config.call.adapters

import dev.jvoyatz.modern.android.network.config.utils.asApiResponse
import dev.jvoyatz.modern.android.network.config.model.ApiResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.lang.reflect.Type

/**
 * A [retrofit2.CallAdapter] used to adapt our response to [ApiResponseCall]
 * otherwise it returns null?
 *
 * By adapt, we mean that it detects that we attempt tou wrap our response inside [dev.jvoyatz.modern.android.network.models.ApiResponse]
 * so it calls [ApiResponseCall] to complete this process
 *
 * @param The type of the object that this adapter uses when payload is deserialized into a Java object
 */
internal class ApiResponseCallDeferredAdapter<S : Any, E : Any>(
    private val successType: Type,
    private val errorConverter: Converter<ResponseBody, E>
) : CallAdapter<S, Deferred<ApiResponse<S, E>>> {
    override fun responseType(): Type {
        return successType
    }

    /**
     * It returns a class that does whatever needed in order to wrap the request's response into
     * the desired class Type.
     */
    override fun adapt(call: Call<S>): Deferred<ApiResponse<S, E>> {
        val deferred = CompletableDeferred<ApiResponse<S, E>>()

        deferred.invokeOnCompletion {
            if (deferred.isCancelled) {
                call.cancel()
            }
        }

        call.enqueue(object : Callback<S> {
            override fun onResponse(call: Call<S>, response: Response<S>) {
                response.asApiResponse(successType, errorConverter).run {
                    deferred.complete(this)
                }
            }

            override fun onFailure(call: Call<S>, t: Throwable) {
                t.asApiResponse<S, E>(successType, errorConverter).run {
                    deferred.complete(this)
                }
            }
        })

        return deferred
    }
}