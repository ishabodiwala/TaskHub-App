package com.example.taskhub.compose

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.taskhub.model.Category
import com.example.taskhub.model.Task
import com.example.taskhub.viewModel.CategoryViewModel
import com.example.taskhub.viewModel.TaskViewModel
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.Manifest
import com.example.taskhub.R

@Composable
fun NewTask(
    navController: NavHostController,
    categoryId: Int,
    taskId: Int?
) {
    val categoryViewModel: CategoryViewModel = hiltViewModel()
    val taskViewModel: TaskViewModel = hiltViewModel()

    val allCategories by categoryViewModel.allCategories.collectAsState(initial = emptyList())
    val category by categoryViewModel.getCategoryById(categoryId).collectAsState(initial = null)
    val existingTask by taskViewModel.getTaskByTaskId(taskId ?: -1).collectAsState(initial = null)

    val selectedCategory = remember { mutableStateOf<Category?>(null) }
    val dropDownMenuStatus = remember { mutableStateOf(false) }
    var boxWidth by remember { mutableIntStateOf(0) }

    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf<Long?>(null) }

    var isTitleError by remember { mutableStateOf(false) }
    var titleErrorMessage by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var isReminderError by remember { mutableStateOf(false) }
    var reminderErrorMessage by remember { mutableStateOf("") }


    LaunchedEffect(existingTask, category) {
        existingTask?.let { task ->
            title.value = task.title
            description.value = task.description
            selectedDateTime = task.reminderTime
        }
        category?.let {
            selectedCategory.value = it
        }
    }

    Scaffold(
        topBar = {
            MyTopBar(
                title = if (taskId == null || taskId == -1) "New Task" else "Edit Task",
                subtitle = selectedCategory.value?.categoryName ?: "No Category",
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(R.drawable.new_task_illustration),
                contentDescription = "Task Header Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                // Category Selector
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { dropDownMenuStatus.value = true }
                        .onGloballyPositioned { coordinates ->
                            boxWidth = coordinates.size.width
                        }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedCategory.value?.categoryName ?: "Select Category",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Dropdown"
                        )
                    }

                    DropdownMenu(
                        expanded = dropDownMenuStatus.value,
                        onDismissRequest = { dropDownMenuStatus.value = false },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { boxWidth.toDp() })
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        allCategories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        category.categoryName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    selectedCategory.value = category
                                    dropDownMenuStatus.value = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                //Title Input
                Text(
                    text = "Title",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = title.value,
                    onValueChange = {
                        title.value = it
                        if (isTitleError) {
                            isTitleError = false
                            titleErrorMessage = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Enter task title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                    ),
                    isError = isTitleError,
                    supportingText = {
                        if (isTitleError) {
                            Text(
                                text = titleErrorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Description Input
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = description.value,
                    onValueChange = { description.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Enter task description") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Function to check if we have notification permission
                fun checkNotificationPermission(): Boolean {
                    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    } else {
                        true
                    }
                }

                // Reminder Section
                Text(
                    text = "Reminder",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isReminderError)
                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                            else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showDatePicker = true }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedDateTime != null) {
                                SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                                    .format(Date(selectedDateTime!!))
                            } else "Set Reminder",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Set reminder"
                        )
                    }
                }

                if (isReminderError) {
                    Text(
                        text = reminderErrorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                if (showDatePicker) {
                    val datePickerDialog = DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            showDatePicker = false
                            showTimePicker = true
                            calendar.set(Calendar.YEAR, year)
                            calendar.set(Calendar.MONTH, month)
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).apply {
                        // Disable past dates
                        datePicker.minDate = System.currentTimeMillis()
                    }

                    LaunchedEffect(Unit) {
                        datePickerDialog.show()
                    }
                }

                // Add the Time Picker

                if (showTimePicker) {
                    val currentTime = Calendar.getInstance()
                    currentTime.add(Calendar.MINUTE, 5)
                    val timePickerDialog = TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)

                            when {
                                !checkNotificationPermission() -> {
                                    isReminderError = true
                                    reminderErrorMessage = "Notification permission required"
                                }
                                else -> {
                                    isReminderError = false
                                    reminderErrorMessage = ""
                                    selectedDateTime = calendar.timeInMillis
                                }
                            }
                            showTimePicker = false
                        },
                        currentTime.get(Calendar.HOUR_OF_DAY),  // Use the time + 5 minutes
                        currentTime.get(Calendar.MINUTE),
                        false // 24 hour format
                    )

                    LaunchedEffect(Unit) {
                        timePickerDialog.show()
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        when {
                            title.value.trim().isEmpty() -> {
                                isTitleError = true
                                titleErrorMessage = "Title cannot be empty"
                            }

                            else -> {
                                val task = if (taskId != null && taskId != -1) {
                                    existingTask?.copy(
                                        title = title.value.trim(),
                                        description = description.value.trim(),
                                        categoryId = selectedCategory.value?.catId
                                            ?: categoryId,
                                        reminderTime = selectedDateTime
                                    )
                                } else {
                                    Task(
                                        id = 0,
                                        title = title.value.trim(),
                                        description = description.value.trim(),
                                        categoryId = selectedCategory.value?.catId
                                            ?: categoryId,
                                        reminderTime = selectedDateTime
                                    )
                                }

                                task?.let {
                                    if (taskId != null && taskId != -1) {
                                        // Update existing task
                                        if (selectedDateTime != null) {
                                            // Update with reminder
                                            taskViewModel.updateTaskWithReminder(
                                                it,
                                                selectedDateTime!!
                                            )
                                        } else {
                                            // Update without reminder
                                            taskViewModel.updateTask(it)
                                        }
                                    } else {
                                        // Create new task
                                        if (selectedDateTime != null) {
                                            // Create with reminder
                                            taskViewModel.addTaskWithReminder(
                                                it,
                                                selectedDateTime!!
                                            )
                                        } else {
                                            // Create without reminder
                                            taskViewModel.insertTask(it)
                                        }
                                    }
                                }

                                navController.navigate("Display_Tasks/${selectedCategory.value?.catId ?: categoryId}") {
                                    popUpTo("Display_Tasks/${selectedCategory.value?.catId ?: categoryId}") {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                ) {
                    Text(
                        text = if (taskId == null || taskId == -1) "Create Task" else "Update Task",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}




