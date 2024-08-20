package com.snowdango.sumire

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.snowdango.sumire.infla.PlayingSongSharedFlow
import com.snowdango.sumire.ui.theme.SumireTheme

@Composable
fun MainScreen(
    songSharedFlow: PlayingSongSharedFlow,
) {
    val navController = rememberNavController()
    SumireTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    ROUTE.entries.forEach { route ->
                        val selected = currentDestination?.hierarchy?.any { it.route == route.name } == true
                        NavigationBarItem(
                            selected = selected,
                            icon = {
                                Icon(
                                    imageVector = if (selected) route.selectedIcon else route.unSelectedIcon,
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = route.label) },
                            onClick = {
                                navController.navigate(route.name)
                            },
                        )
                    }
                }
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = ROUTE.PLAYING.name,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(route = ROUTE.PLAYING.name) {
                    PlayingScreen(
                        songSharedFlow
                    )
                }
                composable(route = ROUTE.HISTORY.name) {
                    HistoryScreen()
                }
            }
        }
    }
}

private enum class ROUTE(val selectedIcon: ImageVector, val unSelectedIcon: ImageVector, val label: String) {
    PLAYING(selectedIcon = Icons.Filled.PlayArrow, unSelectedIcon = Icons.Outlined.PlayArrow, label = "playing"),
    HISTORY(selectedIcon = Icons.Filled.MusicNote, unSelectedIcon = Icons.Outlined.MusicNote, label = "history"),
}