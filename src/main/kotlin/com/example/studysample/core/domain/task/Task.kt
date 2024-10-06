package com.example.studysample.core.domain.task

import com.example.studysample.core.domain.common.BaseTimeEntity
import com.example.studysample.core.domain.dailytask.DailyTask
import com.example.studysample.core.domain.dailytask.DailyTaskStatus
import com.example.studysample.core.domain.member.Member
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "task")
class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    val id: Long? = null,

    @Column(name = "content")
    val content: String? = null,

    @Column(name = "memo")
    val memo: String? = null,

    @Column(name = "time")
    val time: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member? = null,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "task_id")
    val dailyTasks: List<DailyTask>? = null,

    @Column(name = "d_day")
    var dDay: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status")
    var taskStatus: TaskStatus? = null
) : BaseTimeEntity() {

    companion object {
        private const val D_DAY = 3
    }

    fun getTimeSinceStart(): Int = D_DAY - dDay

    fun updateDDay() {
        if (dDay > 0) {
            dDay--
        }
    }

    fun updateTaskStatusBasedOnDailyTasks() {
        taskStatus = if (hasUncheckedDailyTasks()) {
            TaskStatus.FAILED
        } else {
            TaskStatus.SUCCESS
        }
    }

    fun retryTask() {
        if (taskStatus == TaskStatus.FAILED) {
            taskStatus = TaskStatus.RETRY
        } else {
            throw IllegalArgumentException("실패한 태스크만 재시도할 수 있습니다.")
        }
    }

    fun updateDoneStatus() {
        if (taskStatus == TaskStatus.SUCCESS) {
            taskStatus = TaskStatus.DONE
        }
    }

    private fun hasUncheckedDailyTasks(): Boolean =
        dailyTasks?.any { it.dailyTaskStatus != DailyTaskStatus.CHECKED } ?: false
}
