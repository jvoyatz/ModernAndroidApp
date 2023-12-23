package dev.jvoyatz.modern.android.network.config

import dev.jvoyatz.modern.android.network.models.ApiResponse
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
 *  So we always wait to get a Call<ApiResponse<...>> as returns by [ApiResponseCall]
 *  in order to return the appropriate type, otherwise we return null
 */
internal class ApiResponseCallAdapterFactory: CallAdapter.Factory() {

    /**
     * Returns a call adapter for interface methods that return returnType, or null if it cannot be handled by this factory.
     */
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        //we expect to see a generic class here
        if(returnType !is ParameterizedType) {
            return null
        }

        /**
         * Extract the upper bound of the generic parameter at index from type. For example, index 1 of Map<String, ? extends Runnable> returns Runnable.
         * response Type must be an object of [ApiResponse] type and it must be parameterized as well.
         */
        val responseWrapperType = getParameterUpperBound(0, returnType)
        if(getRawType(responseWrapperType) != ApiResponse::class.java || responseWrapperType !is ParameterizedType) {
            return null
        }

        val successType = getParameterUpperBound(0,  responseWrapperType)
        val errorType = getParameterUpperBound(1, responseWrapperType)

        val errorConverter = retrofit.nextResponseBodyConverter<Any>(null, errorType, annotations)

        return when(getRawType(returnType)) {
                Call::class.java -> ApiResponseCallAdapter<Any, Any>(successType, errorConverter)
                else -> null
            }
        }
    }
}
