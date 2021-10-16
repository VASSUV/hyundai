package ru.example.hyundai.domain

import android.content.SharedPreferences

interface ISharedData {
    val instance: SharedPreferences

    val name: String

    fun getString() = instance.getString(name, "")
    fun getInt() = instance.getInt(name, 0)
    fun getBoolean() = instance.getBoolean(name, false)
    fun getLong() = instance.getLong(name, 0)
    fun getFloat() = instance.getFloat(name, 0F)

    fun saveString(value: String) = instance.edit().putString(name, value).apply()
    fun saveInt(value: Int) = instance.edit().putInt(name, value).apply()
    fun saveBoolean(value: Boolean) = instance.edit().putBoolean(name, value).apply()
    fun saveLong(value: Long) = instance.edit().putLong(name, value).apply()
    fun saveFloat(value: Float) = instance.edit().putFloat(name, value).apply()

    fun remove() = instance.edit().remove(name).apply()
    fun contains() = instance.contains(name)
}

