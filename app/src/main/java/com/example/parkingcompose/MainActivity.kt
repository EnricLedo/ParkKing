package com.example.parkingcompose


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.parkingcompose.ui.theme.DaleComposeTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DaleComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(

                    color = MaterialTheme.colorScheme.background
                ) {
                    GreetingImage()
                }
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        this.moveTaskToBack(true)
    }
}



@Composable
fun GreetingImage(modifier: Modifier = Modifier){
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
                        startActivity(localContext, intent, null)
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
                        startActivity(localContext, intent, null)
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
                        startActivity(localContext, intent, null)
                    }
            )
        }
    }
}




