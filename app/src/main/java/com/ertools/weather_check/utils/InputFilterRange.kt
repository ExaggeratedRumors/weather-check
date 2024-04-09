package com.ertools.weather_check.utils

import android.text.InputFilter
import android.text.Spanned

class InputFilterRange(private val min: Double, private val max: Double) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toDouble()
            if (isInRange(min, max, input)) {
                return null
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun isInRange(min: Double, max: Double, input: Double): Boolean {
        return input in min..max
    }
}