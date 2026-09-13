package `in`.hridayan.shapeindicators

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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

@Immutable
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
        selectedColor: Color = selectedShapeColor,
        unselectedColor: Color = unselectedShapeColor,
    ) = ShapeIndicatorColors(selectedColor, unselectedColor)

    /**
     * Set the size of the indicator shape in various states
     *
     * @param selectedSize Size of the indicator shape when it is selected
     * @param unselectedSize Size of the indicator shape when it is not selected
     */
    fun sizes(
        selectedSize: Dp = selectedShapeSize,
        unselectedSize: Dp = unselectedShapeSize
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
        selectedWidth: Dp = noBorder,
        unselectedWidth: Dp = noBorder,
        selectedColor: Color = selectedBorderColor,
        unselectedColor: Color = unselectedBorderColor
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
        selectedColor: Color = selectedGlowColor,
        unselectedColor: Color = unselectedGlowColor,
        selectedRadius: Dp = noGlowRadius,
        unselectedRadius: Dp = noGlowRadius,
        selectedBlur: Dp = noGlowBlur,
        unselectedBlur: Dp = noGlowBlur
    ): ShapeIndicatorGlow = ShapeIndicatorGlow(
        selectedColor = selectedColor,
        unselectedColor = unselectedColor,
        selectedRadius = selectedRadius,
        unselectedRadius = unselectedRadius,
        selectedBlur = selectedBlur,
        unselectedBlur = unselectedBlur
    )

    /**
     * Enables indicator overflow handling when total pages exceed visible space.
     *
     * @param enabled Enables or disables overflow behavior.
     * If false, all indicators are rendered without limits.
     *
     * @param maxVisibleItems Maximum number of indicators allowed at once.
     *
     * @param hintShapeSize Size of the hint indicators shown at the edges.
     */
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
        selectedShapes: List<RoundedPolygon> = ShapeIndicatorDefaults.selectedShapes,
        unselectedShapes: List<RoundedPolygon> = ShapeIndicatorDefaults.unselectedShapes
    ) = IndicatorShapes(selectedShapes, unselectedShapes)


    /**
     * Default list of shapes used for selected indicators.
     * Includes various Material 3 expressive shapes.
     */
    val selectedShapes = listOf(
        MaterialShapes.SoftBurst,
        MaterialShapes.Arrow,
        MaterialShapes.Cookie4Sided,
        MaterialShapes.Pill,
        MaterialShapes.Diamond,
        MaterialShapes.Pentagon,
    )

    /**
     * Default list of shapes used for unselected indicators.
     * By default, unselected indicators are circles.
     */
    val unselectedShapes = listOf(MaterialShapes.Circle)

    /**
     * Default size for selected indicators.
     */
    val selectedShapeSize = 16.dp

    /**
     * Default size for unselected indicators.
     */
    val unselectedShapeSize = 8.dp

    /**
     * Default border width when borders are disabled.
     */
    val noBorder = 0.dp

    /**
     * Default border width.
     */
    val borderWidth = 2.dp

    /**
     * Default glow radius when glow is disabled.
     */
    val noGlowRadius = 0.dp

    /**
     * Default glow blur when glow is disabled.
     */
    val noGlowBlur = 0.dp

    /**
     * Default glow radius.
     */
    val glowRadius = 4.dp

    /**
     * Default glow blur.
     */
    val glowBlur = 6.dp

    /**
     * Default glow color for selected indicators.
     */
    val selectedGlowColor: Color @Composable get() = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)

    /**
     * Default glow color for unselected indicators.
     */
    val unselectedGlowColor: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)

    /**
     * Default color for selected indicators.
     */
    val selectedShapeColor: Color @Composable get() = MaterialTheme.colorScheme.primary

    /**
     * Default color for unselected indicators.
     */
    val unselectedShapeColor: Color @Composable get() = MaterialTheme.colorScheme.surfaceVariant

    /**
     * Default border color for selected indicators.
     */
    val selectedBorderColor: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

    /**
     * Default border color for unselected indicators.
     */
    val unselectedBorderColor: Color @Composable get() = MaterialTheme.colorScheme.outlineVariant

    @Deprecated(
        message = "Use selectedShapes instead!",
        replaceWith = ReplaceWith(
            "ShapeIndicatorDefaults.selectedShapes",
            imports = ["in.hridayan.shapeindicators.ShapeIndicatorDefaults"]
        )
    )
    val defaultSelectedShapes = selectedShapes

    @Deprecated(
        message = "Use unselectedShapes instead!",
        replaceWith = ReplaceWith(
            "ShapeIndicatorDefaults.unselectedShapes",
            imports = ["in.hridayan.shapeindicators.ShapeIndicatorDefaults"]
        )
    )
    val defaultUnselectedShapes = unselectedShapes

    @Deprecated(
        message = "Use selectedShapeSize instead!",
        replaceWith = ReplaceWith(
            "ShapeIndicatorDefaults.selectedShapeSize",
            imports = ["in.hridayan.shapeindicators.ShapeIndicatorDefaults"]
        )
    )
    val defaultSelectedShapeSize = selectedShapeSize

    @Deprecated(
        message = "Use unselectedShapeSize instead! The default size is changed from 10.dp to 8.dp",
        replaceWith = ReplaceWith(
            "ShapeIndicatorDefaults.unselectedShapeSize",
            imports = ["in.hridayan.shapeindicators.ShapeIndicatorDefaults"]
        )
    )
    val defaultUnselectedShapeSize = unselectedShapeSize

    @Deprecated(
        message = "Use borderWidth instead!",
        replaceWith = ReplaceWith(
            "ShapeIndicatorDefaults.borderWidth",
            imports = ["in.hridayan.shapeindicators.ShapeIndicatorDefaults"]
        )
    )
    val defaultBorderWidth = borderWidth

    @Deprecated(
        message = "Use glowRadius instead!",
        replaceWith = ReplaceWith(
            "ShapeIndicatorDefaults.glowRadius",
            imports = ["in.hridayan.shapeindicators.ShapeIndicatorDefaults"]
        )
    )
    val defaultGlowRadius = glowRadius

    @Deprecated(
        message = "Use glowBlur instead!",
        replaceWith = ReplaceWith(
            "ShapeIndicatorDefaults.glowBlur",
            imports = ["in.hridayan.shapeindicators.ShapeIndicatorDefaults"]
        )
    )
    val defaultGlowBlur = glowBlur

    /**
     * The maximum number of items visible when overflow is enabled.
     */
    const val MAX_VISIBLE_ITEMS = 5

    /**
     * Default size of the hint indicators shown at the edges during overflow.
     */
    val overflowHintShapeSize = 6.dp
}

