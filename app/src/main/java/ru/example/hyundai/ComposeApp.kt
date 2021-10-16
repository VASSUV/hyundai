package ru.example.hyundai

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.example.hyundai.screens.*
import ru.example.hyundai.viewmodels.CarsViewModel
import ru.example.hyundai.viewmodels.MainViewModel
import ru.example.hyundai.viewmodels.ProfileViewModel
import ru.example.hyundai.viewmodels.ServiceViewModel

@Composable
fun ComposeApp(
    mainViewModel: MainViewModel = viewModel(),
    serviceViewModel: ServiceViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    carsViewModel: CarsViewModel = viewModel(),
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "main") {
        composable(route = "selectPhone") {
            SelectPhoneScreen()
        }
        composable(route = "main") {
            MainScreen(navController, mainViewModel, serviceViewModel, profileViewModel)
        }
        composable(route = "profile") {
            ProfileScreen()
        }
        composable(route = "colors") {
            ColorsScreen()
        }
        composable(route = "cars") {
            CarsScreen(carsViewModel)
        }
    }
}