package uz.kuntartibi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Telefon o'chib yonganda ham reja yo'qolmasligi uchun. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        AlarmScheduler.rescheduleAll(context)
    }
}
