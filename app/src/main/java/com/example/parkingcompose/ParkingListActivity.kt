package com.example.parkingcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .padding(16.dp)
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