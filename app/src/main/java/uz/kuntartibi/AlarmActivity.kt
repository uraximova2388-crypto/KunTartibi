package uz.kuntartibi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import uz.kuntartibi.databinding.ActivityAlarmBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Bu oyna butun ekranni qoplaydi, qulflangan ekranda ham ochiladi va
 * vazifa suratga olinmaguncha yopilmaydi.
 */
class AlarmActivity : AppCompatActivity() {

    private lateinit var b: ActivityAlarmBinding
    private lateinit var task: Task
    private var imageCapture: ImageCapture? = null
    private var completed = false

    private val cameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCamera()
            else Toast.makeText(this, R.string.camera_needed, Toast.LENGTH_LONG).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOverLockScreen()

        b = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(b.root)

        val id = intent.getIntExtra(AlarmReceiver.EXTRA_TASK_ID, -1)
        task = Schedule.byId(id) ?: run { finish(); return }

        b.time.text = task.timeLabel()
        b.title.text = task.title
        b.message.text = task.messageForToday()
        b.proofPrompt.text = task.proofPrompt

        b.openCamera.setOnClickListener { openCameraStage() }

        // Suratsiz yopish faqat "proofRequired = false" ishlar uchun.
        b.skip.visibility = if (task.proofRequired) View.GONE else View.VISIBLE
        b.skip.setOnClickListener { finishDone(null) }

        b.shutter.setOnClickListener { takePhoto() }
    }

    private fun showOverLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )
    }

    private fun openCameraStage() {
        b.messageStage.visibility = View.GONE
        b.cameraStage.visibility = View.VISIBLE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) startCamera() else cameraPermission.launch(Manifest.permission.CAMERA)
    }

    private fun startCamera() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            val provider = future.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(b.preview.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(this, R.string.camera_error, Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val capture = imageCapture ?: return
        val dir = File(filesDir, "isbot").apply { mkdirs() }
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(dir, "${task.id}_$stamp.jpg")
        val options = ImageCapture.OutputFileOptions.Builder(file).build()

        b.shutter.isEnabled = false
        capture.takePicture(
            options, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    finishDone(file.absolutePath)
                }

                override fun onError(exc: ImageCaptureException) {
                    b.shutter.isEnabled = true
                    Toast.makeText(
                        this@AlarmActivity, R.string.photo_failed, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun finishDone(photoPath: String?) {
        completed = true
        Prefs.markDone(this, task.id, photoPath ?: "-")
        stopService(Intent(this, AlarmService::class.java))
        startService(Intent(this, AlarmService::class.java).setAction(AlarmService.ACTION_STOP))
        Toast.makeText(this, R.string.done_toast, Toast.LENGTH_SHORT).show()
        finish()
    }

    /** Orqaga tugmasi ishlamaydi. */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        Toast.makeText(this, R.string.cannot_close, Toast.LENGTH_SHORT).show()
    }

    /** Ovoz tugmalari bilan ovozni pasaytirib qochib bo'lmaydi. */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_POWER -> true
            else -> super.dispatchKeyEvent(event)
        }
    }

    /** "Home" bosib chiqib ketsa — oyna qaytib keladi. */
    override fun onStop() {
        super.onStop()
        if (!completed && task.proofRequired) {
            val back = Intent(this, AlarmActivity::class.java).apply {
                putExtra(AlarmReceiver.EXTRA_TASK_ID, task.id)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }
            startActivity(back)
        }
    }
}
