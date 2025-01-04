package com.example.taskhub

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskhub.compose.DisplayCategory
import com.example.taskhub.compose.DisplayTasks
import com.example.taskhub.compose.NewTask
import com.example.taskhub.ui.theme.TaskHubTheme
import com.example.taskhub.viewModel.TaskViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
        } else {
            // Permission denied
            Toast.makeText(
                this,
                "Notification permission is required for reminders",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val taskId = intent.getIntExtra("taskId", -1)

        setContent {
            TaskHubTheme {
                NavigatorController(taskId)
            }
        }
    }
}


@Composable
fun NavigatorController(taskId: Int) {

    // can't declare here.
    // error : java.lang.IllegalStateException:
    // Cannot access database on the main thread since it may potentially lock the UI for a long period
    // of time.
    // introduce hilt dependency injection

//    val context = LocalContext.current
//    val taskHubDataBase = TaskHubDataBase.getDatabase(context)
//    val categoryRepository = CategoryRepository(taskHubDataBase.categoryDao())
//    val taskRepository = TaskRepository(taskHubDataBase.taskDao())
//    val categoryViewModel = CategoryViewModel(categoryRepository)
//    val taskViewModel = TaskViewModel(taskRepository)

    val navController = rememberNavController()

    val taskViewModel: TaskViewModel = hiltViewModel()

    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (taskId != -1) {
            try {
                taskViewModel.getTaskByTaskId(taskId).collect { task ->
                    task.let {
                        startDestination = "Display_Tasks/${task.categoryId}"
                    }
                }
            } catch (e: Exception) {
                startDestination = "Display_Categories"
            }
        } else {
            startDestination = "Display_Categories"
        }
    }

    startDestination?.let { startDest ->
        NavHost(navController = navController, startDestination = startDest) {
            composable("Display_Categories") {
                DisplayCategory(navController)
            }
            composable("Display_Tasks/{categoryId}",
                arguments =
                listOf(
                    navArgument(
                        "categoryId"
                    ) {
                        type = NavType.IntType
                    }
                )
            ) { navBackStackEntry ->
                val categoryId = navBackStackEntry.arguments?.getInt("categoryId")
                categoryId?.let {
                    DisplayTasks(navController, categoryId)
                }
            }
            composable("New_Task/{categoryId}/{taskId}", arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.IntType
                },
                navArgument("taskId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )) { navBackStackEntry ->
                val categoryId = navBackStackEntry.arguments?.getInt("categoryId")
                val taskId = navBackStackEntry.arguments?.getInt("taskId")?:  -1
                categoryId?.let {
                    NewTask(navController, categoryId, taskId)
                }
            }
        }
    }


}