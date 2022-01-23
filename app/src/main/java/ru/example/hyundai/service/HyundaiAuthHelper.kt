package ru.example.hyundai.service

import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.example.hyundai.gen.sendCode
import kotlinx.coroutines.delay
import org.json.JSONObject
import ru.example.hyundai.domain.AppModel
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.math.max
import kotlin.math.min
import ru.example.hyundai.service.HyundaiService.Shared

class HyundaiAuthHelper(val service: HyundaiService) {

    private var lastAuthTime = 0L
    private var lastTryAuthTime = 0L

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun startSession() : Triple<Boolean, Boolean, Boolean> {
        val authDeltaTime = (5.5 * 60 * 1000).toLong()
        val isPreAuth = lastAuthTime == 0L && Shared.SESSION_ID.getString()?.isNotEmpty() == true
        val isAuthAgain = if(isPreAuth) {
            lastAuthTime = Calendar.getInstance().timeInMillis - authDeltaTime
            false
        } else {
            lastAuthTime < Calendar.getInstance().timeInMillis - authDeltaTime
        }
        var isStartSession = true
        if(isAuthAgain) {
            Shared.SESSION_ID.remove()
            Shared.CSRF.remove()
            Shared.CSRF_COOKIE.remove()
        }
        if( Shared.SESSION_ID.getString()?.isEmpty() == true) {
            loadSessionId()
            isStartSession = loadCsrf()
        }
        return Triple(isStartSession, isAuthAgain, isPreAuth)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun auth(isAuthAgain: Boolean, isPreAuth: Boolean) : Boolean {

        if(isAuthAgain) {
            while (!service.phoneState.isEmpty) {
                service.phoneState.receive()
            }
            val delayTime = 50 * 60 * 1000L
            val lastTryTime = if(isPreAuth) Calendar.getInstance().timeInMillis else lastTryAuthTime
            val newDelayTime = min(delayTime, max(0, delayTime - Calendar.getInstance().timeInMillis + lastTryTime))
            println("delay $newDelayTime millis to next auth")
            delay(newDelayTime)
            lastTryAuthTime = Calendar.getInstance().timeInMillis
            sendCodeRequest()
            var phone: String
            do {
                phone = service.phoneState.receive()
            } while (phone.isEmpty())
            if(confirm(phone.takeLast(4))) {
                lastAuthTime = Calendar.getInstance().timeInMillis
                return true
            }
        }
        return isPreAuth
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun confirm(code: String) : Boolean {
        val host = "https://showroom.hyundai.ru/rest/auth/login/confirm"
        with(URL(host).openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            setRequestProperty("Accept", "application/json, text/plain, */*")
            setRequestProperty("Content-Type", "application/json;charset=utf-8")
            setRequestProperty("Cookie", "pageviewCount=1; _frontendSessionId=${Shared.SESSION_ID.getString()}; _csrf=${Shared.CSRF_COOKIE.getString()}")
            setRequestProperty("Accept-Language", "ru")
            setRequestProperty("Accept-Encoding", "gzip, deflate, br")
            setRequestProperty("x-csrf-token", Shared.CSRF.getString())
            setRequestProperty("X-Requested-With", "XMLHttpRequest")
            val json = """{
              "code": "$code",
              "phone": "${AppModel.number}",
              "front_id": "1632489745801",
              "utm_object": "{\"utm_medium\":\"referral\",\"utm_source\":\"hyundai.ru\",\"utm_campaign\":\"main_menu\",\"_ga\":\"2.19631099.2107006786.1630044381-2029646611.1629350308\"}"
            }""".trimIndent()

            try {
                outputStream.write(json.toByteArray())
                println("$responseCode - $host")
                if(responseCode == 200) {
                    return true
                }
            } catch(error: Exception) {}
        }
        return false
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun sendCodeRequest() {
        val host = "https://showroom.hyundai.ru/rest/auth/login/send-code"
        with(URL(host).openConnection() as HttpURLConnection) {
            requestMethod = "POST"

            setRequestProperty("Accept", "application/json, text/plain, */*")
            setRequestProperty("Content-Type", "multipart/form-data")
            setRequestProperty("Cookie", "pageviewCount=1; _frontendSessionId=${Shared.SESSION_ID.getString()}; _csrf=${Shared.CSRF_COOKIE.getString()}")
            setRequestProperty("Accept-Language", "ru")
            setRequestProperty("x-csrf-token", Shared.CSRF.getString())
            setRequestProperty("X-Requested-With", "XMLHttpRequest")
            setRequestProperty("X-Captcha", "test")
            sendCode {
                captcha = "test"
                number = AppModel.number
            }.writeTo(outputStream)
//            outputStream.write("\n${0x04.toChar()}test${0x12.toChar()}\n9176181842".toByteArray())
            try {
                println("$responseCode - $host")
            } catch(error: Exception) {}
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun loadSessionId() {
        var sessionId = Shared.SESSION_ID.getString()?.takeIf { it.isNotEmpty() }
        if(sessionId == null) {
            val host = "https://showroom.hyundai.ru/static/icons/fav.svg"
            with(URL(host).openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                try {
                    println("$responseCode - $host")
                    inputStream.bufferedReader()
                    val cookies = getHeaderField("Set-Cookie")
                    sessionId = cookies.getCookie("_frontendSessionId")
                } catch(error: Exception) {}
            }
            Shared.SESSION_ID.saveString(sessionId ?: "")
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun loadCsrf(): Boolean {
        val csrf = Shared.CSRF.getString()?.takeIf { it.isNotEmpty() }
        if(csrf == null) {

            val host = "https://showroom.hyundai.ru/rest/csrf"
            with(URL(host).openConnection() as HttpURLConnection) {
                this.setRequestProperty("Cookie", "_gcl_au=1.1.656215768.1631099114; pageviewCount=1; _frontendSessionId=${Shared.SESSION_ID.getString()}")
                requestMethod = "GET"
                try {
                    println("$responseCode - $host")
                    if (responseCode != 200) {
                        Shared.SESSION_ID.saveString("")
                    } else {
                        val response = Response(
                            inputStream.bufferedReader().readLines().joinToString()
                        )
                        val cookies = getHeaderField("Set-Cookie")
                        val csrfCookie = cookies.getCookie("_csrf")
                        Shared.CSRF.saveString(response.csrf ?: "")
                        Shared.CSRF_COOKIE.saveString(csrfCookie ?: "")
                        return response.csrf?.isNotEmpty() == true
                    }
                } catch(error: Exception) {}
            }
        }
        return false
    }

    class Response(json: String) : JSONObject(json) {
        val csrf: String? = this.optString("csrf")
    }
}