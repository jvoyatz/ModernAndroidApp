package dev.jvoyatz.modern.android.network.config.model

import com.google.common.truth.Truth
import dev.jvoyatz.modern.android.network.config.model.ApiResponse.ApiSuccess
import dev.jvoyatz.modern.android.network.config.model.ApiResponse.HttpError
import dev.jvoyatz.modern.android.network.config.model.ApiResponse.NetworkError
import dev.jvoyatz.modern.android.network.config.model.ApiResponse.UnexpectedError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ApiResponseTest {
    private val success = ApiResponse.success<String, Unit>("234")
    private val successEmpty = ApiResponse.success<Unit, Unit>(Unit)
    private val httpError = ApiResponse.httpError<String, String>(234, "http error")
    private val unexpectedError =
        ApiResponse.unexpectedError<String, String>(NullPointerException("unexpectedError"))
    private  val networkError =
        ApiResponse.networkError<String, String>(IllegalStateException("networkerror"))


    @Test
    fun `static httpError() function returns HttpError instance`() {
        //given
        val code = 1
        val bodyStr = "error"

        //when
        val httpError = ApiResponse.httpError<Unit, String>(code, bodyStr)

        //then
        Truth.assertThat(httpError).isInstanceOf(HttpError::class.java)
    }

    @Test
    fun `static unknownError() function returns unknownError instance`() {
        //given
        val throwable = IllegalStateException()

        //when
        val unknownError = ApiResponse.unexpectedError<Any, Any>(throwable)

        //then
        Truth.assertThat(unknownError).isInstanceOf(UnexpectedError::class.java)
    }

    @Test
    fun `static networkError() function returns networkError instance`() {
        //given
        val throwable = RuntimeException()

        //when
        val networkError = ApiResponse.networkError<Any, Any>(throwable)

        //then
        Truth.assertThat(networkError).isInstanceOf(NetworkError::class.java)
    }

    @Test
    fun `static success() function returns success instance`() {
        //given
        val data = "1"

        //when
        val success = ApiResponse.success<String, Unit>(data)

        //then
        Truth.assertThat(success).isInstanceOf(ApiSuccess::class.java)
    }

    @Test
    fun `asSuccess returns non-null values when the current instance is ApiSuccess`() {
        //given
        val data = "1"

        //when
        val success = ApiResponse.success<String, Unit>(data)

        //then
        Truth.assertThat(success.isSuccess()).isTrue()
        Truth.assertThat(success.asSuccess()).isNotNull()
    }

    @Test
    fun `asSuccess returns null when the current instance is not ApiSuccess`() {
        //when
        val success = ApiResponse.networkError<String, Unit>(null)

        //then
        Truth.assertThat(success.asSuccess()).isNull()
    }

    @Test
    fun `asError returns non-null values when the current instance is ApiError`() {
        //when
        val networkError = ApiResponse.networkError<String, Unit>(null)

        //then
        Truth.assertThat(networkError.asError()).isNotNull()
    }

    @Test
    fun `asError returns null values when the current instance is not ApiError`() {
        //when
        val success = ApiResponse.success<String, Unit>("234")

        //then
        Truth.assertThat(success.asError()).isNull()
    }

    @Test
    fun `asHttpError returns non-null values when the current instance is HttpError`() {
        //when
        val httpError = ApiResponse.httpError<String, Unit>(1, Unit)

        //then
        Truth.assertThat(httpError.asHttpError()).isNotNull()
    }

    @Test
    fun `asHttpError returns null values when the current instance is not HttpError`() {
        //when
        val success = ApiResponse.success<String, Unit>("234")

        //then
        Truth.assertThat(success.asHttpError()).isNull()
    }

    @Test
    fun `asNetworkError returns non-null values when the current instance is HttpError`() {
        //when
        val networkError = ApiResponse.networkError<String, Unit>(null)

        //then
        Truth.assertThat(networkError.asNetworkError()).isNotNull()
    }

    @Test
    fun `asNetworkError returns null values when the current instance is not HttpError`() {
        //when
        val success = ApiResponse.success<String, Unit>("234")

        //then
        Truth.assertThat(success.asNetworkError()).isNull()
    }

    @Test
    fun `asUnexpectedError returns non-null values when the current instance is UnexpectedError`() {
        //when
        val unknownError = ApiResponse.unexpectedError<String, Unit>(null)

        //then
        Truth.assertThat(unknownError.asUnexpectedError()).isNotNull()
    }

    @Test
    fun `asUnexpectedError returns null values when the current instance is not UnexpectedError`() {
        //when
        val success = ApiResponse.success<String, Unit>("234")

        //then
        Truth.assertThat(success.asUnexpectedError()).isNull()
    }

    @Test
    fun `isSuccess returns true when ApiSuccess instance`() {
        //when
        val isSuccess = success.isSuccess()

        //then
        Truth.assertThat(isSuccess).isTrue()
    }

    @Test
    fun `isSuccess returns false when non ApiSuccess instance`() {
        //when
        val isSuccess = httpError.isSuccess()

        //then
        Truth.assertThat(isSuccess).isFalse()
    }

    @Test
    fun `isSuccessEmpty returns true when ApiSuccess and empty body instance`() {
        //when
        val isEmptySuccess = successEmpty.isSuccessEmpty()

        //then
        Truth.assertThat(isEmptySuccess).isTrue()
    }

    @Test
    fun `isSuccessEmpty returns false when  ApiSuccess and other type body instance`() {
        //when
        val isEmptySuccess = success.isSuccessEmpty()

        //then
        Truth.assertThat(isEmptySuccess).isFalse()
    }

    @Test
    fun `isSuccessEmpty returns false when non ApiSuccess`() {
        //when
        val isEmptySuccess = httpError.isSuccessEmpty()

        //then
        Truth.assertThat(isEmptySuccess).isFalse()
    }

    @Test
    fun `isError returns true when HttpError instance`() {
        //when
        val isError = httpError.isError()

        //then
        Truth.assertThat(isError).isTrue()
    }

    @Test
    fun `isError returns true when any ApiError instance`() {
        //when
        val isError = unexpectedError.isError()

        //then
        Truth.assertThat(isError).isTrue()
    }

    @Test
    fun `isError returns false when ApiSuccess instance`() {
        //when
        val isError = success.isError()

        //then
        Truth.assertThat(isError).isFalse()
    }

    @Test
    fun `getOrNull returns not null data when ApiSuccess`() {
        //given
        val body = "234"

        //when
        val data = success.getOrNull()

        //then
        Truth.assertThat(data).isNotNull()
        Truth.assertThat(data).isEqualTo(body)
    }

    @Test
    fun `getOrNull() returns null data when is not an instance of ApiSuccess`() {
        //when
        val data = httpError.getOrNull()

        //then
        Truth.assertThat(data).isNull()
    }

    @Test
    fun `onSuccess is executed when apiResponse is of an ApiSuccess instance`() {
        //given
        var isExecuted = false

        //when
        success.onSuccess {
            isExecuted = true
        }

        //then
        Truth.assertThat(isExecuted).isTrue()
    }

    @Test
    fun `onSuccess is not executed when apiResponse is not an ApiSuccess instance`() {
        //given
        var isExecuted = false

        //when
        httpError.onSuccess {
            isExecuted = true
        }

        //then
        Truth.assertThat(isExecuted).isFalse()
    }

    @Test
    fun `onSuccess with map block is executed when apiResponse is of an ApiSuccess instance`() {
        //given
        var isExecuted = false

        //when
        success.onSuccess(mapper = {
            1
        }, onExecute = {
            isExecuted = true
        })

        //then
        Truth.assertThat(isExecuted).isTrue()
    }
    @Test
    fun `onSuccess with map block check that the expected type is given in onExecute block when apiResponse is an instance of ApiSuccess`() {
        //given
        var isExecuted = false
        var returnedValue: Int? = null

        //when
        success.onSuccess(
            mapper = {
                1
            },
            onExecute = {
                returnedValue = this
                isExecuted = true
            })

        //then
        Truth.assertThat(isExecuted).isTrue()
        Truth.assertThat(returnedValue).isNotNull()
        Truth.assertThat(returnedValue).isEqualTo(1)
    }

    @Test
    fun `onSuccess with map block is not executed when apiResponse is not an ApiSuccess instance`() {
        //given
        var isExecuted = false

        //when
        httpError.onSuccess(mapper = {
            1
        }, onExecute = {
            isExecuted = true
        })

        //then
        Truth.assertThat(isExecuted).isFalse()
    }

    @Test
    fun `onSuspendedSuccess is executed when apiResponse is of an ApiSuccess instance`() = runTest {
        //given
        var isExecuted = false

        //when
        success.onSuspendedSuccess {
            isExecuted = true
        }

        //then
        Truth.assertThat(isExecuted).isTrue()
    }

    @Test
    fun `onSuspendedSuccess is not executed when apiResponse is not an ApiSuccess instance`() =
        runTest {
            //given
            var isExecuted = false

            //when
            httpError.onSuspendedSuccess {
                isExecuted = true
            }

            //then
            Truth.assertThat(isExecuted).isFalse()
        }

    @Test
    fun `onSuspendedSuccess with map block is executed when apiResponse is of an ApiSuccess instance`() =
        runTest {
            //given
            var isExecuted = false

            //when
            success.onSuspendedSuccess (mapper = {
                1
            }, onExecute = {
                isExecuted = true
            })

            //then
            Truth.assertThat(isExecuted).isTrue()
        }

    @Test
    fun `onSuspendedSuccess with map is not executed when apiResponse is not an ApiSuccess instance`() =
        runTest {
            //given
            var isExecuted = false

            //when
            httpError.onSuspendedSuccess (mapper = {
                1
            }, onExecute = {
                isExecuted = true
            })

            //then
            Truth.assertThat(isExecuted).isFalse()
        }

    @Test
    fun `onError is executed when apiResponse is an ApiError instance`() {
        //given
        var isExecuted = false

        //when
        httpError.onError {
            isExecuted = true
        }

        //then
        Truth.assertThat(isExecuted).isTrue()
    }

    @Test
    fun `onError is not executed when apiResponse is of an ApiSuccess instance`() {
        //given
        var isExecuted = false

        //when
        success.onError {
            isExecuted = true
        }

        //then
        Truth.assertThat(isExecuted).isFalse()
    }

    @Test
    fun `onSuspendedError is executed when apiResponse is an ApiError instance`() = runTest {
        //given
        var isExecuted = false

        //when
        httpError.onSuspendedError {
            isExecuted = true
        }

        //then
        Truth.assertThat(isExecuted).isTrue()
    }

    @Test
    fun `onSuspendedError is not executed when apiResponse is of an ApiSuccess instance`() =
        runTest {
            //given
            var isExecuted = false

            //when
            success.onSuspendedError {
                isExecuted = true
            }

            //then
            Truth.assertThat(isExecuted).isFalse()
        }


    @Test
    fun `onHttpError is executed when apiResponse is an HttpError instance`() {
        //given
        var isExecuted = false

        //when
        httpError.onHttpError {
            isExecuted = true
        }

        //then
        Truth.assertThat(isExecuted).isTrue()
    }

    @Test
    fun `onHttpError is not executed when apiResponse is not of an HttpError instance`() {
        //given
        var isExecuted = false

        //when
        success.onHttpError {
            isExecuted = true
        }

        //then
        Truth.assertThat(isExecuted).isFalse()
    }

    @Test
    fun `onSuspendedHttpError is executed when apiResponse is an HttpError instance`() = runTest {
        //given
        var isExecuted = false

        //when
        httpError.onSuspendedHttpError {
            isExecuted = true
        }

        //then
        Truth.assertThat(isExecuted).isTrue()
    }

    @Test
    fun `onSuspendedHttpError is not executed when apiResponse is not of an HttpError instance`() =
        runTest {
            //given
            var isExecuted = false

            //when
            success.onSuspendedHttpError {
                isExecuted = true
            }

            //then
            Truth.assertThat(isExecuted).isFalse()
        }


    @Test
    fun `onNetworkError is executed when apiResponse is an NetworkError instance`() {
        //given
        var isExecuted = false

        //when
        networkError.onNetworkError {
            isExecuted = true
        }

        //then
        Truth.assertThat(isExecuted).isTrue()
    }

    @Test
    fun `onNetworkError is not executed when apiResponse is not of a NetworkError instance`() {
        //given
        var isExecuted = false

        //when
        success.onNetworkError {
            isExecuted = true
        }

        //then
        Truth.assertThat(isExecuted).isFalse()
    }

    @Test
    fun `onSuspendedNetworkError is executed when apiResponse is a NetworkError instance`() =
        runTest {
            //given
            var isExecuted = false

            //when
            networkError.onSuspendedNetworkError {
                isExecuted = true
            }

            //then
            Truth.assertThat(isExecuted).isTrue()
        }

    @Test
    fun `onSuspendedNetworkError is not executed when apiResponse is not of a NetworkError instance`() =
        runTest {
            //given
            var isExecuted = false

            //when
            success.onSuspendedNetworkError {
                isExecuted = true
            }

            //then
            Truth.assertThat(isExecuted).isFalse()
        }
    @Test
    fun `onUnexpectedError is not executed when apiResponse is not an instance of an UnExpectedError`() {
        //given
        var isExecuted = false

        //when
        success.onNetworkError {
            isExecuted = true
        }

        //then
        Truth.assertThat(isExecuted).isFalse()
    }

    @Test
    fun `onUnexpectedError is executed when apiResponse is an UnExpectedError instance`() {
        //given
        var isExecuted = false

        //when
        unexpectedError.onUnexpectedError {
            isExecuted = true
        }

        //then
        Truth.assertThat(isExecuted).isTrue()
    }

    @Test
    fun `onSuspendedUnexpectedError is executed when apiResponse is an UnexpectedError instance`() =
        runTest {
            //given
            var isExecuted = false

            //when
            unexpectedError.onSuspendedUnexpectedError {
                isExecuted = true
            }

            //then
            Truth.assertThat(isExecuted).isTrue()
        }

    @Test
    fun `onSuspendedUnexpectedError is not executed when apiResponse is not of an UnknownError instance`() =
        runTest {
            //given
            var isExecuted = false

            //when
            success.onSuspendedUnexpectedError {
                isExecuted = true
            }

            //then
            Truth.assertThat(isExecuted).isFalse()
        }


    @Test
    fun `apiResponse toFlow emit the body type when success`() = runTest {
        //when
        val flow = success.toFlow()
        val data = flow.first()

        //then
        Truth.assertThat(data).isEqualTo("234")
    }

    @Test
    fun `apiResponse toFlow emit nothing when error`() = runTest {
        //when
        val flow = httpError.toFlow()
        val data = flow.toList()

        //then
        Truth.assertThat(data).isEmpty()
    }

    @Test
    fun `apiResponse toFlow with map block func emit the body type when success`() = runTest {
        //when
        val flow = success.toFlow {
            1
        }
        val data = flow.first()

        //then
        Truth.assertThat(data).isEqualTo(1)
    }

    @Test
    fun `apiResponse toFlow with map block func emit nothing when error`() = runTest {
        //when
        val flow = httpError.toFlow {
            1
        }
        val data = flow.toList()

        //then
        Truth.assertThat(data).isEmpty()
    }

    @Test
    fun `apiResponse toSuspendFlow with map block func emit nothing when error`() = runTest {
        //when
        val flow = httpError.toSuspendFlow {
            1
        }
        val data = flow.toList()

        //then
        Truth.assertThat(data).isEmpty()
    }

    @Test
    fun `apiResponse toSuspendFlow with map block func emit data when success`() = runTest {
        //when
        val flow = httpError.toSuspendFlow {
            1
        }
        val data = flow.toList()

        //then
        Truth.assertThat(data).isEmpty()
    }

    @Test
    fun `calling invoke operator on ApiSuccess returns its body`(){

        //given
        val body = "response"
        val response = ApiResponse.success<String, Unit>(body)

        //when
        val res = response.invoke()

        //then
        assertNotNull(res)
        assertEquals(res, body)
    }

    @Test
    fun `calling invoke operator on HttpError returns null`(){

        //given
        val response = ApiResponse.httpError<String, String>(errorBody = "error")

        //when
        val res = response.invoke()

        //then
        assertNull(res)
    }

    @Test
    fun `calling invoke operator on NetworkError returns null`(){

        //given
        val response = ApiResponse.networkError<String, Unit>()

        //when
        val res = response.invoke()

        //then
        assertNull(res)
    }

    @Test
    fun `calling invoke operator on UnexpectedError returns null`(){

        //given
        val response = ApiResponse.unexpectedError<String, Unit>()

        //when
        val res = response.invoke()

        //then
        assertNull(res)
    }

    @Test
    fun `calling extract error on ApiSuccess returns null`() {
        //given
        val response = success

        //when
        val res = response.extractError()

        //then
        assertNull(res)
    }

    @Test
    fun `calling extract error on HttpError returns non-null`() {
        //given
        val response = httpError

        //when
        val res = response.extractError()

        //then
        assertNotNull(res)
    }

    @Test
    fun `calling extract error on NetworkError returns non-null`() {
        //given
        val response = networkError

        //when
        val res = response.extractError()

        //then
        assertNotNull(res)
    }

    @Test
    fun `calling extract error on UnexpectedError returns non-null`() {
        //given
        val response = unexpectedError

        //when
        val res = response.extractError()

        //then
        assertNotNull(res)
    }
}