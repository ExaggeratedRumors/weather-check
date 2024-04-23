package com.ertools.weather_check.widgets

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ertools.weather_check.activities.LocationListener
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.fragments.ForecastFragment
import com.ertools.weather_check.fragments.WeatherFragment
import com.ertools.weather_check.fragments.DetailsFragment
import com.ertools.weather_check.utils.Utils

class ViewPagerAdapter(
    private val listener: LocationListener,
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {
    private var weatherFragment = WeatherFragment()
    private var detailsFragment = DetailsFragment()
    private var forecastFragment = ForecastFragment()

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> weatherFragment
            1 -> detailsFragment
            2 -> forecastFragment
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    fun updateData(dto: WeatherDTO) {
        val bundle = Bundle().apply {
            putSerializable(Utils.STORE_WEATHER_DTO, dto)
        }
        weatherFragment = WeatherFragment()
        weatherFragment.arguments = bundle
        detailsFragment = DetailsFragment()
        detailsFragment.arguments = bundle
    }

    fun updateData(dto: ForecastDTO) {
        val bundle = Bundle().apply {
            putSerializable(Utils.STORE_FORECAST_DTO, dto)
        }
        forecastFragment = ForecastFragment()
        forecastFragment.arguments = bundle
    }
}