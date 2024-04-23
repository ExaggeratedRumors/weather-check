package com.ertools.weather_check.utils

import android.os.Build
import android.os.Bundle
import com.ertools.weather_check.R
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

fun timestampToTime(timestamp: Long): String {
    val timeInMs = timestamp * 1000
    val date = java.util.Date(timeInMs)
    val sdf = SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm", Locale.ENGLISH)
    return sdf.format(date)
}

fun kelvinToCelsius(kelvin: Double): Double {
    return kelvin - 273.15
}

fun setTemperature(raw: Double): String
        = "${"%.2f".format(kelvinToCelsius(raw))}°C"

fun setDescription(raw: String): String {
    return raw[0].uppercase() + raw.substring(1).replace("_", " ")
}

fun chooseIcon(description: String): Int {
    if(description.contains("thunderstorm"))
        return R.drawable.weather_lightning
    if(description.contains("drizzle"))
        return R.drawable.weather_rainy
    if(description.contains("rain"))
        return R.drawable.weather_pouring
    if(description.contains("snow"))
        return R.drawable.weather_snowy_heavy
    if(description.contains("clouds"))
        return R.drawable.weather_cloudy
    if(description.contains("clear"))
        return R.drawable.weather_sunny
    return R.drawable.weather_fog
}