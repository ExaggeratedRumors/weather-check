package com.ertools.weather_check.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ertools.weather_check.R
import com.ertools.weather_check.activities.LocationListener
import com.ertools.weather_check.dto.WeatherDTO

class SecondDataFragment(
    private val listener: LocationListener,
    private val weatherData: WeatherDTO
) : Fragment() {
    private lateinit var view : View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.view = inflater.inflate(R.layout.fragment_second_data, container, false)
        initializeWeatherData()
        return this.view
    }

    private fun initializeWeatherData() {
        val windStrength = view.findViewById<TextView>(R.id.second_wind_strength)
        windStrength.text = weatherData.wind.speed.toString()

        val windDirection = view.findViewById<TextView>(R.id.second_wind_direction)
        windDirection.text = weatherData.wind.deg.toString()

        val humidity = view.findViewById<TextView>(R.id.second_humidity)
        humidity.text = weatherData.main.humidity.toString()

        val visibility = view.findViewById<TextView>(R.id.second_visibility)
        visibility.text = weatherData.visibility.toString()
    }
}