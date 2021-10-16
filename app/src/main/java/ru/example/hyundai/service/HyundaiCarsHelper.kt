package ru.example.hyundai.service

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import ru.example.hyundai.gen.Allcars
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream
import kotlin.properties.Delegates

class HyundaiCarsHelper(private val service: HyundaiService) {

    var carsState: Allcars.AllCars? by Delegates.observable(null) { _, _, _ -> sendCars() }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun loadAllCars() {
        val host = "https://showroom.hyundai.ru/rest/buy/car-show/all"
        with(URL(host).openConnection() as HttpURLConnection) {
            this.setRequestProperty("Accept", "application/json, text/plain, */*")
            this.setRequestProperty(
                "Cookie",
                "pageviewCount=1; _frontendSessionId=${HyundaiService.Shared.SESSION_ID.getString()}; _csrf=${HyundaiService.Shared.CSRF_COOKIE.getString()}"
            )
            this.setRequestProperty("Accept-Language", "ru")
            this.setRequestProperty("Host", "showroom.hyundai.ru")
            this.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.2 Safari/605.1.15"
            )
            this.setRequestProperty("Referer", "https://showroom.hyundai.ru/")
            this.setRequestProperty("Accept-Encoding", "gzip, deflate, br")
            this.setRequestProperty("Connection", "keep-alive")
            this.setRequestProperty("x-csrf-token", HyundaiService.Shared.CSRF.getString())
            this.setRequestProperty("X-Requested-With", "XMLHttpRequest")
            this.setRequestProperty("X-Captcha", "test")

            requestMethod = "GET"
            try {
                if (responseCode == 200) {
                    val cars = Allcars.AllCars.parseFrom(GZIPInputStream(inputStream))
                    carsState = cars
                }
            } catch (error: Exception) {
                carsState = null
            } finally {
                disconnect()
            }
        }
    }

    fun sendCars() {
        val intent = Intent("HyundaiServiceOutput")
        intent.putExtra("action", "cars")
        intent.putExtra("cars", carsState?.toByteArray())
        LocalBroadcastManager.getInstance(service).sendBroadcast(intent)
    }
}
