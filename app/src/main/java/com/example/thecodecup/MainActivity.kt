package com.example.thecodecup

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.thecodecup.presentation.navigation.AppNavGraph
import com.example.thecodecup.presentation.theme.TheCodeCupTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")

        try {
            enableEdgeToEdge()
            Log.d("MainActivity", "enableEdgeToEdge done")

            setContent {
                TheCodeCupTheme {
                    val navController = rememberNavController()
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        AppNavGraph(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
            Log.d("MainActivity", "setContent completed")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate", e)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TheCodeCupTheme {
        val navController = rememberNavController()
        AppNavGraph(navController = navController)
    }
}