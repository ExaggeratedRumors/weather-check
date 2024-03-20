package com.ertools.weather_check.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.ertools.weather_check.utils.Utils
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class FetchManager(private val context: Context) {
    enum class ConnectionType {
        NONE, CELLULAR, WIFI, VPN
    }

    fun fetchWeatherData() {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                if (getConnectionType(context) == ConnectionType.NONE) {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                    return@execute
                }
                val url = URL(Utils.getApiUrl(11, 11))
                val connection = url.openConnection() as HttpURLConnection
                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Toast.makeText(context, "Server error", Toast.LENGTH_SHORT).show()
                    return@execute
                }
                val inputStream = BufferedInputStream(connection.inputStream)
                val responseData = inputStream.bufferedReader().use(BufferedReader::readText)

            } catch (e: Exception) {
                Toast.makeText(context, "Internet connection error", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

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

    private fun saveDataToFile(context: Context, data: String) {
        try {
            val outputStream = context.openFileOutput(Utils.WEATHER_DATA_PATH, Context.MODE_PRIVATE)
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            writer.write(Gson().toJson(data))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}