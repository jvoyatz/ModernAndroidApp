package dev.jvoyatz.modern.android.network.config.model

import com.google.common.reflect.TypeToken
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type


/**
 *
 * Transforms a [Response] object to an [ApiResponse]
 *
 * @param successType
 * @param errorConverter transforms the body of the error response
 */
fun <S, E> Response<S>.asApiResponse(
    successType: Type,
    errorConverter: Converter<ResponseBody, E>
): ApiResponse<S, E> =
    when {
        isSuccessful -> parseHttpSuccessResponse<S, E>(this, successType)
        else -> parseHttpErrorResponse(this, errorConverter)
    }


val EMPTY_BODY_ERROR_MESSAGE = "not expected null body"
/**
 * It transforms a successful [Response] to an  [ApiResponse]
 *
 * Explanation:
 *  1. If response body is null, then
 *     a. in case the successType is Unit (eg post request with 204 response code), then we return an [ApiResponse] with [Unit] as the body
 *     b. otherwise we return an http error [ApiResponse] with a null body
 *  2. In case response body is not null, then we return [ApiResponse.ApiSuccess] with deserialized body
 */
private fun <S, E> parseHttpSuccessResponse(
    response: Response<S>,
    successType: Type
): ApiResponse<S, E> {
    val body: S? = response.body()

    return if (body != null) {
        ApiResponse.success(body)
    } else if (successType === Unit::class.java) {
        @Suppress("UNCHECKED_CAST")
        ApiResponse.success<Unit, E>(Unit) as ApiResponse<S, E>
    } else {

        ApiResponse.unexpectedError(Exception(EMPTY_BODY_ERROR_MESSAGE))
    }
}


/**
 * It transforms a not successful [Response] to [ApiResponse.ApiError].
 *
 * It tries to convert the error response, if exists, and returns
 * a [ApiResponse.HttpError]. Otherwise, we have caught an exception that
 * its origin comes from the deserialization process and we return
 * [ApiResponse.UnexpectedError]
 *
 * @param response A response which contains error
 * @param errorConverter [Converter] that parses the error response, if exists
 * @return [ApiResponse.ApiError]
 */
private fun <S, E> parseHttpErrorResponse(
    response: Response<S>,
    errorConverter: Converter<ResponseBody, E>
): ApiResponse<S, E> {
    val error = (response.errorBody() ?: return ApiResponse.unexpectedError())

    return try {
        errorConverter.convert(error).run {
            ApiResponse.httpError(
                code = response.code(),
                errorBody = this
            )
        }
    } catch (e: Throwable) {
        ApiResponse.unexpectedError(e)
    }
}

/**
 * Transforms a [Throwable] to an [ApiResponse] object.
 *
 * 1. If the error is [IOException], then returns [ApiResponse.NetworkError]
 * 2. If the error is [retrofit2.HttpException], it parses the error response and returns the result
 * 3. Otherwise it returns [ApiResponse.UnexpectedError]
 */
internal fun <S, E> Throwable.asApiResponse(
    successType: Type,
    errorConverter: Converter<ResponseBody, E>
) = when (this) {
    is IOException -> ApiResponse.networkError<S, E>(this)
    is HttpException -> {
        val response = this.response()
        response?.let {
            response.asApiResponse(successType, errorConverter) as ApiResponse<S, E>
        } ?: ApiResponse.httpError<S, E>(this.code(), null)
    }

    else -> ApiResponse.unexpectedError(this)
}
internal fun <S, E> S.bodyAsApiResponse(
    clazzSuccess: Class<S>,
    clazzError: Class<E>,
    errorConverter: Converter<ResponseBody, E> = getErrorConverter(clazz = clazzError)
): ApiResponse<S, E> = Response.success(this).asApiResponse<S, E>(clazzSuccess, errorConverter)

fun <S, E> Throwable.asApiResponse(
    clazzSuccess: Class<S>,
    clazzError: Class<E>,
    errorConverter: Converter<ResponseBody, E> = getErrorConverter(clazz = clazzError)
): ApiResponse<S, E> {
    val type = TypeToken.of(clazzSuccess).type
    return asApiResponse(type, errorConverter)
}

fun <E> getErrorConverter(
    clazz: Class<E>,
    moshi: Moshi = Moshi.Builder().build()
): Converter<ResponseBody, E> {
    return Converter<ResponseBody, E> { body ->
        try{
            moshi.adapter(clazz).fromJson(body.source())
        }catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}