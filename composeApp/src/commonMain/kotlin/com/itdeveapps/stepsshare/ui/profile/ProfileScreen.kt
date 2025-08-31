package com.itdeveapps.stepsshare.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itdeveapps.stepsshare.domain.model.ColorVariant
import com.itdeveapps.stepsshare.domain.model.UserProfile
import com.itdeveapps.stepsshare.ui.onboarding.AgeInput
import com.itdeveapps.stepsshare.ui.onboarding.GenderInput
import com.itdeveapps.stepsshare.ui.onboarding.HeightInput
import com.itdeveapps.stepsshare.ui.onboarding.WeightInput
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import com.itdeveapps.stepsshare.domain.model.UnitSystem

@Composable
fun ProfileScreen(
    viewModel: UserProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile = uiState.userProfile
    
    var showEditProfile by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Your Profile",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.error != null) {
            ErrorCard(
                error = uiState.error!!,
                onRetry = { viewModel.loadUserProfile() },
                onDismiss = { viewModel.clearError() }
            )
        } else if (userProfile != null) {
            if (showEditProfile) {
                EditProfileSection(
                    userProfile = userProfile,
                    onSave = { updatedProfile ->
                        viewModel.updateUserProfile(updatedProfile)
                        showEditProfile = false
                    },
                    onCancel = { showEditProfile = false }
                )
            } else {
                ProfileInfoSection(
                    userProfile = userProfile,
                    onEditClick = { showEditProfile = true }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Sample calculations section
                CalculationsSection(viewModel = viewModel)
            }
        } else {
            Text(
                text = "No profile found. Please complete onboarding first.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProfileInfoSection(
    userProfile: UserProfile,
    onEditClick: () -> Unit
) {
    var unitSystem by remember { mutableStateOf(
        if (userProfile.unitSystem == UnitSystem.Imperial) "imperial" else "metric"
    ) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = unitSystem == "metric",
                    onClick = { unitSystem = "metric" },
                    label = { Text("Metric") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                )
                FilterChip(
                    selected = unitSystem == "imperial",
                    onClick = { unitSystem = "imperial" },
                    label = { Text("Imperial") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                )
            }
            
            ProfileInfoRow("Gender", userProfile.gender)
            ProfileInfoRow("Age", "${userProfile.age} years")
            val weightText = if (unitSystem == "metric") {
                "${userProfile.weightKg.roundToInt()} kg"
            } else {
                val lb = (userProfile.weightKg * 2.2046226218).roundToInt()
                "$lb lb"
            }
            val heightText = if (unitSystem == "metric") {
                "${userProfile.heightCm.roundToInt()} cm"
            } else {
                val totalInches = (userProfile.heightCm / 2.54).roundToInt()
                val ft = totalInches / 12
                val inch = totalInches % 12
                "$ft ft ${inch} in"
            }
            ProfileInfoRow("Weight", weightText)
            ProfileInfoRow("Height", heightText)
            
            // Privacy notice
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Your data is stored locally and never shared",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun EditProfileSection(
    userProfile: UserProfile,
    onSave: (UserProfile) -> Unit,
    onCancel: () -> Unit
) {
    var currentProfile by remember { mutableStateOf(userProfile) }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit Profile",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                
                // Gender selection
                GenderInput(
                    gender = currentProfile.gender,
                    onGenderChange = { currentProfile = currentProfile.copy(gender = it) },
                    color = ColorVariant(
                        light = MaterialTheme.colorScheme.primary,
                        dark = MaterialTheme.colorScheme.primary
                    )
                )
                
                // Age input
                AgeInput(
                    age = currentProfile.age,
                    onAgeChange = { currentProfile = currentProfile.copy(age = it) },
                    color = ColorVariant(
                        light = MaterialTheme.colorScheme.primary,
                        dark = MaterialTheme.colorScheme.primary
                    )
                )
                
                // Weight input
                WeightInput(
                    weight = currentProfile.weightKg,
                    onWeightChange = { currentProfile = currentProfile.copy(weightKg = it) },
                    color = ColorVariant(
                        light = MaterialTheme.colorScheme.primary,
                        dark = MaterialTheme.colorScheme.primary
                    )
                )
                
                // Height input
                HeightInput(
                    height = currentProfile.heightCm,
                    onHeightChange = { currentProfile = currentProfile.copy(heightCm = it) },
                    color = ColorVariant(
                        light = MaterialTheme.colorScheme.primary,
                        dark = MaterialTheme.colorScheme.primary
                    )
                )
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = { onSave(currentProfile) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}

@Composable
fun CalculationsSection(viewModel: UserProfileViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Sample Calculations",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            
            Text(
                text = "Based on your profile, here are some sample calculations:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Sample calculations for different step counts
            val stepCounts = listOf(1000, 5000, 10000, 15000)
            
            stepCounts.forEach { steps ->
                CalculationRow(
                    steps = steps,
                    calories = viewModel.calculateCaloriesBurned(steps),
                    distance = viewModel.calculateDistance(steps),
                    activeMinutes = viewModel.calculateActiveMinutes(steps)
                )
            }
            
            Text(
                text = "These calculations are estimates based on your profile data and help provide more accurate fitness tracking.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CalculationRow(
    steps: Int,
    calories: Int,
    distance: Double,
    activeMinutes: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$steps steps",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "$calories kcal • ${(distance * 100).roundToInt() / 100.0} km • ${activeMinutes} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.DirectionsWalk,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ErrorCard(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onDismiss) {
                    Text("Dismiss")
                }
                
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}


