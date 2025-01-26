// features/tasks/viewmodel/TaskViewModel.kt
package com.example.formulariokotlin.features.tasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.formulariokotlin.core.api.ApiService
import com.example.formulariokotlin.features.tasks.data.TaskRepository
import com.example.formulariokotlin.features.tasks.data.models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class TaskUIState {
    data object Idle : TaskUIState()
    data object Loading : TaskUIState()
    data class Error(val message: String) : TaskUIState()
    data class Success(val message: String) : TaskUIState()
}

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository(ApiService())

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _uiState = MutableStateFlow<TaskUIState>(TaskUIState.Idle)
    val uiState: StateFlow<TaskUIState> = _uiState

    fun loadTasks(token: String) {
        viewModelScope.launch {
            _uiState.value = TaskUIState.Loading
            repository.getAllTasks(token)
                .onSuccess { taskList ->
                    _tasks.value = taskList
                    _uiState.value = TaskUIState.Idle
                }
                .onFailure { ex ->
                    _uiState.value = TaskUIState.Error(ex.message ?: "Error al cargar tareas.")
                }
        }
    }

    fun createTask(token: String, title: String, content: String) {
        viewModelScope.launch {
            _uiState.value = TaskUIState.Loading
            repository.createTask(token, title, content)
                .onSuccess { newTask ->
                    _tasks.value = _tasks.value + newTask
                    _uiState.value = TaskUIState.Success("Tarea creada!")
                }
                .onFailure { ex ->
                    _uiState.value = TaskUIState.Error(ex.message ?: "Error al crear tarea.")
                }
        }
    }

    fun updateTask(token: String, taskId: Int, title: String, content: String) {
        viewModelScope.launch {
            _uiState.value = TaskUIState.Loading
            repository.updateTask(token, taskId, title, content)
                .onSuccess {
                    val updatedList = _tasks.value.map { t ->
                        if (t.id == taskId) t.copy(title = title, content = content) else t
                    }
                    _tasks.value = updatedList
                    _uiState.value = TaskUIState.Success("Tarea actualizada!")
                }
                .onFailure { ex ->
                    _uiState.value = TaskUIState.Error(ex.message ?: "Error al actualizar tarea.")
                }
        }
    }

    fun deleteTask(token: String, taskId: Int) {
        viewModelScope.launch {
            _uiState.value = TaskUIState.Loading
            repository.deleteTask(token, taskId)
                .onSuccess {
                    val updatedList = _tasks.value.filter { it.id != taskId }
                    _tasks.value = updatedList
                    _uiState.value = TaskUIState.Success("Tarea eliminada!")
                }
                .onFailure { ex ->
                    _uiState.value = TaskUIState.Error(ex.message ?: "Error al eliminar tarea.")
                }
        }
    }

    fun resetUIState() {
        _uiState.value = TaskUIState.Idle
    }
}
