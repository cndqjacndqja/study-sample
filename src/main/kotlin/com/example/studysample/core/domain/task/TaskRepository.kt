package com.example.studysample.core.domain.task

import com.example.studysample.core.domain.member.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long> {

    fun findAllByMemberAndTaskStatusIn(member: Member, taskStatus: List<TaskStatus>): List<Task>

    fun findAllByMember(member: Member): List<Task>
}
