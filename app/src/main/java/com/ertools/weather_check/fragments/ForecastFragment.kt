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

class ForecastFragment: Fragment() {
    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.view = inflater.inflate(R.layout.fragment_main_data, container, false)

        return this.view
    }

    fun updateData(dto: ForecastDTO) {
        val temperature1 = view.findViewById<TextView>(R.id.temperature_1)
        temperature1.text = dto.list[0].main.temp.toString()
        val temperature2 = view.findViewById<TextView>(R.id.temperature_2)
        temperature2.text = dto.list[1].main.temp.toString()
        val temperature3 = view.findViewById<TextView>(R.id.temperature_3)
        temperature3.text = dto.list[2].main.temp.toString()
        val temperature4 = view.findViewById<TextView>(R.id.temperature_4)
        temperature4.text = dto.list[3].main.temp.toString()

        val weather1 = view.findViewById<TextView>(R.id.weather_1)
        weather1.text = setDescription(dto.list[0].weather[0].description)
        val weather2 = view.findViewById<TextView>(R.id.weather_2)
        weather2.text = setDescription(dto.list[1].weather[0].description)
        val weather3 = view.findViewById<TextView>(R.id.weather_3)
        weather3.text = setDescription(dto.list[2].weather[0].description)
        val weather4 = view.findViewById<TextView>(R.id.weather_4)
        weather4.text = setDescription(dto.list[3].weather[0].description)

        val icon1 = view.findViewById<ImageView>(R.id.icon_1)
        icon1.setImageResource(chooseIcon(dto.list[0].weather[0].description))
        val icon2 = view.findViewById<ImageView>(R.id.icon_2)
        icon2.setImageResource(chooseIcon(dto.list[1].weather[0].description))
        val icon3 = view.findViewById<ImageView>(R.id.icon_3)
        icon3.setImageResource(chooseIcon(dto.list[2].weather[0].description))
        val icon4 = view.findViewById<ImageView>(R.id.icon_4)
        icon4.setImageResource(chooseIcon(dto.list[3].weather[0].description))
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