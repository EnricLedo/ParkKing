package com.example.parkingcompose.navegacion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.parkingcompose.R
import com.example.parkingcompose.ui.theme.BlueGreyDark
import com.example.parkingcompose.ui.theme.Orange
import com.example.parkingcompose.ui.theme.OrangeDark

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),  // Ajusta este valor a la altura que prefieras
        containerColor = OrangeDark,
        contentColor = BlueGreyDark
    ) {
        val destinations = listOf(
            Pair("parkingList", painterResource(id = R.drawable.ic_pk_list)),
            Pair("mapa", painterResource(id = R.drawable.ic_map)),
            Pair("profile", painterResource(id = R.drawable.ic_profile))
        )

        destinations.forEach { destination ->
            NavigationBarItem(
                selected = navController.currentDestination?.route == destination.first,
                onClick = { navController.navigate(destination.first) },
                icon = {
                    Image(
                        painter = destination.second,
                        contentDescription = "Go to ${destination.first.capitalize()}"
                    )
                }
            )
        }
    }
}