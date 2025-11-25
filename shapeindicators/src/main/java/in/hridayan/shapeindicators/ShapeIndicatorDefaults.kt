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

