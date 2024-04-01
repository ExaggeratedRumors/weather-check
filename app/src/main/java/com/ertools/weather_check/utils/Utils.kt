package com.ertools.weather_check.utils

object Utils {
    const val WEATHER_DATA_PATH = "weather_data.json"
    const val FORECAST_DATA_PATH = "forecast_data.json"
    const val FAVOURITE_PATH = "favourite.json"
    const val API_KEY = "f106884003f70637150d0c02a289da8c"
    const val connection_date = "2021-07-01 00:00:00"

    //val API_KEY : String = YamlManager.readYamlObject(CONFIGURATION_PATH, String::class.java) as String
    fun getWeatherUrl(lat: Double, lon: Double) =
        "https://pro.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&APPID=$API_KEY"
    fun getForecastUrl(lat: Double, lan: Double) =
        "https://pro.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lan&APPID=$API_KEY"
}