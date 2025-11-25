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
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.toPath
import kotlin.math.abs

/**
 * Displays a morphing Material-shape indicator row for a pager.
 *
 * Each indicator smoothly animates **size**, **color**, and **shape** based on the
 * [PagerState]'s current position and scroll offset.
 * The currently selected indicator animates between the "unselected" and "selected"
 * visual states defined in [sizes], [colors], and [shapes].
 *
 * @param modifier Modifier applied to the indicator row.
 *
 * @param pagerState The [PagerState] used to determine:
 * - total number of pages
 * - current page index
 * - scroll offset for morph/size/color interpolation
 *
 * @param sizes Defines the size of indicators in selected and unselected states.
 * Use [ShapeIndicatorDefaults.sizes] to create.
 *
 * @param colors Defines the color of indicators in selected and unselected states.
 * Use [ShapeIndicatorDefaults.colors] to create.
 * (This must be called inside a composable, because defaults use `MaterialTheme.colorScheme`.)
 *
 * @param horizontalArrangement Spacing arrangement for the indicator row.
 *
 * @param verticalAlignment Alignment of indicators inside the row's height.
 *
 * @param shapes The shapes used for selected vs. unselected indicators.
 * Use [ShapeIndicatorDefaults.shapes] to pull the library’s default Material3 expressive shapes.
 *
 * @param shuffleShapes If true, both selected and unselected shape lists are randomly shuffled once
 * at composition time. Each indicator then cycles through the shuffled list.
 *
 * ## Behavior
 * - Selected indicators animate from `unselected → selected` sizes and shapes.
 * - Scrolling the pager morphs the shape between its unselected and selected variants.
 * - Colors fade between `colors.unselectedColor` ↔ `colors.selectedColor`.
 * - Shapes are morphed using [Morph] with Material expressive shapes.
 *
 * ## Example
 * ```
 * val pagerState = rememberPagerState { 5 }
 *
 * ShapeIndicatorRow(
 *     pagerState = pagerState,
 *     sizes = ShapeIndicatorDefaults.sizes(20.dp, 12.dp),
 *     colors = ShapeIndicatorDefaults.colors(),
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
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    shapes: IndicatorShapes = ShapeIndicatorDefaults.shapes(),
    shuffleShapes: Boolean = false
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

            val targetSize = when (index) {
                currentPage -> lerpSize(selectedSize, unselectedSize, abs(offset))
                currentPage + 1 -> lerpSize(unselectedSize, selectedSize, offset.coerceIn(0f, 1f))
                currentPage - 1 -> lerpSize(unselectedSize, selectedSize, -offset.coerceIn(0f, 1f))
                else -> unselectedSize
            }

            val targetColor = when (index) {
                currentPage -> lerpColor(selectedColor, unselectedColor, abs(offset))
                currentPage + 1 -> lerpColor(
                    unselectedColor,
                    selectedColor,
                    offset.coerceIn(0f, 1f)
                )

                currentPage - 1 -> lerpColor(
                    unselectedColor,
                    selectedColor,
                    (-offset).coerceIn(0f, 1f)
                )

                else -> unselectedColor
            }

            val animatedSize by animateDpAsState(targetSize, label = "")
            val animatedColor by animateColorAsState(targetColor, label = "")

            Box(modifier = Modifier.size(selectedSize)) {
                Box(
                    modifier = Modifier
                        .size(animatedSize)
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

                            val progress = when (index) {
                                currentPage -> 1f - abs(offset)
                                currentPage + 1 -> abs(offset)
                                currentPage - 1 -> abs(offset)
                                else -> 0f
                            }.coerceIn(0f, 1f)

                            val morph = Morph(start = startShape, end = endShape)
                            val path = morph.toPath(progress).asComposePath()

                            onDrawBehind {
                                drawPath(path, animatedColor)
                            }
                        }
                )
            }
        }
    }
}