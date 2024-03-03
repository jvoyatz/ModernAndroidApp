package dev.jvoyatz.modern.android.network.moshi

import com.google.common.truth.Truth
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.jvoyatz.modern.android.network.config.ApiResponseCallAdapterFactory
import dev.jvoyatz.modern.android.network.config.model.ApiResponse
import dev.jvoyatz.modern.android.network.utils.getResFileContent
import dev.jvoyatz.modern.android.network.utils.isTypeOf
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MoshiTest {
    private lateinit var server: MockWebServer
    private lateinit var sut: BoredApiDataSource

    private val filename = "/bored_api_activity_sample.json"

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(ApiResponseCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(server.url("/"))
            .build()

        val api = retrofit.create(BoredApi::class.java)

        sut = BoredApiDataSource(api)


    }

    @After
    fun clear() {
        server.shutdown()
    }


    @Test
    fun `can parse successful get Activity response`() {
        //given
        val expected = "Think of a new business idea"
        server.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(getResFileContent(filename))
        )
        //when
        val response = sut.getRandomActivity()

        //then
        Truth.assertThat(response.isTypeOf<ApiResponse.ApiSuccess<BoredActivity, BoredActivityError>>())
            .isTrue()
        val resp = response as ApiResponse.ApiSuccess<BoredActivity, BoredActivityError>
        Truth.assertThat(resp.body.activity).isEqualTo(expected)
    }

    @Test
    fun `can parse error response successfully`() {
        //given
        val expected = "error sample"
        server.enqueue(
            MockResponse().setResponseCode(400)
                .setBody(getResFileContent("/error_sample.json"))
        )
        //when
        val response = sut.getRandomActivity()

        //then
        Truth.assertThat(response.isTypeOf<ApiResponse.HttpError<BoredActivity, BoredActivityError>>())
            .isTrue()
        val resp = response as ApiResponse.HttpError<BoredActivity, BoredActivityError>
        Truth.assertThat(resp.errorBody?.error).isEqualTo(expected)
    }

    @Test
    fun `cannot parse successful response with errors`() {
        //given
        server.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(getResFileContent(filename))
        )
        //when
        val response = sut.getRandomActivityInvalid()

        //then
        Truth.assertThat(response.isTypeOf<ApiResponse.UnexpectedError<BoredActivityInvalid, BoredActivityErrorInvalid>>())
            .isTrue()
        val resp =
            response as ApiResponse.UnexpectedError<BoredActivityInvalid, BoredActivityErrorInvalid>
        Truth.assertThat(resp.error?.isTypeOf<JsonDataException>()).isTrue()
    }

    @Test
    fun `on Invalid Error Response, can handle the body correctly`() {
        //given
        server.enqueue(
            MockResponse().setResponseCode(404)
                .setBody(
                    """
                    { "test": "down" }
                """.trimIndent()
                )
        )
        //when
        val response = sut.getRandomActivityInvalid()

        //then
        Truth.assertThat(response.isTypeOf<ApiResponse.UnexpectedError<BoredActivityInvalid, BoredActivityErrorInvalid>>())
            .isTrue()
        val resp =
            response as ApiResponse.UnexpectedError<BoredActivityInvalid, BoredActivityErrorInvalid>
        Truth.assertThat(resp.error?.isTypeOf<JsonDataException>()).isTrue()
    }

    @Test
    fun `fail to parse Error response with errors (invalid)`() {
        //given
        server.enqueue(
            MockResponse().setResponseCode(404)
                .setBody(getResFileContent("/error_sample.json"))
        )
        //when
        val response = sut.getRandomActivityInvalid()

        //then
        Truth.assertThat(response.isTypeOf<ApiResponse.UnexpectedError<BoredActivityInvalid, BoredActivityErrorInvalid>>())
            .isTrue()
        val resp =
            response as ApiResponse.UnexpectedError<BoredActivityInvalid, BoredActivityErrorInvalid>
        Truth.assertThat(resp.error?.isTypeOf<JsonDataException>()).isTrue()
    }

    @Test
    fun `can parse successful get Activity response with Deferred`() {
        //given
        val expected = "Think of a new business idea"
        server.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(getResFileContent(filename))
        )
        //when
        val response = sut.getDeferredRandomActivity()

        //then
        Truth.assertThat(response.isTypeOf<ApiResponse.ApiSuccess<BoredActivity, BoredActivityError>>())
            .isTrue()
        val resp = response as ApiResponse.ApiSuccess<BoredActivity, BoredActivityError>
        Truth.assertThat(resp.body.activity).isEqualTo(expected)
    }

    @Test
    fun `can parse error response when using Deferred`() {
        //given
        val expected = "error sample"
        server.enqueue(
            MockResponse().setResponseCode(400)
                .setBody(getResFileContent("/error_sample.json"))
        )
        //when
        val response = sut.getRandomActivity()

        //then
        Truth.assertThat(response.isTypeOf<ApiResponse.HttpError<BoredActivity, BoredActivityError>>())
            .isTrue()
        val resp = response as ApiResponse.HttpError<BoredActivity, BoredActivityError>
        Truth.assertThat(resp.errorBody?.error).isEqualTo(expected)
    }

    @Test
    fun `cannot parse successful response with errors using Deferred`() {
        //given
        server.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(getResFileContent(filename))
        )
        //when
        val response = sut.getDeferredRandomActivityInvalid()

        //then
        Truth.assertThat(response.isTypeOf<ApiResponse.UnexpectedError<BoredActivityInvalid, BoredActivityErrorInvalid>>())
            .isTrue()
        val resp =
            response as ApiResponse.UnexpectedError<BoredActivityInvalid, BoredActivityErrorInvalid>
        Truth.assertThat(resp.error?.isTypeOf<JsonDataException>()).isTrue()
    }

    @Test
    fun `cannot parse error response with errors using Deferred`() {
        //given
        server.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(getResFileContent(filename))
        )
        //when
        val response = sut.getDeferredRandomActivityInvalid()

        //then
        Truth.assertThat(response.isTypeOf<ApiResponse.UnexpectedError<BoredActivityInvalid, BoredActivityErrorInvalid>>())
            .isTrue()
        val resp =
            response as ApiResponse.UnexpectedError<BoredActivityInvalid, BoredActivityErrorInvalid>
        Truth.assertThat(resp.error?.isTypeOf<JsonDataException>()).isTrue()
    }

    @Test
    fun `can parse empty body response as Void or Unit`() {
        //given
        server.enqueue(
            MockResponse().setResponseCode(204)
        )
        //when
        val response = sut.getTestUnit()

        //then
        Truth.assertThat(response.isTypeOf<ApiResponse.ApiSuccess<Unit, BoredActivityError>>())
            .isTrue()
    }

    @Test
    fun `can parse empty body response as Void or Unit using Deferred`() {
        //given
        server.enqueue(
            MockResponse().setResponseCode(204)
        )
        //when
        val response = sut.getDeferredTestUnit()

        //then
        Truth.assertThat(response.isTypeOf<ApiResponse.ApiSuccess<Unit, BoredActivityError>>())
            .isTrue()
    }
}