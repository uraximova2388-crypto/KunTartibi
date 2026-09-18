package uz.kuntartibi

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import uz.kuntartibi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private lateinit var adapter: TaskAdapter

    private val notifPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        adapter = TaskAdapter(this) { task, on ->
            Prefs.setEnabled(this, task.id, on)
            if (on) AlarmScheduler.schedule(this, task) else AlarmScheduler.cancel(this, task)
        }
        b.list.layoutManager = LinearLayoutManager(this)
        b.list.adapter = adapter

        b.gallery.setOnClickListener {
            startActivity(Intent(this, ProofGalleryActivity::class.java))
        }
        b.fixPermissions.setOnClickListener { requestEverything() }

        AlarmScheduler.rescheduleAll(this)
        requestEverything()
    }

    override fun onResume() {
        super.onResume()
        adapter.submit(Schedule.tasks)
        val done = Prefs.doneCountToday(this)
        b.progress.max = Schedule.tasks.size
        b.progress.progress = done
        b.progressText.text = getString(R.string.progress_fmt, done, Schedule.tasks.size)
    }

    private fun requestEverything() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (!AlarmScheduler.canScheduleExact(this) &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        ) {
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
        }
        // Batareya cheklovi signalni o'ldirmasligi uchun.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(android.os.PowerManager::class.java)
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                try {
                    startActivity(
                        Intent(
                            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                            Uri.parse("package:$packageName")
                        )
                    )
                } catch (_: Exception) {}
            }
        }
    }
}
