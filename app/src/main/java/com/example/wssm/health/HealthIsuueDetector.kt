package com.example.wssm.health

class HealthIssueDetector {

    /**
     * Main safety check function called by the ViewModel.
     */
    fun check(data: HealthData): HealthStatus {
        // Parse Heart Rate
        val hr = data.heartRate.toIntOrNull() ?: 0

        // Parse Oxygen (removes % if present)
        val o2 = data.oxygen.replace("%", "").trim().toIntOrNull() ?: 100

        // Logic for Emergency Trigger
        return when {
            // High HR or dangerously Low HR
            hr > 130 || (hr < 40 && hr != 0) -> HealthStatus.Critical

            // Low Oxygen Saturation
            o2 < 90 -> HealthStatus.Critical

            // High Stress or Warning HR
            hr > 110 || data.stress == "High" -> HealthStatus.Warning

            else -> HealthStatus.Normal
        }
    }

    // Legacy helper for specific string messages
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