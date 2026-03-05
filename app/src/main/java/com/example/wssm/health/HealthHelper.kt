package com.example.wssm.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.temporal.ChronoUnit

class HealthHelper(private val context: Context) {
    private val client by lazy { HealthConnectClient.getOrCreate(context) }

    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(OxygenSaturationRecord::class),
        HealthPermission.getReadPermission(MindfulnessSessionRecord::class)
    )

    suspend fun hasPermissions(): Boolean {
        return client.permissionController.getGrantedPermissions().containsAll(permissions)
    }

    suspend fun fetchLatestData(): HealthUiState {
        return try {
            val end = Instant.now()
            val start = end.minus(30, ChronoUnit.MINUTES)
            val filter = TimeRangeFilter.between(start, end)

            // 1. Steps
            val steps = client.readRecords(ReadRecordsRequest(StepsRecord::class, filter))
                .records.sumOf { it.count }

            // 2. Heart Rate - beatsPerMinute is a Double
            val hr = client.readRecords(ReadRecordsRequest(HeartRateRecord::class, filter))
                .records.flatMap { it.samples }.lastOrNull()?.beatsPerMinute

            // 3. Oxygen (SpO2) - percentage is a Percentage object, use .value to get Double
            // FIX: Added .value here
            val o2 = client.readRecords(ReadRecordsRequest(OxygenSaturationRecord::class, filter))
                .records.lastOrNull()?.percentage?.value

            // 4. Stress
            val stressRecord = client.readRecords(ReadRecordsRequest(MindfulnessSessionRecord::class, filter))
                .records.lastOrNull()

            HealthUiState(
                steps = steps.toString(),
                heartRate = hr?.toInt()?.toString() ?: "--",
                oxygen = o2?.let { "${it.toInt()}%" } ?: "--",
                stress = if (stressRecord != null) "Calm" else "Normal"
            )
        } catch (e: Exception) {
            HealthUiState()
        }
    }
}

data class HealthUiState(
    val steps: String = "0",
    val heartRate: String = "--",
    val oxygen: String = "--",
    val stress: String = "Normal"
)