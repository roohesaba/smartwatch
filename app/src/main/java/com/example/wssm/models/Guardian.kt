package com.example.wssm.models

// Firebase needs a default constructor, so we provide default values
data class Guardian(
    val name: String = "",
    val phone: String = "",
    val timestamp: Long = System.currentTimeMillis()
)