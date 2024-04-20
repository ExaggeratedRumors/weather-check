package com.ertools.weather_check.model

class DataFetchException(private val errorMessage: String) : Exception(){
    override fun printStackTrace() {
        println(errorMessage)
        super.printStackTrace()
    }
}