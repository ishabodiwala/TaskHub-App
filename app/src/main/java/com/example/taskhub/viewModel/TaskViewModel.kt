package com.example.taskhub.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskhub.model.Task
import com.example.taskhub.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val taskRepository: TaskRepository) : ViewModel() {

    fun insertTask(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    fun getTasksByCategory(categoryId: Int): Flow<List<Task>> {
        if (categoryId == -1)
            return taskRepository.getAllTasks()
        return taskRepository.getTasksByCategory(categoryId)
    }

    fun getTaskByTaskId(taskId: Int): Flow<Task> {
        return taskRepository.getTaskByTaskId(taskId)
    }

    val totalTaskCount: StateFlow<Int> = taskRepository.getTotalTaskCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun getTaskCountForCategory(categoryId: Int): Flow<Int> =
        taskRepository.getTaskCountForCategory(categoryId)
}