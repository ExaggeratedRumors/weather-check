package com.ertools.weather_check.model

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.FileNotFoundException

class DataManager {
    companion object {
        private val mapper = ObjectMapper(YAMLFactory()).apply {
            this.registerModule(
                KotlinModule.Builder()
                    .withReflectionCacheSize(512)
                    .configure(KotlinFeature.NullToEmptyCollection, false)
                    .configure(KotlinFeature.NullToEmptyMap, false)
                    .configure(KotlinFeature.NullIsSameAsDefault, false)
                    .configure(KotlinFeature.SingletonSupport, false)
                    .configure(KotlinFeature.StrictNullChecks, false)
                    .build()
            )
        }

        fun <T> readObject(sourcePath: String, valueType: Class<T>, context: Context): T? = try {
            context.openFileInput(sourcePath).use {
                println("ENGINE: Data read from file $sourcePath")
                mapper.readValue(it, valueType)
            }
        } catch (e: FileNotFoundException) {
            null
        } catch (e: Exception) {
            println("ENGINE: Cannot read $sourcePath file or file is incorrect with data object.")
            null
        }


        fun writeObject(sourcePath: String, value: Any, context: Context) = try {
            context.openFileOutput(sourcePath, Context.MODE_PRIVATE).use {
                mapper.writeValue(it, value)
                println("ENGINE: Data wrote to file $sourcePath")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("ENGINE: Cannot write to yaml object.")
        }

        fun <T> convertToJson(value: String, outputType: Class<T>): T = try {
            mapper.readValue(value, outputType)
        } catch (e: Exception) {
            println("ENGINE: Cannot convert to json object.")
            throw e
        }

        fun removeFile(sourcePath: String, context: Context) = try {
            context.deleteFile(sourcePath)
            println("ENGINE: File $sourcePath removed.")
        } catch (e: Exception) {
            println("ENGINE: Cannot remove file $sourcePath.")
        }
    }
}