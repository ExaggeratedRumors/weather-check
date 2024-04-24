package com.ertools.weather_check.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.utils.serializable
import com.ertools.weather_check.utils.timestampToTime
import java.util.Locale

class DetailsFragment : Fragment() {
    private lateinit var view : View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_details, container, false)
        println("IN DETAILS ONCREATE")
        arguments?.serializable<WeatherDTO>(Utils.STORE_WEATHER_DTO)?.let { dto ->
            updateData(dto)
        }
        return this.view
    }

    private fun updateData(dto: WeatherDTO) {
        val windStrength = view.findViewById<TextView>(R.id.details_wind_speed)
        windStrength.text = getString(R.string.wind_speed_format, dto.wind.speed)

        val windDirection = view.findViewById<TextView>(R.id.details_wind_direction)
        windDirection.text = getString(R.string.wind_direction_format, dto.wind.deg)

        val humidity = view.findViewById<TextView>(R.id.details_humidity)
        humidity.text = getString(R.string.humidity_format, dto.main.humidity)

        val sunrise = view.findViewById<TextView>(R.id.details_sunrise)
        sunrise.text = timestampToTime(dto.sys.sunrise).split(" ")[2]

        val sunset = view.findViewById<TextView>(R.id.details_sunset)
        sunset.text = timestampToTime(dto.sys.sunset).split(" ")[2]

    }
}