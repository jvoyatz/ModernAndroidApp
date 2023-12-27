package dev.jvoyatz.modern.android.network.config.call.adapters

import dev.jvoyatz.modern.android.network.config.call.ApiResponseCall
import dev.jvoyatz.modern.android.network.config.model.ApiResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Converter
import java.lang.reflect.Type

/**
 * A [retrofit2.CallAdapter] used to adapt our response to [ApiResponseCall]
 * otherwise it returns null?
 *
 * This is the Deferred configuration for our Retrofit Interfaces
 */
internal class ApiResponseCallAdapter<S : Any, E : Any>(
    private val successType: Type,
    private val errorConverter: Converter<ResponseBody, E>
) : CallAdapter<S, Call<ApiResponse<S, E>>> {
    override fun responseType(): Type {
        return successType
    }

    /**
     * It returns a class that does whatever needed in order to wrap the request's response into
     * the desired class Type.
     */
    override fun adapt(call: Call<S>): Call<ApiResponse<S, E>> =
        ApiResponseCall(call, errorConverter, successType)
}