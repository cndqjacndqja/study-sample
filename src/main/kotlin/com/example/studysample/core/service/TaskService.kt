package com.example.studysample.core.service

import com.example.studysample.auth.exception.UnauthenticatedException
import com.example.studysample.core.domain.dailytask.DailyTask
import com.example.studysample.core.domain.dailytask.DailyTaskRepository
import com.example.studysample.core.domain.dailytask.DailyTaskStatus
import com.example.studysample.core.domain.member.Member
import com.example.studysample.core.domain.member.MemberRepository
import com.example.studysample.core.domain.task.Task
import com.example.studysample.core.domain.task.TaskRepository
import com.example.studysample.core.domain.task.TaskStatus
import com.example.studysample.core.service.dto.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream

@Service
@Transactional(readOnly = true)
class TaskService(
    private val taskRepository: TaskRepository,
    private val dailyTaskRepository: DailyTaskRepository,
    private val memberRepository: MemberRepository
) {

    @Transactional
    fun create(memberId: Long, request: CreateTaskRequest): Long? {
        val member = getMember(memberId)
        val dailyTasks = getDailyTasks()
        val task = Task(
            content = request.content,
            memo = request.memo,
            time = request.time,
            member = member,
            dailyTasks = dailyTasks,
            dDay = 3,
            taskStatus = TaskStatus.DOING
        )
        dailyTaskRepository.saveAll(dailyTasks)
        return taskRepository.save(task).id
    }

    fun hasTask(memberId: Long): Boolean {
        val member = getMember(memberId)
        val tasks = taskRepository.findAllByMemberAndTaskStatusIn(member, listOf(TaskStatus.DOING, TaskStatus.SUCCESS))
        return tasks.isNotEmpty()
    }

    @Transactional
    fun updateTaskStatus(memberId: Long, taskId: Long) {
        val member = getMember(memberId)
        val task = getTask(taskId)
        if (task.member?.id != member.id) {
            throw IllegalArgumentException("해당 태스크에 대한 권한이 없습니다.")
        }
        val taskDailyTasks = task.dailyTasks
        taskDailyTasks?.sortedBy { it.position }
        val todayDailyTask = taskDailyTasks?.get(task.getTimeSinceStart())
        todayDailyTask?.updateDailyTaskStatus()
    }

    fun findAll(memberId: Long): FindAllTasksResponse {
        val member = getMember(memberId)
        val tasks = taskRepository.findAllByMemberAndTaskStatusIn(member, listOf(TaskStatus.DOING, TaskStatus.SUCCESS))
        val taskResponses = tasks.map { task ->
            val dailyTaskResponses = getDailyTaskResponses(task)
            FindAllTaskResponse(
                task.id,
                task.content,
                task.memo,
                task.dDay,
                task.time.toString(),
                task.taskStatus,
                dailyTaskResponses
            )
        }
        return FindAllTasksResponse(taskResponses)
    }

    fun getHistories(memberId: Long): FindAllTaskHistoryResponses {
        val member = getMember(memberId)
        val tasks = taskRepository.findAllByMember(member).filter {
            it.taskStatus != TaskStatus.DOING && it.taskStatus != TaskStatus.RETRY
        }
        val taskResponses = tasks.map { task ->
            val dailyTaskResponses = getDailyTaskResponses(task)
            FindAllTaskHistoryResponse(
                task.id,
                task.content,
                task.memo,
                task.dDay,
                task.time.toString(),
                task.taskStatus,
                dailyTaskResponses
            )
        }
        return FindAllTaskHistoryResponses(taskResponses)
    }

    @Transactional
    fun retryTask(memberId: Long, taskId: Long) {
        val member = getMember(memberId)
        val task = getTask(taskId)
        task.retryTask()
        if (task.member?.id != member.id) {
            throw IllegalArgumentException("해당 태스크에 대한 권한이 없습니다.")
        }
        val dailyTasks = getDailyTasks()
        val newTask = Task(
            content = task.content,
            memo = task.memo,
            time = task.time,
            member = member,
            dailyTasks = dailyTasks,
            dDay = 3,
            taskStatus = TaskStatus.DOING
        )
        dailyTaskRepository.saveAll(dailyTasks)
        taskRepository.save(newTask)
    }

    private fun getDailyTasks(): List<DailyTask> {
        return IntStream.range(0, 3)
            .mapToObj { i ->
                DailyTask(
                    position = i,
                    dailyTaskStatus = if (i == 0) DailyTaskStatus.NOT_CHECKED else DailyTaskStatus.NOT_CHECKABLE
                )
            }
            .collect(Collectors.toList())
    }

    private fun getDailyTaskResponses(task: Task): List<DailyTaskResponse> {
        val dailyTasks = task.dailyTasks
        return dailyTasks?.map { dailyTask ->
            DailyTaskResponse(
                dailyTaskId = dailyTask.id,
                position = dailyTask.position,
                dailyTaskStatus = dailyTask.dailyTaskStatus
            )
        } ?: emptyList()
    }

    private fun getTask(taskId: Long): Task {
        return taskRepository.findById(taskId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 태스크입니다.") }
    }

    private fun getMember(memberId: Long): Member {
        return memberRepository.findById(memberId)
            .orElseThrow { UnauthenticatedException("존재하지 않는 회원입니다.") }
    }
}

