package org.lightscout.domain.model

data class Task(
        val id: String,
        val title: String,
        val description: String,
        val isCompleted: Boolean,
        val createdAt: Long,
        val updatedAt: Long
)
