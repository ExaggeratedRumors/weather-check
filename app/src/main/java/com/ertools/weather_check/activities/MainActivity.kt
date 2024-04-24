package com.ertools.weather_check.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.fragments.MenuFragment
import com.ertools.weather_check.model.FetchManager
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.widgets.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), DataFetchListener {
    private lateinit var changeLocationBtn: ImageButton
    private lateinit var changeUnitsBtn: ImageButton
    private lateinit var refreshBtn: ImageButton
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var menuFragment: MenuFragment
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private var unitStateCelsius = true
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** State from instance **/
        unitStateCelsius = savedInstanceState?.getBoolean(Utils.STORE_UNIT_STATE) ?: true

        /** UI widgets **/
        setContentView(R.layout.activity_main)
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)
        changeLocationBtn = findViewById(R.id.change_location)
        menuFragment = MenuFragment()

        /** Buttons listeners **/
        changeLocationBtn.setOnClickListener {
            requestLocation()
        }
        changeUnitsBtn = findViewById(R.id.change_units)
        changeUnitsBtn.setOnClickListener {
            unitStateCelsius = !unitStateCelsius
            changeUnitsBtn.setImageResource(
                if (unitStateCelsius) R.drawable.temperature_celsius else R.drawable.temperature_kelvin
            )
            removeAllFragments()
            viewPagerAdapter = null
            requestData(FetchManager.ForceFetch.DATA)
        }
        refreshBtn = findViewById(R.id.refresh)
        refreshBtn.setOnClickListener {
            removeAllFragments()
            viewPagerAdapter = null
            requestData(FetchManager.ForceFetch.SERVER)
        }

        /** Start application logic **/
        requestLocation()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean(Utils.STORE_UNIT_STATE, unitStateCelsius)
        outState.putSerializable(Utils.STORE_VIEW_PAGER, viewPagerAdapter)
    }

    private fun removeAllFragments() {
        supportFragmentManager.beginTransaction().apply {
            for (fragment in supportFragmentManager.fragments) remove(fragment)
        }.commit()
    }

    /** LocationListener implementation **/

    override fun requestLocation() {
        this.location = null
        supportFragmentManager.beginTransaction().apply {
            for (fragment in supportFragmentManager.fragments) remove(fragment)
        }.commit()
        viewPagerAdapter = null

        menuFragment.listener = this
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.menu, menuFragment)
            .commit()

        tabLayout.visibility = View.GONE
        viewPager.visibility = View.GONE
        changeLocationBtn.visibility = View.GONE
        changeUnitsBtn.visibility = View.GONE
        refreshBtn.visibility = View.GONE
    }

    override fun requestData(forceFetch: FetchManager.ForceFetch) {
        location?.let {
            val fetchManager = FetchManager(this, this)
            fetchManager.fetchWeatherData(it, forceFetch)
            fetchManager.fetchForecastData(it, forceFetch)
        }
    }

    override fun notifyLocationChanged(location: Location?) {
        this.location = location
        requestData()
    }

    override fun <T> notifyDataFetchSuccess(dto: T, valueType: Class<T>) {
        runOnUiThread {
            if(viewPagerAdapter == null) {
                removeAllFragments()

                viewPagerAdapter = ViewPagerAdapter(this)
                viewPagerAdapter?.changeUnits(unitStateCelsius)
                viewPager = findViewById(R.id.view_pager)
                viewPager.isSaveEnabled = false
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
                changeUnitsBtn.visibility = View.VISIBLE
                refreshBtn.visibility = View.VISIBLE

            }

            when(valueType) {
                WeatherDTO::class.java -> viewPagerAdapter?.updateData(dto as WeatherDTO)
                ForecastDTO::class.java -> viewPagerAdapter?.updateData(dto as ForecastDTO)
            }
        }
    }

    override fun <T> notifyDataFetchFailure(valueType: Class<T>) {
        location = null
        Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show()
        requestLocation()
    }
}