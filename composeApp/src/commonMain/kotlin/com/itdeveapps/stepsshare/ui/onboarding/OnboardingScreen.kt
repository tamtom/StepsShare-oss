package com.itdeveapps.stepsshare.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.itdeveapps.stepsshare.domain.model.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.FilterChip
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import com.itdeveapps.stepsshare.domain.model.UnitSystem
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import stepsshare.composeapp.generated.resources.Res
import stepsshare.composeapp.generated.resources.splash_logo

@Composable
fun OnboardingScreen(
    onComplete: (UserProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableIntStateOf(0) }
    var userProfile by remember {
        mutableStateOf(
            UserProfile(
                gender = "Male",
                age = 25,
                weightKg = 70.0,
                heightCm = 170.0
            )
        )
    }
    var selectedWeightUnit by remember { mutableStateOf("kg") }
    var selectedHeightUnit by remember { mutableStateOf("cm") }
    val coroutineScope = rememberCoroutineScope()

    val pages = remember {
        listOf(
            OnboardingPage(
                title = "Welcome to StepsShare",
                subtitle = "Let's get to know you",
                description = "We'll ask a few questions to help calculate accurate calorie burn. Your data stays private and local.",
                icon = "person",
                color = ColorVariant(
                    light = Color(0xFF6200EE),
                    dark = Color(0xFFBB86FC)
                ),
                features = listOf(
                    Feature("Data stays on your device", Icons.Default.Security),
                    Feature("Accurate calorie calculations", Icons.Default.Analytics),
                    Feature("Personalized experience", Icons.Default.Star)
                )
            ),
            OnboardingPage(
                title = "Setup",
                subtitle = "For accurate steps, distance and calories calculations",
                description = "These help personalize calorie and activity metrics.",
                icon = "person",
                color = ColorVariant(
                    light = Color(0xFF009688),
                    dark = Color(0xFF4DB6AC)
                ),
                features = emptyList()
            )
        )
    }

    var isTransitioning by remember { mutableStateOf(false) }

    // Track animation states per page
    val animationStates = remember { mutableMapOf<Int, Boolean>() }

    LaunchedEffect(currentPage) {
        if (!animationStates.containsKey(currentPage) || !animationStates[currentPage]!!) {
            val pageAnimationDuration = 300 + 500
            kotlinx.coroutines.delay(pageAnimationDuration.toLong())
            animationStates[currentPage] = true
        }
    }
    // Use app theme primary as accent to keep onboarding aligned with app theme
    val currentColor = MaterialTheme.colorScheme.primary

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .navigationBarsPadding()
                        .background(Color.Transparent),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val isLastPage by remember { derivedStateOf { currentPage == pages.size - 1 } }
                        if (isLastPage) {
                            PrimaryButton(
                                text = "Complete Setup",
                                onClick = {
                                    if (!isTransitioning) {
                                        isTransitioning = true
                                        val system = if (selectedWeightUnit == "lb" || selectedHeightUnit == "ft") UnitSystem.Imperial else UnitSystem.Metric
                                        userProfile = userProfile.copy(unitSystem = system)
                                        onComplete(userProfile)
                                    }
                                },
                                enabled = true,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = currentColor
                                )
                            )
                        } else {
                            PrimaryButton(
                                text = "Continue",
                                onClick = {
                                    if (!isTransitioning) {
                                        isTransitioning = true
                                        coroutineScope.launch {
                                            currentPage++
                                            kotlinx.coroutines.delay(300)
                                            isTransitioning = false
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = currentColor
                                )
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .background(
                        // Apply app gradient to feel consistent with the rest of the app
                        Brush.linearGradient(
                            colors = listOf(
                                currentColor.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.background,
                                currentColor.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(top = paddingValues.calculateTopPadding())
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                // Progress indicator at top
                ProgressIndicator(
                    currentPage = currentPage,
                    totalPages = pages.size,
                    currentPageColor = MaterialTheme.colorScheme.primary
                )

                // Page content
                val shouldShowAnimations =
                    !animationStates.containsKey(currentPage) || !animationStates[currentPage]!!

                key(currentPage) {
                    when (currentPage) {
                        0 -> {
                            // Welcome page
                            Spacer(modifier = Modifier.height(24.dp))

                            // Icon
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .border(.5.dp, currentColor, CircleShape)
                                        .background(
                                            currentColor.copy(alpha = 0.1f),
                                            CircleShape
                                        )
                                )

                                FadeInItem(
                                    delayMillis = 200,
                                    showImmediately = true,
                                    isActive = true,
                                    pageKey = "welcome_icon"
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.splash_logo),
                                        contentDescription = null,
                                        modifier = Modifier.size(100.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Title and subtitle
                            FadeInItem(
                                delayMillis = 300,
                                showImmediately = !shouldShowAnimations,
                                isActive = true,
                                pageKey = "welcome_title"
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = pages[0].title,
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = pages[0].subtitle,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = currentColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Description
                            FadeInItem(
                                delayMillis = 400,
                                showImmediately = !shouldShowAnimations,
                                isActive = true,
                                pageKey = "welcome_description"
                            ) {
                                Text(
                                    text = pages[0].description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Features
                            if (pages[0].features.isNotEmpty()) {
                                FadeInItem(
                                    delayMillis = 500,
                                    showImmediately = !shouldShowAnimations,
                                    isActive = true,
                                    pageKey = "welcome_features"
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 20.dp)
                                            .background(
                                                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        pages[0].features.forEach { feature ->
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .border(
                                                            .5.dp,
                                                            currentColor,
                                                            CircleShape
                                                        )
                                                        .background(
                                                            currentColor.copy(alpha = 0.1f),
                                                            CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = feature.icon
                                                            ?: Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = currentColor,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }

                                                Text(
                                                    text = feature.text,
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        1 -> {
                            // Profile setup with wheels (accordion)
                            Spacer(modifier = Modifier.height(24.dp))
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .border(.5.dp, currentColor, CircleShape)
                                        .background(
                                            currentColor.copy(alpha = 0.1f),
                                            CircleShape
                                        )
                                )

                                FadeInItem(
                                    delayMillis = 200,
                                    showImmediately = true,
                                    isActive = true,
                                    pageKey = "welcome_icon"
                                ) {
                                    Icon(
                                         imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = currentColor,
                                        modifier = Modifier.size(60.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            FadeInItem(
                                delayMillis = 300,
                                showImmediately = !shouldShowAnimations,
                                isActive = true,
                                pageKey = "profile_title"
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = pages[1].title,
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = pages[1].subtitle,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = currentColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            FadeInItem(
                                delayMillis = 400,
                                showImmediately = !shouldShowAnimations,
                                isActive = true,
                                pageKey = "profile_description"
                            ) {
                                Text(
                                    text = pages[1].description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            FadeInItem(
                                delayMillis = 500,
                                showImmediately = !shouldShowAnimations,
                                isActive = true,
                                pageKey = "profile_wheels"
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    var expandedField by remember { mutableStateOf<String?>(null) }
                                    var weightUnit by remember { mutableStateOf(selectedWeightUnit) }
                                    var heightUnit by remember { mutableStateOf(selectedHeightUnit) }

                                    fun chevron(expanded: Boolean): ImageVector =
                                        if (expanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Default.KeyboardArrowRight

                                    // Gender row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                expandedField = if (expandedField == "Gender") null else "Gender"
                                            },
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.AutoMirrored.Default.DirectionsWalk, contentDescription = null, tint = currentColor)
                                            Spacer(Modifier.width(12.dp))
                                            Text("Gender", style = MaterialTheme.typography.titleMedium)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(userProfile.gender, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Icon(chevron(expandedField == "Gender"), contentDescription = null)
                                        }
                                    }
                                    AnimatedVisibility(visible = expandedField == "Gender") {
                                        Box(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                            val genders = remember { listOf("Male", "Female", "Other") }
                                            val startIndex = remember(userProfile.gender) { genders.indexOf(userProfile.gender).takeIf { it >= 0 } ?: 0 }
                                            WheelTextPicker(
                                                size = DpSize(180.dp, 120.dp),
                                                texts = genders,
                                                rowCount = 5,
                                                startIndex = startIndex,
                                                selectorProperties = WheelPickerDefaults.selectorProperties(),
                                                onScrollFinished = { snapped ->
                                                    userProfile = userProfile.copy(gender = genders[snapped])
                                                    null
                                                }
                                            )
                                        }
                                    }
                                    HorizontalDivider(
                                         color = DividerDefaults.color.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(start = 24.dp, end = 8.dp, top = 8.dp, bottom = 8.dp))

                                    // Age row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                expandedField = if (expandedField == "Age") null else "Age"
                                            },
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Cake, contentDescription = null, tint = currentColor)
                                            Spacer(Modifier.width(12.dp))
                                            Text("Age", style = MaterialTheme.typography.titleMedium)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("${userProfile.age}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Icon(chevron(expandedField == "Age"), contentDescription = null)
                                        }
                                    }
                                    AnimatedVisibility(visible = expandedField == "Age") {
                                        Box(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                            val ages = remember { (10..100).map { it.toString() } }
                                            val startIndex = remember(userProfile.age) { (userProfile.age - 10).coerceIn(0, ages.lastIndex) }
                                            WheelTextPickerWithSuffix(
                                                size = DpSize(180.dp, 120.dp),
                                                texts = ages,
                                                rowCount = 5,
                                                suffix = "yrs",
                                                startIndex = startIndex,
                                                selectorProperties = WheelPickerDefaults.selectorProperties(),
                                                onScrollFinished = { snapped ->
                                                    userProfile = userProfile.copy(age = snapped + 10)
                                                    null
                                                }
                                            )
                                        }
                                    }
                                    HorizontalDivider(
                                        color = DividerDefaults.color.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(start = 24.dp, end = 8.dp, top = 8.dp, bottom = 8.dp))
                                    // Weight row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                expandedField = if (expandedField == "Weight") null else "Weight"
                                            },
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = currentColor)
                                            Spacer(Modifier.width(12.dp))
                                            Text("Weight", style = MaterialTheme.typography.titleMedium)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            val weightDisplay = if (weightUnit == "kg") {
                                                "${userProfile.weightKg.toInt()} kg"
                                            } else {
                                                val lb = (userProfile.weightKg * 2.2046226218).toInt()
                                                "$lb lb"
                                            }
                                            Text(weightDisplay, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Icon(chevron(expandedField == "Weight"), contentDescription = null)
                                        }
                                    }
                                    AnimatedVisibility(visible = expandedField == "Weight") {
                                        Box(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    FilterChip(
                                                        selected = weightUnit == "kg",
                                                        onClick = { weightUnit = "kg"; selectedWeightUnit = "kg" },
                                                        label = { Text("kg") }
                                                    )
                                                    FilterChip(
                                                        selected = weightUnit == "lb",
                                                        onClick = { weightUnit = "lb"; selectedWeightUnit = "lb" },
                                                        label = { Text("lb") }
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                if (weightUnit == "kg") {
                                                    val weights = remember { (30..200).map { it.toString() } }
                                                    val startIndex = remember(userProfile.weightKg) { (userProfile.weightKg.toInt() - 30).coerceIn(0, weights.lastIndex) }
                                                    WheelTextPickerWithSuffix(
                                                        size = DpSize(180.dp, 120.dp),
                                                        texts = weights,
                                                        rowCount = 5,
                                                        suffix = "kg",
                                                        startIndex = startIndex,
                                                        selectorProperties = WheelPickerDefaults.selectorProperties(),
                                                        onScrollFinished = { snapped ->
                                                            userProfile = userProfile.copy(weightKg = (snapped + 30).toDouble())
                                                            null
                                                        }
                                                    )
                                                } else {
                                                    val weightsLb = remember { (66..440).map { it.toString() } }
                                                    val currentLb = remember(userProfile.weightKg) { (userProfile.weightKg * 2.2046226218).toInt() }
                                                    val startIndexLb = (currentLb - 66).coerceIn(0, weightsLb.lastIndex)
                                                    WheelTextPickerWithSuffix(
                                                        size = DpSize(180.dp, 120.dp),
                                                        texts = weightsLb,
                                                        rowCount = 5,
                                                        suffix = "lb",
                                                        startIndex = startIndexLb,
                                                        selectorProperties = WheelPickerDefaults.selectorProperties(),
                                                        onScrollFinished = { snapped ->
                                                            val lb = snapped + 66
                                                            val kg = lb * 0.45359237
                                                            userProfile = userProfile.copy(weightKg = kg)
                                                            null
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    HorizontalDivider(
                                        color = DividerDefaults.color.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(start = 24.dp, end = 8.dp, top = 8.dp, bottom = 8.dp))
                                    // Height row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                expandedField = if (expandedField == "Height") null else "Height"
                                            },
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Height, contentDescription = null, tint = currentColor)
                                            Spacer(Modifier.width(12.dp))
                                            Text("Height", style = MaterialTheme.typography.titleMedium)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            val heightDisplay = if (heightUnit == "cm") {
                                                "${userProfile.heightCm.toInt()} cm"
                                            } else {
                                                val totalInches = (userProfile.heightCm / 2.54).toInt()
                                                val ft = totalInches / 12
                                                val inch = totalInches % 12
                                                "$ft ft $inch in"
                                            }
                                            Text(heightDisplay, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Icon(chevron(expandedField == "Height"), contentDescription = null)
                                        }
                                    }
                                    AnimatedVisibility(visible = expandedField == "Height") {
                                        Box(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    FilterChip(
                                                        selected = heightUnit == "cm",
                                                        onClick = { heightUnit = "cm"; selectedHeightUnit = "cm" },
                                                        label = { Text("cm") }
                                                    )
                                                    FilterChip(
                                                        selected = heightUnit == "ft",
                                                        onClick = { heightUnit = "ft"; selectedHeightUnit = "ft" },
                                                        label = { Text("ft") }
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                if (heightUnit == "cm") {
                                                    val heights = remember { (120..220).map { it.toString() } }
                                                    val startIndex = remember(userProfile.heightCm) { (userProfile.heightCm.toInt() - 120).coerceIn(0, heights.lastIndex) }
                                                    WheelTextPickerWithSuffix(
                                                        size = DpSize(180.dp, 120.dp),
                                                        texts = heights,
                                                        rowCount = 5,
                                                        suffix = "cm",
                                                        startIndex = startIndex,
                                                        selectorProperties = WheelPickerDefaults.selectorProperties(),
                                                        onScrollFinished = { snapped ->
                                                            userProfile = userProfile.copy(heightCm = (snapped + 120).toDouble())
                                                            null
                                                        }
                                                    )
                                                } else {
                                                    val minFeet = 4
                                                    val maxFeet = 7
                                                    var selectedFeet by remember(userProfile.heightCm) {
                                                        val totalInches = (userProfile.heightCm / 2.54).toInt()
                                                        mutableIntStateOf((totalInches / 12).coerceIn(minFeet, maxFeet))
                                                    }
                                                    var selectedInches by remember(userProfile.heightCm) {
                                                        val totalInches = (userProfile.heightCm / 2.54).toInt()
                                                        mutableIntStateOf(totalInches % 12)
                                                    }
                                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                                        val feetOptions = (minFeet..maxFeet).map { it.toString() }
                                                        val feetStart = (selectedFeet - minFeet).coerceIn(0, feetOptions.lastIndex)
                                                        WheelTextPickerWithSuffix(
                                                            size = DpSize(100.dp, 120.dp),
                                                            texts = feetOptions,
                                                            rowCount = 5,
                                                            suffix = "ft",
                                                            startIndex = feetStart,
                                                            selectorProperties = WheelPickerDefaults.selectorProperties(),
                                                            onScrollFinished = { snapped ->
                                                                selectedFeet = snapped + minFeet
                                                                val cm = ((selectedFeet * 12) + selectedInches) * 2.54
                                                                userProfile = userProfile.copy(heightCm = cm)
                                                                null
                                                            }
                                                        )
                                                        val inchOptions = (0..11).map { it.toString() }
                                                        val inchStart = selectedInches.coerceIn(0, inchOptions.lastIndex)
                                                        WheelTextPickerWithSuffix(
                                                            size = DpSize(100.dp, 120.dp),
                                                            texts = inchOptions,
                                                            rowCount = 5,
                                                            suffix = "in",
                                                            startIndex = inchStart,
                                                            selectorProperties = WheelPickerDefaults.selectorProperties(),
                                                            onScrollFinished = { snapped ->
                                                                selectedInches = snapped
                                                                val cm = ((selectedFeet * 12) + selectedInches) * 2.54
                                                                userProfile = userProfile.copy(heightCm = cm)
                                                                null
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

