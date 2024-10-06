package com.example.studysample.auth.presentation.dto

data class LoginResponse(
    val accessToken: String
) {
    constructor() : this("")
}
