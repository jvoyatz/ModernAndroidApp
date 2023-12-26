package dev.jvoyatz.modern.android.network.config

import dev.jvoyatz.modern.android.network.config.call.adapters.ApiResponseCallAdapter
import dev.jvoyatz.modern.android.network.config.call.adapters.ApiResponseCallDeferredAdapter
import dev.jvoyatz.modern.android.network.config.model.ApiResponse
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * This class handles the adaption of all Retrofit api calls
 * defined with the following signature
 *  ```
 *      fun sampleApiCall(): ApiResponse<S, E>
 *  ```
 *
 *  So we always wait to get a Call<ApiResponse<...>> as returns by [dev.jvoyatz.modern.android.network.config.call.ApiResponseCall]
 *  in order to return the appropriate type, otherwise we return null
 */
internal class ApiResponseCallAdapterFactory : CallAdapter.Factory() {

    /**
     * Returns a call adapter for interface methods that return returnType, or null if it cannot be handled by this factory.
     */
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {

        //return type
        //Deferred<ApiResponse<java.lang.String, java.lang.String>>
        //or
        //Call<ApiResponse<java.lang.String, java.lang.String>>
        if (returnType !is ParameterizedType) {
            return null
        }

        //Call
        //or
        //Deferred
        val returnRawType = getRawType(returnType)
        if (returnRawType !in allowedReturnTypes)
            return null

        //ApiResponse<java.lang.String, java.lang.String>
        val responseContainerType = getParameterUpperBound(0, returnType)
        //ApiResponse
        val responseContainerRawType = getRawType(responseContainerType)
        if (responseContainerRawType != ApiResponse::class.java || responseContainerType !is ParameterizedType) {
            return null
        }

        val (successType, errorType) = getParameterUpperBound(
            0,
            responseContainerType
        ) to getParameterUpperBound(1, responseContainerType)
        val errorConverter = retrofit.nextResponseBodyConverter<Any>(null, errorType, annotations)

        return when (getRawType(returnType)) {
            Call::class.java -> ApiResponseCallAdapter<Any, Any>(successType, errorConverter)
            Deferred::class.java -> ApiResponseCallDeferredAdapter<Any, Any>(
                successType,
                errorConverter
            )

            else -> null
        }
    }

    companion object {
        private val allowedReturnTypes = arrayOf(Call::class.java, Deferred::class.java)

        @JvmStatic
        @JvmName("get")
        operator fun invoke() = ApiResponseCallAdapterFactory()
    }
}

