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
                                        "Set up the development environment, install all dependencies, and configure build settings for both debug and release variants",
                                isCompleted = true,
                                createdAt =
                                        Instant.now()
                                                .minusSeconds(259200)
                                                .epochSecond, // 3 days ago
                                updatedAt =
                                        Instant.now().minusSeconds(172800).epochSecond // 2 days ago
                        ),
                        Task(
                                id = UUID.randomUUID().toString(),
                                title = "Design User Interface Mockups",
                                description =
                                        "Create comprehensive UI mockups for all screens including task list, detail view, and settings page. Focus on material design principles and accessibility",
                                isCompleted = true,
                                createdAt =
                                        Instant.now()
                                                .minusSeconds(172800)
                                                .epochSecond, // 2 days ago
                                updatedAt =
                                        Instant.now().minusSeconds(86400).epochSecond // 1 day ago
                        ),
                        Task(
                                id = UUID.randomUUID().toString(),
                                title = "Implement Task List UI with Compose",
                                description =
                                        "Create the main task list screen with Compose, including sorting options, filtering controls, and animations for a smooth user experience",
                                isCompleted = false,
                                createdAt =
                                        Instant.now().minusSeconds(86400).epochSecond, // 1 day ago
                                updatedAt =
                                        Instant.now()
                                                .minusSeconds(43200)
                                                .epochSecond // 12 hours ago
                        ),
                        Task(
                                id = UUID.randomUUID().toString(),
                                title = "Add Task Creation Functionality",
                                description =
                                        "Implement task creation with validation, error handling, and proper integration with the repository layer",
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
                                title = "Implement Task Detail Screen",
                                description =
                                        "Create a detailed view for tasks with editing capabilities, history tracking, and related task management",
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
                                title = "Add Dark Mode Support",
                                description =
                                        "Implement full dark mode theming that respects system settings and allows manual override in app settings",
                                isCompleted = false,
                                createdAt =
                                        Instant.now()
                                                .minusSeconds(10800)
                                                .epochSecond, // 3 hours ago
                                updatedAt =
                                        Instant.now().minusSeconds(3600).epochSecond // 1 hour ago
                        ),
                        Task(
                                id = UUID.randomUUID().toString(),
                                title = "Write Unit and Integration Tests",
                                description =
                                        "Add comprehensive test coverage for all core functionality including repositories, view models, and use cases",
                                isCompleted = false,
                                createdAt =
                                        Instant.now().minusSeconds(3600).epochSecond, // 1 hour ago
                                updatedAt = Instant.now().epochSecond // Now
                        ),
                        Task(
                                id = UUID.randomUUID().toString(),
                                title = "Set Up CI/CD Pipeline",
                                description =
                                        "Configure automated build, test, and deployment pipeline using GitHub Actions and Firebase App Distribution",
                                isCompleted = false,
                                createdAt =
                                        Instant.now().minusSeconds(7200).epochSecond, // 2 hours ago
                                updatedAt =
                                        Instant.now()
                                                .minusSeconds(5400)
                                                .epochSecond // 1.5 hours ago
                        ),
                        Task(
                                id = UUID.randomUUID().toString(),
                                title = "Create App Documentation",
                                description =
                                        "Write comprehensive user and developer documentation including setup instructions, architecture overview, and troubleshooting guide",
                                isCompleted = false,
                                createdAt =
                                        Instant.now()
                                                .minusSeconds(14400)
                                                .epochSecond, // 4 hours ago
                                updatedAt =
                                        Instant.now().minusSeconds(7200).epochSecond // 2 hours ago
                        )
                )
}
