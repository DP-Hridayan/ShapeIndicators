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
        selectedSize: Dp = 16.dp,
        unselectedSize: Dp = 10.dp
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
}

