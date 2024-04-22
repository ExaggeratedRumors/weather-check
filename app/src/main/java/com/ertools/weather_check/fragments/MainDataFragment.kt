package com.ertools.weather_check.fragments

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

class MainDataFragment : Fragment() {
    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.view = inflater.inflate(R.layout.fragment_main_data, container, false)
        savedInstanceState?.serializable<WeatherDTO>(Utils.STORE_WEATHER_DTO)?.let { dto ->
            updateData(dto)
        }
        return this.view
    }

    private fun updateData(dto: WeatherDTO) {
        /** Location **/
        val locationName = view.findViewById<TextView>(R.id.main_localization_name)
        locationName.text = dto.name
        val locationCoordinates = view.findViewById<TextView>(R.id.main_coordinates)
        val coordinates = "(${dto.coord.lat}, ${dto.coord.lon})"
        locationCoordinates.text = coordinates

        /** Time **/
        val time = view.findViewById<TextView>(R.id.main_time)
        time.text = timestampToTime(dto.dt)

        /** Temperature **/
        val temperature = view.findViewById<TextView>(R.id.main_temperature)
        temperature.text = dto.main.temp.toString()
    }
}