package com.ertools.weather_check.utils

object Utils {
    /** Pathing **/
    const val WEATHER_DATA_PATH = "_weather.json"
    const val FORECAST_DATA_PATH = "_forecast.json"
    const val HISTORY_PATH = "history.json"

    /** Storage **/
    const val STORE_WEATHER_DTO = "store_weather_dto"
    const val STORE_FORECAST_DTO = "store_forecast_dto"
    const val STORE_UNIT_STATE = "store_unit_state"
    const val STORE_LOCATION = "store_location"
    const val STORE_FRAGMENT_CART = "store_fragment_cart"

    /** API **/
    private const val API_KEY = "f106884003f70637150d0c02a289da8c"

    /** Connection **/
    const val WEATHER_FETCH_DIFF_SEC = 300
    const val FORECAST_FETCH_DIFF_SEC = 3600

    /** URL **/
    const val WEATHER_URL = "https://pro.openweathermap.org/data/2.5/weather?"
    const val FORECAST_URL = "https://pro.openweathermap.org/data/2.5/forecast?"
    const val API_KEY_SUFFIX = "&APPID=$API_KEY"
    fun getCityAffix(city: String) = "q=$city"
    fun getCoordinatesAffix(lat: Double, lon: Double) = "lat=$lat&lon=$lon"
}