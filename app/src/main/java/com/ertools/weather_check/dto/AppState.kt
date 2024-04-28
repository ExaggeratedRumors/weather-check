package com.ertools.weather_check.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.io.Serializable

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class AppState (
    val location: Location? = null,
    val viewState: ViewState = ViewState.NONE,
    val selectedPage: Int = 0,
    val weatherDTO: WeatherDTO? = null,
    val forecastDTO: ForecastDTO? = null
) : Serializable

enum class ViewState { WEATHER, MENU, NONE }

