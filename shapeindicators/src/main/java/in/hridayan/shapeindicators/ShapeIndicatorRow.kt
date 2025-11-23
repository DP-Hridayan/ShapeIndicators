@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package `in`.hridayan.shapeindicators

import androidx.collection.FloatFloatPair
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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

@ExperimentalMaterial3ExpressiveApi
@Composable
fun ShapeIndicatorRow(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    unselectedSize: Dp = 10.dp,
    selectedSize: Dp = 16.dp,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    shapes: List<RoundedPolygon> = DefaultShapes,
) {
    val pageCount = pagerState.pageCount
    val shuffledShapes = remember { shapes.shuffled() }

    Row(
        modifier = modifier.heightIn(min = selectedSize),
        verticalAlignment = Alignment.CenterVertically
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

            Box(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(animatedSize)
                        .align(Alignment.Center)
                        .drawWithCache {
                            val sizePx = this.size.minDimension

                            val baseShape = MaterialShapes.Circle.scaled(sizePx)
                            val targetShape =
                                shuffledShapes[index % shuffledShapes.size].scaled(sizePx)

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

// ---------- Helpers ----------
private fun lerpSize(start: Dp, end: Dp, f: Float): Dp =
    start + (end - start) * f.coerceIn(0f, 1f)

private fun lerpColor(start: Color, end: Color, f: Float): Color =
    androidx.compose.ui.graphics.lerp(start, end, f.coerceIn(0f, 1f))

private fun RoundedPolygon.scaled(scale: Float): RoundedPolygon =
    transformed { x, y -> FloatFloatPair(x * scale, y * scale) }

// ---------- Default shapes ----------
val DefaultShapes = listOf(
    MaterialShapes.SoftBurst,
    MaterialShapes.Arrow,
    MaterialShapes.Cookie4Sided,
    MaterialShapes.Pill,
    MaterialShapes.Diamond,
    MaterialShapes.Pentagon,
)
