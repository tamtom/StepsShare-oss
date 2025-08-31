package com.itdeveapps.stepsshare.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.itdeveapps.stepsshare.navigation.MainDestination
import com.itdeveapps.stepsshare.navigation.bottomTabItems
import com.itdeveapps.stepsshare.ui.components.BottomNavBar
import com.itdeveapps.stepsshare.ui.components.ToolbarComponent
import com.itdeveapps.stepsshare.ui.goals.GoalsScreen
import com.itdeveapps.stepsshare.ui.friends.FriendsScreen
import com.itdeveapps.stepsshare.ui.profile.ProfileScreen
import com.itdeveapps.stepsshare.ui.stats.StatsScreen
import com.itdeveapps.stepsshare.ui.steps.StepsScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        topBar = { ToolbarComponent() },
        bottomBar = { BottomNavBar(navController = navController, items = bottomTabItems) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NavHost(
                navController = navController,
                startDestination = MainDestination.Steps
            ) {
                composable<MainDestination.Steps> {
                    StepsScreen()
                }
                composable<MainDestination.Goals> {
                    GoalsScreen()
                }
                composable<MainDestination.Stats> {
                    StatsScreen()
                }
                //hiding it for now to focus on core features
/*                composable<MainDestination.Friends> {
                    FriendsScreen()
                }*/
                composable<MainDestination.Profile> {
                    ProfileScreen()
                }
            }
        }
    }
}


