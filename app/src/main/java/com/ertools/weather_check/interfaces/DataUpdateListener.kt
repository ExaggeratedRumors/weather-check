package com.ertools.weather_check.interfaces

interface DataUpdateListener {
    fun <T> updateData(dto: T)
}