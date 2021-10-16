package ru.example.hyundai.domain


import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import ru.example.hyundai.App

enum class SharedData: ISharedData {
    TOKEN;

    override val instance: SharedPreferences
        get() = preferences

    companion object {
        private val preferences: SharedPreferences by lazy {
            App.context.getSharedPreferences("HyundaiPreferences", MODE_PRIVATE)
        }
    }
}
