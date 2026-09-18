package uz.kuntartibi

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.kuntartibi.databinding.ItemTaskBinding

class TaskAdapter(
    private val context: Context,
    private val onToggle: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.VH>() {

    private var items: List<Task> = emptyList()

    fun submit(list: List<Task>) {
        items = list
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemTaskBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = items[position]
        with(holder.b) {
            time.text = t.timeLabel()
            title.text = t.title
            detail.text = if (t.proofRequired)
                context.getString(R.string.proof_line, t.proofPrompt)
            else t.detail
            done.text = if (Prefs.isDoneToday(context, t.id)) "✓" else ""
            toggle.setOnCheckedChangeListener(null)
            toggle.isChecked = Prefs.isEnabled(context, t.id)
            toggle.setOnCheckedChangeListener { _, on -> onToggle(t, on) }
        }
    }
}
