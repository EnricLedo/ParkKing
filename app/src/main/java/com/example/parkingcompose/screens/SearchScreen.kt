package com.example.parkingcompose.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.parkingcompose.LobbyMapActivity
import com.example.parkingcompose.ParkingListActivity
import com.example.parkingcompose.R
import com.example.parkingcompose.UserProfile
import com.example.parkingcompose.viewmodels.SearchScreenViewModel
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@Composable
fun SearchScreen(
    viewModel: SearchScreenViewModel,
    navController: NavHostController
) {

        SearchScreen(navController = navController)
}

@Composable
fun SearchScreen(modifier: Modifier = Modifier, navController: NavHostController){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()  // Llena todo el espacio disponible
    ) {

        Text(
            text = "Seleccione el tipo de vehiculo",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(12.dp)
        )

        val localContext = LocalContext.current // Capturamos el contexto local

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxHeight()  // Llena toda la altura disponible
        ) {
            Image(
                painter = painterResource(id = R.drawable.moto),
                contentDescription = "Imagen de bicicleta",
                modifier = modifier
                    .width(60.dp)  // Ancho de la imagen
                    .height(60.dp)  // Altura de la imagen
                    .border(2.dp, Color.Black, RoundedCornerShape(4.dp))
                    .padding(4.dp)  // Margen entre la imagen y el borde
                    .clickable {  // Add this modifier
                        val intent = Intent(localContext, LobbyMapActivity::class.java)
                        ContextCompat.startActivity(localContext, intent, null)
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.coche),
                contentDescription = "Imagen de coche",
                modifier = modifier
                    .width(60.dp)  // Ancho de la imagen
                    .height(60.dp)  // Altura de la imagen
                    .border(2.dp, Color.Black, RoundedCornerShape(4.dp))
                    .padding(4.dp)  // Margen entre la imagen y el borde
                    .clickable {  // Add this modifier
                        val intent = Intent(localContext, ParkingListActivity::class.java)
                        ContextCompat.startActivity(localContext, intent, null)
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.patinete),
                contentDescription = "Imagen de patinete",
                modifier = modifier
                    .width(60.dp)  // Ancho de la imagen
                    .height(60.dp)  // Altura de la imagen
                    .border(2.dp, Color.Black, RoundedCornerShape(4.dp))
                    .padding(4.dp)  // Margen entre la imagen y el borde
                    .clickable {  // Add this modifier
                        val intent = Intent(localContext, UserProfile::class.java)
                        ContextCompat.startActivity(localContext, intent, null)
                    }
            )
            BottomNavigationBar(navController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = { navController.navigate("profile") },
        modifier = Modifier.fillMaxWidth()) {
            Text(text = "Go to Profile")
        }
    }
}