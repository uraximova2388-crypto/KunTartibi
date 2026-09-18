package uz.kuntartibi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat

class AlarmService : Service() {

    companion object {
        const val CHANNEL_ID = "kuntartibi_alarm"
        const val ACTION_STOP = "uz.kuntartibi.STOP"
        var activeTaskId: Int = -1
            private set
    }

    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var volumeStep = 0

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopEverything()
            return START_NOT_STICKY
        }

        val taskId = intent?.getIntExtra(AlarmReceiver.EXTRA_TASK_ID, -1) ?: -1
        val task = Schedule.byId(taskId)
        if (task == null) { stopSelf(); return START_NOT_STICKY }

        activeTaskId = taskId
        createChannel()
        startForeground(1001, buildNotification(task))
        acquireWakeLock()
        startSound(task)
        startVibration(task)

        // Ekranni to'liq qoplaydigan oynani ochamiz.
        val full = Intent(this, AlarmActivity::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TASK_ID, taskId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(full)

        return START_STICKY
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = getSystemService(NotificationManager::class.java)
        if (nm.getNotificationChannel(CHANNEL_ID) != null) return
        val ch = NotificationChannel(
            CHANNEL_ID, "Kun tartibi signallari", NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Rejadagi ishlar uchun to'liq ekranli eslatmalar"
            setSound(null, null)          // ovozni o'zimiz boshqaramiz
            enableVibration(false)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            setBypassDnd(true)
        }
        nm.createNotificationChannel(ch)
    }

    private fun buildNotification(task: Task): android.app.Notification {
        val full = Intent(this, AlarmActivity::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TASK_ID, task.id)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        val pi = PendingIntent.getActivity(
            this, task.id, full,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("${task.timeLabel()} · ${task.title}")
            .setContentText(task.messageForToday())
            .setStyle(NotificationCompat.BigTextStyle().bigText(task.messageForToday()))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)                 // surilib o'chmaydi
            .setAutoCancel(false)
            .setFullScreenIntent(pi, true)    // ekranni to'liq qoplaydi
            .setContentIntent(pi)
            .build()
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK, "kuntartibi:alarm"
        ).also { it.acquire(15 * 60 * 1000L) }
    }

    private fun rawFor(signal: Signal): Int {
        val name = when (signal) {
            Signal.UYGONISH -> "signal_uygonish"
            Signal.DIQQAT -> "signal_diqqat"
            Signal.YUMSHOQ -> "signal_yumshoq"
        }
        return resources.getIdentifier(name, "raw", packageName)
    }

    private fun startSound(task: Task) {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        // Signal ovozi tizim "silent" rejimida ham eshitilsin.
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.setStreamVolume(
            AudioManager.STREAM_ALARM,
            am.getStreamMaxVolume(AudioManager.STREAM_ALARM),
            0
        )

        val resId = rawFor(task.signal)
        player = try {
            if (resId != 0) {
                MediaPlayer.create(this, resId)
            } else {
                MediaPlayer().apply {
                    setDataSource(
                        this@AlarmService,
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    )
                    prepare()
                }
            }
        } catch (e: Exception) { null }

        player?.apply {
            setAudioAttributes(attrs)
            isLooping = true
            // Uyg'onish signali asta-sekin kuchayadi — quloqqa emas, uyquga tegadi.
            if (task.signal == Signal.UYGONISH) {
                setVolume(0.25f, 0.25f)
                escalateVolume()
            } else {
                setVolume(1f, 1f)
            }
            start()
        }
    }

    private fun escalateVolume() {
        val h = android.os.Handler(mainLooper)
        val step = object : Runnable {
            override fun run() {
                volumeStep++
                val v = (0.25f + volumeStep * 0.15f).coerceAtMost(1f)
                try { player?.setVolume(v, v) } catch (_: Exception) {}
                if (v < 1f) h.postDelayed(this, 8000)
            }
        }
        h.postDelayed(step, 8000)
    }

    private fun startVibration(task: Task) {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(VibratorManager::class.java)).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        val pattern = when (task.signal) {
            Signal.UYGONISH -> longArrayOf(0, 700, 400, 700, 400, 1200, 500)
            Signal.DIQQAT -> longArrayOf(0, 300, 200, 300, 900)
            Signal.YUMSHOQ -> longArrayOf(0, 200, 800)
        }
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
    }

    private fun stopEverything() {
        try { player?.stop(); player?.release() } catch (_: Exception) {}
        player = null
        vibrator?.cancel()
        if (wakeLock?.isHeld == true) wakeLock?.release()
        activeTaskId = -1
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        try { player?.release() } catch (_: Exception) {}
        vibrator?.cancel()
        if (wakeLock?.isHeld == true) wakeLock?.release()
        super.onDestroy()
    }
}
