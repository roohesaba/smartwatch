package com.example.wssm.health

// This defines the "State" of your app
sealed class HealthStatus {
    object Normal : HealthStatus()
    object Warning : HealthStatus()
    object Critical : HealthStatus()
}