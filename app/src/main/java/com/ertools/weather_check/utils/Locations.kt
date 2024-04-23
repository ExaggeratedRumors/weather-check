package com.ertools.weather_check.utils

import com.ertools.weather_check.dto.Location

object Locations {
    val cities = mutableListOf(
        Location("Warszawa", "Warszawa", 52.2297, 21.0122),
        Location("Kraków", "Kraków", 50.0647, 19.9450),
        Location("Wrocław", "Wrocław", 51.1079, 17.0385),
        Location("Poznań", "Poznań", 52.4064, 16.9252),
        Location("Gdańsk", "Gdańsk", 54.3520, 18.6466),
        Location("Szczecin", "Szczecin", 53.4285, 14.5528),
        Location("Bydgoszcz", "Bydgoszcz", 53.1235, 18.0076),
        Location("Lublin", "Lublin", 51.2465, 22.5684),
        Location("Katowice", "Katowice", 50.2649, 19.0238),
        Location("Białystok", "Białystok", 53.1325, 23.1688),
        Location("Gdynia", "Gdynia", 54.5189, 18.5305),
        Location("Częstochowa", "Częstochowa", 50.8110, 19.1200),
        Location("New York", "New York",40.7128, -74.0060),
        Location("Washington", "Washington",38.9072, -77.0369),
        Location("London", "London",51.5074, -0.1278),
        Location("Paris", "Paris",48.8566, 2.3522),
        Location("Berlin", "Berlin",52.5200, 13.4050),
        Location("Rome", "Rome",41.9028, 12.4964),
        Location("Tokyo", "Tokyo",35.6895, 139.6917),
        Location("Beijing", "Beijing",39.9042, 116.4074),
        Location("Moscow", "Moscow",55.7558, 37.6176),
        Location("Sydney", "Sydney",-33.8688, 151.2093),
        Location("Rio de Janeiro", "Rio de Janeiro",-22.9068, -43.1729)
    )
}