package com.ertools.weather_check.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.nio.file.Files
import java.nio.file.Paths

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

        fun <T> readObject(sourcePath: String, valueType: Class<T>): T? = try {
            Files.newBufferedReader(Paths.get(sourcePath))
                .use {
                    println("ENGINE: Data read from file $sourcePath")
                    mapper.readValue(it, valueType)
                }
        } catch (e: Exception) {
            println("ENGINE: Cannot read $sourcePath file or file is incorrect with data object.")
            null
        }

        fun writeObject(sourcePath: String, value: Any) = try {
            Files.newBufferedWriter(Paths.get(sourcePath))
                .use {
                    mapper.writeValue(it, value)
                    println("ENGINE: Data wrote to file $sourcePath")
                }
        } catch (e: Exception) {
            println("ENGINE: Cannot write to yaml object.")
        }

        fun <T> convertToJson(value: String, outputType: Class<T>): T? = try {
            mapper.readValue(value, outputType)
        } catch (e: Exception) {
            println("ENGINE: Cannot convert to json object.")
            null
        }
    }
}