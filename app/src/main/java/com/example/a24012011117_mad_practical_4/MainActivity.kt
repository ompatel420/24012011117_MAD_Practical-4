package com.example.a24012011117_mad_practical_4

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var cardCreateAlarm: MaterialCardView
    private lateinit var cardCancelAlarm: MaterialCardView
    private lateinit var btnCreateAlarm: MaterialButton
    private lateinit var btnCancelAlarm: MaterialButton
    private lateinit var tvSetAlarmTime: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cardCreateAlarm = findViewById(R.id.cardCreateAlarm)
        cardCancelAlarm = findViewById(R.id.cardCancelAlarm)
        btnCreateAlarm = findViewById(R.id.btnCreateAlarm)
        btnCancelAlarm = findViewById(R.id.btnCancelAlarm)
        tvSetAlarmTime = findViewById(R.id.tvSetAlarmTime)

        btnCreateAlarm.setOnClickListener {
            showTimerDialog()
        }

        btnCancelAlarm.setOnClickListener {
            setAlarm(0L, "Stop")
            cardCancelAlarm.visibility = View.GONE
            cardCreateAlarm.visibility = View.VISIBLE
            Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTimerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]

        TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute -> sendDialogDataToActivity(selectedHour, selectedMinute) },
            hour,
            minute,
            false,
        ).show()
    }

    private fun sendDialogDataToActivity(hour: Int, minute: Int) {
        val alarmCalendar = Calendar.getInstance()
        val now = Calendar.getInstance()

        alarmCalendar[Calendar.HOUR_OF_DAY] = hour
        alarmCalendar[Calendar.MINUTE] = minute
        alarmCalendar[Calendar.SECOND] = 0
        alarmCalendar[Calendar.MILLISECOND] = 0

        if (alarmCalendar.before(now)) {
            alarmCalendar.add(Calendar.DATE, 1)
        }

        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        tvSetAlarmTime.text = getString(R.string.set_alarm_time, sdf.format(alarmCalendar.time))

        setAlarm(alarmCalendar.timeInMillis, "Start")

        cardCancelAlarm.visibility = View.VISIBLE
        cardCreateAlarm.visibility = View.GONE

        val diff = alarmCalendar.timeInMillis - now.timeInMillis
        val diffHours = (diff / (1000 * 60 * 60)).toInt()
        val diffMinutes = ((diff / (1000 * 60)) % 60).toInt()

        val toastMessage = if (diffHours > 0) {
            "Alarm in $diffHours Hours $diffMinutes minutes"
        } else {
            "Alarm in $diffMinutes minutes"
        }

        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
    }

    private fun setAlarm(millisTime: Long, action: String) {
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        intent.putExtra("Service1", action)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            234324243,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        if (action == "Start") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    val intentPermission = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    startActivity(intentPermission)
                    return
                }
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                millisTime,
                pendingIntent
            )
        } else if (action == "Stop") {
            alarmManager.cancel(pendingIntent)
            sendBroadcast(intent)
        }
    }
}
