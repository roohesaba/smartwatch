package com.example.wssm.data
import android.content.Context
import android.telephony.SmsManager

class AlertManager(private val context: Context) {

    fun sendAlert(msg: String, phone: String) {
        SmsManager.getDefault().sendTextMessage(
            phone, null, msg, null, null
        )
    }
}