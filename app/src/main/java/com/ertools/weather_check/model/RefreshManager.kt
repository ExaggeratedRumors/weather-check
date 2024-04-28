package com.ertools.weather_check.model

import android.os.CountDownTimer
import com.ertools.weather_check.dto.AppSettings

class RefreshManager {
    private var refreshThread: CountDownTimer? = null
    fun startAutoRefresh(
        appSettings: AppSettings,
        call: () -> Unit
    ) {
        refreshThread = object : CountDownTimer(
            appSettings.autoRefreshPeriodSeconds * 1000,
            appSettings.autoRefreshPeriodSeconds * 1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                call()
            }

            override fun onFinish() {
                start()
            }
        }
        refreshThread?.start()
    }

    fun stopAutoRefresh() {
        refreshThread?.cancel()
    }
}