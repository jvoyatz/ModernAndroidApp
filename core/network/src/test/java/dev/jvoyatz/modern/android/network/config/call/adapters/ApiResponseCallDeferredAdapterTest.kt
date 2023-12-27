package dev.jvoyatz.modern.android.network.config.call.adapters

import com.google.common.truth.Truth
import dev.jvoyatz.modern.android.network.config.ApiResponseCallAdapterFactory
import dev.jvoyatz.modern.android.network.config.model.ApiResponse
import dev.jvoyatz.modern.android.network.config.model.asUnexpectedError
import dev.jvoyatz.modern.android.network.utils.CompletableCall
import dev.jvoyatz.modern.android.network.utils.IntentedError
import dev.jvoyatz.modern.android.network.utils.IntentedErrorConverterFactory
import dev.jvoyatz.modern.android.network.utils.StringConverterFactory
import dev.jvoyatz.modern.android.network.utils.isTypeOf
import dev.jvoyatz.modern.android.network.utils.typeOf
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.http.GET
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class ApiResponseCallDeferredAdapterTest {
    private lateinit var converter: StringConverterFactory
    private lateinit var callAdapterFactory: ApiResponseCallAdapterFactory
    private lateinit var type: Type
    private lateinit var retrofit: Retrofit
    private lateinit var server: MockWebServer
    private lateinit var api: TestApiDeferred

    @Before
    fun setup() {
        converter = StringConverterFactory()
        callAdapterFactory = ApiResponseCallAdapterFactory()
        type = typeOf<Deferred<ApiResponse<String, String>>>()

        server = MockWebServer()
        server.start()


        val client = OkHttpClient.Builder().callTimeout(200, TimeUnit.MILLISECONDS).build()
        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(ApiResponseCallAdapterFactory())
            .addConverterFactory(StringConverterFactory())
            .addConverterFactory(IntentedErrorConverterFactory())
            .baseUrl(server.url("/"))
            .client(client)
            .build()

        api = retrofit.create(TestApiDeferred::class.java)
    }

    @After
    fun clear() {
        server.shutdown()
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `adapts call to ApiResponse (String, String)`() {
        //arrange
        //see setup

        //act
        val call = CompletableCall<String>()
        val adapter = callAdapterFactory.get(type, arrayOf(), retrofit)

        //assert
        Truth.assertThat(adapter).isNotNull()
        Truth.assertThat(adapter!!.isTypeOf<CallAdapter<String, Deferred<ApiResponse<String, String>>>>())
            .isTrue()
        val safeAdapter = adapter as CallAdapter<String, Deferred<ApiResponse<String, String>>>
        val newCall = safeAdapter.adapt(call)
        newCall.isTypeOf<Deferred<ApiResponse<String, String>>>()
    }

    @Test
    fun `returns the given success type`() {
        //arrange
        //see setup

        //act
        val adapter = callAdapterFactory.get(type, arrayOf(), retrofit)

        //assert
        Truth.assertThat(adapter).isNotNull()
        Truth.assertThat(adapter!!.responseType()).isEqualTo(String::class.java)
    }

    @Test
    fun `when invoking get, it should returns ApiResponse success`() = runTest {
        //arrange
        val expectedResult = "success"
        server.enqueue(
            MockResponse().setBody(expectedResult)
        )

        //act
        val response = api.getText().await()


        //assert
        Truth.assertThat(response.isTypeOf<ApiResponse.ApiSuccess<String, String>>()).isTrue()
        val body = (response as ApiResponse.ApiSuccess<String, String>).body
        Truth.assertThat(body).isEqualTo(expectedResult)
    }

    @Test
    fun `in case of an http error it returns HttpError`() = runTest {
        //arrange
        val expectedResult = "bad request"
        server.enqueue(
            MockResponse().setResponseCode(400).setBody(expectedResult)
        )

        //act
        val response = api.getText().await()

        //assert
        Truth.assertThat(response.isTypeOf<ApiResponse.HttpError<String, String>>()).isTrue()
        val error = (response as ApiResponse.HttpError<String, String>).errorBody
        Truth.assertThat(error).isEqualTo(expectedResult)
    }

    @Test
    fun `in case of an network error it returns NetworkError`() = runTest {
        //arrange
        server.enqueue(
            MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE)
        )

        //act
        val response = api.getText().await()

        //assert

        Truth.assertThat(response.isTypeOf<ApiResponse.NetworkError<String, String>>()).isTrue()
        val adaptedResp = response as ApiResponse.NetworkError<String, String>
        Truth.assertThat(adaptedResp.error).isNotNull()
        adaptedResp.error!!.isTypeOf<java.io.IOException>()
    }


    @Test
    fun `in case of an unexpected error it returns Unexpected`() = runTest {
        //arrange
        server.enqueue(
            MockResponse().setResponseCode(200)
        )

        //act
        val response = api.getError().await()


        //assert
        Truth.assertThat(response.isTypeOf<ApiResponse.UnexpectedError<*, *>>()).isTrue()
        val adaptedResp = response as ApiResponse.UnexpectedError<*, *>
        Truth.assertThat(adaptedResp.error).isNotNull()
        adaptedResp.error!!.isTypeOf<IntentedError>()
    }

    @Test
    fun `in case of a successful response without body it is able to handle it properly and returning error if the desired expected is not Unit`() =
        runTest {
            //arrange
            server.enqueue(
                MockResponse().setResponseCode(204)
            )

            //act
            val response = api.getText().await()

            //assert
            Truth.assertThat(response.isTypeOf<ApiResponse.UnexpectedError<String, String>>()).isTrue()
            val error = response.asUnexpectedError()?.error
            Truth.assertThat(error).isNotNull()
        }

    @Test
    fun `in case of a successful response without body it is able to handle it properly and returning success if the desired expected is Unit`() =
        runTest {
            //arrange
            server.enqueue(
                MockResponse().setResponseCode(204)
            )

            //act
            val response = api.getTextEmpty().await()

            //assert
            Truth.assertThat(response.isTypeOf<ApiResponse.ApiSuccess<Unit, String>>()).isTrue()
            val safeResponse = (response as ApiResponse.ApiSuccess<Unit, String>)
            Truth.assertThat(safeResponse.body).isEqualTo(Unit)
        }


    private interface TestApiDeferred {
        @GET("/")
        fun getText(): Deferred<ApiResponse<String, String>>

        @GET("/")
        fun getTextEmpty(): Deferred<ApiResponse<Unit, String>>

        @GET("/error")
        fun getError(): Deferred<ApiResponse<IntentedError, String>>
    }
}

