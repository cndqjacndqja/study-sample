package com.example.studysample.auth.presentation

import com.example.studysample.auth.presentation.dto.GoogleLoginRequest
import com.example.studysample.auth.presentation.dto.LoginResponse
import com.example.studysample.auth.exception.UnauthenticatedException
import com.example.studysample.auth.service.GoogleLoginService
import com.example.studysample.auth.service.JjwtProviderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val jjwtProviderService: JjwtProviderService,
    private val googleLoginService: GoogleLoginService
) {

    @PostMapping("/google/login")
    fun getGoogleToken(@RequestBody request: GoogleLoginRequest): LoginResponse {
        val token = googleLoginService.getToken(request.code)
            .orElseThrow { UnauthenticatedException("토큰이 유효하지 않습니다.") }
        val member = googleLoginService.getMemberInfo(token)
            .orElseGet { googleLoginService.registerMember(token) }
        return LoginResponse(jjwtProviderService.createToken(member.id))
    }
}
