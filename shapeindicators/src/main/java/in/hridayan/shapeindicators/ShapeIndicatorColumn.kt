@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package `in`.hridayan.shapeindicators

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.toPath
import kotlin.math.abs

/**
 * Displays a morphing Material–shape indicator row for a pager.
 *
 * Each indicator animates **size**, **color**, **border**, **glow**, and **shape**
 * based on the current page and scroll offset from [PagerState].
 *
 * @param modifier Modifier for the indicator row.
 *
 * @param pagerState Provides:
 * - total number of pages
 * - selected page index
 * - scroll offset used for interpolation
 *
 * @param sizes Defines selected/unselected indicator sizes.
 * Use [ShapeIndicatorDefaults.sizes].
 *
 * @param colors Defines fill colors for selected/unselected states.
 * Use [ShapeIndicatorDefaults.colors].
 *
 * @param borders Defines stroke widths and stroke colors.
 * Use [ShapeIndicatorDefaults.borders].
 * Set widths to `0.dp` to disable borders.
 *
 * @param glow Defines glow color, radius, and blur for both states.
 * Use [ShapeIndicatorDefaults.glow].
 *
 * @param shapes Defines selected/unselected shapes.
 * Use [ShapeIndicatorDefaults.shapes].
 *
 * @param shuffleShapes If true, shape lists are shuffled once and indicators cycle through them.
 *
 * @param onIndicatorClick Optional click listener for each indicator.
 *
 * If provided, every indicator becomes individually clickable **without ripple**
 *
 * The callback receives the tapped indicator’s index:
 * - Useful for jumping the pager to a specific page.
 * - Can also be used for analytics or custom UI reactions.
 *
 * @param horizontalAlignment Horizontal alignment of indicators.
 *
 * @param verticalArrangement Arrangement between indicators.
 *
 * ## Behavior
 * Indicators smoothly animate between **unselected → selected** states using:
 * - size interpolation
 * - fill color interpolation
 * - border width & color interpolation
 * - glow radius & blur interpolation
 * - shape morphing
 *
 * Scroll offset produces continuous transitions between pages.
 *
 * ## Example
 * ```
 * val pagerState = rememberPagerState { 5 }
 *
 * ShapeIndicatorRow(
 *     pagerState = pagerState,
 *     sizes = ShapeIndicatorDefaults.sizes(20.dp, 12.dp),
 *     colors = ShapeIndicatorDefaults.colors(),
 *     borders = ShapeIndicatorDefaults.borders(2.dp, 0.dp),
 *     glow = ShapeIndicatorDefaults.glow(
 *         selectedRadius = 8.dp,
 *         selectedBlur = 12.dp
 *     ),
 *     shapes = ShapeIndicatorDefaults.shapes(),
 *     shuffleShapes = true
 * )
 * ```
 */
