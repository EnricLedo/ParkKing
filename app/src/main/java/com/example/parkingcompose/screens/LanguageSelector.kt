package com.example.parkingcompose.screens

import android.app.Activity
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.parkingcompose.viewmodels.LanguageViewModel

@Composable
fun LanguageSelector(viewModel: LanguageViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val languages = listOf("English", "Español", "Català")

    Button(onClick = { expanded = true }) {
        Text("Select Language")
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        languages.forEach { language ->
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    (context as? Activity)?.let {
                        when (language) {
                            "English" -> viewModel.setLocale(it, "en")
                            "Español" -> viewModel.setLocale(it, "es")
                            "Català" -> viewModel.setLocale(it, "ca")
                        }
                    }
                }
            ) {
                Text(language)
            }
        }
    }
}
