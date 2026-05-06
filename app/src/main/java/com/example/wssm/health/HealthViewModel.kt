package com.example.wssm.health

import android.content.Context
import com.example.wssm.App
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.wssm.models.Guardian
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class HealthViewModel : ViewModel() {

    private val manager by lazy { HealthConnectManager(App.instance) }
    private val prefs = App.instance.getSharedPreferences("wssm_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private var refreshJob: Job? = null
    val isMonitoringActive = mutableStateOf(false)

    // TRACKING STATE: High-level state for the HomeScreen spinner visibility
    val isSyncing = mutableStateOf(false)

    // Motion manager for secondary triage logic
    private val motionManager = PhoneMotionManager(App.instance) {}

    private var previousSteps = 0
    private var lastSmsSentTime = 0L
    private val smsCooldown = 5 * 60 * 1000

    // UI States
    val uiState = mutableStateOf(HealthData(
        guardianNumber = prefs.getString("priority_guardian_no", null)
    ))

    val guardianList = mutableStateOf<List<Guardian>>(loadGuardians())

    fun canTriggerEmergencyAlert(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSmsSentTime > smsCooldown) {
            lastSmsSentTime = currentTime
            return true
        }
        return false
    }

    private fun runTriage(data: HealthData, stepDelta: Int): HealthData {
        val hr = data.heartRate.toIntOrNull() ?: 0
        val o2 = data.oxygen.replace("%", "").trim().toIntOrNull() ?: 100
        val isPhoneFlat = motionManager.isHorizontal

        var level = 0
        var msg = ""

        when {
            o2 < 91 && hr > 110 -> { level = 4; msg = "CRITICAL: Respiratory Distress" }
            hr > 130 && stepDelta > 20 -> { level = 3; msg = "URGENT: High Physical Stress" }
            hr in 1..45 && stepDelta == 0 && isPhoneFlat -> { level = 2; msg = "WARNING: Potential Collapse" }
            hr > 100 && stepDelta == 0 -> { level = 1; msg = "NOTICE: Elevated Resting HR" }
        }

        val newStatus = if (level >= 3) HealthStatus.Critical else if (level > 0) HealthStatus.Warning else HealthStatus.Normal

        return data.copy(
            alertLevel = level,
            alertMessage = msg,
            status = newStatus,
            stress = if (level >= 2) "High" else "Normal"
        )
    }

    fun startFrequentSync() {
        // Resetting the job ensures Manual Clicks start a fresh 10s cycle immediately
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (true) {
                if (!isMonitoringActive.value && refreshJob != null) {
                    isSyncing.value = false
                    return@launch
                }

                try {
                    // 1. START VISUAL SYNC
                    isSyncing.value = true

                    // 2. FETCH DATA (Health Connect Access)
                    val rawData = manager.readHealthData()

                    val currentTotalSteps = motionManager.currentSteps.toInt()
                    val stepDelta = currentTotalSteps - previousSteps
                    previousSteps = currentTotalSteps

                    val triagedData = runTriage(rawData, stepDelta)

                    // 3. MINIMUM LOADING VISIBILITY:
                    // We hold the spinner for 3 seconds so the user can see data is being fetched.
                    delay(3000L)

                    // 4. UPDATE UI
                    uiState.value = triagedData.copy(
                        steps = currentTotalSteps.toString(),
                        guardianNumber = uiState.value.guardianNumber,
                        heartRateHistory = rawData.heartRateHistory,
                        oxygenHistory = rawData.oxygenHistory
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    // 5. STOP SPINNER: Guaranteed to hide even on error
                    isSyncing.value = false
                }

                // 6. WAIT: Wait for the remaining 7 seconds of the 10-second cycle
                delay(7000L)
            }
        }
    }

    fun stopFrequentSync() {
        refreshJob?.cancel()
        refreshJob = null
        isSyncing.value = false
    }

    fun addGuardianToList(guardian: Guardian) {
        val current = guardianList.value.toMutableList()
        current.add(0, guardian)
        guardianList.value = current
        saveListToPrefs(current)

        prefs.edit().putString("priority_guardian_no", guardian.phone).apply()
        uiState.value = uiState.value.copy(guardianNumber = guardian.phone)
    }

    fun deleteGuardian(guardian: Guardian) {
        val current = guardianList.value.toMutableList()
        current.removeAll { it.phone == guardian.phone }
        guardianList.value = current
        saveListToPrefs(current)

        if (uiState.value.guardianNumber == guardian.phone) {
            prefs.edit().remove("priority_guardian_no").apply()
            uiState.value = uiState.value.copy(guardianNumber = null)
        }
    }

    private fun saveListToPrefs(list: List<Guardian>) {
        val json = gson.toJson(list)
        prefs.edit().putString("guardian_list_json", json).apply()
    }

    private fun loadGuardians(): List<Guardian> = prefs.getString("guardian_list_json", null)?.let {
        gson.fromJson(it, object : TypeToken<List<Guardian>>() {}.type)
    } ?: emptyList()
}