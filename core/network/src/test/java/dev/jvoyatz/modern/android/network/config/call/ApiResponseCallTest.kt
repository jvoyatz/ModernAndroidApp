package dev.jvoyatz.modern.android.network.config.call

import com.google.common.truth.Truth
import dev.jvoyatz.modern.android.network.config.call.ApiResponseCall
import dev.jvoyatz.modern.android.network.config.model.ApiResponse
import dev.jvoyatz.modern.android.network.utils.CompletableCall
import dev.jvoyatz.modern.android.network.utils.isTypeOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.awaitResponse

class ApiResponseCallTest {

    private lateinit var expectedContent: String
    private lateinit var delegateCall: CompletableCall<String>
    private lateinit var errorConverter: Converter<ResponseBody, String>
    private lateinit var successType: Class<String>

    @Before
    fun setup() {
        expectedContent = "test"
        delegateCall = CompletableCall()
        errorConverter = Converter<ResponseBody, String> { it.string() }
        successType = String::class.java
    }

    @Test
    fun `given an ApiResponseCall, when invoking execute, it should return a response`() =
        runBlocking {
            //given
            val call = ApiResponseCall(delegateCall, errorConverter, successType)

            //when
            delegateCall.complete(expectedContent)
            val response = call.execute()

            //then
            with(response) {
                Truth.assertThat(isSuccessful).isTrue()

                Truth.assertThat(body()).isNotNull()
                val safeBody = body()!!

                Truth.assertThat(safeBody.isTypeOf<ApiResponse.ApiSuccess<String, String>>())
                    .isTrue()
                val content = (safeBody as ApiResponse.ApiSuccess).body
                Truth.assertThat(content).isEqualTo(expectedContent)
            }
        }

