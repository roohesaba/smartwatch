package com.example.wssm.health

class StepCounter {

    private var stepCount = 0

    fun update(x: Float, y: Float, z: Float): Int {
        val magnitude = sqrt((x*x + y*y + z*z).toDouble())

        if (magnitude > 12) stepCount++  // threshold

        return stepCount
    }
}