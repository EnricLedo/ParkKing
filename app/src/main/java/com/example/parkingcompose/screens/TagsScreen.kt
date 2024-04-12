package com.example.parkingcompose.screens


import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.parkingcompose.data.Tag
import com.example.parkingcompose.viewmodels.TagViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}



@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TagsScreen(firestore: TagViewModel) {
    var showAddTagDialog by remember { mutableStateOf(false) }

    val tags by firestore.getTagsFlow().collectAsState(emptyList())

    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddTagDialog = true
                },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Tag")
            }
            if (showAddTagDialog) {
                AddTagDialog(
                    onTagAdded = { tag ->
                        scope.launch {
                            firestore.addTag(tag)
                        }
                        showAddTagDialog = false
                    },
                    onDialogDismissed = { showAddTagDialog = false },
                )
            }
        }
    ) {
        if(!tags.isNullOrEmpty()) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(4.dp)
            ) {
                tags.forEach {
                    item {
                        TagItem(tag = it, firestore = firestore)
                    }
                }
            }
        } else{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Default.List, contentDescription = null, modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "No se encontraron \nTags",
                    fontSize = 18.sp, fontWeight = FontWeight.Thin,
                    textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun TagItem(tag: Tag, firestore: TagViewModel) {
    var showDeleteTagDialog by remember { mutableStateOf(false) }

    val onDeleteTagConfirmed: () -> Unit = {
        CoroutineScope(Dispatchers.Default).launch {
            firestore.deleteTag(tag.id ?: "")
        }
    }

    if (showDeleteTagDialog) {
        DeleteTagDialog(
            onConfirmDelete = {
                onDeleteTagConfirmed()
                showDeleteTagDialog = false
            },
            onDismiss = {
                showDeleteTagDialog = false
            }
        )
    }

    Card(
        modifier = Modifier.padding(6.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Text(text = tag.title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = tag.content,
                fontWeight = FontWeight.Thin,
                fontSize = 13.sp,
                lineHeight = 15.sp)
            IconButton(
                onClick = { showDeleteTagDialog = true },
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Icon")
            }
        }
    }
}


@Composable
fun AddTagDialog(onTagAdded: (Tag) -> Unit, onDialogDismissed: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Agregar Tag") },
        confirmButton = {
            Button(
                onClick = {
                    val newTag = Tag(
                        title = title,
                        content = content)
                    onTagAdded(newTag)
                    title = ""
                    content = ""
                }
            ) {
                Text(text = "Agregar")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDialogDismissed()
                }
            ) {
                Text(text = "Cancelar")
            }
        },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    label = { Text(text = "Título") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    value = content,
                    onValueChange = { content = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    maxLines = 4,
                    label = { Text(text = "Contenido") }
                )
            }
        }
    )
}

@Composable
fun DeleteTagDialog(onConfirmDelete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar Tag") },
        text = { Text("¿Estás seguro que deseas eliminar la Tag?") },
        confirmButton = {
            Button(
                onClick = onConfirmDelete
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}