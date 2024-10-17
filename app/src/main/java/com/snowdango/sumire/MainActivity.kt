package com.snowdango.sumire

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.snowdango.sumire.service.SongListenerService
import com.snowdango.sumire.ui.theme.SumireTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel<MainViewModel>()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SumireTheme {
                val isPermission = viewModel.isShowPermissionDialog.collectAsState()
                NotificationListenerDialog(
                    isShow = isPermission.value,
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
    }

    override fun onStart() {
        super.onStart()
        if (getEnabledListenerPackages(this).contains(packageName)) {
            val intent = Intent(this, SongListenerService::class.java)
            startForegroundService(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("permission", getEnabledListenerPackages(this).contains(packageName).not().toString())
        viewModel.setIsShowPermissionDialog(
            getEnabledListenerPackages(this).contains(packageName).not()
        )
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
