package com.example.wssm.health

class StressAlgorithm {

    fun calculateStressLevel(rrIntervals: List<Int>): Int {
        val sdnn = rrIntervals.standardDeviation()
        return when {
            sdnn < 20 -> 90   // high stress
            sdnn < 40 -> 60   // moderate
            else -> 20        // relaxed
        }
    }

    private fun List<Int>.standardDeviation(): Double {
        val mean = average()
        return kotlin.math.sqrt(map { (it - mean)*(it - mean) }.average())
    }
}