package com.ertools.weather_check.widgets

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ertools.weather_check.activities.DataFetchListener
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.fragments.ForecastFragment
import com.ertools.weather_check.fragments.WeatherFragment
import com.ertools.weather_check.fragments.DetailsFragment
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.utils.timestampToTime

class ViewPagerAdapter(
    private val listener: DataFetchListener,
    private val fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {
    private var weatherFragment: WeatherFragment = WeatherFragment()
    private var detailsFragment: DetailsFragment = DetailsFragment()
    private var forecastFragment: ForecastFragment = ForecastFragment()
    private var weatherDTO: WeatherDTO? = null
    private var forecastDTO: ForecastDTO? = null

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle().apply {
            putSerializable(Utils.STORE_WEATHER_DTO, weatherDTO)
            putSerializable(Utils.STORE_FORECAST_DTO, forecastDTO)
        }

        return when (position) {
            0 -> WeatherFragment().apply { arguments = bundle }
            1 -> DetailsFragment().apply { arguments = bundle }
            2 -> ForecastFragment().apply { arguments = bundle }
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    fun updateData(dto: WeatherDTO) {
        this.weatherDTO = dto
        destroyFragments()
        println("STAMPIK: ${timestampToTime(dto.dt)}")

        val fragment = fragmentActivity.supportFragmentManager.findFragmentByTag("fragment_weather")
        fragment?.let {
            fragmentActivity.supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    private fun destroyFragments() {
        weatherFragment.onDestroy()
        detailsFragment.onDestroy()
        forecastFragment.onDestroy()
    }

    fun updateData(dto: ForecastDTO) {
        this.forecastDTO = dto
        destroyFragments()
    }
}