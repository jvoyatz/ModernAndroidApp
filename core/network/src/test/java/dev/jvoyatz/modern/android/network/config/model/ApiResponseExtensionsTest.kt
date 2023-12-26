package dev.jvoyatz.modern.android.network.config.model

import dev.jvoyatz.modern.android.network.config.model.ApiResponse
import dev.jvoyatz.modern.android.network.config.model.invoke
import org.junit.Assert.*
import org.junit.Test

class ApiResponseExtensionsTest {

    @Test
    fun `given a successful response, calling overloaded invoke operator, it returns its body`(){

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
    fun `given an http error, calling overloaded invoke operator, returns null`(){

        //given
        val response = ApiResponse.httpError<String, String>(errorBody = "error")

        //when
        val res = response.invoke()

        //then
        assertNull(res)
    }

    @Test
    fun `given a network error, calling overloaded invoke operator, returns null`(){

        //given
        val response = ApiResponse.networkError<String, Unit>()

        //when
        val res = response.invoke()

        //then
        assertNull(res)
    }

    @Test
    fun `given an unexpected error, calling overloaded invoke operator, returns null`(){

        //given
        val response = ApiResponse.unexpectedError<String, Unit>()

        //when
        val res = response.invoke()

        //then
        assertNull(res)
    }
}