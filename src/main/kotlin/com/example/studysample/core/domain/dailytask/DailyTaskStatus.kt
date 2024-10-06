package com.example.studysample.core.domain.dailytask

enum class DailyTaskStatus {
    NOT_CHECKABLE,  // 체크할 수 없는 상태
    NOT_CHECKED,    // 체크할 수 있으나 아직 체크 안한 상태
    CHECK_FAILED,   // 체크를 못한 상태
    CHECKED         // 체크한 상태
}
