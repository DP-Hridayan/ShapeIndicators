package `in`.hridayan.shapeindicators.demo.screens

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import `in`.hridayan.shapeindicators.ShapeIndicatorColumn
import `in`.hridayan.shapeindicators.ShapeIndicatorDefaults
import `in`.hridayan.shapeindicators.ShapeIndicatorRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DemoScreen() {
    val view = LocalView.current
    val pagerState = rememberPagerState { 5 }
    val scope = rememberCoroutineScope()

    val pagerHeight = 250.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 50.dp)
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
                    selectedRadius = ShapeIndicatorDefaults.defaultGlowRadius,
                    selectedBlur = ShapeIndicatorDefaults.defaultGlowBlur
                ),
                onIndicatorClick = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .height(pagerHeight)
                    .clip(MaterialTheme.shapes.extraLargeIncreased)
            ) { page ->

                val pageColors = listOf(
                    Color(0xFFB39DDB),
                    Color(0xFF80CBC4),
                    Color(0xFFFFAB91),
                    Color(0xFFA5D6A7),
                    Color(0xFFFFCC80)
                )

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
            glow = ShapeIndicatorDefaults.glow(
                selectedRadius = ShapeIndicatorDefaults.defaultGlowRadius,
                selectedBlur = ShapeIndicatorDefaults.defaultGlowBlur
            ),
            onIndicatorClick = { index ->
                scope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )


        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    scope.launch {
                        val prev = (pagerState.currentPage - 1).coerceAtLeast(0)
                        pagerState.animateScrollToPage(prev)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                shapes = ButtonDefaults.shapes(),
                enabled = pagerState.currentPage != 0
            ) {
                Text("Previous")
            }

            Text("Page ${pagerState.currentPage + 1} of ${pagerState.pageCount}")

            Button(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    scope.launch {
                        val next =
                            (pagerState.currentPage + 1).coerceAtMost(pagerState.pageCount - 1)
                        pagerState.animateScrollToPage(next)
                    }
                },
                shapes = ButtonDefaults.shapes(),
                enabled = pagerState.currentPage != pagerState.pageCount - 1
            ) {
                Text("Next")
            }
        }
    }
}