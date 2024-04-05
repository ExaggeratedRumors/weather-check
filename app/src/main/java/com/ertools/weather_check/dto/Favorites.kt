package com.ertools.weather_check.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
class Favorites {
    @JsonProperty
    val locations = mutableListOf<Location>()

    @JsonIgnore
    fun modify(block: MutableList<Location>.() -> Unit) {
        synchronized(locations) {
            block(locations)
        }
    }
}