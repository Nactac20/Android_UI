package com.example.lab2.presentation.sdui

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2.data.sdui.SduiComponentDto
import com.google.android.material.button.MaterialButton
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.textview.MaterialTextView

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
        return when (viewType) {
            VT_TITLE -> TitleVH(createTitle(parent.context))
            VT_TEXT -> TextVH(createBodyText(parent.context))
            VT_BUTTON -> ButtonVH(createButton(parent.context))
            VT_DIVIDER -> DividerVH(createDivider(parent.context))
            VT_IMAGE -> ImageVH(createImage(parent.context))
            VT_STAT -> StatVH(createStatRow(parent.context))
            else -> UnknownVH(createUnknown(parent.context))
        }
    }

    override fun onBindViewHolder(holder: SduiVH, position: Int) {
        val item = items[position]
        holder.bind(item)
        if (item.action != null) {
            holder.itemView.setOnClickListener { onClick(item) }
        } else {
            holder.itemView.setOnClickListener(null)
        }
        onImpression(item)
    }

    sealed class SduiVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(component: SduiComponentDto)
    }

    class TitleVH(private val tv: MaterialTextView) : SduiVH(tv) {
        override fun bind(component: SduiComponentDto) {
            tv.text = component.label.orEmpty()
        }
    }

    class TextVH(private val tv: MaterialTextView) : SduiVH(tv) {
        override fun bind(component: SduiComponentDto) {
            tv.text = component.label.orEmpty()
        }
    }

    class ButtonVH(private val btn: MaterialButton) : SduiVH(btn) {
        override fun bind(component: SduiComponentDto) {
            btn.text = component.label.orEmpty()
        }
    }

    class DividerVH(divider: MaterialDivider) : SduiVH(divider) {
        override fun bind(component: SduiComponentDto) = Unit
    }

    class ImageVH(image: ImageView) : SduiVH(image) {
        override fun bind(component: SduiComponentDto) = Unit
    }

    class StatVH(private val row: LinearLayout) : SduiVH(row) {
        private val labelView: MaterialTextView = row.getChildAt(0) as MaterialTextView
        private val valueView: MaterialTextView = row.getChildAt(1) as MaterialTextView

        override fun bind(component: SduiComponentDto) {
            labelView.text = component.label.orEmpty()
            valueView.text = component.payload?.get("value")?.toString().orEmpty()
        }
    }

    class UnknownVH(private val tv: MaterialTextView) : SduiVH(tv) {
        override fun bind(component: SduiComponentDto) {
            tv.text = "Unknown: ${component.type}"
        }
    }

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

    private fun defaultLp(): ViewGroup.LayoutParams =
        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    private fun dp(ctx: Context, value: Int): Int =
        (value * ctx.resources.displayMetrics.density).toInt()

    private fun createTitle(ctx: Context): MaterialTextView =
        MaterialTextView(ctx).apply {
            layoutParams = defaultLp()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            val pad = dp(ctx, 16)
            setPadding(pad, pad, pad, pad)
        }

    private fun createBodyText(ctx: Context): MaterialTextView =
        MaterialTextView(ctx).apply {
            layoutParams = defaultLp()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            val pad = dp(ctx, 16)
            setPadding(pad, pad, pad, pad)
        }

    private fun createButton(ctx: Context): MaterialButton =
        MaterialButton(ctx).apply {
            layoutParams = defaultLp()
            val pad = dp(ctx, 16)
            setPadding(pad, pad, pad, pad)
        }

    private fun createDivider(ctx: Context): MaterialDivider =
        MaterialDivider(ctx).apply {
            layoutParams = defaultLp()
        }

    private fun createImage(ctx: Context): ImageView =
        ImageView(ctx).apply {
            layoutParams = defaultLp()
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

    private fun createStatRow(ctx: Context): LinearLayout =
        LinearLayout(ctx).apply {
            layoutParams = defaultLp()
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            val pad = dp(ctx, 16)
            setPadding(pad, pad, pad, pad)
            addView(
                MaterialTextView(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                }
            )
            addView(
                MaterialTextView(ctx).apply {
                    layoutParams =
                        LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                }
            )
        }

    private fun createUnknown(ctx: Context): MaterialTextView =
        MaterialTextView(ctx).apply {
            layoutParams = defaultLp()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            val pad = dp(ctx, 16)
            setPadding(pad, pad, pad, pad)
        }
}

