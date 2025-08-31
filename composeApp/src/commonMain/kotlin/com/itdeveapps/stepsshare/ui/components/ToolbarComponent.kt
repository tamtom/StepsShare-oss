package com.itdeveapps.stepsshare.ui.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.itdeveapps.stepsshare.ui.theme.CustomColors
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarComponent(
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        title = {
            Text(
                "Steps Share",
                style = TextStyle(
                    brush = Brush.horizontalGradient(
                        listOf(CustomColors.ButtonGradientStart, CustomColors.ButtonGradientEnd)
                    ),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        },
    )
}

@Preview
@Composable
fun ToolbarComponentPreview() {
    ToolbarComponent()
}
