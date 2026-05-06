package com.example.wssm.health

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class PhoneMotionManager(context: Context, private val onFallDetected: () -> Unit) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    var currentSteps = 0f
    private var initialSteps = -1f

    // NEW: Property to track if the phone is lying flat
    var isHorizontal = false

    fun start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                // --- ORIENTATION LOGIC ---
                // If Z is ~9.8, phone is flat on its back.
                // If Z is ~ -9.8, phone is flat on its face.
                // Values above 8.5 mean the phone is mostly horizontal (Tilt < 30 degrees)
                isHorizontal = z > 8.5 || z < -8.5

                // --- FALL DETECTION (DISABLED/COMMENTED AS REQUESTED) ---
                /*
                val gForce = sqrt((x * x + y * y + z * z).toDouble()) / 9.81
                if (gForce > 3.8) {
                    onFallDetected()
                }
                */
            }
            Sensor.TYPE_STEP_COUNTER -> {
                if (initialSteps == -1f) initialSteps = event.values[0]
                currentSteps = event.values[0] - initialSteps
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}