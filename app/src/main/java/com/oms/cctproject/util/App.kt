package com.oms.cctproject.util

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        PrefUtil.init(this)
    }
}