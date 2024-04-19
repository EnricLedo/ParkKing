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
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
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



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TagsScreen(tagViewModel: TagViewModel, navController: NavController) {
    var showAddTagDialog by remember { mutableStateOf(false) }
    val tags by tagViewModel.getTagsFlow().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTagDialog = true },
                content = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Tag")
                }
            )
        }
    ) {
        if (showAddTagDialog) {
            AddTagDialog(
                onTagAdded = { tag ->
                    scope.launch {
                        // Actualiza esta llamada para incluir los callbacks necesarios
                        tagViewModel.addTag(
                            tag,
                            onSuccess = {
                                // Acciones a realizar si el tag se añade correctamente
                                showAddTagDialog = false
                            },
                            onTagExists = {
                                // Acciones a realizar si el tag ya existe
                                // Podrías mostrar un mensaje de error aquí
                            }
                        )
                    }
                },
                onDialogDismissed = { showAddTagDialog = false },
                tagViewModel = tagViewModel
            )
        }



        if (!tags.isNullOrEmpty()) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(tags) { tag ->
                    TagItem(tag = tag, tagViewModel = tagViewModel, onEdit = { editedTag ->
                        scope.launch {
                            tagViewModel.updateTag(
                                editedTag,
                                onSuccess = {
                                    // Código para manejar el éxito de la actualización
                                },
                                onTagExists = {
                                    // Código para manejar si el tag actualizado ya existe con el mismo título
                                }

                            )
                        }
                    })
                }
            }
        }
        else {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Default.List, contentDescription = null, modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "No se encontraron \nTags", fontSize = 18.sp, fontWeight = FontWeight.Thin, textAlign = TextAlign.Center)
            }
        }
    }
}


@Composable
fun TagItem(tag: Tag, tagViewModel: TagViewModel, onEdit: (Tag) -> Unit) {
    var showDeleteTagDialog by remember { mutableStateOf(false) }
    var showEditTagDialog by remember { mutableStateOf(false) }
    var showContentDialog by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableStateOf(0L) }

    // Función para manejar el doble clic
    fun handleDoubleClick() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < 500L) { // 500 ms como umbral para doble clic
            showContentDialog = true
        }
        lastClickTime = currentTime
    }

    if (showDeleteTagDialog) {
        DeleteTagDialog(
            onConfirmDelete = {
                CoroutineScope(Dispatchers.IO).launch {
                    tagViewModel.deleteTag(tag.id ?: "")
                    showDeleteTagDialog = false
                }
            },
            onDismiss = {
                showDeleteTagDialog = false
            }
        )
    }

    if (showEditTagDialog) {
        EditTagDialog(tag = tag, onTagUpdated = { updatedTag ->
            tagViewModel.updateTag(
                updatedTag,
                onSuccess = {
                    // Código para manejar el éxito
                    showEditTagDialog = false
                },
                onTagExists = {
                    // Código para manejar el error si el tag ya existe
                    // Podrías mostrar un diálogo de error o un Snackbar
                }
            )
        }, onDialogDismissed = { showEditTagDialog = false }, tagViewModel = tagViewModel)
    }

    if (showContentDialog) {
        ShowContentDialog(tag = tag, onDismiss = { showContentDialog = false })
    }

    Card(
        modifier = Modifier.padding(6.dp).clickable(onClick = { handleDoubleClick() })
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth()
        ) {
            Text(text = tag.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                IconButton(onClick = { showDeleteTagDialog = true }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Icon")
                }
                IconButton(onClick = { showEditTagDialog = true }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Icon")
                }
            }
        }
    }
}