@ExperimentalMaterial3ExpressiveApi
@Composable
fun ShapeIndicatorColumn(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    sizes: ShapeIndicatorSizes = ShapeIndicatorDefaults.sizes(),
    colors: ShapeIndicatorColors = ShapeIndicatorDefaults.colors(),
    borders: ShapeIndicatorBorders = ShapeIndicatorDefaults.borders(),
    glow: ShapeIndicatorGlow = ShapeIndicatorDefaults.glow(),
    overflow: ShapeIndicatorOverflow = ShapeIndicatorDefaults.overflow(),
    shapes: IndicatorShapes = ShapeIndicatorDefaults.shapes(),
    shuffleShapes: Boolean = false,
    onIndicatorClick: ((index: Int) -> Unit)? = null,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.SpaceBetween
) {
    val pageCount = pagerState.pageCount

    val shuffledSelectedShapes = remember { shapes.selectedShapes.shuffled() }
    val shuffledUnselectedShapes = remember { shapes.unselectedShapes.shuffled() }

    val selectedSize = sizes.selectedSize
    val unselectedSize = sizes.unselectedSize

    val selectedColor = colors.selectedColor
    val unselectedColor = colors.unselectedColor

    val maxItemSize = maxOf(selectedSize, unselectedSize)

    val currentPage = pagerState.currentPage
    val offset = pagerState.currentPageOffsetFraction

    val windowStart = remember { mutableIntStateOf(0) }

    val usableSlots = if (overflow.enabled) overflow.maxVisibleItems - 1 else pageCount

    LaunchedEffect(currentPage) {

        val firstHintIndex = windowStart.intValue
        val lastHintIndex = windowStart.intValue + usableSlots

        val lastSafeIndex = lastHintIndex - 1
        val firstSafeIndex = firstHintIndex + 1

        when {
            currentPage > lastSafeIndex -> {
                windowStart.intValue = (currentPage - usableSlots + 1)
                    .coerceAtMost(pageCount - usableSlots - 1)
            }

            currentPage < firstSafeIndex -> {
                windowStart.intValue = (currentPage - 1)
                    .coerceAtLeast(0)
            }
        }
    }

    val startIndex = windowStart.intValue

    val hasLeftOverflow = startIndex > 0
    val hasRightOverflow = startIndex + usableSlots < pageCount - 1

    val endIndex =
        if (hasRightOverflow)
            startIndex + usableSlots
        else
            (startIndex + usableSlots).coerceAtMost(pageCount - 1)

    Column(
        modifier = modifier.widthIn(min = maxItemSize),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {

        for (index in startIndex..endIndex) {

            val targetShapeSize = interpolateForIndex(
                index,
                currentPage,
                offset,
                unselectedSize,
                selectedSize,
                ::lerpSize
            )

            val targetShapeColor = interpolateForIndex(
                index,
                currentPage,
                offset,
                unselectedColor,
                selectedColor,
                ::lerpColor
            )

            val targetBorderWidth = interpolateForIndex(
                index, currentPage, offset,
                borders.unselectedWidth,
                borders.selectedWidth,
                ::lerpSize
            )

            val targetBorderColor = interpolateForIndex(
                index,
                currentPage,
                offset,
                borders.unselectedColor,
                borders.selectedColor,
                ::lerpColor
            )

            val targetGlowRadius = interpolateForIndex(
                index,
                currentPage,
                offset,
                glow.unselectedRadius,
                glow.selectedRadius,
                ::lerpSize
            )

            val targetGlowColor = interpolateForIndex(
                index,
                currentPage,
                offset,
                glow.unselectedColor,
                glow.selectedColor,
                ::lerpColor
            )

            val targetGlowBlur = interpolateForIndex(
                index,
                currentPage,
                offset,
                glow.unselectedBlur,
                glow.selectedBlur,
                ::lerpSize
            )

            val isRightHint = index == endIndex && hasRightOverflow && index != currentPage
            val isLeftHint = index == startIndex && hasLeftOverflow && index != currentPage

            val overflowSize =
                if (isLeftHint || isRightHint) overflow.hintShapeSize else targetShapeSize

            val animatedShapeSize by animateDpAsState(overflowSize)
            val animatedShapeColor by animateColorAsState(
                if (isLeftHint || isRightHint)
                    targetShapeColor.copy(alpha = 0.6f)
                else
                    targetShapeColor
            )
            val animatedBorderWidth by animateDpAsState(targetBorderWidth)
            val animatedBorderColor by animateColorAsState(targetBorderColor)
            val animatedGlowRadius by animateDpAsState(targetGlowRadius)
            val animatedGlowColor by animateColorAsState(targetGlowColor)
            val animatedGlowBlur by animateDpAsState(targetGlowBlur)

            Box(
                modifier = Modifier
                    .size(maxItemSize)
                    .pointerInput(onIndicatorClick) {
                        if (onIndicatorClick == null) return@pointerInput
                        detectTapGestures { onIndicatorClick(index) }
                    }) {
                Box(
                    modifier = Modifier
                        .size(animatedShapeSize)
                        .align(Alignment.Center)
                        .drawWithCache {
                            val sizePx = this.size.minDimension

                            val selectedShapes =
                                if (shuffleShapes) shuffledSelectedShapes else shapes.selectedShapes
                            val unselectedShapes =
                                if (shuffleShapes) shuffledUnselectedShapes else shapes.unselectedShapes

                            val startShape =
                                unselectedShapes[index % unselectedShapes.size].scaled(sizePx)
                            val endShape =
                                selectedShapes[index % selectedShapes.size].scaled(sizePx)

                            val morphProgress = when (index) {
                                currentPage -> 1f - abs(offset)
                                currentPage + 1 -> abs(offset)
                                currentPage - 1 -> abs(offset)
                                else -> 0f
                            }.coerceIn(0f, 1f)

                            val morph = Morph(start = startShape, end = endShape)
                            val path = morph.toPath(morphProgress).asComposePath()

                            val blurPx = animatedGlowBlur.toPx()
                            val glowRadiusPx = animatedGlowRadius.toPx()

                            onDrawBehind {
                                // --- Glow ---
                                if (glowRadiusPx > 0f || blurPx > 0f) {

                                    val pathBounds = path.getBounds()
                                    if (pathBounds.width > 0f && pathBounds.height > 0f) {

                                        val fwPaint = Paint().asFrameworkPaint().apply {
                                            isAntiAlias = true
                                            maskFilter = safeBlurMaskFilter(blurPx)
                                            style = android.graphics.Paint.Style.STROKE
                                            strokeWidth = glowRadiusPx * 2f
                                            color = animatedGlowColor.toArgb()
                                        }

                                        drawIntoCanvas { canvas ->
                                            canvas.nativeCanvas.drawPath(
                                                path.asAndroidPath(),
                                                fwPaint
                                            )
                                        }
                                    }
                                }

                                // --- fill ---
                                drawPath(path, animatedShapeColor)

                                // --- border ---
                                if (animatedBorderWidth.value > 0f) {
                                    drawPath(
                                        path = path,
                                        color = animatedBorderColor,
                                        style = Stroke(animatedBorderWidth.value)
                                    )
                                }
                            }
                        }
                )
            }
        }
    }
}