package com.example.wssm.health

import kotlin.math.sqrt

class StressAlgorithm {

    /**
     * Entry point for the ViewModel.
     */
    fun calculate(rawStress: String): String {
        return rawStress.ifBlank { "Normal" }
    }

    fun calculateStressLevel(rrIntervals: List<Int>): Int {
        if (rrIntervals.isEmpty()) return 0
        val sdnn = rrIntervals.standardDeviation()
        return when {
            sdnn < 20 -> 90   // high stress
            sdnn < 40 -> 60   // moderate
            else -> 20        // relaxed
        }
    }

    private fun List<Int>.standardDeviation(): Double {
        val mean = average()
        return sqrt(map { (it - mean) * (it - mean) }.average())
    }
}