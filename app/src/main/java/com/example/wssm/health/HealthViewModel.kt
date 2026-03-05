package com.example.wssm.health

import com.example.wssm.App
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class HealthViewModel : ViewModel() {

    private val manager by lazy { HealthConnectManager(App.instance) }

    // Your proprietary algorithms/sensors
    private val sensor by lazy { SensorDataSource(App.instance) }
    private val hrAlgo by lazy { HeartRateAlgorithm() }
    private val stressAlgo by lazy { StressAlgorithm() }
    private val spo2Algo by lazy { SpO2Algorithm() }
    private val stepCounter by lazy { StepCounter() }
    private val detector by lazy { HealthIssueDetector() }

    private val useCase by lazy {
        MonitorHealthUseCase(sensor, hrAlgo, stressAlgo, spo2Algo, stepCounter, detector)
    }

    // Explicitly typed state
    val uiState = mutableStateOf<HealthData>(HealthData())

    // Ensure HealthStatus is defined in your models/HealthData.kt
    val healthStatus = mutableStateOf<HealthStatus?>(null)

    private var refreshJob: Job? = null

    fun startFrequentSync() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (true) {
                fetchAndProcessData()
                delay(10_000L)
            }
        }
    }

    fun stopFrequentSync() {
        refreshJob?.cancel()
    }

    private fun fetchAndProcessData() {
        viewModelScope.launch {
            try {
                val rawData = manager.readHealthData()

                // Ensure these methods exist in your algorithm classes!
                val refinedHeartRate = hrAlgo.process(rawData.heartRate)
                val refinedStress = stressAlgo.calculate(rawData.stress)

                uiState.value = rawData.copy(
                    heartRate = refinedHeartRate,
                    stress = refinedStress
                )

                startMonitoring()

            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun startMonitoring() {
        viewModelScope.launch {
            try {
                val currentData = uiState.value
                val result = detector.check(currentData)
                healthStatus.value = result

                // Corrected the check for "Critical" status
                // This assumes HealthStatus is a 'sealed class' with a 'Critical' subclass
                if (result is HealthStatus.Critical) {
                    // triggerSOS() logic here
                }
            } catch (e: Exception) {
                // Prevent crash
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopFrequentSync()
    }
}