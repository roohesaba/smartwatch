package com.example.wssm.screens

import android.Manifest
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
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
import androidx.navigation.NavController
import com.example.wssm.health.HealthViewModel
import com.example.wssm.models.Guardian
import com.example.wssm.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardianScreen(
    navController: NavController,
    viewModel: HealthViewModel
) {

    val context = LocalContext.current
    val healthData by viewModel.uiState
    val currentGuardians by viewModel.guardianList

    var inputName by remember { mutableStateOf("") }
    var inputPhone by remember { mutableStateOf("") }

    val smsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(
                context,
                "SMS Permission Denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "GUARDIANS",
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandDeepBlue
                    )
                },

                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BrandDeepBlue
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },

        containerColor = Color.White
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // =====================================================
            // PRIMARY EMERGENCY CONTACT
            // =====================================================

            Text(
                text = "Primary Emergency Contact",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = BrandDeepBlue
            )

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BrandDeepBlue
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                Color.White.copy(alpha = 0.15f),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Priority Guardian",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = healthData.guardianNumber ?: "Not Set",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }

                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // =====================================================
            // ADD CONTACT SECTION
            // =====================================================

            Text(
                text = "Add Trusted Contact",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = BrandDeepBlue
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Name Field
            OutlinedTextField(
                value = inputName,
                onValueChange = { inputName = it },

                label = {
                    Text("Guardian Name")
                },

                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                shape = RoundedCornerShape(8.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandDeepBlue,
                    focusedLabelColor = BrandDeepBlue,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Phone Field
            OutlinedTextField(
                value = inputPhone,

                onValueChange = { input ->
                    if (
                        input.length <= 10 &&
                        input.all { it.isDigit() }
                    ) {
                        inputPhone = input
                    }
                },

                label = {
                    Text("Phone Number")
                },

                prefix = {
                    Text(
                        "+91 ",
                        fontWeight = FontWeight.Bold,
                        color = BrandDeepBlue
                    )
                },

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),

                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                shape = RoundedCornerShape(8.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandDeepBlue,
                    focusedLabelColor = BrandDeepBlue,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Save Button
            Button(
                onClick = {

                    if (
                        inputName.isNotBlank() &&
                        inputPhone.length == 10
                    ) {

                        viewModel.addGuardianToList(
                            Guardian(
                                inputName,
                                "+91$inputPhone"
                            )
                        )

                        inputName = ""
                        inputPhone = ""
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),

                shape = RoundedCornerShape(8.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandDeepBlue
                )
            ) {

                Text(
                    text = "SAVE CONTACT",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // =====================================================
            // TRUSTED LIST
            // =====================================================

            Text(
                text = "Trusted Guardian List",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = BrandDeepBlue
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(currentGuardians) { guardian ->

                    val isPriority =
                        guardian.phone == healthData.guardianNumber

                    Card(
                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(10.dp),

                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFAFAFA)
                        ),

                        border = BorderStroke(
                            1.dp,
                            Color(0xFFEAEAEA)
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),

                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(
                                        if (isPriority)
                                            BrandSuccessGreen.copy(alpha = 0.2f)
                                        else
                                            Color(0xFFF0F0F0),

                                        RoundedCornerShape(8.dp)
                                    ),

                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    if (isPriority)
                                        Icons.Default.Star
                                    else
                                        Icons.Default.Person,

                                    contentDescription = null,

                                    tint =
                                        if (isPriority)
                                            BrandSuccessGreen
                                        else
                                            BrandDeepBlue
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = guardian.name,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandDeepBlue,
                                    fontSize = 15.sp
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = guardian.phone,
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )

                                if (isPriority) {

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "PRIMARY CONTACT",
                                        color = BrandSuccessGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // TEST SMS BUTTON
                            IconButton(
                                onClick = {

                                    if (
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.SEND_SMS
                                        ) == PackageManager.PERMISSION_GRANTED
                                    ) {

                                        val smsManager =
                                            context.getSystemService(
                                                SmsManager::class.java
                                            )

                                        smsManager.sendTextMessage(
                                            guardian.phone,
                                            null,
                                            "WSSM Emergency Connection Test.",
                                            null,
                                            null
                                        )

                                        Toast.makeText(
                                            context,
                                            "Test SMS Sent",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {

                                        smsLauncher.launch(
                                            Manifest.permission.SEND_SMS
                                        )
                                    }
                                }
                            ) {

                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send Test SMS",
                                    tint = BrandDeepBlue
                                )
                            }

                            // DELETE BUTTON
                            IconButton(
                                onClick = {
                                    viewModel.deleteGuardian(guardian)
                                }
                            ) {

                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Guardian",
                                    tint = BrandEmergencyRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}