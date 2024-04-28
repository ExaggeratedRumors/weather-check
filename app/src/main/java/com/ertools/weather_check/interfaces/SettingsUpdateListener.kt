package com.ertools.weather_check.interfaces

import com.ertools.weather_check.dto.AppSettings

interface SettingsUpdateListener {
    fun updateSettings(appSettings: AppSettings)
}