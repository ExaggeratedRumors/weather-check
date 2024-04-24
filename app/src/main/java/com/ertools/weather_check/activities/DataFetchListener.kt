package com.ertools.weather_check.activities

import com.ertools.weather_check.dto.Location

interface DataFetchListener {
    fun notifyLocationChanged(location: Location?)
    fun requestLocation()
    fun <T> notifyDataFetchSuccess(dto: T, valueType: Class<T>)
    fun <T> notifyDataFetchFailure(valueType: Class<T>)
    fun requestData(forceFetchFromServer: Boolean = false)
}