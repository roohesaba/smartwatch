package com.example.wssm.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
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
        // REMOVED the "val status = if (heartRate > 80)..." line from here.
        // We can't check the heart rate before we read it!

        val timeFilter = TimeRangeFilter.between(now.minus(24, ChronoUnit.HOURS), now)

        return try {
            // 1. Fetch Heart Rate Records
            val hrResponse = client.readRecords(ReadRecordsRequest(HeartRateRecord::class, timeFilter))
            val hrList = hrResponse.records.flatMap { record ->
                record.samples.map { sample ->
                    MetricValue(sample.beatsPerMinute.toInt().toString(), sample.time)
                }
            }.sortedByDescending { it.timestamp }
                .take(15)

            // 2. Fetch Oxygen Records
            val o2Response = client.readRecords(ReadRecordsRequest(OxygenSaturationRecord::class, timeFilter))
            val o2List = o2Response.records.map { record ->
                MetricValue("${record.percentage.value.toInt()}%", record.time)
            }.sortedByDescending { it.timestamp }
                .take(10)



            // 4. Extract current values for status check
            val currentHR = hrList.firstOrNull()?.value?.toIntOrNull()
            val currentO2 = o2List.firstOrNull()?.value?.replace("%", "")?.toIntOrNull()

            // Calculate the status based on the current vitals
            val finalStatus = determineStatus(currentHR, currentO2)

            return HealthData(
                heartRate = hrList.firstOrNull()?.value ?: "--",
                oxygen = o2List.firstOrNull()?.value ?: "--",
                steps = "--",
                stress = "Normal",
                heartRateHistory = hrList,
                oxygenHistory = o2List,
                status = finalStatus, // Use the calculated status here
                isSyncing = false
            )

        } catch (e: Exception) {
            HealthData()
        }
    }

    // This is the "Brain" that decides if it's an emergency
    private fun determineStatus(hr: Int?, o2: Int?): HealthStatus {
        if (hr == null) return HealthStatus.Normal

        // YOUR TEST TRIGGER: If HR is above 80, return Critical
        if (hr > 80 || (o2 != null && o2 <= 90)) {
            return HealthStatus.Critical
        }

        if (hr in 101..125 || (o2 != null && o2 in 91..94)) {
            return HealthStatus.Warning
        }

        return HealthStatus.Normal
    }
}