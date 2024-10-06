package com.example.studysample.core.controller

import com.example.studysample.auth.domain.AuthenticationPrincipal
import com.example.studysample.common.BaseApiResponse
import com.example.studysample.core.service.TaskService
import com.example.studysample.core.service.dto.CreateTaskRequest
import com.example.studysample.core.service.dto.FindAllTaskHistoryResponses
import com.example.studysample.core.service.dto.FindAllTasksResponse
import com.example.studysample.core.service.dto.FindHasTaskResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    private val taskService: TaskService
) {

    @PostMapping
    fun create(@AuthenticationPrincipal memberId: Long, @RequestBody request: CreateTaskRequest): BaseApiResponse<Long> {
        val savedId = taskService.create(memberId, request)
        return BaseApiResponse.ok(savedId)
    }

    @GetMapping("/has-task")
    fun hasGoal(@AuthenticationPrincipal memberId: Long): BaseApiResponse<FindHasTaskResponse> {
        val hasGoal = taskService.hasTask(memberId)
        return BaseApiResponse.ok(FindHasTaskResponse(hasGoal))
    }

    @GetMapping
    fun findAll(@AuthenticationPrincipal memberId: Long): BaseApiResponse<FindAllTasksResponse> {
        return BaseApiResponse.ok(taskService.findAll(memberId))
    }

    @GetMapping("/history")
    fun getHistories(@AuthenticationPrincipal memberId: Long): BaseApiResponse<FindAllTaskHistoryResponses> {
        return BaseApiResponse.ok(taskService.getHistories(memberId))
    }

    @PostMapping("/{taskId}/update-status")
    fun checkFinishedStatusDailyTask(@AuthenticationPrincipal memberId: Long, @PathVariable taskId: Long): BaseApiResponse<Long> {
        taskService.updateTaskStatus(memberId, taskId)
        return BaseApiResponse.ok(null)
    }

    @PostMapping("/{taskId}/retry")
    fun retryTask(@AuthenticationPrincipal memberId: Long, @PathVariable taskId: Long): BaseApiResponse<Long> {
        taskService.retryTask(memberId, taskId)
        return BaseApiResponse.ok(null)
    }
}
