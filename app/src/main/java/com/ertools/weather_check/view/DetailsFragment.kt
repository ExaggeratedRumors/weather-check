package com.ertools.weather_check.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.AppSettings
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.interfaces.DataUpdateListener
import com.ertools.weather_check.interfaces.SettingsUpdateListener
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.utils.serializable
import com.ertools.weather_check.utils.timestampToTime

class DetailsFragment : Fragment(), DataUpdateListener, SettingsUpdateListener {
    private var view: View? = null
    private var appSettings: AppSettings = AppSettings()
    private var weatherData: WeatherDTO? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.view = inflater.inflate(R.layout.fragment_details, container, false)
        arguments?.serializable<AppSettings>(Utils.STORE_SETTINGS)?.let { settings ->
            updateSettings(settings)
        }
        arguments?.serializable<WeatherDTO>(Utils.STORE_WEATHER_DTO)?.let { dto ->
            updateData(dto)
        }
        return this.view
    }

    override fun <T> updateData(dto: T) {
        if(dto !is WeatherDTO) return
        this.weatherData = dto
        if(this.view == null) return
        createView(dto)
    }

    private fun createView(dto: WeatherDTO) {
        val windStrength = view?.findViewById<TextView>(R.id.details_wind_speed)
        windStrength?.text = getString(R.string.wind_speed_format, dto.wind.speed)

        val windDirection = view?.findViewById<TextView>(R.id.details_wind_direction)
        windDirection?.text = getString(R.string.wind_direction_format, dto.wind.deg)

        val humidity = view?.findViewById<TextView>(R.id.details_humidity)
        humidity?.text = getString(R.string.humidity_format, dto.main.humidity)

        val sunrise = view?.findViewById<TextView>(R.id.details_sunrise)
        sunrise?.text = timestampToTime(dto.sys.sunrise).split(" ")[2]

        val sunset = view?.findViewById<TextView>(R.id.details_sunset)
        sunset?.text = timestampToTime(dto.sys.sunset).split(" ")[2]
    }

    override fun updateSettings(appSettings: AppSettings) {
        this.appSettings = appSettings
        updateData(this.weatherData)
    }
}