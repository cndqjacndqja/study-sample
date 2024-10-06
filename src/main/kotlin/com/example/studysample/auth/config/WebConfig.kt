package com.example.studysample.auth.config

import com.example.studysample.auth.presentation.AuthInterceptor
import com.example.studysample.auth.presentation.AuthenticationArgumentResolver
import com.example.studysample.auth.service.JjwtProviderService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.http.HttpHeaders
import org.springframework.web.method.support.HandlerMethodArgumentResolver

@Configuration
class WebConfig(private val jjwtProviderService: JjwtProviderService) : WebMvcConfigurer {

    companion object {
        const val ALLOWED_METHOD_NAMES = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH"
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(AuthInterceptor(jjwtProviderService))
            .addPathPatterns("/api/**")
            .excludePathPatterns("/actuator/prometheus")
            .excludePathPatterns("/api/v1/query")
            .excludePathPatterns("/api/auth/**")
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods(*ALLOWED_METHOD_NAMES.split(",").toTypedArray())
            .exposedHeaders(HttpHeaders.LOCATION)
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authenticationArgumentResolver())
    }

    @Bean
    fun authenticationArgumentResolver(): AuthenticationArgumentResolver {
        return AuthenticationArgumentResolver(jjwtProviderService)
    }
}
