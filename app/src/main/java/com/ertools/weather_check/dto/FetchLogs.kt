package com.ertools.weather_check.dto

import java.io.Serializable

data class FetchLogs (
    val weatherTimestamp: Long,
    val forecastTimestamp: Long
) : Serializable