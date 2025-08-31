package com.itdeveapps.stepsshare.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itdeveapps.stepsshare.domain.model.ColorVariant

@Composable
fun WeightInput(
    weight: Double,
    onWeightChange: (Double) -> Unit,
    color: ColorVariant,
    modifier: Modifier = Modifier
) {
    var weightText by remember { mutableStateOf(weight.toString()) }
    var showError by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What's your weight?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This helps us calculate accurate calorie burn",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Weight input field
        OutlinedTextField(
            value = weightText,
            onValueChange = { 
                weightText = it
                val newWeight = it.toDoubleOrNull()
                if (newWeight != null && newWeight > 0 && newWeight < 500) {
                    onWeightChange(newWeight)
                    showError = false
                } else {
                    showError = true
                }
            },
            label = { Text("Weight (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color.getThemeAwareColor(),
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            isError = showError
        )
        
        if (showError) {
            Text(
                text = "Please enter a valid weight between 1-500 kg",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Privacy notice
        Text(
            text = "🔒 Your data is stored locally and never shared",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun HeightInput(
    height: Double,
    onHeightChange: (Double) -> Unit,
    color: ColorVariant,
    modifier: Modifier = Modifier
) {
    var heightText by remember { mutableStateOf(height.toString()) }
    var showError by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What's your height?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This helps us calculate accurate calorie burn",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Height input field
        OutlinedTextField(
            value = heightText,
            onValueChange = { 
                heightText = it
                val newHeight = it.toDoubleOrNull()
                if (newHeight != null && newHeight > 0 && newHeight < 300) {
                    onHeightChange(newHeight)
                    showError = false
                } else {
                    showError = true
                }
            },
            label = { Text("Height (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color.getThemeAwareColor(),
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            isError = showError
        )
        
        if (showError) {
            Text(
                text = "Please enter a valid height between 1-300 cm",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Privacy notice
        Text(
            text = "🔒 Your data is stored locally and never shared",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun AgeInput(
    age: Int,
    onAgeChange: (Int) -> Unit,
    color: ColorVariant,
    modifier: Modifier = Modifier
) {
    var ageText by remember { mutableStateOf(age.toString()) }
    var showError by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What's your age?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This helps us calculate accurate calorie burn",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Age input field
        OutlinedTextField(
            value = ageText,
            onValueChange = { 
                ageText = it
                val newAge = it.toIntOrNull()
                if (newAge != null && newAge > 0 && newAge < 120) {
                    onAgeChange(newAge)
                    showError = false
                } else {
                    showError = true
                }
            },
            label = { Text("Age (years)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color.getThemeAwareColor(),
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            isError = showError
        )
        
        if (showError) {
            Text(
                text = "Please enter a valid age between 1-120 years",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Privacy notice
        Text(
            text = "🔒 Your data is stored locally and never shared",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun GenderInput(
    gender: String,
    onGenderChange: (String) -> Unit,
    color: ColorVariant,
    modifier: Modifier = Modifier
) {
    val genders = listOf("Male", "Female", "Other")
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What's your gender?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This helps us calculate accurate calorie burn",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Gender selection
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            genders.forEach { genderOption ->
                val isSelected = gender == genderOption
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.02f else 1f,
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                    label = "scale"
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            shadowElevation = if (isSelected) 12.dp.toPx() else 6.dp.toPx()
                        },
                    onClick = { onGenderChange(genderOption) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            color.getThemeAwareColor().copy(alpha = 0.08f)
                        } else {
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        }
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (isSelected) 8.dp else 4.dp
                    ),
                    border = if (isSelected) {
                        androidx.compose.foundation.BorderStroke(
                            2.dp,
                            color.getThemeAwareColor().copy(alpha = 0.3f)
                        )
                    } else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = genderOption,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isSelected) color.getThemeAwareColor()
                            else MaterialTheme.colorScheme.onSurface
                        )
                        
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = color.getThemeAwareColor(),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Privacy notice
        Text(
            text = "🔒 Your data is stored locally and never shared",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}
