package com.example.studysample.core.domain.dailytask

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DailyTaskRepository : JpaRepository<DailyTask, Long>
