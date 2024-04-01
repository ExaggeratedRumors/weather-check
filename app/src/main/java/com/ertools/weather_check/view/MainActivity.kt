package com.ertools.weather_check.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.Favourities
import com.ertools.weather_check.model.FetchManager
import com.ertools.weather_check.model.Location

class MainActivity : AppCompatActivity() {
    val favourities = Favourities()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val testLocation = Location("London", 51.5074, 0.1278)
        val fm = FetchManager(this, testLocation)
        val data = fm.fetchWeatherData()
        println(data.toString())
    }

    fun initializeFragments() {
        val mainDataFragment = MainDataFragment()
        val secondDataFragment = SecondDataFragment()
        val forecastFragment = ForecastFragment()
    }
}