package com.ertools.weather_check.widgets

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ertools.weather_check.R
import com.ertools.weather_check.activities.DataFetchListener
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.fragments.ForecastFragment
import com.ertools.weather_check.fragments.WeatherFragment
import com.ertools.weather_check.fragments.DetailsFragment
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.utils.timestampToTime
import java.io.Serializable

class ViewPagerAdapter(
    private val listener: DataFetchListener,
    private val fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity), Serializable {
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
            0 -> weatherFragment.apply { arguments = bundle }
            1 -> detailsFragment.apply { arguments = bundle }
            2 -> forecastFragment.apply { arguments = bundle }
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    fun updateData(dto: WeatherDTO) {
        this.weatherDTO = dto
    }

    fun updateData(dto: ForecastDTO) {
        this.forecastDTO = dto
    }
}