package com.itdeveapps.stepsshare.ui.components

import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itdeveapps.stepsshare.ui.theme.CustomColors
import kotlinx.coroutines.delay

@Composable
fun GoalMetricDial(
    progress: Float,
    icon: ImageVector,
    valueText: String,
    unitText: String,
    modifier: Modifier = Modifier,
    progressColors: List<Color> = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
) {
    var shouldAnimate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        shouldAnimate = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (shouldAnimate) progress.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = EaseOutExpo
        )
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(contentAlignment = Alignment.Center) {
            GradientCircularProgress(
                progress = animatedProgress,
                diameter = 40.dp,
                strokeWidth = 2.dp,
                progressColors = progressColors
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append(valueText)
                }
                append("\n")
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Normal
                    )
                ) {
                    append(unitText)
                }
            },
            fontSize = 12.sp,
            lineHeight = TextUnit(12f, TextUnitType.Sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.offset(y = (-10).dp)
        )
    }
}


