package com.ertools.weather_check.activities

import com.ertools.weather_check.dto.Location

interface LocationListener {
    fun notifyLocationChanged(location: Location?)
    fun requestLocation()
}