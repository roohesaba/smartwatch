package com.example.wssm.health

class HeartRateAlgorithm {

    fun calculateBPM(ppg: List<Int>): Int {
        val peaks = detectPeaks(ppg)
        val timeBetweenPeaks = averageInterval(peaks)

        val bpm = 60_000 / timeBetweenPeaks
        return bpm
    }

    private fun detectPeaks(ppg: List<Int>): List<Int> {
        return ppg.indices
            .filter { i -> i > 0 && i < ppg.size - 1 && ppg[i] > ppg[i - 1] && ppg[i] > ppg[i + 1] }
    }

    private fun averageInterval(peaks: List<Int>): Int {
        val intervals = peaks.zipWithNext { a, b -> b - a }
        return intervals.average().toInt()
    }
}