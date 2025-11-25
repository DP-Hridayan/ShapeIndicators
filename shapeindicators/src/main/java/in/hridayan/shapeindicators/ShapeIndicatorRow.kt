@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package `in`.hridayan.shapeindicators

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.toPath
import kotlin.math.abs

/**
 * Displays a morphing Material–shape indicator row for a pager.
 *
 * Each indicator smoothly animates **size**, **color**, **border**, and **shape**
 * based on the [PagerState]'s current position and scroll offset.
 *
 * When an indicator becomes selected, it animates between the visual states defined in:
 * - [sizes] → animates indicator size
 * - [colors] → animates fill color
 * - [borders] → animates stroke width + stroke color
 * - [shapes] → morphs between expressive Material shapes
 *
 * @param modifier Modifier applied to the indicator row.
 *
 * @param pagerState The [PagerState] used to determine:
 * - total number of pages
 * - current selected page index
 * - scroll offset for smooth morph/size/color/border interpolation.
 *
 * @param sizes Defines the size of indicators in selected and unselected states.
 * Use [ShapeIndicatorDefaults.sizes] to create.
 *
 * @param colors Defines the fill color of indicators in both states.
 * Use [ShapeIndicatorDefaults.colors] to create.
 * (Must be called inside a composable because defaults read `MaterialTheme.colorScheme`.)
 *
 * @param borders Defines stroke width and stroke color for selected and unselected indicators.
 * Use [ShapeIndicatorDefaults.borders] to create.
 * - When scrolling between pages, the border animates
 *   from `unselectedWidth → selectedWidth` and
 *   `unselectedColor → selectedColor`.
 * - Set both widths to `0.dp` if you don't want borders at all.
 *
 * @param shapes Defines the selected and unselected indicator shapes.
 * Use [ShapeIndicatorDefaults.shapes] to use Material 3 expressive shapes.
 *
 * @param shuffleShapes If true, the selected and unselected shape lists are randomly
 * shuffled once at composition time, and indicators cycle through that shuffled order.
 *
 * @param horizontalArrangement Spacing arrangement for the indicator row.
 *
 * @param verticalAlignment Alignment of indicators inside the row’s height.
 *
 * ## Behavior
 * - Selected indicators animate from **unselected → selected** using:
 *   - size interpolation
 *   - color interpolation
 *   - border width/color interpolation
 *   - shape morphing
 * - Pager scroll smoothly morphs indicators between the two states using
 *   fractional offsets.
 * - Shape transitions use expressive M3 shapes (via [Morph]).
 *
 * ## Example
 * ```
 * val pagerState = rememberPagerState { 5 }
 *
 * ShapeIndicatorRow(
 *     pagerState = pagerState,
 *     sizes = ShapeIndicatorDefaults.sizes(20.dp, 12.dp),
 *     colors = ShapeIndicatorDefaults.colors(),
 *     borders = ShapeIndicatorDefaults.borders(
 *         selectedWidth = 2.dp,
 *         unselectedWidth = 0.dp
 *     ),
 *     shapes = ShapeIndicatorDefaults.shapes(),
 *     shuffleShapes = true
 * )
 * ```
 */

@ExperimentalMaterial3ExpressiveApi
@Composable
fun ShapeIndicatorRow(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    sizes: ShapeIndicatorSizes = ShapeIndicatorDefaults.sizes(),
    colors: ShapeIndicatorColors = ShapeIndicatorDefaults.colors(),
    borders: ShapeIndicatorBorders = ShapeIndicatorDefaults.borders(),
    shapes: IndicatorShapes = ShapeIndicatorDefaults.shapes(),
    shuffleShapes: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically
) {
    val pageCount = pagerState.pageCount

    val shuffledSelectedShapes = remember { shapes.selectedShapes.shuffled() }
    val shuffledUnselectedShapes = remember { shapes.unselectedShapes.shuffled() }

    val selectedSize = sizes.selectedSize
    val unselectedSize = sizes.unselectedSize

    val selectedColor = colors.selectedColor
    val unselectedColor = colors.unselectedColor

    Row(
        modifier = modifier.heightIn(min = selectedSize),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {

        val currentPage = pagerState.currentPage
        val offset = pagerState.currentPageOffsetFraction

        repeat(pageCount) { index ->
            val targetShapeSize = interpolateForIndex(
                index, currentPage, offset,
                unselectedSize, selectedSize,
                ::lerpSize
            )

            val targetShapeColor = interpolateForIndex(
                index, currentPage, offset,
                unselectedColor, selectedColor,
                ::lerpColor
            )

            val targetBorderWidth = interpolateForIndex(
                index, currentPage, offset,
                borders.unselectedWidth, borders.selectedWidth,
                ::lerpSize
            )

            val targetBorderColor = interpolateForIndex(
                index, currentPage, offset,
                borders.unselectedColor, borders.selectedColor,
                ::lerpColor
            )

            val animatedShapeSize by animateDpAsState(targetShapeSize)
            val animatedShapeColor by animateColorAsState(targetShapeColor)
            val animatedBorderWidth by animateDpAsState(targetBorderWidth)
            val animatedBorderColor by animateColorAsState(targetBorderColor)

            Box(modifier = Modifier.size(selectedSize)) {
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

                            onDrawBehind {
                                drawPath(path, animatedShapeColor)

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