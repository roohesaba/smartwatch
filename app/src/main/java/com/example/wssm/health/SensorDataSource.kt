package com.example.wssm.health

class SensorDataSource(
    private val context: Context
) {
    fun getHeartRate(): Int {
        // from BLE smartwatch or sensor
        return currentHeartRate
    }

    fun getPPGSignal(): List<Int> {
        return ppgValues
    }

    fun getAccelerometerData(): Triple<Float, Float, Float> {
        return Triple(x, y, z)
    }
}