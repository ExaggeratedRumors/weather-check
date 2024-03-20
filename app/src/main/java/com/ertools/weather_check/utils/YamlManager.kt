package com.ertools.weather_check.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

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
            exitProcess(1)
        }!!

        fun writeYamlObject(sourcePath: String, value: Any) = try {
            Files.newBufferedWriter(Paths.get(sourcePath))
                .use {
                    mapper.writeValue(it, value)
                }
        } catch (e: Exception) {
            println("ENGINE: Cannot write to yaml object.")
            exitProcess(1)
        }
    }
}