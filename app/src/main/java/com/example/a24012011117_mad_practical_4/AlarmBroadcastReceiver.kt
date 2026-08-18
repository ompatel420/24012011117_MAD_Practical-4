package com.example.a24012011117_mad_practical_4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.getStringExtra("Service1") ?: return

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("Service1", action)
        }

        when (action) {
            "Start" -> context.startService(serviceIntent)
            "Stop" -> context.stopService(serviceIntent)
        }
    }
}
