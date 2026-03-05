package com.example.wssm.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.* // This includes the 'by' delegate imports
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wssm.health.HealthHelper
import com.example.wssm.health.HealthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HealthViewModel = viewModel()) {
    val context = LocalContext.current
    val healthHelper = remember { HealthHelper(context) }
    var isMonitoringActive by remember { mutableStateOf(true) }

    // FIX: Removed .collectAsState() because uiState is already a MutableState
    val healthData by viewModel.uiState

    val launcher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { viewModel.startFrequentSync() }

    LaunchedEffect(isMonitoringActive) {
        if (isMonitoringActive) {
            if (healthHelper.hasPermissions()) {
                viewModel.startFrequentSync()
            } else {
                launcher.launch(healthHelper.permissions)
            }
        } else {
            viewModel.stopFrequentSync()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Safety Dashboard", fontWeight = FontWeight.Bold) },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                        Text(if (isMonitoringActive) "LIVE" else "OFF", fontSize = 12.sp)
                        Switch(checked = isMonitoringActive, onCheckedChange = { isMonitoringActive = it })
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ensure healthData property names match exactly with your HealthData class
                item { HealthCard("Heart Rate", healthData.heartRate, Color(0xFFFFEBEE)) }
                item { HealthCard("Oxygen", healthData.oxygen, Color(0xFFE3F2FD)) }
                item { HealthCard("Steps", healthData.steps, Color(0xFFF1F8E9)) }
                item { HealthCard("Stress", healthData.stress, Color(0xFFFFF3E0)) }
            }

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = { navController.navigate("guardian") },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("MANAGE GUARDIANS")
            }
        }
    }
}

@Composable
fun HealthCard(label: String, value: String, bgColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().height(110.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}