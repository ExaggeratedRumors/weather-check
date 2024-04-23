package com.ertools.weather_check.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.fragments.MenuFragment
import com.ertools.weather_check.model.FetchManager
import com.ertools.weather_check.widgets.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), LocationListener {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private lateinit var changeLocationBtn: Button

    private var location: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)
        changeLocationBtn = findViewById(R.id.change_location)
        changeLocationBtn.setOnClickListener {
            requestLocation()
        }
        requestLocation()
    }

    override fun notifyLocationChanged(location: Location?) {
        this.location = location
        requestData()
    }

    override fun requestLocation() {
        this.location = null

        supportFragmentManager.beginTransaction().apply {
            for (fragment in supportFragmentManager.fragments) remove(fragment)
        }.commit()
        viewPagerAdapter = null

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.menu, MenuFragment(this))
            .commit()

        tabLayout.visibility = View.GONE
        viewPager.visibility = View.GONE
        changeLocationBtn.visibility = View.GONE
    }

    override fun <T> notifyDataFetchSuccess(dto: T, valueType: Class<T>) {
        runOnUiThread {
            if(viewPagerAdapter == null) {
                supportFragmentManager.beginTransaction().apply {
                    for (fragment in supportFragmentManager.fragments) remove(fragment)
                }.commit()

                viewPagerAdapter = ViewPagerAdapter(this, this)
                viewPager = findViewById(R.id.view_pager)
                viewPager.adapter = viewPagerAdapter

                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    when (position) {
                        0 -> tab.text = "Weather"
                        1 -> tab.text = "Details"
                        2 -> tab.text = "Forecast"
                    }
                }.attach()

                tabLayout.visibility = View.VISIBLE
                viewPager.visibility = View.VISIBLE
                changeLocationBtn.visibility = View.VISIBLE
            }

            when(valueType) {
                WeatherDTO::class.java -> viewPagerAdapter?.updateData(dto as WeatherDTO)
                ForecastDTO::class.java -> viewPagerAdapter?.updateData(dto as ForecastDTO)
            }
        }
    }

    override fun <T> notifyDataFetchFailure(valueType: Class<T>) {
        location = null
        requestLocation()
    }

    override fun requestData() {
        location?.let {
            val fetchManager = FetchManager(this, this)
            fetchManager.fetchWeatherData(it)
            fetchManager.fetchForecastData(it)
        }
    }
}