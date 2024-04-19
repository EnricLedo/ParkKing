package com.example.parkingcompose.viewmodels

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkingcompose.data.Tag

@Composable
fun TagsDropdownMenu(tags: List<Tag>, onTagSelected: (Tag) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedTagName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (selectedTagName.isNotEmpty()) selectedTagName else "Select Tag",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(16.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            tags.forEach { tag ->
                DropdownMenuItem(
                    text = { Text(tag.title) },
                    onClick = {
                        selectedTagName = tag.title
                        expanded = false
                        onTagSelected(tag)
                    }
                )
            }
        }
    }
}
