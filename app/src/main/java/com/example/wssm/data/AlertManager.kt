package com.example.wssm.data

class AlertManager(private val context: Context) {

    fun sendAlert(msg: String, phone: String) {
        SmsManager.getDefault().sendTextMessage(
            phone, null, msg, null, null
        )
    }
}