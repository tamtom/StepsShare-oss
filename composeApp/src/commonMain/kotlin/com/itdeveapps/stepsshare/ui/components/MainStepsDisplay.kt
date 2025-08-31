package com.itdeveapps.stepsshare.ui.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.material3.MaterialTheme

@Composable
fun MainStepsDisplay(
    currentSteps: Long,
    goalSteps: Int = 10000,
    averageSteps: Int = 0,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (currentSteps.toFloat() / goalSteps).coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = 1000,
            easing = EaseOutExpo
        )
    )
    val animatedSteps by animateIntAsState(
        targetValue = currentSteps.toInt(),
        animationSpec = tween(
            durationMillis = 1000,
            easing = EaseOutExpo
        )
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(280.dp)
        ) {
            // Large circular progress
            GradientCircularProgress(
                progress = animatedProgress,
                diameter = 280.dp,
                strokeWidth = 12.dp,
                modifier = Modifier.size(280.dp)
            )
            
            // Content in center
            Column(
                Modifier.align(Alignment.TopCenter).padding(top = 45.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Walking icon
                Icon(
                    imageVector = Icons.AutoMirrored.Default.DirectionsWalk,
                    contentDescription = "Walking",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Steps count
                Text(
                    text = animatedSteps.toString(),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                
                // "Steps" label
                Text(
                    text = "Steps",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Goal info
                Text(
                    text = "from ${goalSteps.formatWithSpaces()}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // Average info (only show if we have data)
                if (averageSteps > 0) {
                    Text(
                        text = "on average ${averageSteps.formatWithSpaces()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        

        // Goal and average info

    }
}

// Extension function to format numbers with spaces (10 000 instead of 10000)
private fun Int.formatWithSpaces(): String {
    return this.toString().reversed().chunked(3).joinToString(" ").reversed()
}

private fun Long.formatWithSpaces(): String {
    return this.toString().reversed().chunked(3).joinToString(" ").reversed()
}

@Preview
@Composable
fun MainStepsDisplayPreview() {
    MainStepsDisplay(currentSteps = 7500, goalSteps = 10000, averageSteps = 8000)
}

@Preview
@Composable
fun MainStepsDisplayWithoutAveragePreview() {
    MainStepsDisplay(currentSteps = 5000)
}

@Preview
@Composable
fun MainStepsDisplayFullProgressPreview() {
    MainStepsDisplay(currentSteps = 12000, goalSteps = 10000, averageSteps = 9500)
}
