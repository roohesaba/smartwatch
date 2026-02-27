package com.example.wssm.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GuardianScreen() {

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(20.dp)) {

        Text("Guardian Details", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })

        Spacer(Modifier.height(20.dp))

        Button(onClick = { }) {
            Text("Save")
        }
    }
}