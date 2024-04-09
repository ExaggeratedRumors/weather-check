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

class MainDataFragment : Fragment() {
    private lateinit var view: View
    private lateinit var selectedLocation: Location
    private var savedInstance: Bundle? = null
    private lateinit var weatherData: WeatherDTO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.view = inflater.inflate(R.layout.fragment_main_data, container, false)
        this.savedInstance = savedInstanceState
        fetchSelectedLocation()
        initializeLocation()
        fetchWeatherData()
        initializeTime()
        return this.view
    }

    private fun fetchSelectedLocation() {
        this.savedInstance?.serializable<Location>(Utils.STORE_FAVORITE_LOCATION)?.let {
            this.selectedLocation = it
        } ?: throw Exception("No location selected")
    }

    private fun initializeLocation() {
        val locationName = view.findViewById<TextView>(R.id.main_localization_name)
        locationName.text = selectedLocation.name
        val locationCoordinates = view.findViewById<TextView>(R.id.main_coordinates)
        val coordinates = "(${selectedLocation.lat}, ${selectedLocation.lon})"
        locationCoordinates.text = coordinates
    }

    private fun fetchWeatherData() {
        val fetchManager = FetchManager(requireContext())
        weatherData = fetchManager.fetchWeatherData(selectedLocation)
    }

    private fun initializeTime() {
        val time = view.findViewById<TextView>(R.id.main_time)
        time.text = timestampToTime(weatherData.dt)
    }
}