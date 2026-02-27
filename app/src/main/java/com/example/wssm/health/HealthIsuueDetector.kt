package com.example.wssm.health

class HealthIssueDetector {

    fun detect(heartRate: Int, spo2: Int, stress: Int): String? {
        return when {
            heartRate > 120 -> "High Heart Rate"
            heartRate < 45 -> "Low Heart Rate"
            spo2 < 92 -> "Low Oxygen"
            stress > 80 -> "High Stress"
            else -> null
        }
    }
}