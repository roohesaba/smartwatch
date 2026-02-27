package com.example.wssm.health

class SpO2Algorithm {

    fun calculateSpO2(acRed: Double, dcRed: Double, acIR: Double, dcIR: Double): Int {
        val ratio = (acRed / dcRed) / (acIR / dcIR)
        return (110 - 25 * ratio).toInt()
    }
}