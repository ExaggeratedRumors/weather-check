package com.ertools.weather_check.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.utils.Utils
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class FetchManager(private val context: Context) {

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
    ): T {
        var response : T?
        try {
            response = fetchDataFromServer(location, call, valueType)
            saveDataToFile(destinationPath, response!!)
        } catch (e: Exception) {
            Toast.makeText(context, "Internet connection error", Toast.LENGTH_SHORT).show()
            response = null
            e.printStackTrace()
        }
        if(response == null) response = (fetchDataFromFile(destinationPath, valueType) as T)!!
        return response
    }

    private fun <T> fetchDataFromServer(
        location: Location,
        call: (Double, Double) -> String,
        valueType: Class<T>
    ): T? {
        var response: T? = null
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            if (getConnectionType(context) == ConnectionType.NONE) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                throw Exception("No internet connection")
            }
            val url = URL(call(location.lat, location.lon))
            val connection = url.openConnection() as HttpURLConnection
            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Toast.makeText(context, "Server error", Toast.LENGTH_SHORT).show()
                throw Exception("Server error")
            }
            val inputStream = BufferedInputStream(connection.inputStream)
            val responseText = inputStream.bufferedReader().use(BufferedReader::readText)
            response = DataManager.convertToJson(responseText, valueType)
        }
        return response
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