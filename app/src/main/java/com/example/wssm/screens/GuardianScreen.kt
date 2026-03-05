package com.example.wssm.screens

import android.Manifest
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.wssm.models.Guardian

@Composable
fun GuardianScreen() {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") } // We store only the 10 digits here
    val guardianList = remember { mutableStateListOf<Guardian>() }

    var hasSmsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasSmsPermission = isGranted }
    )

    LaunchedEffect(Unit) {
        if (!hasSmsPermission) {
            launcher.launch(Manifest.permission.SEND_SMS)
        }
    }

    // Helper to send the "Bachao" SMS
    fun sendAlertSms(rawPhone: String) {
        if (hasSmsPermission) {
            try {
                val smsManager: SmsManager = context.getSystemService(SmsManager::class.java)
                // Ensure the number has the +91 prefix for the actual transmission
                val fullNumber = if (rawPhone.startsWith("+91")) rawPhone else "+91$rawPhone"
                val message = "Bachao"

                smsManager.sendTextMessage(fullNumber, null, message, null, null)
                Toast.makeText(context, "Alert Sent to $fullNumber", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        } else {
            launcher.launch(Manifest.permission.SEND_SMS)
        }
    }

    Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
        Text("Guardian Details", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { input ->
                // Filter: Allow only digits and limit to 10 characters
                if (input.all { it.isDigit() } && input.length <= 10) {
                    phone = input
                }
            },
            label = { Text("Phone Number") },
            // Visual constant prefix
            prefix = { Text("+91 ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text("Enter 10 digit number") }
        )

        Spacer(Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && phone.length == 10,
            onClick = {
                // Save with the prefix included so it's ready to use
                guardianList.add(0, Guardian(name, "+91$phone"))
                name = ""
                phone = ""
            }
        ) {
            Text("Save Guardian")
        }

        Spacer(Modifier.height(30.dp))
        Text("Registered Guardians", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(guardianList) { guardian ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = guardian.name, fontWeight = FontWeight.Bold)
                            Text(text = guardian.phone, style = MaterialTheme.typography.bodyMedium)
                        }

                        IconButton(onClick = { sendAlertSms(guardian.phone) }) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send SMS",
                                tint = if (hasSmsPermission) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}