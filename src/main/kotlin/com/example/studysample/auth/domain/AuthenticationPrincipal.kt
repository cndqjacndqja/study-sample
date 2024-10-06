package com.example.studysample.auth.domain

import java.lang.annotation.ElementType

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthenticationPrincipal
