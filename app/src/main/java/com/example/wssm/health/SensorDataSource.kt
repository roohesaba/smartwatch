package com.example.wssm.health

import android.content.Context

class SensorDataSource(
    private val context: Context
) {
    // Fake placeholder sensor values (you will replace with real sensor code later)
    private var currentHeartRate: Int = 75
    private var ppgValues: List<Int> = listOf(120, 130, 125, 118)
    private var x: Float = 0.1f
    private var y: Float = -0.3f
    private var z: Float = 9.8f

    fun getHeartRate(): Int {
        return currentHeartRate
    }

    fun getPPGSignal(): List<Int> {
        return ppgValues
    }

    fun getAccelerometerData(): Triple<Float, Float, Float> {
        return Triple(x, y, z)
    }
}