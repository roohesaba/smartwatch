package com.example.wssm.screens

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {
    Button(onClick = { navController.navigate("home") }) {
        Text("Login")
    }
}