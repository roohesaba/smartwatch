package com.example.wssm.health

class HeartRateAlgorithm {

    /**
     * Entry point for the ViewModel.
     * Takes the String from Health Connect and returns a processed String.
     */
    fun process(rawHeartRate: String): String {
        return if (rawHeartRate.isBlank() || rawHeartRate == "0") "--" else rawHeartRate
    }

    /**
     * Optional: Use this if you are getting raw PPG sensor data from the phone/watch
     */
    fun calculateBPM(ppg: List<Int>): Int {
        if (ppg.size < 2) return 0
        val peaks = detectPeaks(ppg)
        if (peaks.size < 2) return 0

        val timeBetweenPeaks = averageInterval(peaks)
        return if (timeBetweenPeaks > 0) 60_000 / timeBetweenPeaks else 0
    }

    private fun detectPeaks(ppg: List<Int>): List<Int> {
        return ppg.indices
            .filter { i -> i > 0 && i < ppg.size - 1 && ppg[i] > ppg[i - 1] && ppg[i] > ppg[i + 1] }
    }

    private fun averageInterval(peaks: List<Int>): Int {
        val intervals = peaks.zipWithNext { a, b -> b - a }
        return if (intervals.isEmpty()) 0 else intervals.average().toInt()
    }
}