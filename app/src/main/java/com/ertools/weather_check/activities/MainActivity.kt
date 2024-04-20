package com.ertools.weather_check.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.fragments.DecisionFragment
import com.ertools.weather_check.fragments.ForecastFragment
import com.ertools.weather_check.fragments.MainDataFragment
import com.ertools.weather_check.fragments.SecondDataFragment
import com.ertools.weather_check.model.DataFetchException
import com.ertools.weather_check.model.FetchManager
import com.ertools.weather_check.widgets.ViewPagerAdapter

class MainActivity : AppCompatActivity(), LocationListener {
    private var currentFragment: Fragment? = null
    private var location: Location? = null
    private val viewPagerAdapter = ViewPagerAdapter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showFragment(DecisionFragment(this))
    }

    private fun showFragment(fragment: Fragment) {
        this.currentFragment = fragment
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.decision, fragment)
//        transaction.hide(decisionFragment)
//        transaction.remove(decisionFragment)
        // transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun notifyLocationChanged(location: Location?) {
        this.location = location
        requestData()
    }

    override fun requestLocation() {
        this.location = null
        showFragment(DecisionFragment(this))
    }

    override fun <T> notifyDataFetchSuccess(dto: T, valueType: Class<T>) {
        when(valueType) {
            WeatherDTO::class.java -> viewPagerAdapter.updateData(dto as WeatherDTO)
            ForecastDTO::class.java -> viewPagerAdapter.updateData(dto as ForecastDTO)
            else -> return
        }
    }

    override fun <T> notifyDataFetchFailure(valueType: Class<T>) {
        location = null
        showFragment(DecisionFragment(this))
    }

    override fun requestData() {
        location?.let {
            val fetchManager = FetchManager(this, this)
            fetchManager.fetchWeatherData(it)
            fetchManager.fetchForecastData(it)
        }
    }
}