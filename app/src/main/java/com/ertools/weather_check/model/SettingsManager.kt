package com.ertools.weather_check.model

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.AppSettings
import com.ertools.weather_check.interfaces.SettingsUpdateListener
import com.ertools.weather_check.utils.Utils
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsManager(
    private val context: Context,
    private val listener: SettingsUpdateListener
) {
    fun openSettings() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.input_window_title))

        val unitsInput = SwitchMaterial(context)
        val intervalInput = EditText(context)

        unitsInput.text = context.getString(R.string.input_window_switch_on)
        unitsInput.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                unitsInput.text = context.getString(R.string.input_window_switch_on)
            } else {
                unitsInput.text = context.getString(R.string.input_window_switch_off)
            }
        }
        intervalInput.hint = context.getString(R.string.input_window_interval)

        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(unitsInput)
        layout.addView(intervalInput)
        builder.setView(layout)

        builder.setPositiveButton(context.getString(R.string.input_window_ok)) { _, _ ->
            val interval = intervalInput.text.toString().toLongOrNull()
            val units = unitsInput.isChecked

            if(interval == null || interval < 0) {
                Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val newSettings = AppSettings(units, interval)
            DataManager.writeObject(Utils.SETTINGS_PATH, newSettings, context)
            listener.updateSettings(newSettings)
        }

        builder.setNegativeButton(context.getString(R.string.input_window_cancel)) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}