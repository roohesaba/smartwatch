package com.example.wssm.health

class MonitorHealthUseCase(
    private val sensor: SensorDataSource,
    private val hrAlgo: HeartRateAlgorithm,
    private val stressAlgo: StressAlgorithm,
    private val spo2Algo: SpO2Algorithm,
    private val stepCounter: StepCounter,
    private val detector: HealthIssueDetector
) {

    /**
     * This function now returns the sealed HealthStatus (Normal, Warning, or Critical)
     * as expected by the HealthViewModel logic.
     */
    fun monitor(): HealthStatus {
        // 1. Collect raw data from local sensors
        val ppg = sensor.getPPGSignal()

        // 2. Run algorithms
        val hr = hrAlgo.calculateBPM(ppg)
        val spo2 = spo2Algo.calculateSpO2(10.0, 1.0, 9.0, 1.0)
        val stress = stressAlgo.calculateStressLevel(listOf(800, 790, 810))
        val stepsCount = stepCounter.update(1f, 2f, 3f)

        // 3. Create a temporary data object to pass to the detector
        val currentSnapshot = HealthData(
            heartRate = hr.toString(),
            oxygen = "$spo2%",
            steps = stepsCount.toString(),
            stress = if (stress > 70) "High" else "Normal"
        )

        // 4. Return the Safety Status (Normal/Warning/Critical)
        // This matches the detector.check(data) call in your ViewModel
        return detector.check(currentSnapshot)
    }
}

/**
 * Renamed from HealthStatus to HealthSnapshot to avoid the
 * "Redeclaration" error with the sealed class in HealthData.kt
 */
data class HealthSnapshot(
    val heartRate: Int,
    val spo2: Int,
    val stress: Int,
    val steps: Int,
    val issue: String?
)