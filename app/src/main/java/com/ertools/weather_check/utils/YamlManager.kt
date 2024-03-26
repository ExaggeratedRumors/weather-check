package com.ertools.weather_check.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.nio.file.Files
import java.nio.file.Paths

class YamlManager {
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

        fun <T> readYamlObject(sourcePath: String, valueType: Class<T>) = try {
            Files.newBufferedReader(Paths.get(sourcePath))
                .use {
                    mapper.readValue(it, valueType)
                }
        } catch (e: Exception) {
            println("ENGINE: Cannot read $sourcePath file or file is incorrect with data object.")
        }!!

        fun writeYamlObject(sourcePath: String, value: Any) = try {
            Files.newBufferedWriter(Paths.get(sourcePath))
                .use {
                    mapper.writeValue(it, value)
                }
        } catch (e: Exception) {
            println("ENGINE: Cannot write to yaml object.")
        }

        fun <T> convertToJson(value: String, outputType: Class<T>) = try {
            val p = mapper.readValue(value, outputType)
            p
        } catch (e: Exception) {
            println("ENGINE: Cannot convert to json object.")
        }
    }
}