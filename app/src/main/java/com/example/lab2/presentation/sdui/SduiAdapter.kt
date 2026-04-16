package com.example.lab2.presentation.sdui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2.data.sdui.SduiComponentDto

class SduiAdapter(
    private val onImpression: (SduiComponentDto) -> Unit,
    private val onClick: (SduiComponentDto) -> Unit
) : RecyclerView.Adapter<SduiAdapter.SduiVH>() {

    private var items: List<SduiComponentDto> = emptyList()

    fun submit(newItems: List<SduiComponentDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return typeToViewType(items[position].type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SduiVH {
        val component = SduiComponentDto(id = "template", type = viewTypeToType(viewType))
        val view = SduiFactory.createView(parent, component)
        return SduiVH(view)
    }

    override fun onBindViewHolder(holder: SduiVH, position: Int) {
        val item = items[position]
        SduiFactory.bindView(holder.itemView, item)
        holder.itemView.setOnClickListener { onClick(item) }
        onImpression(item)
    }

    class SduiVH(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun typeToViewType(type: String): Int = when (type) {
        "title" -> VT_TITLE
        "text" -> VT_TEXT
        "button" -> VT_BUTTON
        "divider" -> VT_DIVIDER
        "image" -> VT_IMAGE
        "stat" -> VT_STAT
        else -> VT_UNKNOWN
    }

    private fun viewTypeToType(viewType: Int): String = when (viewType) {
        VT_TITLE -> "title"
        VT_TEXT -> "text"
        VT_BUTTON -> "button"
        VT_DIVIDER -> "divider"
        VT_IMAGE -> "image"
        VT_STAT -> "stat"
        else -> "unknown"
    }

    private companion object {
        const val VT_TITLE = 1
        const val VT_TEXT = 2
        const val VT_BUTTON = 3
        const val VT_DIVIDER = 4
        const val VT_IMAGE = 5
        const val VT_STAT = 6
        const val VT_UNKNOWN = 100
    }
}

