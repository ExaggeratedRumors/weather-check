package com.ertools.weather_check.interfaces

import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.model.FetchManager

interface DataFetchListener {
    fun notifyLocationChanged(location: Location?)
    fun requestLocation()
    fun <T> notifyDataFetchSuccess(dto: T, valueType: Class<T>)
    fun <T> notifyDataFetchFailure(valueType: Class<T>, message: String)
    fun requestData(forceFetch: FetchManager.ForceFetch = FetchManager.ForceFetch.NONE)
}