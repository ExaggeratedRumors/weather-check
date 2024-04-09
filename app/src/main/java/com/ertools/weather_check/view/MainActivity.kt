package com.ertools.weather_check.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ertools.weather_check.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun initializeDecisionFragment() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val decisionFragment = DecisionFragment()
        transaction.add(R.id.decision, decisionFragment)
        transaction.commit()
    }

    fun initializeFragments() {
        val mainDataFragment = MainDataFragment()
        val secondDataFragment = SecondDataFragment()
        val forecastFragment = ForecastFragment()
    }
}