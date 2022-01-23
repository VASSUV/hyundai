package ru.example.hyundai.service

import android.annotation.SuppressLint
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

fun disableSSLCertificateChecking() {
    val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
    object : X509TrustManager {

        @SuppressLint("TrustAllX509TrustManager")
        @Throws(CertificateException::class)
        override fun checkClientTrusted(arg0: Array<X509Certificate?>?, arg1: String?) {
            // Not implemented
        }

        @SuppressLint("TrustAllX509TrustManager")
        @Throws(CertificateException::class)
        override fun checkServerTrusted(arg0: Array<X509Certificate?>?, arg1: String?) {
            // Not implemented
        }

        override fun getAcceptedIssuers(): Array<X509Certificate>?  = null
    })
    try {
        val sc: SSLContext = SSLContext.getInstance("TLS")
        sc.init(null, trustAllCerts, SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
    } catch (e: KeyManagementException) {
        e.printStackTrace()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
}