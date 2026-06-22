package com.sky.app.wear

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

/**
 * Launchable on-watch settings (appears in the app list) for picking what fills
 * the center of the SKY watch face. Writes the choice to [SkyStyle]'s preferences;
 * the running watch face redraws via its preference listener. Built with plain
 * Android views so the :wear module stays Compose-free.
 */
class SkySettingsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(buildUi())
    }

    private fun buildUi(): ScrollView {
        val column = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(16), dp(40), dp(16), dp(40))
        }

        column.addView(TextView(this).apply {
            text = "Center"
            setTextColor(Color.WHITE)
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(10))
        })

        val current = SkyStyle.centerOption(this)
        SkyStyle.CENTER_OPTIONS.forEach { (id, label) ->
            column.addView(Button(this).apply {
                text = labelFor(id, label, current)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dp(6) }
                setOnClickListener {
                    SkyStyle.setCenterOption(this@SkySettingsActivity, id)
                    refresh(column, id)
                }
            })
        }

        return ScrollView(this).apply {
            setBackgroundColor(Color.BLACK)
            addView(column)
        }
    }

    private fun refresh(column: LinearLayout, selectedId: String) {
        // Buttons start at index 1 (index 0 is the title).
        SkyStyle.CENTER_OPTIONS.forEachIndexed { i, (id, label) ->
            (column.getChildAt(i + 1) as? Button)?.text = labelFor(id, label, selectedId)
        }
    }

    private fun labelFor(id: String, label: String, selectedId: String): String =
        if (id == selectedId) "● $label" else label

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
