package com.example.wssm.health

import android.telephony.SmsManager
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object EmergencySmsHandler {
    private var lastAlertTime: Long = 0
    private val COOLDOWN_MS = 60_000 // 1 minute for testing

    fun scanBatchAndNotify(context: android.content.Context, data: HealthData) {
        val guardian = data.guardianNumber

        // FIX: Removed .value (heartRate is now a String)
        Log.d("SMS_DEBUG", "Checking Alert... Guardian: $guardian, HR: ${data.heartRate}")

        if (guardian.isNullOrBlank()) return

        // Cooldown check logic
        if (System.currentTimeMillis() - lastAlertTime < COOLDOWN_MS) {
            Log.d("SMS_DEBUG", "Alert skipped: Cooldown active")
            return
        }

        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

        // 1. Check CURRENT Heart Rate (String to Int)
        val currentHr = data.heartRate.toIntOrNull() ?: 0

        // 2. Check Heart Rate HISTORY
        // MetricValue objects STILL have .value, so we keep it here for history lists
        val dangerousHrRecord = data.heartRateHistory.find { (it.value.toIntOrNull() ?: 0) > 80 }

        // 3. Check Oxygen (Current and History)
        val currentO2 = data.oxygen.replace("%", "").trim().toIntOrNull() ?: 100
        val dangerousO2Record = data.oxygenHistory.find {
            (it.value.replace("%", "").trim().toIntOrNull() ?: 100) < 90
        }

        // Logic to decide which message to send
        val isEmergency = currentHr > 80 || dangerousHrRecord != null || currentO2 < 90 || dangerousO2Record != null

        if (isEmergency) {
            val alertValue = if (currentHr > 80) currentHr.toString() else dangerousHrRecord?.value ?: "Abnormal"
            val timeStr = if (currentHr > 80) "Now" else dangerousHrRecord?.timestamp?.let { timeFormatter.format(Date.from(it)) } ?: "recently"

            val message = "EMERGENCY: Abnormal vitals detected via WSSM App.\n" +
                    "Value: $alertValue bpm\n" +
                    "Time: $timeStr\n" +
                    "Please check in immediately."

            Log.d("SMS_DEBUG", "Sending SMS to $guardian: $message")
            sendSMS(guardian, message)
            lastAlertTime = System.currentTimeMillis()
        }
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.d("SMS_DEBUG", "SMS successfully handed to system")
        } catch (e: Exception) {
            Log.e("SMS_DEBUG", "SMS failed to send: ${e.message}")
        }
    }
}