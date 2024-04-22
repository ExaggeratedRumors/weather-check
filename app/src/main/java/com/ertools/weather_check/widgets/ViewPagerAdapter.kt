package com.ertools.weather_check.widgets

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ertools.weather_check.activities.LocationListener
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.Weather
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.fragments.ForecastFragment
import com.ertools.weather_check.fragments.MainDataFragment
import com.ertools.weather_check.fragments.SecondDataFragment
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.utils.serializable

class ViewPagerAdapter(
    private val listener: LocationListener,
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {
    private val mainDataFragment = MainDataFragment()
    private val secondDataFragment = SecondDataFragment()
    private val forecastFragment = ForecastFragment()

    init {
        println("VIEW PAGER ADAPTER INIT")
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        println("CREATE FRAGMENT")
        return when (position) {
            0 -> mainDataFragment
            1 -> secondDataFragment
            2 -> forecastFragment
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    fun updateData(dto: WeatherDTO) {
        val bundle = Bundle().apply {
            putSerializable(Utils.STORE_WEATHER_DTO, dto)
        }
        mainDataFragment.arguments = bundle
        secondDataFragment.arguments = bundle
    }

    fun updateData(dto: ForecastDTO) {
        val bundle = Bundle().apply {
            putSerializable(Utils.STORE_FORECAST_DTO, dto)
        }
        forecastFragment.arguments = bundle
    }
}