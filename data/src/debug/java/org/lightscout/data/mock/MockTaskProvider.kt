package org.lightscout.data.mock

import java.time.Instant
import java.util.UUID
import org.lightscout.domain.model.Task

object MockTaskProvider {
        fun getMockTasks(): List<Task> =
                listOf(
                        Task(
                                id = UUID.randomUUID().toString(),
                                title = "Complete Project Setup",
                                description =
                                        "Set up the development environment and project structure",
                                isCompleted = true,
                                createdAt =
                                        Instant.now().minusSeconds(86400).epochSecond, // 1 day ago
                                updatedAt =
                                        Instant.now()
                                                .minusSeconds(43200)
                                                .epochSecond // 12 hours ago
                        ),
                        Task(
                                id = UUID.randomUUID().toString(),
                                title = "Implement Task List UI",
                                description = "Create the main task list screen with Compose",
                                isCompleted = false,
                                createdAt =
                                        Instant.now()
                                                .minusSeconds(43200)
                                                .epochSecond, // 12 hours ago
                                updatedAt =
                                        Instant.now().minusSeconds(21600).epochSecond // 6 hours ago
                        ),
                        Task(
                                id = UUID.randomUUID().toString(),
                                title = "Add Task Creation",
                                description = "Implement the task creation functionality",
                                isCompleted = false,
                                createdAt =
                                        Instant.now()
                                                .minusSeconds(21600)
                                                .epochSecond, // 6 hours ago
                                updatedAt =
                                        Instant.now().minusSeconds(10800).epochSecond // 3 hours ago
                        ),
                        Task(
                                id = UUID.randomUUID().toString(),
                                title = "Write Unit Tests",
                                description = "Add unit tests for the task management features",
                                isCompleted = false,
                                createdAt =
                                        Instant.now()
                                                .minusSeconds(10800)
                                                .epochSecond, // 3 hours ago
                                updatedAt = Instant.now().epochSecond
                        )
                )
}
