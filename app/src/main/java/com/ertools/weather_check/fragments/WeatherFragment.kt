package com.ertools.weather_check.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.utils.chooseIcon
import com.ertools.weather_check.utils.serializable
import com.ertools.weather_check.utils.setDescription
import com.ertools.weather_check.utils.setTemperature
import com.ertools.weather_check.utils.timestampToTime

class WeatherFragment : Fragment() {
    private lateinit var view: View
    private var unitRes = R.string.temperature_celsius_long

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.view = inflater.inflate(R.layout.fragment_weather, container, false)
        println("IN WEATHER ONCREATE")
        arguments?.getBoolean(Utils.STORE_UNIT_STATE)?.let { isCelsius ->
            unitRes = if (isCelsius) R.string.temperature_celsius_long else R.string.temperature_kelvin_long
        }
        arguments?.serializable<WeatherDTO>(Utils.STORE_WEATHER_DTO)?.let { dto ->
            updateData(dto)
        }
        return this.view
    }

    private fun updateData(dto: WeatherDTO) {
        /** Location **/
        val locationName = view.findViewById<TextView>(R.id.weather_localization_name)
        locationName.text = dto.name
        val locationCoordinates = view.findViewById<TextView>(R.id.weather_coordinates)
        val coordinates = "(${dto.coord.lat}, ${dto.coord.lon})"
        locationCoordinates.text = coordinates

        /** Time **/
        val time = view.findViewById<TextView>(R.id.weather_time)
        time.text = timestampToTime(dto.dt)

        /** Temperature **/
        val temperature = view.findViewById<TextView>(R.id.weather_temperature)
        temperature.text = getString(unitRes, setTemperature(dto.main.temp))

        /** Pressure **/
        val pressure = view.findViewById<TextView>(R.id.weather_pressure)
        pressure.text = getString(R.string.pressure_format, dto.main.pressure)

        /** Description **/
        val description = view.findViewById<TextView>(R.id.weather_description)
        description.text = setDescription(dto.weather[0].description)

        /** Icon **/
        val icon = view.findViewById<ImageView>(R.id.weather_icon)
        icon.setImageResource(chooseIcon(dto.weather[0].description))
    }
}