package ru.example.hyundai.service

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.BufferedInputStream
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.NoRouteToHostException
import java.net.URL
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream
import kotlin.properties.Delegates

class HyundaiProfileHelper (val service: HyundaiService) {

    var profileState: String by Delegates.observable("") { _, _, _ -> sendProfile() }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun loadProfile() : Boolean {
        val host = "https://showroom.hyundai.ru/rest/cabinet/user/profile"
        with(URL(host).openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            setRequestProperty("Host", "showroom.hyundai.ru")
            setRequestProperty("Accept", "application/json, text/plain, */*")
//            setRequestProperty("Content-Type", "application/json;charset=utf-8")
            setRequestProperty("Cookie", "pageviewCount=1; _frontendSessionId=${HyundaiService.Shared.SESSION_ID.getString()}; _csrf=${HyundaiService.Shared.CSRF_COOKIE.getString()}")
            setRequestProperty("Accept-Language", "ru")
            setRequestProperty("Accept-Encoding", "gzip, deflate, br")
            setRequestProperty("x-csrf-token", HyundaiService.Shared.CSRF.getString())
            setRequestProperty("X-Requested-With", "XMLHttpRequest")
            setRequestProperty("Connection", "keep-alive")
            setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.2 Safari/605.1.15")
            setRequestProperty("Referer", "https://showroom.hyundai.ru/cabinet")
            setRequestProperty("X-Captcha", "test")
            try {
                println("$responseCode - $host")
                if(responseCode == 200) {
                    val element = GZIPInputStream(inputStream).bufferedReader(Charset.forName("UTF-8")).readText()
                    profileState = element
                    return true
                }
            } catch(exception: NoRouteToHostException) {

            } finally {
                disconnect()
            }
        }
        return false
    }

    fun sendProfile() {
        val intent = Intent("HyundaiServiceOutput")
        intent.putExtra("action", "profile")
        intent.putExtra("profile", profileState)
        LocalBroadcastManager.getInstance(service).sendBroadcast(intent)
    }
}