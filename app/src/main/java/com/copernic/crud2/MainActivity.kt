package com.copernic.crud2

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copernic.crud2.Category.Category
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.coil.rememberCoilPainter
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter


class MainActivity : ComponentActivity() {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private val viewModel: CategoryViewModel by viewModels()
    val typography = Typography(
        displaySmall = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            letterSpacing = 0.15.sp
        ),
        bodySmall = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            letterSpacing = 0.25.sp
        )
        // Define otros estilos de tipografía aquí si lo deseas
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Aquí actualizamos el estado dentro del ViewModel con la URI de la imagen seleccionada.
            viewModel.setSelectedImageUri(uri)
        }
        setContent {
            MyApp {
                CategoryScreen(viewModel = viewModel, imagePickerLauncher = imagePickerLauncher)

            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = typography // Usa tu objeto Typography aquí
    ) {
        content()
    }
}



@Composable
fun CategoryItemCard(
    category: Category,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    // La tarjeta que muestra la información de la categoría
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onEdit)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Cargar y mostrar la imagen si el icono no está vacío
            if (category.icon.isNotEmpty()) {
                val imageUri = category.icon
                AsyncImage(
                    model = imageUri, // URI de la imagen como modelo para AsyncImage
                    contentDescription = "Category Icon",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape) // Forma circular para la imagen
                )
            }

            // Mostrar el nombre de la categoría
            Text(
                text = category.name,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Mostrar la descripción de la categoría
            Text(
                text = category.description,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Botón para eliminar la categoría
            Button(
                onClick = onDelete,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Eliminar")
            }
        }
    }
}



@Composable
fun CategoryScreen(viewModel: CategoryViewModel = viewModel(), imagePickerLauncher: ActivityResultLauncher<String>) {
    val allCategories by viewModel.allCategories.observeAsState(listOf())
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(allCategories) { category ->
                CategoryItemCard(category = category, onDelete = { viewModel.delete(category) }, onEdit = {
                    selectedCategory = category
                    showEditDialog = true
                })
            }
        }

        CircularButton(onClick = { showDialog = true }, modifier = Modifier.align(Alignment.BottomCenter))
    }

    if (showDialog) {
        CategoryDialog(onDismiss = { showDialog = false }, viewModel = viewModel)
    }

    if (showEditDialog && selectedCategory != null) {
        EditCategoryDialog(category = selectedCategory!!, onDismiss = {
            showEditDialog = false
            selectedCategory = null
        }, viewModel = viewModel, )


    }

        LazyColumn {
            items(allCategories) { category ->
                CategoryItemCard(category = category, onDelete = { viewModel.delete(category) }, onEdit = {
                    selectedCategory = category
                    showEditDialog = true
                })
            }
        }
    }



@Composable
fun CircularButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(56.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.Blue
        )
    }
}



/*@Composable
fun CategoryDialog(
    onDismiss: () -> Unit,
    viewModel: CategoryViewModel,
    imagePickerLauncher: ActivityResultLauncher<String>,
    selectedImageUri: Uri?,
    onImageSelected: (Uri?) -> Unit // This might not be needed if you handle it all within the dialog
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var uri by remember {
        mutableStateOf<Uri?>(null)
    }
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uriResult ->
            uri = uriResult
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Categoría") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                TextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
                SinglePhoto()
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.insert(Category(name = name, description = description, icon = selectedImageUri.toString()))
                onDismiss()
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}*/
@Composable
fun CategoryDialog(onDismiss: () -> Unit, viewModel: CategoryViewModel) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            imageUri = uri
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Categoría") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                TextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
                Button(onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                    Text("Seleccionar imagen")
                }
                // Mostrar la imagen seleccionada si está disponible
                imageUri?.let { uri ->
                    Image(painter = rememberAsyncImagePainter(uri), contentDescription = null)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.insert(name, description, imageUri.toString())
                onDismiss()
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}



@Composable
fun EditCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    viewModel: CategoryViewModel
) {
    var name by remember { mutableStateOf(category.name) }
    var description by remember { mutableStateOf(category.description) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            imageUri = uri
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Categoría") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                TextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
                Button(onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                    Text("Seleccionar imagen")
                }
                // Mostrar la imagen seleccionada si está disponible
                imageUri?.let { uri ->
                    Image(painter = rememberAsyncImagePainter(uri), contentDescription = null, modifier = Modifier.size(100.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Asegúrate de manejar adecuadamente la conversión de Uri a String o manejar Uri directamente en tu modelo de datos si es necesario
                viewModel.update(category.copy(name = name, description = description, icon = imageUri.toString()))
                onDismiss()
            }) {
                Text("Actualizar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun SinglePhoto() {
    var uri by remember {
        mutableStateOf<Uri?>(null)
    }
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uriResult ->
            uri = uriResult
        }
    )

    Column {
        Button(onClick = {
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text(text = "Open Gallery")
        }

        uri?.let {
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier.size(248.dp)
            )
        }
    }
}
