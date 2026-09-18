package uz.kuntartibi

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Prefs {
    private const val FILE = "kuntartibi"

    private fun sp(c: Context) = c.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun isEnabled(c: Context, taskId: Int): Boolean =
        sp(c).getBoolean("on_$taskId", true)

    fun setEnabled(c: Context, taskId: Int, on: Boolean) {
        sp(c).edit().putBoolean("on_$taskId", on).apply()
    }

    fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    /** Suratga olib tasdiqlangan ish. */
    fun markDone(c: Context, taskId: Int, photoPath: String) {
        sp(c).edit()
            .putString("done_${today()}_$taskId", photoPath)
            .apply()
    }

    fun photoOf(c: Context, taskId: Int, day: String = today()): String? =
        sp(c).getString("done_${day}_$taskId", null)

    fun isDoneToday(c: Context, taskId: Int): Boolean = photoOf(c, taskId) != null

    fun doneCountToday(c: Context): Int =
        Schedule.tasks.count { isDoneToday(c, it.id) }
}
