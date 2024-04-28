package com.ertools.weather_check.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ertools.weather_check.interfaces.DataUpdateListener
import com.ertools.weather_check.dto.AppSettings
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.interfaces.SettingsUpdateListener
import com.ertools.weather_check.utils.Utils

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity), DataUpdateListener, SettingsUpdateListener {
    private var weatherFragment: WeatherFragment = WeatherFragment()
    private var detailsFragment: DetailsFragment = DetailsFragment()
    private var forecastFragment: ForecastFragment = ForecastFragment()
    private var weatherDTO: WeatherDTO? = null
    private var forecastDTO: ForecastDTO? = null
    private var appSettings: AppSettings? = null

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle().apply {
            putSerializable(Utils.STORE_WEATHER_DTO, weatherDTO)
            putSerializable(Utils.STORE_FORECAST_DTO, forecastDTO)
            putSerializable(Utils.STORE_SETTINGS, appSettings)
        }

        return when (position) {
            0 -> weatherFragment.apply { arguments = bundle }
            1 -> detailsFragment.apply { arguments = bundle }
            2 -> forecastFragment.apply { arguments = bundle }
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    override fun updateSettings(appSettings: AppSettings) {
        this.appSettings = appSettings
        weatherFragment.updateSettings(appSettings)
        detailsFragment.updateSettings(appSettings)
        forecastFragment.updateSettings(appSettings)
    }

    override fun <T> updateData(dto: T) {
        when (dto) {
            is WeatherDTO -> {
                this.weatherDTO = dto
                weatherFragment.updateData(dto)
                detailsFragment.updateData(dto)
            }
            is ForecastDTO -> {
                this.forecastDTO = dto
                forecastFragment.updateData(dto)
            }
        }
    }
}