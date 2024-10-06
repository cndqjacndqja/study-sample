package com.example.studysample.auth.service

import com.example.studysample.auth.exception.UnauthenticatedException
import com.example.studysample.core.domain.member.Member
import com.example.studysample.core.domain.member.MemberRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.*
import org.springframework.http.*

@Service
class GoogleLoginService(
    private val memberRepository: MemberRepository,
    @Value("\${google.login.client_id}") private val CLIENT_ID: String,
    @Value("\${google.login.client_secret}") private val CLIENT_SECRET: String,
) {

    private val logger = LoggerFactory.getLogger(GoogleLoginService::class.java)
    private val restTemplate = RestTemplate()
    private val objectMapper = ObjectMapper()

    companion object {
        private const val REDIRECT_URI = "http://localhost:3000/routine/auth/login/google"
        private const val TOKEN_URL = "https://oauth2.googleapis.com/token"
        private const val USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo"
    }

    fun getToken(code: String): Optional<String> {
        val request = createTokenRequestEntity(code)
        val response = restTemplate.postForEntity(TOKEN_URL, request, String::class.java)

        return if (response.statusCode == HttpStatus.OK) {
            parseAccessToken(response.body)
        } else {
            throw UnauthenticatedException("Failed to obtain access token from Google")
        }
    }

    private fun createTokenRequestEntity(code: String): HttpEntity<MultiValueMap<String, String>> {
        val headers = createHeaders(MediaType.APPLICATION_FORM_URLENCODED)
        val params = createTokenParams(code)
        return HttpEntity(params, headers)
    }

    private fun createHeaders(mediaType: MediaType): HttpHeaders {
        return HttpHeaders().apply {
            contentType = mediaType
            accept = listOf(MediaType.APPLICATION_JSON)
        }
    }

    private fun createTokenParams(code: String): MultiValueMap<String, String> {
        return LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("client_id", CLIENT_ID)
            add("client_secret", CLIENT_SECRET)
            add("redirect_uri", REDIRECT_URI)
            add("code", code)
        }
    }

    private fun parseAccessToken(responseBody: String?): Optional<String> {
        return try {
            val jsonNode = objectMapper.readTree(responseBody)
            Optional.of(jsonNode.get("access_token").asText())
        } catch (e: Exception) {
            logger.error("Error parsing access token response", e)
            throw UnauthenticatedException("Failed to parse access token response")
        }
    }

    fun getMemberInfo(token: String): Optional<Member> {
        return try {
            val email = getEmail(token)
            memberRepository.findByEmail(email)
        } catch (e: Exception) {
            logger.error("Error retrieving member info", e)
            throw UnauthenticatedException("Failed to retrieve member info")
        }
    }

    fun registerMember(token: String): Member {
        val email = getEmail(token)
        return memberRepository.findByEmail(email)
            .orElseGet { memberRepository.save(createNewMember(email)) }
    }

    private fun createNewMember(email: String): Member {
        return Member(
            email = email
        )
    }

    fun getEmail(accessToken: String): String {
        val request = createUserInfoRequestEntity(accessToken)
        val response = restTemplate.exchange(USER_INFO_URL, HttpMethod.GET, request, String::class.java)

        return if (response.statusCode == HttpStatus.OK) {
            parseEmail(response.body)
        } else {
            throw UnauthenticatedException("Failed to retrieve user info from Google")
        }
    }

    private fun createUserInfoRequestEntity(accessToken: String): HttpEntity<Void> {
        val headers = createHeaders(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(accessToken)
        return HttpEntity(headers)
    }

    private fun parseEmail(responseBody: String?): String {
        return try {
            val jsonNode = objectMapper.readTree(responseBody)
            jsonNode.get("email").asText()
        } catch (e: Exception) {
            logger.error("Error parsing user info response", e)
            throw UnauthenticatedException("Failed to parse user info response")
        }
    }
}
