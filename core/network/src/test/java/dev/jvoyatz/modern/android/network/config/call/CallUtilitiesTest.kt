package dev.jvoyatz.modern.android.network.config.call


import com.google.common.truth.Truth
import dev.jvoyatz.modern.android.network.config.model.ApiResponse
import dev.jvoyatz.modern.android.network.config.model.asError
import dev.jvoyatz.modern.android.network.config.model.asHttpError
import dev.jvoyatz.modern.android.network.config.model.asNetworkError
import dev.jvoyatz.modern.android.network.config.model.asSuccess
import dev.jvoyatz.modern.android.network.config.model.asUnexpectedError
import dev.jvoyatz.modern.android.network.config.model.isSuccess
import dev.jvoyatz.modern.android.network.config.model.isSuccessEmpty


import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


@Suppress("KotlinConstantConditions")
@OptIn(ExperimentalCoroutinesApi::class)
class CallUtilitiesTest {

    private val ERROR = "{\"error\": [\"errorError\"]}"
    private val ERROR_RESPONSE_BODY = ERROR.toResponseBody("application/json".toMediaTypeOrNull())
    @Test
    fun `safeApiCall - on successful response, it returns ApiSuccess`() = runTest {
        //given
        val body = true

        //when
        val result = safeApiCall<Boolean, Unit> { Response.success(body) }

        //then
        Truth.assertThat(result).isNotNull()
        Truth.assertThat(result.isSuccess()).isTrue()
        Truth.assertThat(result.asSuccess()!!.body).isEqualTo(body)
    }

    @Test
    fun `safeApiCall - on successful response with not expected null body, returns UnexpectedError`() = runTest {
        //given
        val body = null

        //when
        val response = safeApiCall<String, Unit> {
            Response.success(body)
        }

        Truth.assertThat(response).isNotNull()
        Truth.assertThat(response.asError()).isInstanceOf(ApiResponse.UnexpectedError::class.java)
    }

    @Test
    fun `safeApiCall - on successful response with expected null body, returns ApiSuccess`() = runTest {
        //given
        val body = null

        //when
        val response = safeApiCall<Unit, Unit> {
            Response.success(body)
        }

        Truth.assertThat(response).isNotNull()
        Truth.assertThat(response.isSuccessEmpty()).isTrue()
    }

    @Test
    fun `safeApiCall - on successful response with expected unit(void) body, returns ApiSuccess`() = runTest {
        //given
        val body = Unit

        //when
        val response = safeApiCall<Unit, Unit> {
            Response.success(body)
        }

        Truth.assertThat(response).isNotNull()
        Truth.assertThat(response.isSuccessEmpty()).isTrue()
    }

    @Test
    fun `safeApiCall - on http bad request response, returns HttpError`() = runTest {
        //given
        val body = ERROR_RESPONSE_BODY

        //when
        val errorResponse = safeApiCall<Boolean, String>({
            ERROR
        }) {
            Response.error(400, body)
        }

        //then
        Truth.assertThat(errorResponse).isInstanceOf(ApiResponse.HttpError::class.java)
        Truth.assertThat(errorResponse.asHttpError()!!.code).isEqualTo(400)
        Truth.assertThat(errorResponse.asHttpError()!!.errorBody)
            .isEqualTo(ERROR)
    }

    @Test
    fun `safeApiCall - on IOException response, returns NetworkError`() = runTest {
        //when
        val errorResponse = safeApiCall<Unit, Unit> {
            throw IOException("io exception ")
        }

        //then
        Truth.assertThat(errorResponse).isInstanceOf(ApiResponse.NetworkError::class.java)
        Truth.assertThat(errorResponse.asNetworkError()!!.error)
            .isInstanceOf(IOException::class.java)
    }

    @Test
    fun `safeApiCall - on an unknown error, returns UnexpectedError`() = runTest {
        //when
        val errorResponse = safeApiCall<String, String> {
            throw IllegalStateException("test state exception")
        }

        //then
        Truth.assertThat(errorResponse).isInstanceOf(ApiResponse.UnexpectedError::class.java)
        Truth.assertThat(errorResponse.asUnexpectedError()!!.error)
            .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `safeRawApiCall - on successful response, it returns ApiSuccess`() = runTest {
        //given
        val body = true

        //when
        val result = safeRawApiCall<Boolean, Unit> { body }

        //then
        Truth.assertThat(result).isNotNull()
        Truth.assertThat(result.isSuccess()).isTrue()
        Truth.assertThat(result.asSuccess()!!.body).isEqualTo(body)
    }

    @Test
    fun `safeRawApiCall - on successful response with not expected null body, returns UnexpectedError`() = runTest {
        //given
        val body = null

        //when
        val response = safeRawApiCall<String, Unit> {
            body!!
        }

        Truth.assertThat(response).isNotNull()
        Truth.assertThat(response.asError()).isInstanceOf(ApiResponse.UnexpectedError::class.java)
    }

    @Test
    fun `safeRawApiCall - on successful response with expected null body, returns ApiSuccess`() = runTest {
        //given
        val body = null

        //when
        val response = safeApiCall<Unit, Unit> {
            Response.success(body)
        }

        Truth.assertThat(response).isNotNull()
        Truth.assertThat(response.isSuccessEmpty()).isTrue()
    }

    @Test
    fun `safeRawApiCall - on successful response with expected unit(void) body, returns ApiSuccess`() = runTest {
        //given
        val body = Unit

        //when
        val response = safeApiCall<Unit, Unit> {
            Response.success(body)
        }

        Truth.assertThat(response).isNotNull()
        Truth.assertThat(response.isSuccessEmpty()).isTrue()
    }

    @Test
    fun `safeRawApiCall - on http bad request response, returns HttpError`() = runTest {
        //given
        val body = ERROR_RESPONSE_BODY

        //when
        val errorResponse = safeRawApiCall<Boolean, String>({
            ERROR
        }) {
            throw HttpException(Response.error<String>(400, body))
        }

        //then
        Truth.assertThat(errorResponse).isInstanceOf(ApiResponse.HttpError::class.java)
        Truth.assertThat(errorResponse.asHttpError()!!.code).isEqualTo(400)
        Truth.assertThat(errorResponse.asHttpError()!!.errorBody)
            .isEqualTo(ERROR)
    }

    @Test
    fun `safeRawApiCall - on IOException response, returns NetworkError`() = runTest {
        //when
        val errorResponse = safeRawApiCall<Unit, Unit> {
            throw IOException("io exception ")
        }

        //then
        Truth.assertThat(errorResponse).isInstanceOf(ApiResponse.NetworkError::class.java)
        Truth.assertThat(errorResponse.asNetworkError()!!.error)
            .isInstanceOf(IOException::class.java)
    }

    @Test
    fun `safeRawApiCall - on an unknown error, returns UnexpectedError`() = runTest {
        //when
        val errorResponse = safeRawApiCall<String, String> {
            throw IllegalStateException("test state exception")
        }

        //then
        Truth.assertThat(errorResponse).isInstanceOf(ApiResponse.UnexpectedError::class.java)
        Truth.assertThat(errorResponse.asUnexpectedError()!!.error)
            .isInstanceOf(IllegalStateException::class.java)
    }
}