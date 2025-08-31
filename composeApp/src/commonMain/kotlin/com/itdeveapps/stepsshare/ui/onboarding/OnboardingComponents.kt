package com.itdeveapps.stepsshare.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun ProgressIndicator(
    currentPage: Int,
    totalPages: Int,
    currentPageColor: Color,
    modifier: Modifier = Modifier
) {
    val progress = (currentPage + 1).toFloat() / totalPages.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "progress_animation"
    )

    val animatedColor by animateColorAsState(
        targetValue = currentPageColor,
        animationSpec = tween(durationMillis = 300),
        label = "color_animation"
    )

    Column(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${currentPage + 1} of $totalPages",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(8.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                animatedColor,
                                animatedColor.copy(alpha = 0.7f)
                            )
                        ),
                        RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
    ),
    enabled: Boolean = true
) {
    val backgroundColor = if (enabled) {
        colors.containerColor
    } else {
        MaterialTheme.colorScheme.outline
    }

    val animatedColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "color_animation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        animatedColor,
                        animatedColor.copy(alpha = 0.6f)
                    )
                ),
                RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            enabled = enabled,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1
                )
                if (enabled && !text.contains("Select")) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FadeInItem(
    delayMillis: Int = 0,
    showImmediately: Boolean = false,
    isActive: Boolean = true,
    pageKey: String = "",
    content: @Composable () -> Unit
) {
    var visible by remember(pageKey) { mutableStateOf(false) }

    LaunchedEffect(showImmediately, isActive, delayMillis, pageKey) {
        if (showImmediately) {
            visible = true
        } else if (isActive) {
            visible = false
            kotlinx.coroutines.delay(delayMillis.toLong())
            visible = true
        } else {
            visible = false
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.fadeIn(animationSpec = tween(durationMillis = 1000)),
        exit = androidx.compose.animation.fadeOut()
    ) {
        content()
    }
}


@Composable
fun WheelTextPicker(
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    size: DpSize = DpSize(128.dp, 128.dp),
    texts: List<String>,
    rowCount: Int,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onScrollFinished: (snappedIndex: Int) -> Int? = { null },
) {
    WheelPicker(
        modifier = modifier,
        startIndex = startIndex,
        size = size,
        count = texts.size,
        rowCount = rowCount,
        selectorProperties = selectorProperties,
        onScrollFinished = onScrollFinished
    ) { index ->
        Text(
            text = texts[index],
            style = style,
            color = color,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun WheelTextPickerWithSuffix(
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    size: DpSize = DpSize(128.dp, 128.dp),
    texts: List<String>,
    rowCount: Int,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = LocalContentColor.current,
    suffix: String = "",
    suffixStyle: TextStyle = style,
    suffixColor: Color = color,
    textToSuffixSpacing: Dp = 8.dp,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onScrollFinished: (snappedIndex: Int) -> Int? = { null },
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val suffixWidth = remember(suffix, suffixStyle) {
        if (suffix.isNotEmpty()) {
            val textLayoutResult = textMeasurer.measure(
                text = AnnotatedString(suffix),
                style = suffixStyle
            )
            with(density) { textLayoutResult.size.width.toDp() }
        } else {
            0.dp
        }
    }

    val textWidth = remember(style) {
        val textLayoutResult = textMeasurer.measure(
            text = AnnotatedString(texts.last()),
            style = style
        )
        with(density) { textLayoutResult.size.width.toDp() }
    }

    Box(modifier = modifier) {
        WheelPicker(
            startIndex = startIndex,
            size = size,
            count = texts.size,
            rowCount = rowCount,
            selectorProperties = selectorProperties,
            onScrollFinished = onScrollFinished
        ) { index ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = texts[index],
                    style = style,
                    color = color,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
                if (suffix.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(suffixWidth + textToSuffixSpacing))
                }
            }
        }

        if (suffix.isNotEmpty()) {
            Text(
                text = suffix,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = textWidth + textToSuffixSpacing),
                style = suffixStyle,
                color = suffixColor,
                maxLines = 1
            )
        }
    }
}

@Composable
internal fun WheelPicker(
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    count: Int,
    rowCount: Int,
    size: DpSize = DpSize(128.dp, 128.dp),
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onScrollFinished: (snappedIndex: Int) -> Int? = { null },
    content: @Composable LazyItemScope.(index: Int) -> Unit,
) {
    val lazyListState = rememberLazyListState(startIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState)
    val isScrollInProgress = lazyListState.isScrollInProgress

    LaunchedEffect(isScrollInProgress, count) {
        if (!isScrollInProgress) {
            onScrollFinished(calculateSnappedItemIndex(lazyListState))?.let {
                lazyListState.scrollToItem(it)
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (selectorProperties.enabled().value) {
            Surface(
                modifier = Modifier
                    .size(size.width, size.height / rowCount),
                shape = selectorProperties.shape().value,
                color = selectorProperties.color().value,
                border = selectorProperties.border().value
            ) {}
        }
        LazyColumn(
            modifier = Modifier
                .height(size.height)
                .width(size.width),
            state = lazyListState,
            contentPadding = PaddingValues(vertical = size.height / rowCount * ((rowCount - 1) / 2)),
            flingBehavior = flingBehavior
        ) {
            items(count) { index ->
                val (newAlpha, newRotationX) = calculateAnimatedAlphaAndRotationX(
                    lazyListState = lazyListState,
                    index = index,
                    rowCount = rowCount
                )

                Box(
                    modifier = Modifier
                        .height(size.height / rowCount)
                        .width(size.width)
                        .alpha(newAlpha)
                        .graphicsLayer {
                            rotationX = newRotationX
                        },
                    contentAlignment = Alignment.Center
                ) {
                    content(index)
                }
            }
        }
    }
}

private fun calculateSnappedItemIndex(lazyListState: LazyListState): Int {
    val currentItemIndex = lazyListState.firstVisibleItemIndex
    val itemCount = lazyListState.layoutInfo.totalItemsCount
    val offset = lazyListState.firstVisibleItemScrollOffset
    val itemHeight = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: return currentItemIndex

    return if (offset > itemHeight / 2 && currentItemIndex < itemCount - 1) {
        currentItemIndex + 1
    } else {
        currentItemIndex
    }
}

@Composable
private fun calculateAnimatedAlphaAndRotationX(
    lazyListState: LazyListState,
    index: Int,
    rowCount: Int
): Pair<Float, Float> {

    val layoutInfo = remember { derivedStateOf { lazyListState.layoutInfo } }.value
    val viewPortHeight = layoutInfo.viewportSize.height.toFloat()
    val singleViewPortHeight = viewPortHeight / rowCount

    val centerIndex = remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }.value
    val centerIndexOffset = remember { derivedStateOf { lazyListState.firstVisibleItemScrollOffset } }.value

    val distanceToCenterIndex = index - centerIndex

    val distanceToIndexSnap = distanceToCenterIndex * singleViewPortHeight.toInt() - centerIndexOffset
    val distanceToIndexSnapAbs = abs(distanceToIndexSnap)

    val animatedAlpha = if (abs(distanceToIndexSnap) in 0..singleViewPortHeight.toInt()) {
        1.2f - (distanceToIndexSnapAbs / singleViewPortHeight)
    } else {
        0.2f
    }

    val animatedRotationX = (-20 * (distanceToIndexSnap / singleViewPortHeight)).takeUnless { it.isNaN() } ?: 0f

    return animatedAlpha to animatedRotationX
}

object WheelPickerDefaults {
    @Composable
    fun selectorProperties(
        enabled: Boolean = true,
        shape: Shape = RoundedCornerShape(16.dp),
        color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
    ): SelectorProperties = DefaultSelectorProperties(
        enabled = enabled,
        shape = shape,
        color = color,
        border = border
    )
}

interface SelectorProperties {
    @Composable
    fun enabled(): State<Boolean>

    @Composable
    fun shape(): State<Shape>

    @Composable
    fun color(): State<Color>

    @Composable
    fun border(): State<BorderStroke?>
}

@Immutable
internal class DefaultSelectorProperties(
    private val enabled: Boolean,
    private val shape: Shape,
    private val color: Color,
    private val border: BorderStroke?
) : SelectorProperties {

    @Composable
    override fun enabled(): State<Boolean> {
        return rememberUpdatedState(enabled)
    }

    @Composable
    override fun shape(): State<Shape> {
        return rememberUpdatedState(shape)
    }

    @Composable
    override fun color(): State<Color> {
        return rememberUpdatedState(color)
    }

    @Composable
    override fun border(): State<BorderStroke?> {
        return rememberUpdatedState(border)
    }
}
