package org.lightscout.data.remote

import org.lightscout.data.dto.TaskDto
import retrofit2.http.*

interface TaskApiService {
    @GET("tasks") suspend fun getTasks(): List<TaskDto>

    @GET("tasks/{id}") suspend fun getTaskById(@Path("id") id: String): TaskDto

    @POST("tasks") suspend fun createTask(@Body task: TaskDto): TaskDto

    @PUT("tasks/{id}") suspend fun updateTask(@Path("id") id: String, @Body task: TaskDto): TaskDto

    @DELETE("tasks/{id}") suspend fun deleteTask(@Path("id") id: String)
}
