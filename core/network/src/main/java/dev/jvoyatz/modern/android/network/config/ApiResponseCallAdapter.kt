package dev.jvoyatz.modern.android.network.config

import dev.jvoyatz.modern.android.network.models.ApiResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Converter
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
internal class ApiResponseCallAdapter<S: Any, E: Any> (
    private val successType: Type,
    private val errorConverter: Converter<ResponseBody, E>
): CallAdapter<S, Call<ApiResponse<S, E>>> {
    override fun responseType(): Type {
        return successType
    }

    /**
     * It returns a class that does whatever needed in order to wrap the request's response into
     * the desired class Type.
     */
    override fun adapt(call: Call<S>): Call<ApiResponse<S, E>> = ApiResponseCall(call, errorConverter, successType)
}