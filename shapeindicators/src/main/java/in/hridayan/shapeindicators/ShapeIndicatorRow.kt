@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package `in`.hridayan.shapeindicators

import androidx.collection.FloatFloatPair
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import kotlin.math.abs

/**
 * A composable that displays a row of shape indicators for a pager.
 *
 * This composable animates the size, color, and shape of each indicator based on the
 * [PagerState]'s current page and offset. The selected indicator is larger and colored
 * with `colors.selectedColor`, while others use `colors.unselectedColor`.
 *
 * It leverages Material3's experimental shapes API, hence the opt-in:
 * `@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)`.
 *
 * @param modifier Modifier to be applied to the row.
 * @param pagerState The [PagerState] controlling the current page and scroll offset.
 * @param sizes The selected and unselected sizes of indicators. Use [ShapeIndicatorDefaults.sizes] to create.
 * @param colors The selected and unselected colors of indicators. Use [ShapeIndicatorDefaults.colors] to create.
 *               Must be called from a composable context since it uses [MaterialTheme.colorScheme] by default.
 * @param horizontalArrangement How indicators are arranged horizontally.
 * @param verticalAlignment How indicators are aligned vertically within the row.
 * @param shapes A list of [RoundedPolygon] shapes used for the indicators.
 * @param shuffleShapes If true, the shapes are shuffled randomly to assign to indicators.
 *
 * ### Behavior / Notes:
 * - The size and color of the indicators are animated smoothly when the pager scrolls.
 * - Each indicator morphs from a circle to the assigned [RoundedPolygon] shape using a [Morph] object.
 * - The selected indicator grows from `sizes.unselectedSize` to `sizes.selectedSize` based on the page offset.
 * - Colors are taken from the `colors` parameter, allowing composable-aware theme defaults.
 * - If `shuffleShapes` is true, the shapes are shuffled once at composition and reused.
 *
 * ### Experimental API:
 * This function uses the experimental Material3 expressive shapes API. To use it:
 * ```kotlin
 * @file:OptIn(ExperimentalMaterial3ExpressiveApi::class)
 * ```
 * This allows access to `MaterialShapes` like [MaterialShapes.Cookie9Sided], [MaterialShapes.Pill], [MaterialShapes.Arrow], etc.
 *
 * ### Example:
 * ```kotlin
 * val pagerState = rememberPagerState { 5 }
 * ShapeIndicatorRow(
 *     pagerState = pagerState,
 *     colors = ShapeIndicatorDefaults.colors(
 *         selectedColor = Color.Red,
 *         unselectedColor = Color.Gray
 *     ),
 *     sizes = ShapeIndicatorDefaults.sizes(
 *         selectedSize = 20.dp,
 *         unselectedSize = 12.dp
 *     ),
 *     shapes = listOf(MaterialShapes.Pill, MaterialShapes.Diamond),
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
    shapes: List<RoundedPolygon> = DefaultShapes,
    shuffleShapes: Boolean = false
) {
    val pageCount = pagerState.pageCount
    val shuffledShapes = remember { shapes.shuffled() }

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

                            val baseShape = MaterialShapes.Circle.scaled(sizePx)

                            val shapesSet = if (shuffleShapes) shuffledShapes else shapes

                            val targetShape =
                                shapesSet[index % shapesSet.size].scaled(sizePx)

                            val progress = when (index) {
                                currentPage -> 1f - abs(offset)
                                currentPage + 1 -> abs(offset)
                                currentPage - 1 -> abs(offset)
                                else -> 0f
                            }.coerceIn(0f, 1f)

                            val morph = Morph(start = baseShape, end = targetShape)
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


data class ShapeIndicatorColors(
    val selectedColor: Color,
    val unselectedColor: Color
)

data class ShapeIndicatorSizes(
    val selectedSize: Dp,
    val unselectedSize: Dp
)

@ExperimentalMaterial3ExpressiveApi
object ShapeIndicatorDefaults {
    /**
     * Set the color of the indicator shape in various states
     *
     * @param selectedColor Color of the indicator shape when it is selected
     * @param unselectedColor Color of the indicator shape when it is not selected
     */
    @Composable
    fun colors(
        selectedColor: Color = MaterialTheme.colorScheme.primary,
        unselectedColor: Color = MaterialTheme.colorScheme.surfaceVariant
    ) = ShapeIndicatorColors(selectedColor, unselectedColor)

    /**
     * Set the size of the indicator shape in various states
     *
     * @param selectedSize Size of the indicator shape when it is selected
     * @param unselectedSize Size of the indicator shape when it is not selected
     */
    fun sizes(
        selectedSize: Dp = 16.dp,
        unselectedSize: Dp = 10.dp
    ) = ShapeIndicatorSizes(selectedSize, unselectedSize)
}

val DefaultShapes = listOf(
    MaterialShapes.SoftBurst,
    MaterialShapes.Arrow,
    MaterialShapes.Cookie4Sided,
    MaterialShapes.Pill,
    MaterialShapes.Diamond,
    MaterialShapes.Pentagon,
)

private fun lerpSize(start: Dp, end: Dp, f: Float): Dp =
    start + (end - start) * f.coerceIn(0f, 1f)

private fun lerpColor(start: Color, end: Color, f: Float): Color =
    androidx.compose.ui.graphics.lerp(start, end, f.coerceIn(0f, 1f))

private fun RoundedPolygon.scaled(scale: Float): RoundedPolygon =
    transformed { x, y -> FloatFloatPair(x * scale, y * scale) }