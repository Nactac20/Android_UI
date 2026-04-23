package com.example.lab2.presentation.sdui

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.example.lab2.data.sdui.SduiComponentDto
import com.google.android.material.button.MaterialButton
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.textview.MaterialTextView

object SduiFactory {

    fun createView(parent: ViewGroup, component: SduiComponentDto): View {
        val ctx = parent.context
        return when (component.type) {
            "title" -> createTitle(ctx)
            "text" -> createBodyText(ctx)
            "button" -> createButton(ctx)
            "divider" -> createDivider(ctx)
            "image" -> createImage(ctx)
            "stat" -> createStatRow(ctx)
            else -> createUnknown(ctx)
        }
    }

    fun bindView(view: View, component: SduiComponentDto) {
        when (component.type) {
            "title" -> (view as TextView).text = component.label.orEmpty()
            "text" -> (view as TextView).text = component.label.orEmpty()
            "button" -> (view as MaterialButton).text = component.label.orEmpty()
            "divider" -> Unit
            "image" -> Unit
            "stat" -> bindStatRow(view, component)
            else -> (view as TextView).text = "Unknown: ${component.type}"
        }
    }

    private fun createTitle(ctx: Context): MaterialTextView =
        MaterialTextView(ctx).apply {
            layoutParams = defaultLp()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            setPadding(dp(ctx, 16))
        }

    private fun createBodyText(ctx: Context): MaterialTextView =
        MaterialTextView(ctx).apply {
            layoutParams = defaultLp()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setPadding(dp(ctx, 16))
        }

    private fun createButton(ctx: Context): MaterialButton =
        MaterialButton(ctx).apply {
            layoutParams = defaultLp()
            val pad = dp(ctx, 16)
            setPadding(pad)
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
            setPadding(pad)
            addView(
                MaterialTextView(ctx).apply {
                    id = android.R.id.text1
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                }
            )
            addView(
                MaterialTextView(ctx).apply {
                    id = android.R.id.text2
                    layoutParams =
                        LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                }
            )
        }

    private fun bindStatRow(view: View, component: SduiComponentDto) {
        val row = view as LinearLayout
        val labelView = row.getChildAt(0) as TextView
        val valueView = row.getChildAt(1) as TextView
        labelView.text = component.label.orEmpty()
        valueView.text = component.payload?.get("value")?.toString().orEmpty()
    }

    private fun createUnknown(ctx: Context): MaterialTextView =
        MaterialTextView(ctx).apply {
            layoutParams = defaultLp()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setPadding(dp(ctx, 16))
        }

    private fun defaultLp(): ViewGroup.LayoutParams =
        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    private fun dp(ctx: Context, value: Int): Int =
        (value * ctx.resources.displayMetrics.density).toInt()
}

