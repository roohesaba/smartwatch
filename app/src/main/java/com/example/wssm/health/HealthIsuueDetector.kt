package com.example.wssm.health

class HealthIssueDetector {

    fun check(data: HealthData): HealthStatus {
        // FIX: Remove .value because heartRate is already a String
        val hr = data.heartRate.toIntOrNull() ?: 0
        val o2 = data.oxygen.replace("%", "").trim().toIntOrNull() ?: 100

        return when {
            // --- TEST TRIGGER: HR > 80 ---
            hr > 80 || (hr < 40 && hr != 0) -> HealthStatus.Critical

            // Low Oxygen Saturation
            o2 < 90 -> HealthStatus.Critical

            // High Stress or Warning HR
            hr > 70 || data.stress == "High" -> HealthStatus.Warning

            else -> HealthStatus.Normal
        }
    }

    // Legacy helper (keeping Int parameters)
    fun detect(heartRate: Int, spo2: Int, stress: Int): String? {
        return when {
            heartRate > 80 -> "High Heart Rate (Test Mode)"
            heartRate < 45 -> "Low Heart Rate"
            spo2 < 92 -> "Low Oxygen"
            stress > 80 -> "High Stress"
            else -> null
        }
    }
}