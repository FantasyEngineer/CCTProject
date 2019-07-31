package com.oms.cctproject.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

object PrefUtil {
    var settings: SharedPreferences? = null

    fun init(application: Application) {
        settings = application.getSharedPreferences("productFrame", Context.MODE_PRIVATE)
    }

    fun contains(key: String): Boolean? {
        return settings?.contains(key)
    }

    fun remove(key: String) {
        settings?.edit()?.remove(key)?.commit()
    }

    @JvmOverloads
    fun getString(key: String, defaultValue: String = ""): String? {
        return settings?.getString(key, defaultValue)
    }

    fun putString(key: String, value: String) {
        settings?.edit()?.putString(key, value)?.commit()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean? {
        return settings?.getBoolean(key, defaultValue)
    }

    fun hasKey(key: String): Boolean? {
        return settings?.contains(key)
    }

    fun putBoolean(key: String, value: Boolean) {
        settings?.edit()?.putBoolean(key, value)?.commit()
    }

    fun putInt(key: String, value: Int) {
        settings?.edit()?.putInt(key, value)?.commit()
    }

    fun getInt(key: String, defaultValue: Int): Int? {
        return settings?.getInt(key, defaultValue)
    }

    fun putLong(key: String, value: Int) {
        settings?.edit()?.putInt(key, value)?.commit()
    }

    fun getlong(key: String, defaultValue: Long): Long? {
        return settings?.getLong(key, defaultValue)
    }

    fun putFloat(key: String, value: Float) {
        settings?.edit()?.putFloat(key, value)?.commit()
    }

    fun getFloat(key: String, defaultValue: Float): Float? {
        return settings?.getFloat(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        settings?.edit()?.putLong(key, value)?.commit()
    }

    @JvmOverloads
    fun getLong(key: String, defaultValue: Long = 0): Long? {
        return settings?.getLong(key, defaultValue)
    }

    fun clear(context: Context, p: SharedPreferences) {
        val editor = p.edit()
        editor.clear()
        editor.commit()
    }

    fun clear() {
        settings?.edit()?.clear()?.commit()
    }
}
