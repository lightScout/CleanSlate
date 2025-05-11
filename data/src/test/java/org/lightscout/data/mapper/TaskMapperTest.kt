package org.lightscout.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Test
import org.lightscout.data.dto.TaskDto
import org.lightscout.domain.model.Task

class TaskMapperTest {

    @Test
    fun `Given a TaskDto, When mapped to domain model, Then all fields are correctly mapped`() {
        // Given
        val taskDto =
                TaskDto(
                        id = "1",
                        title = "Test Task",
                        description = "Test Description",
                        isCompleted = true,
                        createdAt = 1234567890,
                        updatedAt = 1234567890
                )

        // When
        val task = taskDto.toDomain()

        // Then
        assertEquals(taskDto.id, task.id)
        assertEquals(taskDto.title, task.title)
        assertEquals(taskDto.description, task.description)
        assertEquals(taskDto.isCompleted, task.isCompleted)
        assertEquals(taskDto.createdAt, task.createdAt)
        assertEquals(taskDto.updatedAt, task.updatedAt)
    }

    @Test
    fun `Given a domain Task, When mapped to DTO, Then all fields are correctly mapped`() {
        // Given
        val task =
                Task(
                        id = "1",
                        title = "Test Task",
                        description = "Test Description",
                        isCompleted = true,
                        createdAt = 1234567890,
                        updatedAt = 1234567890
                )

        // When
        val taskDto = task.toDto()

        // Then
        assertEquals(task.id, taskDto.id)
        assertEquals(task.title, taskDto.title)
        assertEquals(task.description, taskDto.description)
        assertEquals(task.isCompleted, taskDto.isCompleted)
        assertEquals(task.createdAt, taskDto.createdAt)
        assertEquals(task.updatedAt, taskDto.updatedAt)
    }
}
