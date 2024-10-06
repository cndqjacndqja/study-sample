package com.example.studysample.auth.service

import com.example.studysample.auth.exception.UnauthenticatedException
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JjwtProviderService(
    @Value("\${security.jwt.token.secret-key}") secretKey: String,
    @Value("\${security.jwt.token.expire-length}") private val expireTime: Long
) {

    private val key: Key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey))

    fun createToken(memberId: Long?): String {
        val now = Date()
        val expireDate = Date(now.time + expireTime)

        return Jwts.builder()
            .setSubject(memberId.toString())
            .setIssuedAt(now)
            .setExpiration(expireDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractSubject(token: String): String {
        return extractBody(token).subject
    }

    private fun extractBody(token: String): Claims {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: ExpiredJwtException) {
            val message = "만료된 토큰입니다. token = $token"
            throw UnauthenticatedException(message)
        } catch (e: JwtException) {
            throw UnauthenticatedException("올바르지 않은 토큰입니다.")
        }
    }
}
