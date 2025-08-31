package com.itdeveapps.stepsshare

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.itdeveapps.stepsshare.data.AndroidStepsRepository
import com.itdeveapps.stepsshare.data.StepsRepository
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    
    private val stepsRepository: StepsRepository by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        
        setContent {
            val useDarkTheme = isSystemInDarkTheme()
            
            // Apply WindowCompat system bar theming
            LaunchedEffect(useDarkTheme) {
                // Theme-based system bar appearance
                WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
                    useDarkTheme.not()
                WindowCompat.getInsetsController(window, window.decorView)
                    .isAppearanceLightNavigationBars = false
            }
            
            // Additional system bar control functions
            val setLightStatusBar: (Boolean) -> Unit = { isLightStatusBar ->
                WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
                    isLightStatusBar
            }
            
            val setLightNavBar: (Boolean) -> Unit = { isLightNavBar ->
                WindowCompat.getInsetsController(window, window.decorView)
                    .isAppearanceLightNavigationBars = isLightNavBar
            }
            
            App()
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Start real-time step tracking when app comes to foreground
        (stepsRepository as? AndroidStepsRepository)?.startRealtimeTracking()
    }
    
    override fun onPause() {
        super.onPause()
        // Stop real-time step tracking when app goes to background
        (stepsRepository as? AndroidStepsRepository)?.stopRealtimeTracking()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}