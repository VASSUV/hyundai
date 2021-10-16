package ru.example.hyundai.service

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.NoRouteToHostException
import java.net.URL
import java.util.zip.GZIPInputStream
import kotlin.properties.Delegates

class HyundaiColorHelper(val service: HyundaiService) {

    var colorsState: String by Delegates.observable("") { _, _, _ -> sendColors() }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun loadColors() : Boolean {
        val host = "https://showroom.hyundai.ru/rest/configurator/showroom/colors"
        with(URL(host).openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            setRequestProperty("Accept", "application/json, text/plain, */*")
//            setRequestProperty("Content-Type", "application/json;charset=utf-8")
            setRequestProperty("Cookie", "pageviewCount=1; _frontendSessionId=${HyundaiService.Shared.SESSION_ID.getString()}; _csrf=${HyundaiService.Shared.CSRF_COOKIE.getString()}")
            setRequestProperty("Accept-Language", "ru")
            setRequestProperty("Accept-Encoding", "gzip, deflate, br")
            setRequestProperty("x-csrf-token", HyundaiService.Shared.CSRF.getString())
            setRequestProperty("X-Requested-With", "XMLHttpRequest")
            try {
                println("$responseCode - $host")
                if(responseCode == 200) {
                    val element = GZIPInputStream(inputStream).bufferedReader().readText()
                    colorsState = element
                    return true
                }
            } catch (exception: NoRouteToHostException) {

            } finally {
                disconnect()
            }
        }
        return false
    }

    fun sendColors() {
        val intent = Intent("HyundaiServiceOutput")
        intent.putExtra("action", "colors")
        intent.putExtra("colors", colorsState)
        LocalBroadcastManager.getInstance(service).sendBroadcast(intent)
    }
}