package `in`.hridayan.shapeindicators

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon

data class ShapeIndicatorColors(
    val selectedColor: Color,
    val unselectedColor: Color
)

data class ShapeIndicatorSizes(
    val selectedSize: Dp,
    val unselectedSize: Dp
)

data class ShapeIndicatorBorders(
    val selectedWidth: Dp,
    val unselectedWidth: Dp,
    val selectedColor: Color,
    val unselectedColor: Color
)

data class ShapeIndicatorGlow(
    val selectedColor: Color,
    val unselectedColor: Color,
    val selectedRadius: Dp,
    val unselectedRadius: Dp,
    val selectedBlur: Dp,
    val unselectedBlur: Dp
)

data class ShapeIndicatorOverflow(
    val enabled: Boolean,
    val maxVisibleItems: Int,
    val hintShapeSize: Dp
)

data class IndicatorShapes(
    val selectedShapes: List<RoundedPolygon>,
    val unselectedShapes: List<RoundedPolygon>
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
        selectedSize: Dp = defaultSelectedShapeSize,
        unselectedSize: Dp = defaultUnselectedShapeSize
    ) = ShapeIndicatorSizes(selectedSize, unselectedSize)

    /**
     * Defines the border style for indicators in both selected and unselected states.
     *
     * This controls the **border width** and **border color** of each indicator.
     * Borders smoothly animate when the pager scrolls, using the same transition
     * progress that controls size, color, and shape morphing.
     *
     * ## Behavior
     * - Indicators interpolate between `unselectedWidth → selectedWidth`
     * - Border colors interpolate between `unselectedColor → selectedColor`
     * - If both widths are `0.dp`, borders are effectively disabled
     *
     * ## Example
     * ```
     * borders = ShapeIndicatorDefaults.borders(
     *     selectedWidth = 2.dp,
     *     unselectedWidth = 0.dp,
     *     selectedColor = MaterialTheme.colorScheme.primary,
     *     unselectedColor = MaterialTheme.colorScheme.outlineVariant
     * )
     * ```
     *
     * @param selectedWidth Border width when the indicator is selected.
     * @param unselectedWidth Border width when the indicator is not selected.
     * @param selectedColor Border color for the selected indicator.
     * @param unselectedColor Border color for the unselected indicator.
     */
    @Composable
    fun borders(
        selectedWidth: Dp = 0.dp,
        unselectedWidth: Dp = 0.dp,
        selectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedColor: Color = MaterialTheme.colorScheme.outlineVariant
    ) = ShapeIndicatorBorders(
        selectedWidth = selectedWidth,
        unselectedWidth = unselectedWidth,
        selectedColor = selectedColor,
        unselectedColor = unselectedColor
    )

    /**
     * Defines the glow effect for each indicator.
     *
     * Glow is drawn **behind** the shape using a thick blurred stroke.
     *
     * Each glow property supports separate values for:
     * - selected indicators
     * - unselected indicators
     *
     * ## What Glow Controls
     * - **Color** → tint of the glow halo
     * - **Radius** → how thick the glow stroke is (bigger = stronger halo)
     * - **Blur** → how soft the glow spreads (only applied on supported devices)
     *
     * Glow automatically animates during page transitions, keeping behavior
     * consistent with size/color/border/shape morphing.
     *
     * ## Parameters
     *
     * @param selectedColor Color of the glow when the indicator is selected.
     * Defaults to a subtle highlight of the primary color.
     *
     * @param unselectedColor Color of the glow when unselected.
     *
     * @param selectedRadius Stroke radius of the glow when selected.
     * Set to `0.dp` to disable glow.
     *
     * @param unselectedRadius Stroke radius of the glow for unselected indicators.
     *
     * @param selectedBlur Blur amount applied to the glow on selected indicators.
     * Larger values = softer, more diffused glow.
     *
     * @param unselectedBlur Blur amount for unselected indicators.
     *
     * @return A [ShapeIndicatorGlow] configuration used by [ShapeIndicatorRow].
     *
     * ## Example
     * ```
     * ShapeIndicatorRow(
     *     pagerState = pagerState,
     *     glow = ShapeIndicatorDefaults.glow(
     *         selectedRadius = 8.dp,
     *         selectedBlur = 12.dp
     *     )
     * )
     * ```
     */
    @Composable
    fun glow(
        selectedColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
        unselectedColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
        selectedRadius: Dp = 0.dp,
        unselectedRadius: Dp = 0.dp,
        selectedBlur: Dp = 0.dp,
        unselectedBlur: Dp = 0.dp
    ): ShapeIndicatorGlow = ShapeIndicatorGlow(
        selectedColor = selectedColor,
        unselectedColor = unselectedColor,
        selectedRadius = selectedRadius,
        unselectedRadius = unselectedRadius,
        selectedBlur = selectedBlur,
        unselectedBlur = unselectedBlur
    )

    fun overflow(
        enabled: Boolean = true,
        maxVisibleItems: Int = MAX_VISIBLE_ITEMS,
        hintShapeSize: Dp = overflowHintShapeSize
    ) = ShapeIndicatorOverflow(enabled, maxVisibleItems, hintShapeSize)

    /**
     * Defines which shapes are used for the selected and unselected indicators.
     *
     * - You can supply **any** list of [RoundedPolygon] shapes.
     * - Selected shapes are used when an indicator becomes active.
     * - Unselected shapes are used when an indicator is inactive.
     * - Shapes are paired with morphing for smooth transitions.
     *
     * By default, the selected shapes are expressive Material 3 shapes
     * (SoftBurst, Arrow, Diamond, etc.), while unselected indicators are circles.
     *
     * @param selectedShapes List of shapes used when indicators are selected.
     * @param unselectedShapes List of shapes for unselected indicators.
     */
    @Composable
    fun shapes(
        selectedShapes: List<RoundedPolygon> = defaultSelectedShapes,
        unselectedShapes: List<RoundedPolygon> = defaultUnselectedShapes
    ) = IndicatorShapes(selectedShapes, unselectedShapes)

    val defaultSelectedShapes = listOf(
        MaterialShapes.SoftBurst,
        MaterialShapes.Arrow,
        MaterialShapes.Cookie4Sided,
        MaterialShapes.Pill,
        MaterialShapes.Diamond,
        MaterialShapes.Pentagon,
    )

    val defaultUnselectedShapes = listOf(MaterialShapes.Circle)
    val defaultSelectedShapeSize = 16.dp
    val defaultUnselectedShapeSize = 10.dp
    val defaultBorderWidth = 2.dp
    val defaultGlowRadius = 4.dp
    val defaultGlowBlur = 6.dp
    const val MAX_VISIBLE_ITEMS = 5
    val overflowHintShapeSize = 6.dp
}

