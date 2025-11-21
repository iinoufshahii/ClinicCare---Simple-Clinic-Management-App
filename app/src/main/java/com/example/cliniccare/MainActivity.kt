package com.example.cliniccare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.cliniccare.navigation.NavGraph
import com.example.cliniccare.ui.theme.ClinicCareTheme

/** Main activity of the ClinicCare application */
class MainActivity : ComponentActivity() {
    /** Sets up the UI with navigation and theme */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClinicCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }

        }
    }
}

/** Composable for greeting text */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

/** Preview for the greeting composable */
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ClinicCareTheme {
        Greeting("Android")
    }
}