package com.example.studysample.auth.presentation

import com.example.studysample.auth.domain.AuthenticationPrincipal
import com.example.studysample.auth.exception.UnauthenticatedException
import com.example.studysample.auth.service.JjwtProviderService
import com.google.auth.http.AuthHttpConstants.AUTHORIZATION
import org.springframework.core.MethodParameter
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer



class AuthenticationArgumentResolver(
    private val jjwtProviderService: JjwtProviderService
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AuthenticationPrincipal::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Long {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)!!
        val token = getToken(request)
        return jjwtProviderService.extractSubject(token).toLong()
    }

    private fun getToken(request: HttpServletRequest): String {
        return try {
            val header = request.getHeader(AUTHORIZATION)
            val authorizationValue = header.split(" ")
            authorizationValue[1]
        } catch (e: Exception) {
            throw UnauthenticatedException("토큰이 유요하지 않습니다.")
        }
    }
}
