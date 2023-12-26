package dev.jvoyatz.modern.android.network.utils

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * A simple class that is used to flag an Error api call
 */
internal class IntentedError

internal class IntentedException(): IllegalStateException() {
    override val message: String
        get() = "an error that we want to happen"
}

/**
 * A converter factory for [IntentedError] types which always
 * throws [IntentedException] objects when invoked from Retrofit Api Services
 * given the above mentioned type
 */
internal class IntentedErrorConverterFactory: Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, IntentedError> {
        return Converter<ResponseBody, IntentedError> { throw IntentedException() }
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<IntentedError, RequestBody> {
        return Converter<IntentedError, RequestBody> { throw IntentedException() }
    }
}