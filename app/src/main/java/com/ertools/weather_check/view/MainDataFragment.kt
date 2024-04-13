package com.ertools.weather_check.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.model.FetchManager
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.utils.serializable
import com.ertools.weather_check.utils.timestampToTime

class MainDataFragment(
    private val listener: LocationListener,
    private val location: Location
) : Fragment() {
    private lateinit var view: View
    private var savedInstance: Bundle? = null
    private lateinit var weatherData: WeatherDTO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.view = inflater.inflate(R.layout.fragment_main_data, container, false)
        this.savedInstance = savedInstanceState
        initializeLocation()
        fetchWeatherData()
        initializeTime()
        initializeTemperature()
        return this.view
    }

    private fun initializeLocation() {
        val locationName = view.findViewById<TextView>(R.id.main_localization_name)
        locationName.text = location.name
        val locationCoordinates = view.findViewById<TextView>(R.id.main_coordinates)
        val coordinates = "(${location.lat}, ${location.lon})"
        locationCoordinates.text = coordinates
    }

    private fun fetchWeatherData() {
        val fetchManager = FetchManager(requireContext())
        weatherData = fetchManager.fetchWeatherData(location)
    }

    private fun initializeTime() {
        val time = view.findViewById<TextView>(R.id.main_time)
        time.text = timestampToTime(weatherData.dt)
    }

    private fun initializeTemperature() {
        val temperature = view.findViewById<TextView>(R.id.main_temperature)
        temperature.text = weatherData.main.temp.toString()
    }
}