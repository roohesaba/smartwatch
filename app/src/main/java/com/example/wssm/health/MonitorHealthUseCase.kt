package com.example.wssm.health

import java.time.Instant

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
        val now = Instant.now()

        val hr = hrAlgo.calculateBPM(ppg)
        val spo2 = spo2Algo.calculateSpO2(10.0, 1.0, 9.0, 1.0)
        val stressScore = stressAlgo.calculateStressLevel(listOf(800, 790, 810))
        val stepsCount = stepCounter.update(1f, 2f, 3f)

        // FIX: Pass strings directly for heartRate and oxygen
        val currentData = HealthData(
            heartRate = hr.toString(),
            oxygen = "$spo2%",
            steps = stepsCount.toString(),
            stress = if (stressScore > 70) "High" else "Normal",
            // Optional: add to history lists
            heartRateHistory = listOf(MetricValue(hr.toString(), now)),
            oxygenHistory = listOf(MetricValue("$spo2%", now))
        )

        return detector.check(currentData)
    }
}