    @Test
    fun `given an ApiResponseCall, when invoking enqueue, it should complete in a non sync way`() =
        runBlocking {
            //given
            val call = ApiResponseCall(delegateCall, errorConverter, successType)
            val deferred = CompletableDeferred<ApiResponse<String, String>>()

            //when
            call.enqueue(object : Callback<ApiResponse<String, String>> {
                override fun onResponse(
                    call: Call<ApiResponse<String, String>>,
                    response: Response<ApiResponse<String, String>>
                ) {
                    deferred.complete(response.body()!!)
                }

                override fun onFailure(call: Call<ApiResponse<String, String>>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
            delegateCall.complete(expectedContent)
            val response = deferred.await()

            //then
            with(response) {
                Truth.assertThat(isTypeOf<ApiResponse.ApiSuccess<String, String>>())
                    .isTrue()
                val content = (this as ApiResponse.ApiSuccess).body
                Truth.assertThat(content).isEqualTo(expectedContent)
            }
        }

    @Test
    fun `given an ApiResponseCall, when invoking enqueue and io exception is caught, it should complete in a non sync way with a network error response`() =
        runBlocking {
            //given
            val call = ApiResponseCall(delegateCall, errorConverter, successType)
            val deferred = CompletableDeferred<ApiResponse<String, String>>()
            val exception = IOException("test")

            //when
            call.enqueue(object : Callback<ApiResponse<String, String>> {
                override fun onResponse(
                    call: Call<ApiResponse<String, String>>,
                    response: Response<ApiResponse<String, String>>
                ) {
                    deferred.complete(response.body()!!)
                }

                override fun onFailure(call: Call<ApiResponse<String, String>>, t: Throwable) {
                    deferred.completeExceptionally(t)
                }
            })
            delegateCall.completeWithException(exception)
            val response = deferred.await()

            //then
            with(response) {
                Truth.assertThat(isTypeOf<ApiResponse.ApiError<String, String>>())
                    .isTrue()
                Truth.assertThat(isTypeOf<ApiResponse.NetworkError<String, String>>()).isTrue()
            }
        }

    @Test
    fun `given an ApiResponseCall, when invoking awaitResponse and io exception is caught, it should complete with a network error response`() =
        runBlocking {
            //given
            val call = ApiResponseCall(delegateCall, errorConverter, successType)
            val exception = IOException("test")

            //when
            delegateCall.completeWithException(exception)
            val response = call.awaitResponse()

            //then
            with(response) {
                Truth.assertThat(body()).isNotNull()
                val safeBody = body()!!
                Truth.assertThat(safeBody.isTypeOf<ApiResponse.ApiError<String, String>>())
                    .isTrue()
                val error = (safeBody as ApiResponse.ApiError<*, *>)
                Truth.assertThat(error.isTypeOf<ApiResponse.NetworkError<String, String>>())
                    .isTrue()
            }
        }

    @Test
    fun `given an ApiResponseCall, when invoking enqueue and an unexpected exception is caught, it should complete in a non sync way and returns unexpected error`() =
        runBlocking {
            //given
            val call = ApiResponseCall(delegateCall, errorConverter, successType)
            val deferred = CompletableDeferred<ApiResponse<String, String>>()
            val exception = Exception("test")

            //when
            call.enqueue(object : Callback<ApiResponse<String, String>> {
                override fun onResponse(
                    call: Call<ApiResponse<String, String>>,
                    response: Response<ApiResponse<String, String>>
                ) {
                    deferred.complete(response.body()!!)
                }

                override fun onFailure(call: Call<ApiResponse<String, String>>, t: Throwable) {
                    deferred.completeExceptionally(t)
                }
            })
            delegateCall.completeWithException(exception)
            val response = deferred.await()

            //then
            with(response) {
                Truth.assertThat(isTypeOf<ApiResponse.ApiError<String, String>>())
                    .isTrue()
                Truth.assertThat(isTypeOf<ApiResponse.UnexpectedError<String, String>>()).isTrue()
            }
        }

    @Test
    fun `given an ApiResponseCall, when invoking execute and an unexpected exception is caught, it should complete with an unexpected error response`() =
        runBlocking {
            //given
            val call = ApiResponseCall(delegateCall, errorConverter, successType)
            val exception = Exception("test")

            //when
            delegateCall.completeWithException(exception)
            val response = call.awaitResponse()

            //then
            with(response) {
                Truth.assertThat(body()).isNotNull()
                val safeBody = body()!!
                Truth.assertThat(safeBody.isTypeOf<ApiResponse.ApiError<String, String>>())
                    .isTrue()
                val error = (safeBody as ApiResponse.ApiError<*, *>)
                Truth.assertThat(error.isTypeOf<ApiResponse.UnexpectedError<String, String>>())
                    .isTrue()
            }
        }

    @Test
    fun `given an ApiResponseCall, when invoking enqueue and a http error is caught, it should complete in a non sync way and returns http error`() =
        runBlocking {
            //given
            val call = ApiResponseCall(delegateCall, errorConverter, successType)
            val deferred = CompletableDeferred<ApiResponse<String, String>>()
            val errorResponse =
                Response.error<String>(400, "".toResponseBody("text/plain".toMediaTypeOrNull()))

            //when
            call.enqueue(object : Callback<ApiResponse<String, String>> {
                override fun onResponse(
                    call: Call<ApiResponse<String, String>>,
                    response: Response<ApiResponse<String, String>>
                ) {
                    deferred.complete(response.body()!!)
                }

                override fun onFailure(call: Call<ApiResponse<String, String>>, t: Throwable) {
                    deferred.completeExceptionally(t)
                }
            })
            delegateCall.complete(errorResponse)
            val response = deferred.await()

            //then
            with(response) {
                Truth.assertThat(isTypeOf<ApiResponse.ApiError<String, String>>())
                    .isTrue()
                Truth.assertThat(isTypeOf<ApiResponse.HttpError<String, String>>()).isTrue()
            }
        }

    @Test
    fun `given an ApiResponseCall, when invoking execute and a http exception is caught, it should complete with an http error response`() =
        runBlocking {
            //given
            val call = ApiResponseCall(delegateCall, errorConverter, successType)
            val exception = Exception("test")

            //when
            delegateCall.completeWithException(exception)
            val response = call.awaitResponse()

            //then
            with(response) {
                Truth.assertThat(body()).isNotNull()
                val safeBody = body()!!
                Truth.assertThat(safeBody.isTypeOf<ApiResponse.ApiError<String, String>>())
                    .isTrue()
                val error = (safeBody as ApiResponse.ApiError<*, *>)
                Truth.assertThat(error.isTypeOf<ApiResponse.UnexpectedError<String, String>>())
                    .isTrue()
            }
        }

    @Test
    fun `given an ApiResponseCall, when invoking cancel it should cancel delegate call`() =
        runBlocking {
            //given
            val call = ApiResponseCall(delegateCall, errorConverter, successType)

            //when
            Truth.assertThat(delegateCall.isCanceled).isFalse()
            call.cancel()

            //then
            Truth.assertThat(delegateCall.isCanceled).isTrue()
            Truth.assertThat(call.isCanceled).isTrue()
        }
}
