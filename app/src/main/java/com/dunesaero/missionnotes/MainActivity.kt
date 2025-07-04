package com.dunesaero.missionnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dunesaero.missionnotes.ui.screens.MainScreen // Updated import
import com.dunesaero.missionnotes.ui.theme.DroneMissionNotesTheme // Import the theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // It's common to apply the theme at the top level here
            // MainScreen itself also applies it, which is fine, but often it's done once here.
            DroneMissionNotesTheme {
                MainScreen() // Call the new MainScreen
            }
        }
    }
}
