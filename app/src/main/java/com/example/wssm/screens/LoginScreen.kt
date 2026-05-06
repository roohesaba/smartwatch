package com.example.wssm.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wssm.ui.theme.*
import com.example.wssm.data.UserSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {

    // STEP 1 DATA
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("18") }

    // STEP 2 DATA
    var bloodGroup by remember { mutableStateOf("") }
    var guardianNumber by remember { mutableStateOf("") }
    var medicalConditions by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // PAGE CONTROL
    var currentPage by remember { mutableStateOf(1) }

    // DROPDOWNS
    var ageExpanded by remember { mutableStateOf(false) }
    var bloodExpanded by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }

    val ageList = (1..100).map { it.toString() }

    val bloodGroups = listOf(
        "A+",
        "A-",
        "B+",
        "B-",
        "AB+",
        "AB-",
        "O+",
        "O-"
    )

    val genderList = listOf(
        "Male",
        "Female",
        "Other"
    )

    Scaffold(
        containerColor = Color.White
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "WSSM Safety",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandDeepBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Emergency Health Registration",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // =========================
            // PAGE 1
            // =========================
            if (currentPage == 1) {

                // NAME FIELD
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandDeepBlue,
                        focusedLabelColor = BrandDeepBlue
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // AGE DROPDOWN
                ExposedDropdownMenuBox(
                    expanded = ageExpanded,
                    onExpandedChange = { ageExpanded = !ageExpanded }
                ) {

                    OutlinedTextField(
                        value = age,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Age") },
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandDeepBlue,
                            focusedLabelColor = BrandDeepBlue
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = ageExpanded,
                        onDismissRequest = { ageExpanded = false }
                    ) {
                        ageList.forEach { selectedAge ->
                            DropdownMenuItem(
                                text = { Text(selectedAge) },
                                onClick = {
                                    age = selectedAge
                                    ageExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            currentPage = 2
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandDeepBlue
                    )
                ) {
                    Text(
                        text = "Next",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            // =========================
            // PAGE 2
            // =========================
            else {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Health Details",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = BrandDeepBlue
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // BLOOD GROUP DROPDOWN
                ExposedDropdownMenuBox(
                    expanded = bloodExpanded,
                    onExpandedChange = { bloodExpanded = !bloodExpanded }
                ) {

                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Blood Group") },
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandDeepBlue,
                            focusedLabelColor = BrandDeepBlue
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = bloodExpanded,
                        onDismissRequest = { bloodExpanded = false }
                    ) {
                        bloodGroups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group) },
                                onClick = {
                                    bloodGroup = group
                                    bloodExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // GUARDIAN NUMBER
                OutlinedTextField(
                    value = guardianNumber,
                    onValueChange = {
                        if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                            guardianNumber = it
                        }
                    },
                    label = { Text("Guardian Number") },
                    prefix = {
                        Text(
                            "+91 ",
                            color = BrandDeepBlue,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandDeepBlue,
                        focusedLabelColor = BrandDeepBlue
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // MEDICAL CONDITIONS
                OutlinedTextField(
                    value = medicalConditions,
                    onValueChange = { medicalConditions = it },
                    label = { Text("Medical Conditions") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandDeepBlue,
                        focusedLabelColor = BrandDeepBlue
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // GENDER DROPDOWN
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {

                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandDeepBlue,
                            focusedLabelColor = BrandDeepBlue
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        genderList.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    gender = item
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ADDRESS
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandDeepBlue,
                        focusedLabelColor = BrandDeepBlue
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {

                        // SAVE GUARDIAN NUMBER
                        UserSession.guardianNumber = "+91$guardianNumber"

                        // SAVE USER DATA
                        UserSession.name = name
                        UserSession.age = age
                        UserSession.bloodGroup = bloodGroup
                        UserSession.gender = gender
                        UserSession.address = address
                        UserSession.medicalConditions = medicalConditions

                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandDeepBlue
                    )
                ) {
                    Text(
                        text = "Login",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}