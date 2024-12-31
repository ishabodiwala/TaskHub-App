package com.example.taskhub

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.taskhub.model.Category
import com.example.taskhub.model.Task
import com.example.taskhub.viewModel.CategoryViewModel
import com.example.taskhub.viewModel.TaskViewModel
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun NewTask(navController: NavHostController, categoryId: Int, taskId: Int?) {
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
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(existingTask, category) {
        existingTask?.let { task ->
            title.value = task.title
            description.value = task.description
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
            // Header Image
            Image(
                painter = painterResource(R.drawable.new_task_illustration),
                contentDescription = "Task Header Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )

            // Content Container
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (taskId == null || taskId == -1) "Create New Task" else "Update Task",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

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

                Spacer(modifier = Modifier.height(24.dp))

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
                        if (isError) {
                            isError = false
                            errorMessage = ""
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
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true
                )


                Spacer(modifier = Modifier.height(24.dp))

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

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        when {
                            title.value.trim().isEmpty() -> {
                                isError = true
                                errorMessage = "Title cannot be empty"
                            }
                            else -> {
                                val task = if (taskId != null && taskId != -1) {
                                    existingTask?.copy(
                                        title = title.value.trim(),
                                        description = description.value.trim(),
                                        categoryId = selectedCategory.value?.catId ?: categoryId
                                    )
                                } else {
                                    Task(
                                        id = 0,
                                        title = title.value.trim(),
                                        description = description.value.trim(),
                                        categoryId = selectedCategory.value?.catId ?: categoryId
                                    )
                                }

                                task?.let {
                                    if (taskId != null && taskId != -1) {
                                        taskViewModel.updateTask(it)
                                    } else {
                                        taskViewModel.insertTask(it)
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, onBackClick : () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = title
            )
        },
        navigationIcon = {
            Icon(imageVector = Icons.Filled.ArrowBack,
                contentDescription = "back icon",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        onBackClick()
                    })
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}


