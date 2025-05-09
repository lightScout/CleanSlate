package org.lightscout.data.mapper

import org.lightscout.data.dto.TaskDto
import org.lightscout.domain.model.Task

fun TaskDto.toDomain(): Task {
    return Task(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            createdAt = createdAt,
            updatedAt = updatedAt
    )
}

fun Task.toDto(): TaskDto {
    return TaskDto(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            createdAt = createdAt,
            updatedAt = updatedAt
    )
}
