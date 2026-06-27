package `in`.hridayan.shapeindicators.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import `in`.hridayan.shapeindicators.demo.screens.DemoScreen
import `in`.hridayan.shapeindicators.demo.ui.theme.ShapeIndicatorsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShapeIndicatorsTheme {
                DemoScreen()
            }
        }
    }
}