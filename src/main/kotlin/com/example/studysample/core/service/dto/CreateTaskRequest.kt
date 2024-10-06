package com.example.studysample.core.service.dto

import java.time.LocalDateTime

data class CreateTaskRequest(
    var content: String? = null,
    var memo: String? = null,
    var time: LocalDateTime? = null
)
