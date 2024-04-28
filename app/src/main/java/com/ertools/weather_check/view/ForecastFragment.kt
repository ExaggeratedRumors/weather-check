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
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.interfaces.DataUpdateListener
import com.ertools.weather_check.interfaces.SettingsUpdateListener
import com.ertools.weather_check.utils.*

class ForecastFragment: Fragment(), DataUpdateListener, SettingsUpdateListener {
    private var view: View? = null
    private var appSettings: AppSettings = AppSettings()
    private var unitRes = R.string.temperature_celsius
    private var forecastData: ForecastDTO? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.view = inflater.inflate(R.layout.fragment_forecast, container, false)
        arguments?.serializable<AppSettings>(Utils.STORE_SETTINGS)?.let { settings ->
            updateSettings(settings)
        }
        arguments?.serializable<ForecastDTO>(Utils.STORE_FORECAST_DTO)?.let { dto ->
            updateData(dto)
        }
        return this.view
    }

    override fun <T> updateData(dto: T) {
        if(dto !is ForecastDTO) return
        println("TEST: UPDATE FORECAST FRAGMENT")
        this.forecastData = dto
        if(this.view == null) return
        createView(dto)
    }

    private fun createView(dto: ForecastDTO) {
        /** Dates **/
        val day1 = view?.findViewById<TextView>(R.id.day_1)
        day1?.text = timestampToTime(dto.list[8].dt).split(" ").dropLast(1).joinToString(" ")
        val day2 = view?.findViewById<TextView>(R.id.day_2)
        day2?.text = timestampToTime(dto.list[16].dt).split(" ").dropLast(1).joinToString(" ")
        val day3 = view?.findViewById<TextView>(R.id.day_3)
        day3?.text = timestampToTime(dto.list[24].dt).split(" ").dropLast(1).joinToString(" ")
        val day4 = view?.findViewById<TextView>(R.id.day_4)
        day4?.text = timestampToTime(dto.list[32].dt).split(" ").dropLast(1).joinToString(" ")

        /** Temperatures **/
        val temperature1 = view?.findViewById<TextView>(R.id.temperature_1)
        temperature1?.text = getString(this.unitRes, setTemperature(
            if(this.appSettings.isSIUnit) kelvinToCelsius(dto.list[8].main.temp)
            else kelvinToFahrenheit(dto.list[8].main.temp)
        ))
        val temperature2 = view?.findViewById<TextView>(R.id.temperature_2)
        temperature2?.text =  getString(this.unitRes, setTemperature(
            if(this.appSettings.isSIUnit) kelvinToCelsius(dto.list[16].main.temp)
            else kelvinToFahrenheit(dto.list[16].main.temp)
        ))
        val temperature3 = view?.findViewById<TextView>(R.id.temperature_3)
        temperature3?.text = getString(this.unitRes, setTemperature(
            if(this.appSettings.isSIUnit) kelvinToCelsius(dto.list[24].main.temp)
            else kelvinToFahrenheit(dto.list[24].main.temp)
        ))
        val temperature4 = view?.findViewById<TextView>(R.id.temperature_4)
        temperature4?.text = getString(this.unitRes, setTemperature(
            if(this.appSettings.isSIUnit) kelvinToCelsius(dto.list[32].main.temp)
            else kelvinToFahrenheit(dto.list[32].main.temp)
        ))

        /** Descriptions **/
        val weather1 = view?.findViewById<TextView>(R.id.weather_1)
        weather1?.text = setDescription(dto.list[8].weather[0].description)
        val weather2 = view?.findViewById<TextView>(R.id.weather_2)
        weather2?.text = setDescription(dto.list[16].weather[0].description)
        val weather3 = view?.findViewById<TextView>(R.id.weather_3)
        weather3?.text = setDescription(dto.list[24].weather[0].description)
        val weather4 = view?.findViewById<TextView>(R.id.weather_4)
        weather4?.text = setDescription(dto.list[32].weather[0].description)

        /** Icons **/
        val icon1 = view?.findViewById<ImageView>(R.id.icon_1)
        icon1?.setImageResource(chooseIcon(dto.list[8].weather[0].description))
        val icon2 = view?.findViewById<ImageView>(R.id.icon_2)
        icon2?.setImageResource(chooseIcon(dto.list[16].weather[0].description))
        val icon3 = view?.findViewById<ImageView>(R.id.icon_3)
        icon3?.setImageResource(chooseIcon(dto.list[24].weather[0].description))
        val icon4 = view?.findViewById<ImageView>(R.id.icon_4)
        icon4?.setImageResource(chooseIcon(dto.list[32].weather[0].description))
    }

    override fun updateSettings(appSettings: AppSettings) {
        this.appSettings = appSettings
        unitRes = if (appSettings.isSIUnit) R.string.temperature_celsius else R.string.temperature_fahrenheit
    }
}