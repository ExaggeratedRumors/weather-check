package com.ertools.weather_check.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.activities.LocationListener
import com.ertools.weather_check.dto.ForecastDTO

class ForecastFragment(
    private val listener: LocationListener,
    private val forecastData: ForecastDTO
): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forecast, container, false)
    }
}