@Composable
fun ShowContentDialog(tag: Tag, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Contenido de Tag") },
        text = { Text(text = tag.content) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun EditTagDialog(tag: Tag, onTagUpdated: (Tag) -> Unit, onDialogDismissed: () -> Unit, tagViewModel: TagViewModel) {
    var titleText by remember { mutableStateOf(tag.title) }
    var contentText by remember { mutableStateOf(tag.content) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf(tag.image) }
    var showErrorDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text("A tag with this title already exists.") },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDialogDismissed,
        title = { Text(text = "Editar Tag") },
        confirmButton = {
            Button(onClick = {
                if (titleText.isNotBlank() && contentText.isNotBlank()) {
                    // Actualización de la imagen si es necesario
                    if (imageUri != null) {
                        val inputStream = context.contentResolver.openInputStream(imageUri!!)
                        inputStream?.let { stream ->
                            tagViewModel.uploadImageToFirebaseStorage(stream, onSuccess = { newImageUrl ->
                                val updatedTag = tag.copy(title = titleText, content = contentText, image = newImageUrl)
                                tagViewModel.updateTag(
                                    updatedTag,
                                    onSuccess = {
                                        onTagUpdated(updatedTag)
                                        onDialogDismissed()
                                    },
                                    onTagExists = {
                                        showErrorDialog = true
                                    }
                                )
                            }, onFailure = {
                                // Maneja el fallo de carga de imagen
                            })
                        }
                    } else {
                        val updatedTag = tag.copy(title = titleText, content = contentText, image = imageUrl)
                        tagViewModel.updateTag(
                            updatedTag,
                            onSuccess = {
                                onTagUpdated(updatedTag)
                                onDialogDismissed()
                            },
                            onTagExists = {
                                showErrorDialog = true
                            }
                        )
                    }
                }
            }) {
                Text(text = "Actualizar")
            }
        },
        dismissButton = {
            Button(onClick = onDialogDismissed) {
                Text(text = "Cancelar")
            }
        },
        text = {
            Column {
                TextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text(text = "Título") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = contentText,
                    onValueChange = { contentText = it },
                    label = { Text(text = "Contenido") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUri),
                        contentDescription = "Tag Image",
                        modifier = Modifier.size(100.dp)
                    )
                } else if (imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUrl),
                        contentDescription = "Tag Image",
                        modifier = Modifier.size(100.dp)
                    )
                }
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Seleccionar Imagen")
                }
            }
        }
    )
}


@Composable
fun AddTagDialog(
    onTagAdded: (Tag) -> Unit,
    onDialogDismissed: () -> Unit,
    tagViewModel: TagViewModel
) {
    var titleText by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text("A tag with this title already exists.") },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDialogDismissed,
        title = { Text("Add New Tag") },
        text = {
            Column {
                TextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Title") }
                )
                TextField(
                    value = contentText,
                    onValueChange = { contentText = it },
                    label = { Text("Content") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Select Image")
                }
                imageUri?.let { uri ->
                    Image(painter = rememberAsyncImagePainter(model = uri), contentDescription = "Selected Image", modifier = Modifier.size(100.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titleText.isNotBlank() && contentText.isNotBlank()) {
                        imageUri?.let { uri ->
                            val inputStream = context.contentResolver.openInputStream(uri)
                            inputStream?.let {
                                tagViewModel.uploadImageToFirebaseStorage(it, onSuccess = { imageUrl ->
                                    val newTag = Tag(title = titleText, content = contentText, image = imageUrl)
                                    tagViewModel.addTag(
                                        newTag,
                                        onSuccess = {
                                            onTagAdded(newTag)
                                            onDialogDismissed()
                                        },
                                        onTagExists = {
                                            showErrorDialog = true
                                        }
                                    )
                                }, onFailure = {
                                    // Handle upload failure
                                })
                            }
                        } ?: run {
                            val newTag = Tag(title = titleText, content = contentText)
                            tagViewModel.addTag(
                                newTag,
                                onSuccess = {
                                    onTagAdded(newTag)
                                    onDialogDismissed()
                                },
                                onTagExists = {
                                    showErrorDialog = true
                                }
                            )
                        }
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDialogDismissed) {
                Text("Cancel")
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