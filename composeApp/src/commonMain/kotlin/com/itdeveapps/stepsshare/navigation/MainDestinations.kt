package com.itdeveapps.stepsshare.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

/**
 * Type-safe root destinations for the app's bottom navigation using Navigation Compose typed routes.
 */
sealed interface MainDestination {
    @Serializable
    data object Steps : MainDestination

    @Serializable
    data object Goals : MainDestination

    @Serializable
    data object Stats : MainDestination

    @Serializable
    data object Friends : MainDestination

    @Serializable
    data object Profile : MainDestination
}

data class BottomTabItem(
    val destination: MainDestination,
    val label: String,
    val icon: ImageVector
)

val bottomTabItems: List<BottomTabItem> = listOf(
    BottomTabItem(
        destination = MainDestination.Steps,
        label = "Steps",
        icon = Icons.AutoMirrored.Outlined.DirectionsWalk
    ),
    BottomTabItem(
        destination = MainDestination.Goals,
        label = "Goals",
        icon = Icons.Outlined.Flag
    ),
    BottomTabItem(
        destination = MainDestination.Stats,
        label = "Statistics",
        icon = Icons.Outlined.Insights
    ),
    //hiding it for now to focus on core features
/*    BottomTabItem(
        destination = MainDestination.Friends,
        label = "Friends",
        icon = Icons.Outlined.Group
    ),*/
    BottomTabItem(
        destination = MainDestination.Profile,
        label = "Profile",
        icon = Icons.Outlined.Person
    )
)


