package org.lightscout.data.dto

import com.google.gson.annotations.SerializedName

data class TaskDto(
        @SerializedName("id") val id: String,
        @SerializedName("title") val title: String,
        @SerializedName("description") val description: String,
        @SerializedName("completed") val isCompleted: Boolean,
        @SerializedName("created_at") val createdAt: Long,
        @SerializedName("updated_at") val updatedAt: Long
)
