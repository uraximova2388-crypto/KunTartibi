package uz.kuntartibi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_TASK_ID = "task_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)
        val task = Schedule.byId(taskId) ?: return

        val svc = Intent(context, AlarmService::class.java)
            .putExtra(EXTRA_TASK_ID, taskId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(svc)
        } else {
            context.startService(svc)
        }

        // Ertangi kunga qayta qo'yamiz.
        AlarmScheduler.schedule(context, task)
    }
}
