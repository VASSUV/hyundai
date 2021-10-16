package ru.example.hyundai.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.NoRouteToHostException
import java.net.URL

class HyundaiDecryptFasmHelper(val service: HyundaiService) {
    var fasmFile: String? = null

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun loadDecryptFile() : Boolean {
        val host = "https://showroom.hyundai.ru/static/lorem/lorem_bg.wasm"
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
                    fasmFile = inputStream.readBytes().map { it.toInt().toChar() }.joinToString(separator = "")
                    return true
                }
            } catch (exception: NoRouteToHostException) {

            } finally {
                disconnect()
            }
        }
        return false
    }
}