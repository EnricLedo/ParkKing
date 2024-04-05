package com.example.parkingcompose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.parkingcompose.ui.theme.DaleComposeTheme

class ParkingListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DaleComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    myParkingList()
                }
            }
        }
    }

}
@Preview
@Composable
fun myParkingList() {

    var counter by remember { mutableStateOf(0) }
    val localContext = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                //val intent = Intent(localContext, CreateParkking::class.java)
                //ContextCompat.startActivity(localContext, intent, null)
            },
            modifier = Modifier.width(200.dp).height(40.dp).align(Alignment.CenterHorizontally)
        ) {
            Text("Add new Parkking")

        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            item{
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(id = R.drawable.parking_ninot),
                    contentDescription = "Parking Ninot"
                )

                Row( modifier = Modifier.padding(top = 8.dp)) {
                    Image(
                        modifier = Modifier.clickable { counter++},
                        contentDescription = "Icono likes",
                        painter = painterResource(id = R.drawable.baseline_favorite_24)

                    )
                    Text(text = counter.toString())
                    Text("Parking Ninot")
                }
            }
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
        ,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = { navController.navigate("profile") },
            modifier = Modifier.weight(1f)) {
            Text(text = "Go to Profile")
        }

        Button(onClick = { navController.navigate("mapa") },
            modifier = Modifier.weight(1f)) {
            Text(text = "Go Map")
        }
        Button(onClick = { navController.navigate("parkingList") },
            modifier = Modifier.weight(1f)) {
            Text(text = "Go Parkings")
        }
    }
}