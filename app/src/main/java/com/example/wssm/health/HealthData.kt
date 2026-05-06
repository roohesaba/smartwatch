package com.example.wssm.health

import java.time.Instant

// Small helper class for history items
data class MetricValue(
    val value: String = "--",
    val timestamp: Instant? = null
)

data class HealthData(
    val heartRate: String = "--",
    val oxygen: String = "--",
    val steps: String = "0",
    val stress: String = "Normal",
    val status: HealthStatus = HealthStatus.Normal,
    val isSyncing: Boolean = false,
    val guardianNumber: String? = null,

    // History lists for popups
    val heartRateHistory: List<MetricValue> = emptyList(),
    val oxygenHistory: List<MetricValue> = emptyList(),

    // Triage Alert Fields
    val alertLevel: Int = 0,
    val alertMessage: String = ""
)