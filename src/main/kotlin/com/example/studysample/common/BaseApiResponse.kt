package com.example.studysample.common

import org.springframework.http.HttpStatus

data class BaseApiResponse<T>(
    val status: String,
    val message: String?,
    val data: T?
) {
    companion object {
        fun <T> ok(data: T?): BaseApiResponse<T> {
            return BaseApiResponse(HttpStatus.OK.value().toString(), null, data)
        }

        fun <T> fail(message: String, httpStatus: HttpStatus): BaseApiResponse<T> {
            return BaseApiResponse(httpStatus.value().toString(), message, null)
        }
    }
}
