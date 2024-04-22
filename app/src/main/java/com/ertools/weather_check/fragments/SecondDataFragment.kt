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
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.utils.serializable

class SecondDataFragment : Fragment() {
    private lateinit var view : View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_second_data, container, false)
        savedInstanceState?.serializable<WeatherDTO>(Utils.STORE_WEATHER_DTO)?.let { dto ->
            updateData(dto)
        }
        return this.view
    }

    private fun updateData(dto: WeatherDTO) {
        val windStrength = view.findViewById<TextView>(R.id.second_wind_strength)
        windStrength.text = dto.wind.speed.toString()

        val windDirection = view.findViewById<TextView>(R.id.second_wind_direction)
        windDirection.text = dto.wind.deg.toString()

        val humidity = view.findViewById<TextView>(R.id.second_humidity)
        humidity.text = dto.main.humidity.toString()

        val visibility = view.findViewById<TextView>(R.id.second_visibility)
        visibility.text = dto.visibility.toString()
    }
}