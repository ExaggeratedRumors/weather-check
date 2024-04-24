package com.ertools.weather_check.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.utils.chooseIcon
import com.ertools.weather_check.utils.serializable
import com.ertools.weather_check.utils.setDescription
import com.ertools.weather_check.utils.setTemperature
import com.ertools.weather_check.utils.timestampToTime

class ForecastFragment: Fragment() {
    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.view = inflater.inflate(R.layout.fragment_forecast, container, false)
        arguments?.serializable<ForecastDTO>(Utils.STORE_FORECAST_DTO)?.let { dto ->
            updateData(dto)
        }
        return this.view
    }

    private fun updateData(dto: ForecastDTO) {
        /** Dates **/
        val day1 = view.findViewById<TextView>(R.id.day_1)
        day1.text = timestampToTime(dto.list[0].dt)
        val day2 = view.findViewById<TextView>(R.id.day_2)
        day2.text = timestampToTime(dto.list[8].dt)
        val day3 = view.findViewById<TextView>(R.id.day_3)
        day3.text = timestampToTime(dto.list[16].dt)
        val day4 = view.findViewById<TextView>(R.id.day_4)
        day4.text = timestampToTime(dto.list[24].dt)

        /** Temperatures **/
        val temperature1 = view.findViewById<TextView>(R.id.temperature_1)
        temperature1.text = getString(R.string.temperature_format_short, setTemperature(dto.list[0].main.temp))
        val temperature2 = view.findViewById<TextView>(R.id.temperature_2)
        temperature2.text = getString(R.string.temperature_format_short, setTemperature(dto.list[8].main.temp))
        val temperature3 = view.findViewById<TextView>(R.id.temperature_3)
        temperature3.text = getString(R.string.temperature_format_short, setTemperature(dto.list[16].main.temp))
        val temperature4 = view.findViewById<TextView>(R.id.temperature_4)
        temperature4.text = getString(R.string.temperature_format_short, setTemperature(dto.list[24].main.temp))

        /** Descriptions **/
        val weather1 = view.findViewById<TextView>(R.id.weather_1)
        weather1.text = setDescription(dto.list[0].weather[0].description)
        val weather2 = view.findViewById<TextView>(R.id.weather_2)
        weather2.text = setDescription(dto.list[8].weather[0].description)
        val weather3 = view.findViewById<TextView>(R.id.weather_3)
        weather3.text = setDescription(dto.list[16].weather[0].description)
        val weather4 = view.findViewById<TextView>(R.id.weather_4)
        weather4.text = setDescription(dto.list[24].weather[0].description)

        /** Icons **/
        val icon1 = view.findViewById<ImageView>(R.id.icon_1)
        icon1.setImageResource(chooseIcon(dto.list[0].weather[0].description))
        val icon2 = view.findViewById<ImageView>(R.id.icon_2)
        icon2.setImageResource(chooseIcon(dto.list[8].weather[0].description))
        val icon3 = view.findViewById<ImageView>(R.id.icon_3)
        icon3.setImageResource(chooseIcon(dto.list[16].weather[0].description))
        val icon4 = view.findViewById<ImageView>(R.id.icon_4)
        icon4.setImageResource(chooseIcon(dto.list[24].weather[0].description))
    }
}