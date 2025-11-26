@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package `in`.hridayan.shapeindicators

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
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
 * Glow appears behind indicators and animates during scroll.
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
 * @param horizontalArrangement Spacing between indicators.
 *
 * @param verticalAlignment Vertical alignment of indicators.
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
fun ShapeIndicatorRow(
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
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically
) {
    Row(
        modifier = modifier.heightIn(min = maxOf(sizes.selectedSize, sizes.unselectedSize)),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        ShapeIndicatorItems(
            pagerState = pagerState,
            sizes = sizes,
            colors = colors,
            borders = borders,
            glow = glow,
            overflow = overflow,
            shapes = shapes,
            shuffleShapes = shuffleShapes,
            onIndicatorClick = onIndicatorClick
        )
    }
}