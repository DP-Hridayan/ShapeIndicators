package `in`.hridayan.shapeindicators

import androidx.collection.FloatFloatPair
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.graphics.shapes.RoundedPolygon

internal fun lerpSize(start: Dp, end: Dp, f: Float): Dp =
    start + (end - start) * f.coerceIn(0f, 1f)

internal fun lerpColor(start: Color, end: Color, f: Float): Color =
    androidx.compose.ui.graphics.lerp(start, end, f.coerceIn(0f, 1f))

internal fun RoundedPolygon.scaled(scale: Float): RoundedPolygon =
    transformed { x, y -> FloatFloatPair(x * scale, y * scale) }
