package com.example.studysample.core.domain.dailytask

import com.example.studysample.core.domain.common.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "daily_task")
class DailyTask(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_task_id")
    val id: Long? = null,

    @Column(name = "position")
    val position: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "daily_task_status")
    var dailyTaskStatus: DailyTaskStatus
) : BaseTimeEntity() {

    // JPA를 위한 no-arg 생성자
    protected constructor() : this(null, 0, DailyTaskStatus.NOT_CHECKED)

    fun updateDailyTaskStatus() {
        dailyTaskStatus = when (dailyTaskStatus) {
            DailyTaskStatus.NOT_CHECKED -> DailyTaskStatus.CHECKED
            DailyTaskStatus.CHECKED -> DailyTaskStatus.NOT_CHECKED
            else -> dailyTaskStatus
        }
    }

    fun updateCheckFailedStatus() {
        if (dailyTaskStatus == DailyTaskStatus.NOT_CHECKED) {
            dailyTaskStatus = DailyTaskStatus.CHECK_FAILED
        }
    }

    fun updateCheckableStatus() {
        if (dailyTaskStatus == DailyTaskStatus.NOT_CHECKABLE) {
            dailyTaskStatus = DailyTaskStatus.NOT_CHECKED
        }
    }

    companion object {
        fun create(position: Int, dailyTaskStatus: DailyTaskStatus): DailyTask {
            return DailyTask(position = position, dailyTaskStatus = dailyTaskStatus)
        }
    }
}
