package uz.kuntartibi

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object AlarmScheduler {

    private fun pending(c: Context, task: Task): PendingIntent {
        val i = Intent(c, AlarmReceiver::class.java).apply {
            action = "uz.kuntartibi.FIRE"
            putExtra(AlarmReceiver.EXTRA_TASK_ID, task.id)
        }
        return PendingIntent.getBroadcast(
            c, task.id, i,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun nextTime(task: Task): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, task.hour)
            set(Calendar.MINUTE, task.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (cal.timeInMillis <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    fun canScheduleExact(c: Context): Boolean {
        val am = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) am.canScheduleExactAlarms() else true
    }

    fun schedule(c: Context, task: Task) {
        if (!Prefs.isEnabled(c, task.id)) { cancel(c, task); return }
        val am = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = pending(c, task)
        val time = nextTime(task)
        try {
            if (canScheduleExact(c)) {
                // Soat ilovasidek aniq ishlashi uchun — Doze rejimida ham uyg'otadi.
                am.setAlarmClock(AlarmManager.AlarmClockInfo(time, pi), pi)
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pi)
            }
        } catch (e: SecurityException) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pi)
        }
    }

    fun cancel(c: Context, task: Task) {
        val am = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pending(c, task))
    }

    fun rescheduleAll(c: Context) {
        Schedule.tasks.forEach { schedule(c, it) }
    }
}
