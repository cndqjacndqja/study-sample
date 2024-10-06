package com.example.studysample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
@EnableAsync
class StudySampleApplication

fun main(args: Array<String>) {
    runApplication<StudySampleApplication>(*args)
}
