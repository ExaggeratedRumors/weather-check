package com.ertools.weather_check.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.ertools.weather_check.activities.LocationListener
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.utils.Utils
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import okhttp3.*
import okio.IOException

class FetchManager(
    private val context: Context,
    private val listener: LocationListener
) {

    enum class ConnectionType {
        NONE, CELLULAR, WIFI, VPN
    }

    fun fetchWeatherData(location: Location) =
         fetchData(location, Utils::getWeatherUrl, Utils.WEATHER_DATA_PATH, WeatherDTO::class.java)

    fun fetchForecastData(location: Location) =
        fetchData(location, Utils::getForecastUrl, Utils.FORECAST_DATA_PATH, ForecastDTO::class.java)

    private fun <T> fetchData(
        location: Location,
        call: (Double, Double) -> String,
        destinationPath: String,
        valueType: Class<T>
    ) {
        if(true) fetchDataFromServer(location, call, destinationPath, valueType)
        else fetchDataFromFile(destinationPath, valueType)
    }

    private fun <T> fetchDataFromServer(
        location: Location,
        endpointCall: (Double, Double) -> String,
        destinationPath: String,
        valueType: Class<T>
    ) {
        if (getConnectionType(context) == ConnectionType.NONE) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                throw DataFetchException("No internet connection")
        }
        val client = OkHttpClient()
        val request = Request.Builder().url(
            endpointCall(location.lat, location.lon)
        ).build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                fetchDataFromFile(Utils.WEATHER_DATA_PATH, valueType)
                listener.notifyDataFetchFailure(valueType)
            }
            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful) {
                    val json = response.body!!.string()
                    val dataResponse : T = DataManager.convertToJson(json, valueType)
                    listener.notifyDataFetchSuccess(dataResponse, valueType)
                    saveDataToFile(destinationPath, dataResponse as Any)
                } else {
                    listener.notifyDataFetchFailure(valueType)
                }
            }
        })
//
//        //
//        val executor = Executors.newSingleThreadExecutor()
//        executor.execute {
//            if (getConnectionType(context) == ConnectionType.NONE) {
//                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
//                throw DataFetchException("No internet connection")
//            }
//            val url = URL(call(location.lat, location.lon))
//            val connection = url.openConnection() as HttpURLConnection
//            println("responseCode: $connection.responseCode")
//            val responseCode = connection.responseCode
//            if (responseCode != HttpURLConnection.HTTP_OK) {
//                Toast.makeText(context, "Server error", Toast.LENGTH_SHORT).show()
//                throw DataFetchException("Server error")
//            }
//            val inputStream = BufferedInputStream(connection.inputStream)
//            val responseText = inputStream.bufferedReader().use(BufferedReader::readText)
//            response = DataManager.convertToJson(responseText, valueType)
//        }
//        return response
    }

    private fun <T> fetchDataFromFile(sourcePath: String, valueType: Class<T>) =
        DataManager.readObject(sourcePath, valueType)

    private fun saveDataToFile(destinationPath: String, value: Any) =
        DataManager.writeObject(destinationPath, value)

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