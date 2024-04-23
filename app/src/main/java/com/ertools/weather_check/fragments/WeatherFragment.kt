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
import com.ertools.weather_check.utils.serializable
import com.ertools.weather_check.utils.timestampToTime

class WeatherFragment : Fragment() {
    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.view = inflater.inflate(R.layout.fragment_weather, container, false)
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
        temperature.text = dto.main.temp.toString()

        /** Pressure **/
        val pressure = view.findViewById<TextView>(R.id.weather_pressure)
        pressure.text = dto.main.pressure.toString()

        /** Description **/
        val description = view.findViewById<TextView>(R.id.weather_description)
        description.text = setDescription(dto.weather[0].description)

        /** Icon **/
        val icon = view.findViewById<ImageView>(R.id.weather_icon)
        icon.setImageResource(chooseIcon(dto.weather[0].description))
    }

    private fun setDescription(raw: String): String {
        val withSpaces = raw.replace("_", " ")
        withSpaces[0].uppercase()
        return withSpaces
    }

    private fun chooseIcon(description: String): Int {
        if(description.contains("thunderstorm"))
            return R.drawable.weather_lightning
        if(description.contains("drizzle"))
            return R.drawable.weather_rainy
        if(description.contains("rain"))
            return R.drawable.weather_pouring
        if(description.contains("snow"))
            return R.drawable.weather_snowy_heavy
        if(description.contains("clouds"))
            return R.drawable.weather_cloudy
        if(description.contains("clear"))
            return R.drawable.weather_sunny
        return R.drawable.weather_fog
    }
}