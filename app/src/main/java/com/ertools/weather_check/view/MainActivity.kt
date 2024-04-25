package com.ertools.weather_check.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.model.FetchManager
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.utils.serializable
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
    private var fragmentCart: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** State from instance **/
        unitStateCelsius = savedInstanceState?.getBoolean(Utils.STORE_UNIT_STATE) ?: true
        location = savedInstanceState?.serializable<Location>(Utils.STORE_LOCATION)
        fragmentCart = savedInstanceState?.getInt(Utils.STORE_FRAGMENT_CART) ?: 0

        /** UI widgets **/
        setContentView(R.layout.activity_main)
        menuFragment = MenuFragment()
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)
        changeLocationBtn = findViewById(R.id.change_location)
        changeUnitsBtn = findViewById(R.id.change_units)
        refreshBtn = findViewById(R.id.refresh)

        /** Buttons listeners **/
        changeLocationBtn.setOnClickListener { requestLocation() }
        changeUnitsBtn.setImageResource(
            if (unitStateCelsius) R.drawable.temperature_kelvin
            else R.drawable.temperature_celsius
        )

        changeUnitsBtn.setOnClickListener {
            unitStateCelsius = !unitStateCelsius
            changeUnitsBtn.setImageResource(
                if (unitStateCelsius) R.drawable.temperature_kelvin
                else R.drawable.temperature_celsius
            )
            removeAllFragments()
            viewPagerAdapter = null
            requestData(FetchManager.ForceFetch.DATA)
        }

        refreshBtn.setOnClickListener {
            removeAllFragments()
            viewPagerAdapter = null
            requestData(FetchManager.ForceFetch.SERVER)
        }

        /** Start application logic **/
        if(location == null) {
            println("TEST: Location null")
            requestLocation()
        }
        else {
            println("TEST: Location fetched")
            requestData()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(Utils.STORE_UNIT_STATE, unitStateCelsius)
        outState.putSerializable(Utils.STORE_LOCATION, location)
        outState.putInt(Utils.STORE_FRAGMENT_CART, viewPager.currentItem)

        super.onSaveInstanceState(outState)
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

        menuFragment = MenuFragment()
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
            fetchManager.fetchData(it, WeatherDTO::class.java, forceFetch)
            fetchManager.fetchData(it, ForecastDTO::class.java, forceFetch)
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
                viewPager.currentItem = fragmentCart

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
        runOnUiThread {
            location = null
            Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            requestLocation()
        }
    }
}