// features/tasks/data/TaskRepository.kt
package com.example.formulariokotlin.features.tasks.data

import com.example.formulariokotlin.core.api.ApiService
import com.example.formulariokotlin.features.tasks.data.models.Task

class TaskRepository(private val apiService: ApiService) {

    suspend fun getAllTasks(token: String): Result<List<Task>> {
        return apiService.getAllTasks(token)
    }

    suspend fun createTask(token: String, title: String, content: String): Result<Task> {
        return apiService.createTask(token, title, content)
    }

    suspend fun updateTask(token: String, taskId: Int, title: String, content: String): Result<Boolean> {
        return apiService.updateTask(token, taskId, title, content)
    }

    suspend fun deleteTask(token: String, taskId: Int): Result<Boolean> {
        return apiService.deleteTask(token, taskId)
    }
}
