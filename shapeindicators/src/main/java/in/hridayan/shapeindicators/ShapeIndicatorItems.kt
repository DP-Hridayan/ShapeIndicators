package `in`.hridayan.shapeindicators

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import `in`.hridayan.shapeindicators.utils.interpolateForIndex
import `in`.hridayan.shapeindicators.utils.lerpColor
import `in`.hridayan.shapeindicators.utils.lerpSize
import `in`.hridayan.shapeindicators.utils.safeBlurMaskFilter
import `in`.hridayan.shapeindicators.utils.scaled
import kotlin.math.abs

@Composable
internal fun ShapeIndicatorItems(
    pagerState: PagerState,
    sizes: ShapeIndicatorSizes,
    colors: ShapeIndicatorColors,
    borders: ShapeIndicatorBorders,
    glow: ShapeIndicatorGlow,
    overflow: ShapeIndicatorOverflow,
    shapes: IndicatorShapes,
    shuffleShapes: Boolean,
) {
    val pageCount = pagerState.pageCount

    // Only currentPage is read in composition scope. The continuously-changing
    // currentPageOffsetFraction is deferred to the draw phase, preventing
    // recomposition on every scroll frame.
    val currentPage = pagerState.currentPage

    // Key on the list reference so that a changed shape set is reshuffled correctly.
    val shuffledSelectedShapes =
        remember(shapes.selectedShapes) { shapes.selectedShapes.shuffled() }
    val shuffledUnselectedShapes =
        remember(shapes.unselectedShapes) { shapes.unselectedShapes.shuffled() }

    val selectedShapes = if (shuffleShapes) shuffledSelectedShapes else shapes.selectedShapes
    val unselectedShapes = if (shuffleShapes) shuffledUnselectedShapes else shapes.unselectedShapes

    val maxItemSize = maxOf(sizes.selectedSize, sizes.unselectedSize)

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

    for (index in startIndex..endIndex) {
        val isHint =
            (index == endIndex && hasRightOverflow && index != currentPage) ||
            (index == startIndex && hasLeftOverflow && index != currentPage)

        // key() gives each item a stable identity so that animate* state is
        // correctly associated with its indicator even when the window slides.
        key(index) {
            ShapeIndicatorItem(
                index = index,
                pagerState = pagerState,
                isHint = isHint,
                sizes = sizes,
                colors = colors,
                borders = borders,
                glow = glow,
                overflow = overflow,
                selectedShapes = selectedShapes,
                unselectedShapes = unselectedShapes,
                maxItemSize = maxItemSize,
            )
        }
    }
}

