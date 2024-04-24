package com.ertools.weather_check.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.ertools.weather_check.activities.DataFetchListener
import com.ertools.weather_check.dto.FetchLogs
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.utils.timestampToTime
import okhttp3.*
import okio.IOException

class FetchManager(
    private val context: Context,
    private val listener: DataFetchListener
) {

    enum class ConnectionType {
        NONE, CELLULAR, WIFI, VPN
    }

    fun  fetchWeatherData(location: Location, force: Boolean = false) {
        /** Init data **/
        val logs: FetchLogs? = fetchDataFromFile(
            Utils.FETCH_LOGS_PATH, FetchLogs::class.java
        )
        val data: WeatherDTO? = fetchDataFromFile(
            "${location.name}${Utils.WEATHER_DATA_PATH}", WeatherDTO::class.java
        )
        val isInternetAvailable = getConnectionType(context) != ConnectionType.NONE
        val onSuccess: (Any) -> Unit = {
            saveDataToFile("${location.name}${Utils.WEATHER_DATA_PATH}", it)
            val newLogs = if(logs == null)
                FetchLogs(System.currentTimeMillis() / 1000, 0L)
            else
                FetchLogs(System.currentTimeMillis() / 1000, logs.forecastTimestamp)
            saveDataToFile(Utils.FETCH_LOGS_PATH, newLogs)
        }

        /** If fetch from server is forced and no internet connection, notify failure **/
        if(force && !isInternetAvailable)
            return listener.notifyDataFetchFailure(WeatherDTO::class.java)
        println("F : 1")
        /** If logs or data are not available and no internet connection, notify failure **/
        if((logs == null || data == null) && !isInternetAvailable)
            return listener.notifyDataFetchFailure(WeatherDTO::class.java)
        println("F : 2")

        /** If logs & data available and no internet connection, load data **/
        if(data != null && !isInternetAvailable)
            return listener.notifyDataFetchSuccess(data, WeatherDTO::class.java)
        println("F : 3")

        /** If logs or data are not available, fetch data from server **/
        if(logs == null || data == null)
            return fetchDataFromServer(location, Utils.WEATHER_URL, onSuccess, WeatherDTO::class.java)
        println("F : 4 ${timestampToTime(logs.weatherTimestamp)} and ${timestampToTime(System.currentTimeMillis())} and ${System.currentTimeMillis()}")

        /** If data is deprecated, fetch data from server **/
        if(force || logs.weatherTimestamp + (Utils.WEATHER_FETCH_DIFF_SEC * 60) < System.currentTimeMillis())
            return fetchDataFromServer(location, Utils.WEATHER_URL, onSuccess, WeatherDTO::class.java)
        println("F : 5")

        /** Otherwise return data **/
        listener.notifyDataFetchSuccess(data, WeatherDTO::class.java)
    }

    fun fetchForecastData(location: Location, force: Boolean = false) {
        /** Init data **/
        val logs: FetchLogs? = fetchDataFromFile(
            Utils.FETCH_LOGS_PATH, FetchLogs::class.java
        )
        val data: ForecastDTO? = fetchDataFromFile(
            "${location.name}${Utils.FORECAST_DATA_PATH}", ForecastDTO::class.java
        )
        val isInternetAvailable = getConnectionType(context) != ConnectionType.NONE
        val onSuccess: (Any) -> Unit = {
            saveDataToFile("${location.name}${Utils.FORECAST_DATA_PATH}", it)
            val newLogs = if(logs == null)
                FetchLogs(0L, System.currentTimeMillis() / 1000)
            else
                FetchLogs(logs.weatherTimestamp, System.currentTimeMillis() / 1000)
            saveDataToFile(Utils.FETCH_LOGS_PATH, newLogs)
        }

        /** If fetch from server is forced and no internet connection, notify failure **/
        if(force && !isInternetAvailable)
            return listener.notifyDataFetchFailure(WeatherDTO::class.java)

        /** If logs or data are not available and no internet connection, notify failure **/
        if((logs == null || data == null) && !isInternetAvailable)
            return listener.notifyDataFetchFailure(ForecastDTO::class.java)

        /** If logs & data available and no internet connection, load data **/
        if(data != null && !isInternetAvailable)
            return listener.notifyDataFetchSuccess(data, ForecastDTO::class.java)

        /** If logs or data are not available, fetch data from server **/
        if(logs == null || data == null)
            return fetchDataFromServer(location, Utils.FORECAST_URL, onSuccess, ForecastDTO::class.java)

        /** If data is deprecated, fetch data from server **/
        if(force || logs.forecastTimestamp + (Utils.FORECAST_FETCH_DIFF_SEC * 60) < System.currentTimeMillis())
            return fetchDataFromServer(location, Utils.FORECAST_URL, onSuccess, ForecastDTO::class.java)

        /** Otherwise return data **/
        listener.notifyDataFetchSuccess(data, ForecastDTO::class.java)
    }

    private fun <T> fetchDataFromServer(
        location: Location,
        endpointCall: String,
        onSuccess: (Any) -> (Unit),
        valueType: Class<T>
    ) {
        /** Build URL **/
        val client = OkHttpClient()
        val url = if(location.city != null)
            "$endpointCall${Utils.getCityAffix(location.city)}${Utils.API_KEY_SUFFIX}"
        else if(location.lat != null && location.lon != null)
            "$endpointCall${Utils.getCoordinatesAffix(location.lat, location.lon)}${Utils.API_KEY_SUFFIX}"
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