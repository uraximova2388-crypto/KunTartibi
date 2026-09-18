package uz.kuntartibi

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

/** Bugun suratga olib tasdiqlagan ishlaring. */
class ProofGalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scroll = android.widget.ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        scroll.addView(root)
        setContentView(scroll)

        var any = false
        Schedule.tasks.forEach { task ->
            val path = Prefs.photoOf(this, task.id) ?: return@forEach
            any = true
            root.addView(TextView(this).apply {
                text = "${task.timeLabel()} · ${task.title}"
                textSize = 16f
                setPadding(0, 24, 0, 8)
            })
            val f = File(path)
            if (f.exists()) {
                val opts = BitmapFactory.Options().apply { inSampleSize = 4 }
                root.addView(ImageView(this).apply {
                    setImageBitmap(BitmapFactory.decodeFile(path, opts))
                    adjustViewBounds = true
                })
            }
        }
        if (!any) {
            root.addView(TextView(this).apply {
                text = getString(R.string.no_proofs)
                textSize = 16f
            })
        }
    }
}
