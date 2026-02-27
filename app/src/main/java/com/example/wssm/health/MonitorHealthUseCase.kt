package com.example.wssm.health

class MonitorHealthUseCase(
    private val sensor: SensorDataSource,
    private val hrAlgo: HeartRateAlgorithm,
    private val stressAlgo: StressAlgorithm,
    private val spo2Algo: SpO2Algorithm,
    private val stepCounter: StepCounter,
    private val detector: HealthIssueDetector
) {

    fun monitor(): HealthStatus {
        val ppg = sensor.getPPGSignal()
        val hr = hrAlgo.calculateBPM(ppg)
        val spo2 = spo2Algo.calculateSpO2(10.0, 1.0, 9.0, 1.0)
        val stress = stressAlgo.calculateStressLevel(listOf(800, 790, 810))
        val steps = stepCounter.update(1f, 2f, 3f)

        val issue = detector.detect(hr, spo2, stress)

        return HealthStatus(hr, spo2, stress, steps, issue)
    }
}

data class HealthStatus(
    val heartRate: Int,
    val spo2: Int,
    val stress: Int,
    val steps: Int,
    val issue: String?
)