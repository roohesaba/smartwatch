package com.example.wssm.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wssm.health.*
import com.example.wssm.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.example.wssm.data.UserSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HealthViewModel = viewModel()) {
    val context = LocalContext.current
    val healthData by viewModel.uiState
    val isMonitoringActive by viewModel.isMonitoringActive
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    var showHistory by remember { mutableStateOf(false) }
    var selectedMetric by remember { mutableStateOf("") }
    var historyList by remember { mutableStateOf<List<String>>(emptyList()) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WSSM MONITOR", fontWeight = FontWeight.ExtraBold, color = BrandDeepBlue) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 4.dp)) {
                        IconButton(
                            onClick = { viewModel.startFrequentSync() },
                            enabled = !viewModel.isSyncing.value
                        ) {
                            if (viewModel.isSyncing.value) {
                                // FIX 1: Size moved inside Modifier
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = BrandDeepBlue
                                )
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = "Manual Load", tint = BrandDeepBlue)
                            }
                        }

                        Switch(
                            checked = isMonitoringActive,
                            onCheckedChange = {
                                viewModel.isMonitoringActive.value = it
                                if(it) viewModel.startFrequentSync() else viewModel.stopFrequentSync()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BrandDeepBlue
                            )
                        )
                    }
                }
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {

            if (isMonitoringActive && healthData.alertLevel > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if(healthData.alertLevel >= 3) BrandEmergencyRed else BrandSoftYellow
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = healthData.alertMessage,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Text("Emergency QR", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = BrandDeepBlue)

            Spacer(Modifier.height(16.dp))

            GenerateQRCode(
                    text =
                    """
                WSSM EMERGENCY PROFILE
                
                Name: ${UserSession.name}
                Age: ${UserSession.age}
                
                Blood Group: ${UserSession.bloodGroup}
                
                Gender: ${UserSession.gender}
                
                Guardian Number: ${UserSession.guardianNumber}
                
                Medical Conditions:
                ${UserSession.medicalConditions}
                
                Address:
                ${UserSession.address}
                """.trimIndent()
                            )

            Spacer(Modifier.height(28.dp))

            Text("Health Overview", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = BrandDeepBlue)
            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.heightIn(max = 320.dp)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    item {
                        val ts = healthData.heartRateHistory.firstOrNull()?.timestamp?.let {
                            timeFormatter.format(Date.from(it))
                        } ?: "Updating..."
                        HealthCard("Heart Rate", "${healthData.heartRate} bpm", ts, BrandLightBlue.copy(0.2f), viewModel.isSyncing.value) {
                            selectedMetric = "Heart Rate"
                            historyList = healthData.heartRateHistory.take(10).map { "${it.value} bpm" }
                            showHistory = true
                        }
                    }
                    item {
                        val ts = healthData.oxygenHistory.firstOrNull()?.timestamp?.let {
                            timeFormatter.format(Date.from(it))
                        } ?: "Updating..."
                        HealthCard("Oxygen", healthData.oxygen, ts, BrandSuccessGreen.copy(0.2f), viewModel.isSyncing.value) {
                            selectedMetric = "Oxygen (SpO2)"
                            historyList = healthData.oxygenHistory.take(10).map { it.value }
                            showHistory = true
                        }
                    }
                    item {
                        HealthCard("Steps", healthData.steps, "Today", BrandEmergencyRed.copy(0.1f), viewModel.isSyncing.value) {
                            selectedMetric = "Steps"
                            historyList = listOf("Current: ${healthData.steps}")
                            showHistory = true
                        }
                    }
                    item {
                        HealthCard("Status", if(healthData.alertLevel == 0) "Normal" else "Issue", "Triage", BrandCreme, viewModel.isSyncing.value) {
                            selectedMetric = "Triage Status"
                            historyList = listOf("Status: ${if(healthData.alertLevel == 0) "Normal" else "Alert"}")
                            showHistory = true
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            Text("Emergency Quick-Dial", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = BrandDeepBlue)
            Spacer(Modifier.height(12.dp))

            EmergencyActionCard("Police", "100", Color(0xFFF5F5F5)) {
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:100")))
            }
            Spacer(Modifier.height(8.dp))
            EmergencyActionCard("Ambulance", "102", BrandSuccessGreen.copy(0.15f)) {
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:102")))
            }
            Spacer(Modifier.height(8.dp))
            EmergencyActionCard("Guardian", healthData.guardianNumber ?: "Not Set", BrandDeepBlue, textColor = Color.White) {
                navController.navigate("guardian")
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { /* Panic SMS logic */ },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandEmergencyRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("TRIGGER EMERGENCY ALERT", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 16.sp)
            }
        }

        if (showHistory) {
            ModalBottomSheet(
                onDismissRequest = { showHistory = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
                    Text("$selectedMetric History", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = BrandDeepBlue)
                    Spacer(Modifier.height(16.dp))
                    historyList.forEach { item ->
                        ListItem(headlineContent = { Text(item, fontWeight = FontWeight.Bold) })
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun HealthCard(label: String, value: String, subtext: String, bgColor: Color, isSyncing: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(125.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (isSyncing) {
                // FIX 2: Size moved inside Modifier
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp).align(Alignment.TopEnd),
                    strokeWidth = 3.dp,
                    color = BrandDeepBlue
                )
            }
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandDeepBlue.copy(0.6f))
                Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = BrandDeepBlue)
                Text(subtext, fontSize = 10.sp, color = BrandDeepBlue.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun EmergencyActionCard(name: String, number: String, bgColor: Color, textColor: Color = BrandDeepBlue, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(70.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = textColor)
                Text(number, fontSize = 14.sp, color = textColor.copy(alpha = 0.7f))
            }
            Icon(Icons.Default.Phone, contentDescription = null, tint = textColor)
        }
    }
}

@Composable
fun GenerateQRCode(text: String) {

    val bitmap = remember(text) {

        val size = 512

        val bits = QRCodeWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            size,
            size
        )

        Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).apply {

            for (x in 0 until size) {
                for (y in 0 until size) {

                    setPixel(
                        x,
                        y,
                        if (bits[x, y])
                            android.graphics.Color.BLACK
                        else
                            android.graphics.Color.WHITE
                    )
                }
            }
        }
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "QR Code",
        modifier = Modifier.size(190.dp)
    )
}

