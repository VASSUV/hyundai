package ru.example.hyundai.service

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import ru.example.hyundai.service.HyundaiService.Shared
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@OptIn(DelicateCoroutinesApi::class)
class HyundaiLoadHelper(val service: HyundaiService) {
    private val carsChannel = Channel<String>()

    init {
        GlobalScope.launch {
            carsChannel.consumeEach(service::notifyCar)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun loadNext() {
        val host = "https://showroom.hyundai.ru/rest/car"
        with(URL(host).openConnection() as HttpURLConnection) {
            this.setRequestProperty("Accept", "application/json, text/plain, */*")
            this.setRequestProperty(
                "Cookie",
                "pageviewCount=1; _frontendSessionId=${Shared.SESSION_ID.getString()}; _csrf=${Shared.CSRF_COOKIE.getString()}"
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
            this.setRequestProperty("x-csrf-token", Shared.CSRF.getString())
            this.setRequestProperty("X-Requested-With", "XMLHttpRequest")
            this.setRequestProperty("X-Captcha", "test")

            requestMethod = "GET"
            try {
                println("$responseCode - $host")
                if (responseCode != 200) {
                    Shared.SESSION_ID.remove()
                    Shared.CSRF.remove()
                    Shared.CSRF_COOKIE.remove()
                } else {
//                    if (getHeaderField("Content-Type") == "application/json; charset=UTF-8") {
//                        carsChannel.send("")
//                    } else {
                    println("$responseCode - $host")
//                        val cars = inputStream.readBytes().map { it.toInt().toChar() }
//                            .joinToString(separator = "")
//                        println(cars)

                    val bytes = inputStream.readBytes()
                    if(bytes.size > 50) {
                        carsChannel.send(bytes.map { it.toInt().toChar() }
                            .joinToString(separator = ""))
                    }
//                    decryptFile(bytes)

//                        carsChannel.send(cars)
//                    }
                }
            } catch (error: Exception) {
                carsChannel.send("")
            }
        }
    }

    private suspend fun decryptFile(readBytes: ByteArray) {
        val tmp =
            "6MXDzz5cSFi2i7V6QbNc1GAA8bzgwQjIZGXnQU360cKjASEwSAWUaqpBUUjWb5mOJG/h1kHIm05TNMJ+D9zZvTP848eLBi87CZbZHhMJOiRdwRk96FPKAZpCwK+fKawX/nDUB4uVdSFM25RtZGIOdrVCdBbTVHI4KyLTuFvX4+GoBGt/WQ//q2BlVykUwK5EtLxccsl5RGNtt6TMjPvWgHzK+Cl6SPXA1NLGTLPS5HUAsFthIJoFZnpKL4p7MG/e9qBYiWNPv2iOPZVP8SdDxfbABr8E6yqhG4y1TqG0h3S4kfJCDMXajmMMzuiycb+4Mfb1Wq3Rf9/uEDJiMj5tVLZjpOWMjiYy/0dvgtlISBq1Gnl70tcf7p8BNZxKmpg+jQM7a0WVdj81LrsVlETyUX4Y3ncQpo9mUrbIdSowDEj5RQBtidGiqZWFxQSlUmUlvZK3YHwOG6Us5csRczTmTHv/Gxg8JtQjQ2Ir7K89G1VDcae63ecaPj/gKIo7aNt+GAGC8uoDPOy2k6g5vZtor8lQGSDxn80+5GyjC0QYhqKUvCzsuOJ6EMQjmSGuocRYJr/O/+AkKVz5McFCIAbeCw=="
        val result = runAsmble(tmp).await()
        println(result)
    }

    private suspend fun runAsmble(text: String): Deferred<String> {

        return CompletableDeferred<String>().apply {
            withContext(Dispatchers.Main) {
                service.webView?.evaluateJavascript(
                    "function str2ab(str) {\n" +
                            "  var buf = new ArrayBuffer(str.length*2); // 2 bytes for each char\n" +
                            "  var bufView = new Uint16Array(buf);\n" +
                            "  for (var i=0, strLen=str.length; i<strLen; i++) {\n" +
                            "    bufView[i] = str.charCodeAt(i);\n" +
                            "  }\n" +
                            "  return buf;\n" +
                            "}" +
                            "  try {\n" +
                            "    const promise1 = new Promise((resolve, reject) => { \n" +
                            "      var importObject = { imports: { imported_func: arg => resolve(arg) } };\n" +
                            "      WebAssembly.instantiate(str2ab('${service.fasmHelper.fasmFile}'), importObject).instance;\n" +
                            "    });\n" +
                            "    await Promise.all([promise1]);\n" +
                            "  } catch(e) { \n" +
                            "     e.toString;\n" +
                            "  }",
                    ::complete
                )
            }
        } /*
{ imports: { imported_func: arg => console.log(arg) } }
        */

//        val path = getAppPath(service)
//        return ShellExecuter().execute("$path/asmble/bin/asmble")
    }
}


class ShellExecuter {
    fun execute(command: String?): String {
        val output = StringBuffer()
        val p: Process
        try {
            p = Runtime.getRuntime().exec(command)
            p.waitFor()
            val reader =
                BufferedReader(InputStreamReader(p.inputStream))
            var line = ""
            while (reader.readLine().also { line = it } != null) {
                output.append(line + "n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return output.toString()
    }
}