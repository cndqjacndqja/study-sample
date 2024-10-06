package com.example.studysample.core.service.dto

import com.example.studysample.core.domain.task.TaskStatus

data class FindAllTaskResponse(
    val taskId: Long?,
    val content: String?,
    val memo: String?,
    val dDay: Int,
    val time: String,
    val taskStatus: TaskStatus?,
    val dailyTasks: List<DailyTaskResponse>
)
