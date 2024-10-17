package com.snowdango.sumire

import android.annotation.SuppressLint
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.snowdango.presenter.history.HistoryScreen
import com.snowdango.sumire.presenter.playing.PlayingScreen
import com.snowdango.sumire.settings.SettingsScreen

@SuppressLint("RestrictedApi")
@Composable
fun MainScreen(
    windowSize: WindowSizeClass,
) {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .shadow(8.dp),
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                ROUTE.entries.forEach { route ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == route.name } == true
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors().copy(
                            selectedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        selected = selected,
                        icon = {
                            Icon(
                                imageVector = if (selected) route.selectedIcon else route.unSelectedIcon,
                                contentDescription = null,
                            )
                        },
                        label = { Text(text = route.label) },
                        onClick = {
                            if (!selected) {
                                navController.navigate(route = route.name) {
                                    launchSingleTop = true
                                }
                            }
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE.PLAYING.name,
            exitTransition = { ExitTransition.None },
            enterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            composable(
                route = ROUTE.PLAYING.name,
                exitTransition = null,
                enterTransition = null,
                popExitTransition = null,
                popEnterTransition = null,
            ) {
                PlayingScreen(
                    windowSize,
                )
            }
            composable(
                route = ROUTE.HISTORY.name,
                exitTransition = null,
                enterTransition = null,
                popExitTransition = null,
                popEnterTransition = null,
            ) {
                HistoryScreen(
                    windowSize,
                )
            }

            composable(
                route = ROUTE.SETTINGS.name,
                exitTransition = null,
                enterTransition = null,
                popExitTransition = null,
                popEnterTransition = null,
            ) {
                SettingsScreen()
            }
        }
    }
}

private enum class ROUTE(
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val label: String,
) {
    PLAYING(
        selectedIcon = Icons.Filled.PlayArrow,
        unSelectedIcon = Icons.Outlined.PlayArrow,
        label = "playing",
    ),
    HISTORY(
        selectedIcon = Icons.Filled.MusicNote,
        unSelectedIcon = Icons.Outlined.MusicNote,
        label = "history",
    ),
    SETTINGS(
        selectedIcon = Icons.Filled.Settings,
        unSelectedIcon = Icons.Outlined.Settings,
        label = "settings",
    ),
}
