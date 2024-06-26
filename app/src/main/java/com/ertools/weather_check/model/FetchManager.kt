package com.ertools.weather_check.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.ertools.weather_check.interfaces.DataFetchListener
import com.ertools.weather_check.dto.ForecastDTO
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.dto.WeatherDTO
import com.ertools.weather_check.utils.Utils
import okhttp3.*
import okio.IOException

class FetchManager(
    private val context: Context,
    private val listener: DataFetchListener
) {

    enum class ForceFetch {
        DEVICE, SERVER, NONE
    }

    enum class ConnectionType {
        NONE, CELLULAR, WIFI, VPN
    }

    fun <T> fetchData(
        location: Location,
        valueType: Class<T>,
        force: ForceFetch = ForceFetch.NONE
    ) {
        if(valueType != WeatherDTO::class.java && valueType != ForecastDTO::class.java)
            throw IllegalArgumentException("Invalid data type")

        /** Choose constance **/
        val dataPath = if(valueType == WeatherDTO::class.java) Utils.WEATHER_DATA_PATH
        else Utils.FORECAST_DATA_PATH

        val url = if(valueType == WeatherDTO::class.java) Utils.WEATHER_URL
        else Utils.FORECAST_URL

        val fetchDiff = if(valueType == WeatherDTO::class.java) Utils.WEATHER_FETCH_DIFF_SEC
        else Utils.FORECAST_FETCH_DIFF_SEC

        /** Init data **/
        val data: T? = fetchDataFromFile("${location.name}$dataPath", valueType)
        val isInternetAvailable = getConnectionType(context) != ConnectionType.NONE
        val onSuccess: (Any) -> Unit = {
            saveDataToFile("${location.name}$dataPath", it)
        }

        /** Service force fetch server **/
        if(force == ForceFetch.SERVER && !isInternetAvailable)
            return listener.notifyDataFetchFailure(valueType, "Force server fetch failed.")
        else if(force == ForceFetch.SERVER)
            return fetchDataFromServer(location, url, onSuccess, valueType)

        /** Service force fetch data **/
        if(force == ForceFetch.DEVICE && data == null)
            return listener.notifyDataFetchFailure(valueType, "Force data fetch failed.")
        else if(force == ForceFetch.DEVICE && data != null)
            return listener.notifyDataFetchSuccess(data, valueType)

        /** If logs or data are not available and no internet connection, notify failure **/
        if((data == null) && !isInternetAvailable)
            return listener.notifyDataFetchFailure(valueType, "Data and connection not available.")

        /** If logs & data available and no internet connection, load data **/
        if(data != null && !isInternetAvailable)
            return listener.notifyDataFetchSuccess(data, valueType)

        /** If logs or data are not available, fetch data from server **/
        if(data == null)
            return fetchDataFromServer(location, url, onSuccess, valueType)

        /** If data is deprecated, fetch data from server **/
        val dataFetchTimestamp = if(valueType == WeatherDTO::class.java) (data as WeatherDTO).dt
        else (data as ForecastDTO).list[0].dt
        println("$dataFetchTimestamp $fetchDiff ${System.currentTimeMillis()/1000}")
        if(dataFetchTimestamp + fetchDiff < System.currentTimeMillis() / 1000)
            return fetchDataFromServer(location, url, onSuccess, valueType)

        /** Otherwise return data **/
        listener.notifyDataFetchSuccess(data, valueType)
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
        else return listener.notifyDataFetchFailure(valueType, "Invalid location")

        /** Build connection **/
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object: Callback {

            override fun onFailure(call: Call, e: IOException) {
                listener.notifyDataFetchFailure(valueType, "Call build error.")
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful) {
                    val json = response.body!!.string()
                    val dataResponse : T = DataManager.convertToJson(json, valueType)
                    onSuccess(dataResponse as Any)
                    listener.notifyDataFetchSuccess(dataResponse, valueType)
                } else {
                    listener.notifyDataFetchFailure(valueType, "Response error.")
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