package com.snowdango.sumire

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat.getEnabledListenerPackages
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.snowdango.sumire.dialog.NotificationListenerDialog
import com.snowdango.sumire.dialog.NotificationPostDialog
import com.snowdango.sumire.service.SongListenerService
import com.snowdango.sumire.ui.theme.SumireTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel<MainViewModel>()
    private val requestPermission = registerForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        callback = {}
    )

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SumireTheme {
                val isListenerPermission = viewModel.isShowPermissionDialog.collectAsState()
                val isNotificationPermission = viewModel.isShowNotificationDialog.collectAsState()
                NotificationPostDialog(
                    isShow = isNotificationPermission.value,
                    onDismissListener = {
                        viewModel.setIsNotificationPermissionDialog(false)
                        viewModel.setFirstTimeLaunch(false)
                    },
                    onConfirmClick = {
                        requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                )
                NotificationListenerDialog(
                    isShow = isListenerPermission.value && isNotificationPermission.value.not(),
                    onDismissRequest = {
                        viewModel.setIsShowPermissionDialog(false)
                    },
                    onConfirmClick = {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        startActivity(intent)
                    }
                )

                MainScreen(
                    calculateWindowSizeClass(activity = this),
                )
            }
        }
        viewModel.setIsShowPermissionDialog(
            getEnabledListenerPackages(this).contains(packageName).not()
        )
    }

    override fun onStart() {
        super.onStart()
        if (getEnabledListenerPackages(this).contains(packageName)) {
            val intent = Intent(this, SongListenerService::class.java)
            startForegroundService(intent)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SumireTheme {
        Greeting("Android")
    }
}
