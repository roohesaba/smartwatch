package com.example.wssm.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class HealthConnectManager(private val context: Context) {

    private fun getClient(): HealthConnectClient? {
        return try {
            HealthConnectClient.getOrCreate(context)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun readHealthData(): HealthData {
        val client = getClient() ?: return HealthData()

        val now = Instant.now()
        val startOfDay = ZonedDateTime.now().toLocalDate().atStartOfDay(ZonedDateTime.now().zone).toInstant()
        val lookback24h = now.minus(24, ChronoUnit.HOURS)

        return try {
            // 1. Fetch Steps
            val stepsResponse = client.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
                )
            )
            val totalSteps = stepsResponse.records.sumOf { it.count }

            // 2. Fetch Heart Rate
            val hrResponse = client.readRecords(
                ReadRecordsRequest(
                    HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(lookback24h, now)
                )
            )
            // Fix: beatsPerMinute is a Double. We use .toInt() for a clean display.
            val lastHr = hrResponse.records.flatMap { it.samples }.lastOrNull()?.beatsPerMinute

            // 3. Fetch Oxygen (SpO2)
            val spo2Response = client.readRecords(
                ReadRecordsRequest(
                    OxygenSaturationRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(lookback24h, now)
                )
            )
            // Fix: .percentage is a Percentage object, we must call .value to get the Double
            val lastSpo2 = spo2Response.records.lastOrNull()?.percentage?.value

            // 4. Fetch Stress (Mindfulness)
            val stressResponse = client.readRecords(
                ReadRecordsRequest(
                    MindfulnessSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(lookback24h, now)
                )
            )
            val stressLevel = if (stressResponse.records.isNotEmpty()) "Calm" else "Normal"

            HealthData(
                steps = totalSteps.toString(),
                heartRate = lastHr?.toInt()?.toString() ?: "--",
                oxygen = lastSpo2?.let { "${it.toInt()}%" } ?: "--",
                stress = stressLevel
            )
        } catch (e: Exception) {
            HealthData()
        }
    }
}