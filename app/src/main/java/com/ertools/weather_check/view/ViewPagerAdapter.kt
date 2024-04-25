package com.ertools.weather_check.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.utils.Utils
import java.io.Serializable

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity), Serializable {
    private var weatherFragment: WeatherFragment = WeatherFragment()
    private var detailsFragment: DetailsFragment = DetailsFragment()
    private var forecastFragment: ForecastFragment = ForecastFragment()
    private var weatherDTO: WeatherDTO? = null
    private var forecastDTO: ForecastDTO? = null
    private var unitStateCelsius = true

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle().apply {
            putSerializable(Utils.STORE_WEATHER_DTO, weatherDTO)
            putSerializable(Utils.STORE_FORECAST_DTO, forecastDTO)
            putBoolean(Utils.STORE_UNIT_STATE, unitStateCelsius)
        }

        return when (position) {
            0 -> weatherFragment.apply { arguments = bundle }
            1 -> detailsFragment.apply { arguments = bundle }
            2 -> forecastFragment.apply { arguments = bundle }
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    fun updateData(dto: WeatherDTO) {
        this.weatherDTO = dto
        weatherFragment.updateData(dto)
        detailsFragment.updateData(dto)
    }

    fun updateData(dto: ForecastDTO) {
        this.forecastDTO = dto
        forecastFragment.updateData(dto)
    }

    fun changeUnits(isCelsius: Boolean) {
        this.unitStateCelsius = isCelsius
    }
}