package com.example.studysample.core.service.dto

import com.example.studysample.core.domain.dailytask.DailyTaskStatus

data class DailyTaskResponse(
    val dailyTaskId: Long?,
    val position: Int,
    val dailyTaskStatus: DailyTaskStatus
)
