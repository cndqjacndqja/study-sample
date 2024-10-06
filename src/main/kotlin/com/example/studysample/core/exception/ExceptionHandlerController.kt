package com.example.studysample.core.exception

import com.example.studysample.auth.exception.UnauthenticatedException
import com.example.studysample.common.BaseApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class ExceptionHandlerController {

    @ExceptionHandler(ServiceException::class, IllegalArgumentException::class)
    fun handleServiceException(ex: RuntimeException): BaseApiResponse<String> {
        return BaseApiResponse.fail(ex.message ?: "Unknown error", HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UnauthenticatedException::class)
    fun handleUnauthenticatedException(ex: UnauthenticatedException): BaseApiResponse<String> {
        return BaseApiResponse.fail(ex.message ?: "Unauthorized access", HttpStatus.UNAUTHORIZED)
    }
}
