package com.example.taskhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskhub.ui.theme.ToDoListAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.LightGray
                ) {
                    NavigatorController()
                }
            }
        }
    }
}

@Composable
fun NavigatorController() {

    // can't declare here.
    // error : java.lang.IllegalStateException: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
    // introduce hilt dependency injection

//    val context = LocalContext.current
//    val toDoDataBase = ToDoDataBase.getDatabase(context)
//    val categoryRepository = CategoryRepository(toDoDataBase.categoryDao())
//    val taskRepository = TaskRepository(toDoDataBase.taskDao())
//    val categoryViewModel = CategoryViewModel(categoryRepository)
//    val taskViewModel = TaskViewModel(taskRepository)

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Display_Categories") {
        composable("Display_Categories") {
            HomeScreen(navController)
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