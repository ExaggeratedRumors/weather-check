package com.ertools.weather_check.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.fragments.DecisionFragment
import com.ertools.weather_check.fragments.ForecastFragment
import com.ertools.weather_check.fragments.MainDataFragment
import com.ertools.weather_check.fragments.SecondDataFragment
import com.ertools.weather_check.model.FetchManager

class MainActivity : AppCompatActivity(), LocationListener {
    private var currentFragment: Fragment? = null
    private var location: Location? = null
    private val decisionFragment = DecisionFragment(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showFragment(DecisionFragment(this))
    }

    private fun showStackedFragments() {
        val fetchManager = FetchManager(this)
        val weatherData = fetchManager.fetchWeatherData(location!!)
        val forecastData = fetchManager.fetchForecastData(location!!)

        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.hide(decisionFragment)
        transaction.remove(decisionFragment)
        transaction.add(R.id.main_data, MainDataFragment(this, weatherData, location!!))
//        transaction.add(R.id.second_data, SecondDataFragment(this, weatherData))
//        transaction.add(R.id.forecast, ForecastFragment(this, forecastData))
        transaction.commit()
    }

    private fun showFragment(fragment: Fragment) {
        this.currentFragment = fragment
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.decision, fragment)
        // transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun notifyLocationChanged(location: Location?) {
        this.location = location
        showStackedFragments()
    }

    override fun requestLocation() {
        this.location = null
        showFragment(DecisionFragment(this))
    }
}