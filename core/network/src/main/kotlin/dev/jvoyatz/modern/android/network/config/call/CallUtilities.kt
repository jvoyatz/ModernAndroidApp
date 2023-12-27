package dev.jvoyatz.modern.android.network.config.call

import com.google.common.reflect.TypeToken
import dev.jvoyatz.modern.android.network.config.model.ApiResponse
import dev.jvoyatz.modern.android.network.config.model.asApiResponse
import dev.jvoyatz.modern.android.network.config.model.bodyAsApiResponse
import dev.jvoyatz.modern.android.network.config.model.getErrorConverter
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response

/**
 * Receives a lambda functions, which returns
 * a retrofit response.
 * After this function has been executed, the response is parsed
 * and a response containing the state of the HttpResponse is returned
 * see ApiResponse
 *
 * A downside using this functions is that you need to include it in every repository.
 * It could be reused (and improved) by using a retrofit call adapter
 */
suspend inline fun <reified S,  reified E> safeRawApiCall(
    errorConverter: Converter<ResponseBody, E> = getErrorConverter(E::class.java),
    noinline apiCall: suspend () -> S
): ApiResponse<S, E> = rawApiCall(S::class.java, E::class.java, errorConverter, apiCall)

suspend fun <S,  E> rawApiCall(
    clazzSuccess: Class<S>,
    clazzError: Class<E>,
    errorMapper: Converter<ResponseBody, E> = getErrorConverter(clazzError),
    apiCall: suspend () -> S): ApiResponse<S, E> {
    return try {
        val response = apiCall()
        response.bodyAsApiResponse(clazzSuccess, clazzError, errorMapper)
    } catch (t: Throwable) {
        t.asApiResponse(clazzSuccess, clazzError, errorMapper)
    }
}
/**
 * Same as above but handles Retrofit with Response<S> type
 */
suspend inline fun <reified S, reified E> safeApiCall(
    errorConverter: Converter<ResponseBody, E> = getErrorConverter(E::class.java),
    noinline apiCall: suspend () -> Response<S>
): ApiResponse<S, E> = apiCall( S::class.java, E::class.java, errorConverter =errorConverter, apiCall = apiCall)

suspend fun <S, E> apiCall(
    clazzSuccess: Class<S>,
    clazzError: Class<E>,
    errorConverter: Converter<ResponseBody, E> = getErrorConverter(clazzError),
    apiCall: suspend () -> Response<S>): ApiResponse<S, E> {
    return try {
        val response = apiCall()
        val type = TypeToken.of(clazzSuccess).type
        response.asApiResponse(type, errorConverter)
    } catch (t: Throwable) {
        t.asApiResponse(clazzSuccess, clazzError, errorConverter)
    }
}