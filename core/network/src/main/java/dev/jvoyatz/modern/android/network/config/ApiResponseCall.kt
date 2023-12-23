package dev.jvoyatz.modern.android.network.config

import dev.jvoyatz.modern.android.network.models.ApiResponse
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.lang.reflect.Type
import kotlin.reflect.jvm.internal.impl.util.CheckResult.SuccessCheck

/**
 * A custom [Call] which encapsulates a normal Retrofit call and transforms the
 * default Retrofit's response to an [ApiResponse]
 */
internal class ApiResponseCall<Success, Error>(
    private val delegate: Call<Success>,
    private val errorConverter: Converter<ResponseBody, Error>,
    private val successType: Type
) : Call<ApiResponse<Success, Error>> {

    /**
     * Create a new, identical call to this one which can be enqueued or executed even if this call has already been.
     */
    override fun clone(): Call<ApiResponse<Success, Error>> = ApiResponseCall(delegate.clone(), errorConverter, successType)

    /**
     * Synchronously send the request and return its response.
     *
     * Throws:
     * IOException – if a problem occurred talking to the server.
     * RuntimeException – (and subclasses) if an unexpected error occurs creating the request or decoding the response.
     */
    override fun execute(): Response<ApiResponse<Success, Error>> {
        val response = delegate.execute()
        val apiResponse = response.asApiResponse(successType, errorConverter)
        return Response.success(apiResponse)
    }

    /**
     * Returns true if this call has been either executed or enqueued. It is an error to execute or enqueue a call more than once.
     */
    override fun isExecuted(): Boolean = delegate.isExecuted

    /**
     * Cancel this call. An attempt will be made to cancel in-flight calls, and if the call has not yet been executed it never will be.
     */
    override fun cancel() = delegate.cancel()

    /**
     * True if cancel() was called.
     */
    override fun isCanceled(): Boolean = delegate.isCanceled

    /**
     * The original HTTP request.
     */
    override fun request(): Request = delegate.request()

    /**
     * Returns a timeout that spans the entire call: resolving DNS, connecting, writing the request body,
     * server processing, and reading the response body. If the call requires redirects or retries all must
     * complete within one timeout period.
     */
    override fun timeout(): Timeout = delegate.timeout()

    /**
     * Asynchronously send the request and notify callback of its response or if an error occurred
     * talking to the server, creating the request, or processing the response.
     */
    override fun enqueue(callback: Callback<ApiResponse<Success, Error>>) = synchronized(this) {
        delegate.enqueue(object: Callback<Success> {
            override fun onResponse(call: Call<Success>, response: Response<Success>) {
                val apiResponse = response.asApiResponse(successType, errorConverter)
                callback.onResponse(this@ApiResponseCall, Response.success(apiResponse))
            }

            override fun onFailure(call: Call<Success>, t: Throwable) {
                val apiResponse = t.asApiResponse<Success, Error>(successType, errorConverter)
                callback.onResponse(this@ApiResponseCall, Response.success(apiResponse))
            }
        })
    }
}