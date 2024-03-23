package com.ertools.weather_check.utils

object Utils {
    const val WEATHER_DATA_PATH = "weather_data.json"
    const val FORECAST_DATA_PATH = "forecast_data.json"
    const val CONFIGURATION_PATH = "res/data/config.yaml"
    val API_KEY : String = YamlManager.readYamlObject(CONFIGURATION_PATH, String::class.java)
    fun getWeatherUrl(lat: Double, lon: Double) =
        "https://pro.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&APPID=$API_KEY"
    fun getForecastUrl(lat: Double, lan: Double) =
        "https://pro.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lan&APPID=$API_KEY"
}