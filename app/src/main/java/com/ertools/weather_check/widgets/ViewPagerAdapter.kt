package com.ertools.weather_check.widgets

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ertools.weather_check.activities.LocationListener
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.fragments.ForecastFragment
import com.ertools.weather_check.fragments.MainDataFragment
import com.ertools.weather_check.fragments.SecondDataFragment

class ViewPagerAdapter(
    private val listener: LocationListener,
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {
    private val mainDataFragment = MainDataFragment()
    private val secondDataFragment = SecondDataFragment()
    private val forecastFragment = ForecastFragment()

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> mainDataFragment
            1 -> secondDataFragment
            2 -> forecastFragment
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    fun updateData(dto: WeatherDTO) {
        mainDataFragment.updateData(dto)
        secondDataFragment.updateData(dto)
    }

    fun updateData(dto: ForecastDTO) {
        forecastFragment.updateData(dto)
    }
}