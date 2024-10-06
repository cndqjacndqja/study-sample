package com.example.studysample.core.domain.task

enum class TaskStatus(val description: String) {
    DOING("진행 중"),
    SUCCESS("성공"),
    FAILED("실패"),
    RETRY("재시도"),
    DONE("완료")
}
