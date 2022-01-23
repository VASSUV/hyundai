package ru.example.hyundai

import android.Manifest
import android.app.ActivityManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.compose.NavHost
import ru.example.hyundai.screens.MainScreen
import ru.example.hyundai.service.HyundaiService
import ru.example.hyundai.service.disableSSLCertificateChecking
import ru.example.hyundai.ui.theme.HyundaiTheme
import ru.example.hyundai.viewmodels.CarsViewModel
import ru.example.hyundai.viewmodels.ProfileViewModel
import ru.example.hyundai.viewmodels.ServiceViewModel


class MainActivity : ComponentActivity() {
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val serviceViewModel by viewModels<ServiceViewModel>()
    private val carsViewModel by viewModels<CarsViewModel>()

    private val serviceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            if (bundle != null) {
                println("receive event from service - $bundle")
                when(val actionName = bundle.getString("action")) {
                    "colors" -> {

                    }
                    "profile" -> {
                        profileViewModel.onProfile(bundle.getString(actionName) ?: "")
                    }
                    "cars" -> {
                        carsViewModel.onAllCars(bundle.getByteArray(actionName) ?: byteArrayOf())
                    }
                    "listenCars" -> {
                        carsViewModel.onListenedCars(bundle.getString(actionName) ?: "")
                    }
                    "captcha" -> {
                        carsViewModel.onListenedCars(bundle.getString(actionName) ?: "")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        disableSSLCertificateChecking()
        sendStartEventToService()

        serviceViewModel.getEventLiveData().observe(this) { event ->
            when(event) {
                ServiceViewModel.Event.START -> startService()
                ServiceViewModel.Event.STOP -> stopService()
                else -> Unit
            }
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(serviceReceiver, IntentFilter("HyundaiServiceOutput"))

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.PROCESS_OUTGOING_CALLS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.ANSWER_PHONE_CALLS,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED
                ),
                1
            )
        }


        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                ActivityCompat.startActivityForResult(this, intent, 1234, null)
            }
        }

        setContent {
            HyundaiTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ComposeApp()
                }
            }
        }
    }

    private fun sendStartEventToService() {
        val intent = Intent("HyundaiServiceInput")
        intent.putExtra("action", "start")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun startService() {
        if(!isServiceRunning()) {
            val intent = Intent(this, HyundaiService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }

    private fun stopService() {
        if(isServiceRunning()) {
            Intent("HyundaiServiceInput").apply {
                putExtra("action", "stop")
                LocalBroadcastManager.getInstance(this@MainActivity).sendBroadcast(this)
            }
            Intent(this, HyundaiService::class.java).apply {
                stopService(this)
            }
        }
    }

    private fun isServiceRunning(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE))
            if (HyundaiService::class.java.name == service.service.className)
                return true
        return false
    }
}