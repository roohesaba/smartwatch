package com.example.wssm.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wssm.health.HealthViewModel
// Fixes "Unresolved reference" errors
import com.example.wssm.ui.theme.* @Composable
fun NavGraph() {
    val navController = rememberNavController()
    val healthViewModel: HealthViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            if (currentDestination?.route != "login") {
                NavigationBar(
                    containerColor = BrandDeepBlue,
                    contentColor = Color.White
                ) {
                    val items = listOf(
                        Triple("home", "Home", Icons.Default.Home),
                        Triple("location", "Location", Icons.Default.LocationOn),
                        Triple("guardian", "Guardians", Icons.Default.Person)
                    )

                    items.forEach { (route, label, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                            colors = NavigationBarItemDefaults.colors(
                                // FIX: Change selected color to WHITE
                                selectedIconColor = BrandDeepBlue,
                                selectedTextColor = Color.White,
                                indicatorColor = Color.White,
                                unselectedIconColor = Color.White.copy(0.6f),
                                unselectedTextColor = Color.White.copy(0.6f)
                            ),
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(navController)
            }

            composable("home") {
                HomeScreen(navController, healthViewModel)
            }

            composable("location") {
                LocationScreen(navController)
            }

            composable("guardian") {
                // FIX: Passing parameters correctly (navController, then viewModel)
                GuardianScreen(navController, healthViewModel)
            }
        }
    }
}