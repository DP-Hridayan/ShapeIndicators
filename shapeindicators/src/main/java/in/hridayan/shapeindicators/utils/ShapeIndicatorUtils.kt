package `in`.hridayan.shapeindicators.utils

import android.graphics.BlurMaskFilter
import androidx.collection.FloatFloatPair
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.graphics.shapes.RoundedPolygon
import kotlin.math.abs

internal fun lerpSize(start: Dp, end: Dp, f: Float): Dp =
    start + (end - start) * f.coerceIn(0f, 1f)

internal fun lerpColor(start: Color, end: Color, f: Float): Color =
    lerp(start, end, f.coerceIn(0f, 1f))

internal fun RoundedPolygon.scaled(scale: Float): RoundedPolygon =
    transformed { x, y -> FloatFloatPair(x * scale, y * scale) }

internal inline fun <T> interpolateForIndex(
    index: Int,
    currentPage: Int,
    offset: Float,
    unselected: T,
    selected: T,
    lerp: (T, T, Float) -> T
): T {
    return when (index) {
        currentPage -> lerp(selected, unselected, abs(offset))
        currentPage + 1 -> lerp(unselected, selected, offset.coerceIn(0f, 1f))
        currentPage - 1 -> lerp(unselected, selected, (-offset).coerceIn(0f, 1f))
        else -> return unselected
    }
}

internal fun safeBlurMaskFilter(radius: Float): BlurMaskFilter? {
    return if (radius > 0f && radius < 3000f) {
        try {
            BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
        } catch (_: Exception) {
            null
        }
    } else null
}
