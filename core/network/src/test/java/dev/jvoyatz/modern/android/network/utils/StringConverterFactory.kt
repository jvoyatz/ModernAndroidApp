package dev.jvoyatz.modern.android.network.utils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Convert objects to and from their representation in HTTP. Instances are created by a factory which is installed into the Retrofit instance.
 *
 * We use this so as to be able to deserialize string responses while executing Unit Tests
 */
class StringConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, String>? {
        if (type !== String::class.java) {
            return null
        }

        return Converter<ResponseBody, String> { value ->
            value.string()
        }
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<String, RequestBody>? {
        if (type !== String::class.java) {
            return null
        }

        return Converter<String, RequestBody> { value -> value.toRequestBody("text/plain".toMediaTypeOrNull()) }
    }
}