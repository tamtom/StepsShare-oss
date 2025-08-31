package com.itdeveapps.stepsshare.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import com.itdeveapps.stepsshare.navigation.BottomTabItem
import com.itdeveapps.stepsshare.navigation.MainDestination
import androidx.compose.material3.MaterialTheme

@Composable
fun BottomNavBar(
    navController: NavHostController,
    items: List<BottomTabItem>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->
            val selected = when (item.destination) {
                MainDestination.Steps -> currentDestination?.hierarchy?.any { it.hasRoute<MainDestination.Steps>() } == true
                MainDestination.Goals -> currentDestination?.hierarchy?.any { it.hasRoute<MainDestination.Goals>() } == true
                MainDestination.Stats -> currentDestination?.hierarchy?.any { it.hasRoute<MainDestination.Stats>() } == true
                MainDestination.Friends -> currentDestination?.hierarchy?.any { it.hasRoute<MainDestination.Friends>() } == true
                MainDestination.Profile -> currentDestination?.hierarchy?.any { it.hasRoute<MainDestination.Profile>() } == true
            }

            NavigationBarItem(
                selected = selected,
                onClick = {
                    val destination = item.destination
                    val options = navOptions {
                        // Pop up to the root of the graph and save state for proper bottom nav behavior
                        popUpTo<MainDestination.Steps> {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    when (destination) {
                        MainDestination.Steps -> navController.navigate(MainDestination.Steps, options)
                        MainDestination.Goals -> navController.navigate(MainDestination.Goals, options)
                        MainDestination.Stats -> navController.navigate(MainDestination.Stats, options)
                        MainDestination.Friends -> navController.navigate(MainDestination.Friends, options)
                        MainDestination.Profile -> navController.navigate(MainDestination.Profile, options)
                    }
                },
				icon = { androidx.compose.material3.Icon(imageVector = item.icon, contentDescription = item.label) },
				label = { Text(item.label) },
				colors = NavigationBarItemDefaults.colors(
					        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.outlineVariant,
        unselectedTextColor = MaterialTheme.colorScheme.outlineVariant,
        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
				)
            )
        }
    }
}


