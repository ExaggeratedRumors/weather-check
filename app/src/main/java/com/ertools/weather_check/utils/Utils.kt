package com.ertools.weather_check.utils

object Utils {
    const val CONFIGURATION_PATH = "res/data/config.yaml"
    val API_KEY : String = YamlManager.readYamlObject(CONFIGURATION_PATH, String::class.java)
    fun getApiKey(lat: Int, lon: Int) =
        "https://api.openweathermap.org/data/3.0/onecall?lat=$lat&lon=$lon&exclude=minutely&appid=$API_KEY"
}