package com.ertools.weather_check.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.AppSettings
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.interfaces.DataUpdateListener
import com.ertools.weather_check.interfaces.SettingsUpdateListener
import com.ertools.weather_check.utils.*

class WeatherFragment : Fragment(), SettingsUpdateListener, DataUpdateListener {
    private var view: View? = null
    private var unitRes = R.string.temperature_celsius
    private var appSettings: AppSettings = AppSettings()
    private var weatherData: WeatherDTO? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.view = inflater.inflate(R.layout.fragment_weather, container, false)
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
        println("TEST: UPDATE WEATHER FRAGMENT")
        this.weatherData = dto
        if(this.view == null) return
        createView(dto)
    }

    private fun createView(data: WeatherDTO) {
        /** Location **/
        val locationName = view?.findViewById<TextView>(R.id.weather_localization_name)
        locationName?.text = data.name
        val locationCoordinates = view?.findViewById<TextView>(R.id.weather_coordinates)
        val coordinates = "(${data.coord.lat}, ${data.coord.lon})"
        locationCoordinates?.text = coordinates

        /** Time **/
        val time = view?.findViewById<TextView>(R.id.weather_time)
        time?.text = timestampToTime(data.dt)

        /** Temperature **/
        val temperature = view?.findViewById<TextView>(R.id.weather_temperature)
        temperature?.text = getString(unitRes, setTemperature(
            if(this.appSettings.isSIUnit) kelvinToCelsius(data.main.temp)
            else kelvinToFahrenheit(data.main.temp)
        ))

        /** Pressure **/
        val pressure = view?.findViewById<TextView>(R.id.weather_pressure)
        pressure?.text = getString(R.string.pressure_format, data.main.pressure)

        /** Description **/
        val description = view?.findViewById<TextView>(R.id.weather_description)
        description?.text = setDescription(data.weather[0].description)

        /** Icon **/
        val icon = view?.findViewById<ImageView>(R.id.weather_icon)
        icon?.setImageResource(chooseIcon(data.weather[0].description))
    }

    override fun updateSettings(appSettings: AppSettings) {
        this.appSettings = appSettings
        unitRes = if (appSettings.isSIUnit) R.string.temperature_celsius else R.string.temperature_fahrenheit
    }
}