package com.ertools.weather_check.model

import android.content.Context
import android.widget.Toast
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule

class DataManager {
    companion object {
        fun saveDataToFile(path: String, data: String, context: Context) {
            try {
                val fos = context.openFileOutput(path, Context.MODE_PRIVATE)
                fos.write(data.toByteArray())
            } catch(e: Exception) {
                Toast.makeText(context, "Cannot save data to file $path", Toast.LENGTH_SHORT).show()
            }
        }

        fun readDataFromFile(path: String, context: Context): String {
            var data = ""
            try {
                val fis = context.openFileInput(path)
                val br = fis.bufferedReader()
                data = br.readText()
            } catch(e: Exception) {
                Toast.makeText(context, "Cannot read data from file $path", Toast.LENGTH_SHORT).show()
            }
            return data
        }
    }
}