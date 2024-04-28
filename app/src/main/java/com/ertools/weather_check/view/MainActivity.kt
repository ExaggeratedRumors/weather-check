package com.ertools.weather_check.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.ertools.weather_check.R
import com.ertools.weather_check.interfaces.DataFetchListener
import com.ertools.weather_check.dto.*
import com.ertools.weather_check.interfaces.DataUpdateListener
import com.ertools.weather_check.interfaces.SettingsUpdateListener
import com.ertools.weather_check.model.DataManager
import com.ertools.weather_check.model.FetchManager
import com.ertools.weather_check.model.RefreshManager
import com.ertools.weather_check.model.SettingsManager
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.utils.serializable
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), DataFetchListener, SettingsUpdateListener {

    /** Common widgets **/
    private lateinit var changeLocationBtn: ImageButton
    private lateinit var changeUnitsBtn: ImageButton
    private lateinit var refreshBtn: ImageButton

    /** Non-fragments depends on resolutions widgets **/
    private var viewPager: ViewPager2? = null
    private var tabLayout: TabLayout? = null

    /** Data **/
    private lateinit var appSettings: AppSettings
    private lateinit var appState: AppState

    /** Listeners **/
    private val dataUpdateListeners: ArrayList<DataUpdateListener> = ArrayList()
    private val settingsUpdateListeners: ArrayList<SettingsUpdateListener> = ArrayList()

    /** Refresh thread **/
    private val refreshManager = RefreshManager()

    /** AppCompatActivity implementation **/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** Restore state **/
        appSettings = savedInstanceState?.serializable<AppSettings>(Utils.STORE_SETTINGS)
            ?: DataManager.readObject(Utils.SETTINGS_PATH, AppSettings::class.java, this)
                    ?: AppSettings()
        appState = savedInstanceState?.serializable<AppState>(Utils.STORE_STATE)
            ?: AppState()

        /** UI widgets **/
        setContentView(R.layout.activity_main)
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)
        changeLocationBtn = findViewById(R.id.change_location)
        changeUnitsBtn = findViewById(R.id.settings)
        refreshBtn = findViewById(R.id.refresh)

        /** Buttons listeners **/
        changeLocationBtn.setOnClickListener { requestLocation() }

        changeUnitsBtn.setOnClickListener {
            SettingsManager(this, this).openSettings()
        }

        refreshBtn.setOnClickListener {
            requestData(FetchManager.ForceFetch.SERVER)
        }

        /** Start application logic **/
        startActivity()

    }

    override fun onDestroy() {
        super.onDestroy()
        refreshManager.stopAutoRefresh()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(Utils.STORE_SETTINGS, appSettings)
        if(viewPager != null) {
            outState.putSerializable(Utils.STORE_STATE, appState.copy(
                selectedPage = viewPager!!.currentItem
            ))
        } else {
            outState.putSerializable(Utils.STORE_STATE, appState)
        }
        super.onSaveInstanceState(outState)
    }


    /** Application logic **/

    private fun startActivity() {
        val savedState = this.appState.viewState
        this.appState = this.appState.copy(viewState = ViewState.NONE)

        if(this.appState.location == null)
            requestLocation()
        else if(savedState == ViewState.WEATHER)
            requestData(FetchManager.ForceFetch.DEVICE)
        else
            requestData()
    }

    private fun removeAllFragments() {
        refreshManager.stopAutoRefresh()
        supportFragmentManager.beginTransaction().apply {
            for (fragment in supportFragmentManager.fragments) remove(fragment)
        }.commit()
        dataUpdateListeners.clear()
        settingsUpdateListeners.clear()
        this.appState = this.appState.copy(viewState = ViewState.NONE)
    }

    private fun openMenu() {
        if(this.appState.viewState == ViewState.MENU) return
        removeAllFragments()
        val menuFragment = MenuFragment()
        menuFragment.listener = this
        menuFragment.setMenuVisibility(true)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.menu, menuFragment)
            .commit()

        tabLayout?.visibility = View.GONE
        viewPager?.visibility = View.GONE

        changeLocationBtn.visibility = View.GONE
        changeUnitsBtn.visibility = View.GONE
        refreshBtn.visibility = View.GONE
        this.appState = this.appState.copy(viewState = ViewState.MENU)
    }

    private fun openWeather() {
        if(this.appState.viewState == ViewState.WEATHER) return
        removeAllFragments()

        if (resources.configuration.smallestScreenWidthDp >= Utils.TABLET_RESOLUTION)
            openWeatherTablets()
        else
            openWeatherPhone()

        refreshManager.startAutoRefresh(this.appSettings) {
            requestData(FetchManager.ForceFetch.SERVER)
        }
        this.appState = this.appState.copy(viewState = ViewState.WEATHER)
    }

    private fun openWeatherPhone() {
        if(this.appState.viewState == ViewState.WEATHER) return
        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPagerAdapter.updateSettings(appSettings)
        viewPager = findViewById(R.id.view_pager)
        viewPager?.isSaveEnabled = false
        viewPager?.adapter = viewPagerAdapter
        viewPager?.currentItem = appState.selectedPage

        settingsUpdateListeners.add(viewPagerAdapter)
        dataUpdateListeners.add(viewPagerAdapter)

        TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
            when (position) {
                0 -> tab.text = "Weather"
                1 -> tab.text = "Details"
                2 -> tab.text = "Forecast"
            }
        }.attach()

        tabLayout?.visibility = View.VISIBLE
        viewPager?.visibility = View.VISIBLE
        changeLocationBtn.visibility = View.VISIBLE
        changeUnitsBtn.visibility = View.VISIBLE
        refreshBtn.visibility = View.VISIBLE
    }

    private fun openWeatherTablets() {
        if(this.appState.viewState == ViewState.WEATHER) return
        val bundle = Bundle().apply {
            putSerializable(Utils.STORE_SETTINGS, appSettings)
            putSerializable(Utils.STORE_WEATHER_DTO, appState.weatherDTO)
            putSerializable(Utils.STORE_FORECAST_DTO, appState.forecastDTO)
        }

        val weather = WeatherFragment()
        weather.arguments = bundle
        val details = DetailsFragment()
        details.arguments = bundle
        val forecast = ForecastFragment()
        forecast.arguments = bundle


        supportFragmentManager.beginTransaction().run {
            add(R.id.weather, weather)
            add(R.id.details, details)
            add(R.id.forecast, forecast)
        }.commit()

        dataUpdateListeners.add(weather)
        dataUpdateListeners.add(details)
        dataUpdateListeners.add(forecast)
        settingsUpdateListeners.add(weather)
        settingsUpdateListeners.add(details)
        settingsUpdateListeners.add(forecast)

        changeLocationBtn.visibility = View.VISIBLE
        changeUnitsBtn.visibility = View.VISIBLE
        refreshBtn.visibility = View.VISIBLE
    }


    /** DataFetchListener implementation **/

    override fun requestLocation() {
        this.appState = this.appState.copy(location = null)
        openMenu()
    }

    override fun requestData(forceFetch: FetchManager.ForceFetch) {
        openWeather()
        appState.location?.let {
            val fetchManager = FetchManager(this, this)
            fetchManager.fetchData(it, WeatherDTO::class.java, forceFetch)
            fetchManager.fetchData(it, ForecastDTO::class.java, forceFetch)
        }
    }

    override fun notifyLocationChanged(location: Location?) {
        this.appState = this.appState.copy(location = location)
        requestData()
    }

    override fun <T> notifyDataFetchSuccess(dto: T, valueType: Class<T>) {
        runOnUiThread {
            when (valueType) {
                WeatherDTO::class.java -> {
                    appState = appState.copy(weatherDTO = (dto as WeatherDTO))
                    dataUpdateListeners.forEach{ it.updateData(dto as WeatherDTO) }
                }
                ForecastDTO::class.java -> {
                    appState = appState.copy(forecastDTO = (dto as ForecastDTO))
                    dataUpdateListeners.forEach { it.updateData(dto as ForecastDTO) }
                }
            }
        }
    }

    override fun <T> notifyDataFetchFailure(valueType: Class<T>, message: String) {
        runOnUiThread {
            this.appState = this.appState.copy(location = null)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            requestLocation()
        }
    }


    /** SettingsUpdateListener implementation **/

    override fun updateSettings(appSettings: AppSettings) {
        this.appSettings = appSettings
        settingsUpdateListeners.forEach { it.updateSettings(appSettings) }
    }

}