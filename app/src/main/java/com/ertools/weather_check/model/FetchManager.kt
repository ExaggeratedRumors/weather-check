package com.ertools.weather_check.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.ertools.weather_check.activities.LocationListener
import com.ertools.weather_check.dto.FetchLogs
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.utils.Utils
import okhttp3.*
import okio.IOException

class FetchManager(
    private val context: Context,
    private val listener: LocationListener
) {

    enum class ConnectionType {
        NONE, CELLULAR, WIFI, VPN
    }

    fun fetchWeatherData(location: Location) {
        val fetchLogs: FetchLogs? = fetchDataFromFile(Utils.FETCH_LOGS_PATH, FetchLogs::class.java)

        val onSuccess: (Any) -> Unit = {
            saveDataToFile(Utils.WEATHER_DATA_PATH, it)
            val logs = if(fetchLogs == null) FetchLogs(System.currentTimeMillis(), 0L)
            else FetchLogs(System.currentTimeMillis(), fetchLogs.forecastTimestamp)
            saveDataToFile(Utils.FETCH_LOGS_PATH, logs)
        }

        if(fetchLogs == null)
            return fetchDataFromServer(location, Utils.WEATHER_URL, onSuccess, WeatherDTO::class.java)
        if(fetchLogs.weatherTimestamp + (Utils.WEATHER_FETCH_DIFF_SEC * 60) < System.currentTimeMillis())
            return fetchDataFromServer(location, Utils.WEATHER_URL, onSuccess, WeatherDTO::class.java)
        val data: WeatherDTO = fetchDataFromFile(Utils.WEATHER_DATA_PATH, WeatherDTO::class.java)
            ?: return fetchDataFromServer(location, Utils.WEATHER_URL, onSuccess, WeatherDTO::class.java)
        listener.notifyDataFetchSuccess(data, WeatherDTO::class.java)
    }

    fun fetchForecastData(location: Location) {
        val fetchLogs: FetchLogs? = fetchDataFromFile(Utils.FETCH_LOGS_PATH, FetchLogs::class.java)

        val onSuccess: (Any) -> Unit = {
            saveDataToFile(Utils.FORECAST_DATA_PATH, it)
            val logs = if(fetchLogs == null) FetchLogs(0L, System.currentTimeMillis())
            else FetchLogs(fetchLogs.weatherTimestamp, System.currentTimeMillis())
            saveDataToFile(Utils.FETCH_LOGS_PATH, logs)
        }

        if(fetchLogs == null)
            return fetchDataFromServer(location, Utils.FORECAST_URL, onSuccess, ForecastDTO::class.java)
        if(fetchLogs.forecastTimestamp + (Utils.FORECAST_FETCH_DIFF_SEC * 60) < System.currentTimeMillis())
            return fetchDataFromServer(location, Utils.FORECAST_URL, onSuccess, ForecastDTO::class.java)
        val data: ForecastDTO = fetchDataFromFile(Utils.FORECAST_DATA_PATH, ForecastDTO::class.java)
            ?: return fetchDataFromServer(location, Utils.FORECAST_URL, onSuccess, ForecastDTO::class.java)
        listener.notifyDataFetchSuccess(data, ForecastDTO::class.java)
    }

    private fun <T> fetchDataFromServer(
        location: Location,
        endpointCall: String,
        onSuccess: (Any) -> (Unit),
        valueType: Class<T>
    ) {
        /** Check connection **/
        if (getConnectionType(context) == ConnectionType.NONE) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                throw DataFetchException("No internet connection")
        }

        /** Build URL **/
        val client = OkHttpClient()
        val url = if(location.city != null)
            "$endpointCall${Utils.getCityInterfix(location.city)}${Utils.API_KEY_SUFFIX}"
        else if(location.lat != null && location.lon != null)
            "$endpointCall${Utils.getCoordinatesInterfix(location.lat, location.lon)}${Utils.API_KEY_SUFFIX}"
        else return listener.notifyDataFetchFailure(valueType)

        /** Build connection **/
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object: Callback {

            override fun onFailure(call: Call, e: IOException) {
                listener.notifyDataFetchFailure(valueType)
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful) {
                    val json = response.body!!.string()
                    val dataResponse : T = DataManager.convertToJson(json, valueType)
                    onSuccess(dataResponse as Any)
                    listener.notifyDataFetchSuccess(dataResponse, valueType)
                } else {
                    listener.notifyDataFetchFailure(valueType)
                }
            }

        })
    }

    private fun <T> fetchDataFromFile(sourcePath: String, valueType: Class<T>) =
        DataManager.readObject(sourcePath, valueType, context)

    private fun saveDataToFile(destinationPath: String, value: Any) =
        DataManager.writeObject(destinationPath, value, context)

    private fun getConnectionType(context: Context): ConnectionType {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return ConnectionType.WIFI
                } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return ConnectionType.CELLULAR
                } else if (hasTransport(NetworkCapabilities.TRANSPORT_VPN)){
                    return ConnectionType.VPN
                } else return ConnectionType.NONE
            }
        }
        return ConnectionType.NONE
    }
}