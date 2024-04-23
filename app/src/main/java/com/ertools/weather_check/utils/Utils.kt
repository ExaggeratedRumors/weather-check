package com.ertools.weather_check.utils

object Utils {
    /** Pathing **/
    const val WEATHER_DATA_PATH = "weather_data.json"
    const val FORECAST_DATA_PATH = "forecast_data.json"
    const val HISTORY_PATH = "history.json"
    const val FETCH_LOGS_PATH = "fetch_logs.json"

    /** Storage **/
    const val STORE_FAVORITE_LOCATION = "store_favorite_location"
    const val STORE_WEATHER_DTO = "store_weather_dto"
    const val STORE_FORECAST_DTO = "store_forecast_dto"

    /** Permissions **/
    const val PERMISSIONS_REQUEST_LOCATION = 100

    /** API **/
    const val API_KEY = "f106884003f70637150d0c02a289da8c"

    /** Connection **/
    const val WEATHER_FETCH_DIFF_SEC = 300
    const val FORECAST_FETCH_DIFF_SEC = 3600

    /** URL **/
    const val WEATHER_URL = "https://pro.openweathermap.org/data/2.5/weather?"
    const val FORECAST_URL = "https://pro.openweathermap.org/data/2.5/forecast?"
    const val API_KEY_SUFFIX = "&APPID=$API_KEY"
    fun getCityInterfix(city: String) = "q=$city"
    fun getCoordinatesInterfix(lat: Double, lon: Double) = "lat=$lat&lon=$lon"
}