@Composable
private fun ShapeIndicatorItem(
    index: Int,
    pagerState: PagerState,
    isHint: Boolean,
    sizes: ShapeIndicatorSizes,
    colors: ShapeIndicatorColors,
    borders: ShapeIndicatorBorders,
    glow: ShapeIndicatorGlow,
    overflow: ShapeIndicatorOverflow,
    selectedShapes: List<RoundedPolygon>,
    unselectedShapes: List<RoundedPolygon>,
    maxItemSize: Dp,
) {
    // Animates only the discrete hint ↔ normal transition (triggered by window slides).
    // All continuous scroll-driven changes (size, color, morph) are computed in the
    // draw phase so they never cause recomposition.
    val hintProgress by animateFloatAsState(
        targetValue = if (isHint) 1f else 0f,
        label = "hintProgress[$index]",
    )

    // Single Box sized at maxItemSize. Visual size is achieved via canvas scaling
    // inside onDrawBehind, removing the need for a second Box node per indicator.
    Box(
        modifier = Modifier
            .size(maxItemSize)
            .drawWithCache {
                val sizePx = size.minDimension
                val center = Offset(sizePx / 2f, sizePx / 2f)

                // Scaled shapes are built once here and reused across draw calls.
                // The drawWithCache block only re-runs when layout size changes.
                val startShape = unselectedShapes[index % unselectedShapes.size].scaled(sizePx)
                val endShape = selectedShapes[index % selectedShapes.size].scaled(sizePx)

                // Morph construction is expensive (computes polygon interpolation data).
                // Cached here — rebuilt only when the Box is re-laid-out.
                val morph = Morph(start = startShape, end = endShape)

                // Reuse a single path buffer across every draw call.
                val composePath = Path()
                val androidPath = composePath.asAndroidPath()

                // Cache the native Paint. BlurMaskFilter is rebuilt only when the
                // effective blur value changes, not on every frame.
                val nativePaint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    style = android.graphics.Paint.Style.STROKE
                }
                var cachedBlurForFilter = -1f

                onDrawBehind {
                    // Reading pagerState here (draw phase) avoids triggering recomposition
                    // on every scroll frame — snapshot reads in draw are free.
                    val currentPage = pagerState.currentPage
                    val offset = pagerState.currentPageOffsetFraction

                    // --- Compute all lerped values for this frame ---

                    val targetSize = interpolateForIndex(
                        index, currentPage, offset,
                        sizes.unselectedSize, sizes.selectedSize, ::lerpSize
                    )
                    val targetColor = interpolateForIndex(
                        index, currentPage, offset,
                        colors.unselectedColor, colors.selectedColor, ::lerpColor
                    )
                    val targetBorderWidth = interpolateForIndex(
                        index, currentPage, offset,
                        borders.unselectedWidth, borders.selectedWidth, ::lerpSize
                    )
                    val targetBorderColor = interpolateForIndex(
                        index, currentPage, offset,
                        borders.unselectedColor, borders.selectedColor, ::lerpColor
                    )
                    val targetGlowRadius = interpolateForIndex(
                        index, currentPage, offset,
                        glow.unselectedRadius, glow.selectedRadius, ::lerpSize
                    )
                    val targetGlowColor = interpolateForIndex(
                        index, currentPage, offset,
                        glow.unselectedColor, glow.selectedColor, ::lerpColor
                    )
                    val targetGlowBlur = interpolateForIndex(
                        index, currentPage, offset,
                        glow.unselectedBlur, glow.selectedBlur, ::lerpSize
                    )

                    // --- Blend in the discrete hint state ---

                    val hp = hintProgress
                    val drawSize = lerpSize(targetSize, overflow.hintShapeSize, hp)
                    val drawColor = lerpColor(targetColor, targetColor.copy(alpha = 0.6f), hp)
                    val drawBorderWidth = lerpSize(targetBorderWidth, 0.dp, hp)
                    val drawBorderColor = targetBorderColor
                    val drawGlowRadius = lerpSize(targetGlowRadius, 0.dp, hp)
                    val drawGlowColor = targetGlowColor
                    val drawGlowBlur = lerpSize(targetGlowBlur, 0.dp, hp)

                    // --- Morph ---

                    val morphProgress = when (index) {
                        currentPage -> 1f - abs(offset)
                        currentPage + 1 -> abs(offset)
                        currentPage - 1 -> abs(offset)
                        else -> 0f
                    }.coerceIn(0f, 1f)

                    // Sample the morph into the reused path buffer (no allocation).
                    androidPath.reset()
                    morph.toPath(morphProgress, androidPath)

                    // --- Draw ---

                    // Scale the pre-built path to the current draw size. Dividing stroke
                    // and blur values by scaleFactor keeps them at their intended screen-pixel
                    // size regardless of the canvas transform.
                    val scaleFactor = drawSize.toPx() / sizePx

                    val glowRadiusPx = drawGlowRadius.toPx()
                    val blurPx = drawGlowBlur.toPx()
                    val borderWidthPx = drawBorderWidth.toPx()

                    withTransform({ scale(scaleFactor, scaleFactor, pivot = center) }) {

                        // --- Glow ---
                        if (glowRadiusPx > 0f || blurPx > 0f) {
                            val blurForFilter = if (scaleFactor > 0f) blurPx / scaleFactor else blurPx
                            if (blurForFilter != cachedBlurForFilter) {
                                nativePaint.maskFilter = safeBlurMaskFilter(blurForFilter)
                                cachedBlurForFilter = blurForFilter
                            }
                            nativePaint.strokeWidth = (glowRadiusPx * 2f) / scaleFactor
                            nativePaint.color = drawGlowColor.toArgb()
                            drawIntoCanvas { canvas ->
                                canvas.nativeCanvas.drawPath(androidPath, nativePaint)
                            }
                        }

                        // --- Fill ---
                        drawPath(composePath, drawColor)

                        // --- Border ---
                        if (borderWidthPx > 0f) {
                            drawPath(
                                path = composePath,
                                color = drawBorderColor,
                                style = Stroke(borderWidthPx / scaleFactor),
                            )
                        }
                    }
                }
            },
    )
}