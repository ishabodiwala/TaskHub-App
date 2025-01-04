package com.example.taskhub.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskhub.model.Task
import com.example.taskhub.repository.TaskRepository
import com.example.taskhub.utils.ReminderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val reminderManager: ReminderManager
) : ViewModel() {

    fun insertTask(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            reminderManager.cancelReminder(task.id)
            taskRepository.deleteTask(task)
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

    fun updateTaskWithReminder(task: Task, reminderTime: Long) {
        viewModelScope.launch {
            // Cancel existing reminder first
            reminderManager.cancelReminder(task.id)

            // Only schedule new reminder if it's in the future
            if (reminderTime > System.currentTimeMillis()) {
                taskRepository.updateTask(task)
                reminderManager.scheduleReminder(
                    taskId = task.id,
                    title = task.title,
                    description = task.description,
                    timeInMillis = reminderTime
                )
            } else {
                // Update task without reminder if time has passed
                taskRepository.updateTask(task.copy(reminderTime = null))
            }
        }
    }

    fun addTaskWithReminder(task: Task, reminderTime: Long) {
        viewModelScope.launch {
            // Only schedule reminder if it's in the future
            if (reminderTime > System.currentTimeMillis()) {
                val savedTask = taskRepository.insertTask(task)
                reminderManager.scheduleReminder(
                    taskId = savedTask.id,
                    title = savedTask.title,
                    description = savedTask.description,
                    timeInMillis = reminderTime
                )
            } else {
                // Insert task without reminder if time has passed
                taskRepository.insertTask(task.copy(reminderTime = null))
            }
        }
    }
}