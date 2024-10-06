package com.example.studysample.auth.presentation

import com.example.studysample.auth.exception.UnauthenticatedException
import com.example.studysample.auth.service.JjwtProviderService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsUtils
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.http.HttpHeaders
import java.util.*
import java.util.regex.Pattern

@Component
class AuthInterceptor(
    private val jjwtProviderService: JjwtProviderService
) : HandlerInterceptor {

    companion object {
        private val JWT_TOKEN_PATTERN: Pattern = Pattern.compile("^Bearer \\b([A-Za-z\\d-_]*\\.[A-Za-z\\d-_]*\\.[A-Za-z\\d-_]*)$")
        private const val TOKEN_INDEX = 1
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (CorsUtils.isPreFlightRequest(request)) {
            return true
        }

        val maybeToken = getToken(request)
        if (maybeToken.isEmpty) {
            println("토큰이 없는데 왜 핸들러에서 안잡냐ㅑㅏㅏ")
            throw UnauthenticatedException("토큰이 유요하지 않습니다.")
        }

        val memberId = jjwtProviderService.extractSubject(maybeToken.get())
        return memberId != null
    }

    private fun getToken(request: HttpServletRequest): Optional<String> {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header.isNullOrEmpty()) {
            return Optional.empty()
        }

        val matcher = JWT_TOKEN_PATTERN.matcher(header)
        return if (matcher.find()) {
            Optional.of(matcher.group(TOKEN_INDEX))
        } else {
            Optional.empty()
        }
    }
}
