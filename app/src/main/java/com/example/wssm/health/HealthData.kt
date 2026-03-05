package com.example.wssm.health

data class HealthData(
    val heartRate: String = "--",
    val oxygen: String = "--",
    val steps: String = "0",
    val stress: String = "Normal"
)

sealed class HealthStatus {
    object Normal : HealthStatus()
    object Warning : HealthStatus()
    object Critical : HealthStatus()
}