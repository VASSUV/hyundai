package ru.example.hyundai.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import android.app.NotificationManager

import android.app.NotificationChannel
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color

import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import ru.example.hyundai.R
import ru.example.hyundai.domain.ISharedData
import android.telecom.TelecomManager
import androidx.core.app.ActivityCompat
import android.widget.LinearLayout

import android.widget.RelativeLayout

import android.view.Gravity

import android.graphics.PixelFormat

import android.view.WindowManager
import android.app.Activity

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.net.http.SslError
import android.provider.Settings
import android.view.LayoutInflater
import android.view.WindowManager.LayoutParams
import android.webkit.*
import android.widget.FrameLayout

import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat


class HyundaiService: Service() {
    private val channelID = "hyundai listen"
    private val notificationID = 1
    private val loadHelper = HyundaiLoadHelper(this)
    private val authHelper = HyundaiAuthHelper(this)
    private val profileHelper = HyundaiProfileHelper(this)
    private val colorsHelper = HyundaiColorHelper(this)
    private val carsHelper = HyundaiCarsHelper(this)
    val fasmHelper = HyundaiDecryptFasmHelper(this)
//    private val copyAsmbleHelper = HyundayCopyAsmbleHelper()
    private var job: Job? = null
    var webView: WebView? = null

    val phoneState = Channel<String>()

    @OptIn(DelicateCoroutinesApi::class)
    val phoneStateListener = object: PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            GlobalScope.launch {
                val phone = phoneNumber ?: ""
                if(phone.isNotEmpty()) {
                    val tm = getSystemService(TELECOM_SERVICE) as TelecomManager

                    val success = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        if (ActivityCompat.checkSelfPermission(this@HyundaiService, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                            return@launch
                        }
                        tm.endCall()
                    } else {
                        TODO("VERSION.SDK_INT < P")
                    }
                    phoneState.send(phone)
                }
            }
        }
    }

    private val phoneReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val tm = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    private val serviceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            if (bundle != null) {
                println("receive event to service - $bundle")
                if(bundle.getString("action") == "start") {
                    profileHelper.sendProfile()
                    colorsHelper.sendColors()
                    carsHelper.sendCars()
                }
                if(bundle.getString("action") == "stop") {
                    stopHyundaiListening()
                }
            }
        }
    }

    private fun stopHyundaiListening() {
        TODO("Not yet implemented")
    }

    override fun onBind(intent: Intent?): Nothing? = null

    override fun onCreate() {
        super.onCreate()
        disableSSLCertificateChecking()
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            LayoutParams.TYPE_APPLICATION_OVERLAY;
        else
            LayoutParams.TYPE_PHONE

        val params = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT,
            type,
            LayoutParams.FLAG_SPLIT_TOUCH or LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 100
        params.height = 1200
        params.width = 600

        val view = FrameLayout(this)
        view.background = ColorDrawable(ContextCompat.getColor(this, R.color.teal_200))
        view.layoutParams =
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        webView = WebView(this)
        webView?.setInitialScale(90)
        webView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                println("Override -----" + request?.url?.toString())
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                println("Intercept -----" + request?.url?.toString())
                return super.shouldInterceptRequest(view, request)
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()
//                super.onReceivedSslError(view, handler, error)
            }
        }
        webView?.webChromeClient = object : WebChromeClient() {

        }
        webView?.settings?.javaScriptEnabled = true
        webView?.settings?.javaScriptCanOpenWindowsAutomatically = true
        webView?.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        view.addView(webView)
        val lp = webView?.layoutParams as FrameLayout.LayoutParams
        lp.setMargins(10, 10, 10, 10)
        webView?.layoutParams = lp
        webView?.loadUrl("https://showroom.hyundai.ru/auth")
        windowManager.addView(view, params)

        Shared.preferences = getSharedPreferences("HyundaiPreferences", MODE_PRIVATE)

        val filter = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL)
        registerReceiver(phoneReceiver, filter)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(serviceReceiver, IntentFilter("HyundaiServiceInput"))
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        Shared.preferences = null
        unregisterReceiver(phoneReceiver)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startNotification()
        job?.cancel()
        var authDeferred: Deferred<Unit>? = null
//        copyAsmbleHelper.run(this)
     /*   job = GlobalScope.launch {
            var counter = 1;
            while(counter != 0) {
                counter--
                delay(2000)
                val (isStartSession, isAuthAgain, isPreAuth) = authHelper.startSession()
                loadHelper.loadNext()
                if(authDeferred?.isActive != true) {
                    authDeferred = async {
                        if(isStartSession) {
                            launch {
                                colorsHelper.loadColors()
                                carsHelper.loadAllCars()
                                fasmHelper.loadDecryptFile()
                            }
                        }
                        if (authHelper.auth(isAuthAgain, isPreAuth)) {
                            if (!profileHelper.loadProfile()) {
                                Shared.SESSION_ID.remove()
                                Shared.CSRF.remove()
                                Shared.CSRF_COOKIE.remove()
                            }
                        }
                    }
                }
            }
        }*/
        return START_NOT_STICKY
    }

    private fun startNotification() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = buildNotification().build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nc = NotificationChannel(channelID, "Listen", NotificationManager.IMPORTANCE_HIGH)
            nm.createNotificationChannel(nc)
        }
        nm.notify(notificationID, notification)
        startForeground(notificationID, notification)
    }

    public fun notifyCar(car: String) {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = buildNotification()
            .apply {
                if(car.isEmpty() && false)
                    setVibrate(longArrayOf())
                else {
                    setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                    setContentText(car)
                    setLights(Color.RED, 3000, 3000)
                }
            }
            .build()
        nm.notify(notificationID, notification)
    }

    private fun buildNotification() = NotificationCompat.Builder(this, channelID)
            .setContentTitle("Hyundai")
            .setCategory(Notification.CATEGORY_SERVICE)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(PRIORITY_MAX)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(getLaunchPendingIntent())
            .setCategory(Notification.CATEGORY_SERVICE)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setChannelId(channelID);
                }
            }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun getLaunchPendingIntent(): PendingIntent? {
        val packageName = applicationInfo.packageName
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        return PendingIntent.getActivity(this, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    enum class Shared: ISharedData {
        SESSION_ID, CSRF, CSRF_COOKIE,
        ASMBLE_COPIED;

        override val instance: SharedPreferences
            get() = preferences!!

        companion object {
            var preferences: SharedPreferences? = null
        }
    }
}

fun String.getCookie(name: String): String? {
    val splits = split("; ", ";", "=")
    val index = splits.indexOf(name)
    if(index < 0) return null
    return if(splits.size > index + 1) splits[index + 1] else null
}