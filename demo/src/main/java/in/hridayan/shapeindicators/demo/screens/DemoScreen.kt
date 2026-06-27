package `in`.hridayan.shapeindicators.demo.screens

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import `in`.hridayan.shapeindicators.ShapeIndicatorColumn
import `in`.hridayan.shapeindicators.ShapeIndicatorDefaults
import `in`.hridayan.shapeindicators.ShapeIndicatorRow
import kotlinx.coroutines.launch

private val pageColors = listOf(
    Color(0xFFB39DDB),
    Color(0xFF80CBC4),
    Color(0xFFFFAB91),
    Color(0xFFA5D6A7),
    Color(0xFFFFCC80),
    Color(0xFF90CAF9),
    Color(0xFFF48FB1),
    Color(0xFFBCAAA4),
    Color(0xFFCE93D8),
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DemoScreen() {
    val view = LocalView.current
    val pagerState = rememberPagerState { pageColors.size }
    val scope = rememberCoroutineScope()

    val currentPage by remember { derivedStateOf { pagerState.currentPage } }
    val pageCount = pagerState.pageCount

    var glowEnabled by remember { mutableStateOf(false) }
    var borderEnabled by remember { mutableStateOf(false) }
    var shuffleEnabled by remember { mutableStateOf(false) }
    var overflowEnabled by remember { mutableStateOf(true) }

    val pagerHeight = 250.dp

    // Surface fills the screen and applies the correct background for both light and dark themes
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 50.dp, bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = pagerHeight),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShapeIndicatorColumn(
                    pagerState = pagerState,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    sizes = ShapeIndicatorDefaults.sizes(selectedSize = 16.dp),
                    glow = ShapeIndicatorDefaults.glow(
                        selectedRadius = if (glowEnabled) ShapeIndicatorDefaults.defaultGlowRadius else 0.dp,
                        selectedBlur = if (glowEnabled) ShapeIndicatorDefaults.defaultGlowBlur else 0.dp,
                    ),
                    borders = ShapeIndicatorDefaults.borders(
                        selectedWidth = if (borderEnabled) ShapeIndicatorDefaults.defaultBorderWidth else 0.dp,
                    ),
                    shuffleShapes = shuffleEnabled,
                    overflow = ShapeIndicatorDefaults.overflow(enabled = overflowEnabled),
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .height(pagerHeight)
                        .clip(MaterialTheme.shapes.extraLargeIncreased)
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp)
                            .clip(MaterialTheme.shapes.extraLargeIncreased)
                            .background(pageColors[page % pageColors.size]),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Page ${page + 1}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ShapeIndicatorRow(
                pagerState = pagerState,
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                sizes = ShapeIndicatorDefaults.sizes(selectedSize = 16.dp),
                shuffleShapes = shuffleEnabled,
                glow = ShapeIndicatorDefaults.glow(
                    selectedRadius = if (glowEnabled) ShapeIndicatorDefaults.defaultGlowRadius else 0.dp,
                    selectedBlur = if (glowEnabled) ShapeIndicatorDefaults.defaultGlowBlur else 0.dp,
                ),
                borders = ShapeIndicatorDefaults.borders(
                    selectedWidth = if (borderEnabled) ShapeIndicatorDefaults.defaultBorderWidth else 0.dp,
                ),
                overflow = ShapeIndicatorDefaults.overflow(enabled = overflowEnabled),
            )

            Spacer(modifier = Modifier.height(8.dp))

            NavRow(
                currentPage = currentPage,
                pageCount = pageCount,
                onPrevious = {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    scope.launch {
                        pagerState.animateScrollToPage((currentPage - 1).coerceAtLeast(0))
                    }
                },
                onNext = {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    scope.launch {
                        pagerState.animateScrollToPage((currentPage + 1).coerceAtMost(pageCount - 1))
                    }
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            TogglePanel(
                glowEnabled = glowEnabled,
                borderEnabled = borderEnabled,
                shuffleEnabled = shuffleEnabled,
                overflowEnabled = overflowEnabled,
                onGlowToggle = { glowEnabled = it },
                onBorderToggle = { borderEnabled = it },
                onShuffleToggle = { shuffleEnabled = it },
                onOverflowToggle = { overflowEnabled = it },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NavRow(
    currentPage: Int,
    pageCount: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = onPrevious,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            ),
            shapes = ButtonDefaults.shapes(),
            enabled = currentPage != 0,
        ) {
            Text("Previous")
        }

        Text("Page ${currentPage + 1} of $pageCount")

        Button(
            onClick = onNext,
            shapes = ButtonDefaults.shapes(),
            enabled = currentPage != pageCount - 1,
        ) {
            Text("Next")
        }
    }
}

@Composable
private fun TogglePanel(
    glowEnabled: Boolean,
    borderEnabled: Boolean,
    shuffleEnabled: Boolean,
    overflowEnabled: Boolean,
    onGlowToggle: (Boolean) -> Unit,
    onBorderToggle: (Boolean) -> Unit,
    onShuffleToggle: (Boolean) -> Unit,
    onOverflowToggle: (Boolean) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Options",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            ToggleRow(label = "Glow", checked = glowEnabled, onCheckedChange = onGlowToggle)
            ToggleRow(label = "Border", checked = borderEnabled, onCheckedChange = onBorderToggle)
            ToggleRow(label = "Shuffle shapes", checked = shuffleEnabled, onCheckedChange = onShuffleToggle)
            ToggleRow(label = "Overflow", checked = overflowEnabled, onCheckedChange = onOverflowToggle)
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}