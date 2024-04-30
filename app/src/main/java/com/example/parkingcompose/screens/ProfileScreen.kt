package com.example.parkingcompose.screens

import com.example.parkingcompose.R
import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.parkingcompose.dao.UserDao
import com.example.parkingcompose.model.UserData
import com.example.parkingcompose.navegacion.BottomNavigationBar
import com.example.parkingcompose.ui.theme.ButtonTextStyle
import com.example.parkingcompose.ui.theme.DaleComposeTheme
import com.example.parkingcompose.viewmodels.LanguageViewModel
import com.example.parkingcompose.viewmodels.ProfileScreenViewModel
import com.example.parkingcompose.viewmodels.UpdateUsernameViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavHostController,
    username: String,
    userDao: UserDao
) {
    var userIsAdmin by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        userDao.userIsAdmin {
            userIsAdmin = it
        }
    }

    DaleComposeTheme {
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(bottom = 82.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(6.dp,8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onSignOut,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logout),
                            contentDescription = "Logout icon",
                            tint = Color.Black
                        )
                        Text(text = stringResource(id = R.string.logout), style = ButtonTextStyle, color = Color.Black)
                    }
                }
                Spacer(modifier = Modifier.height(56.dp))

                val painter = if (userData?.profilePictureUrl != null) {
                    rememberImagePainter(data = userData.profilePictureUrl)
                } else {
                    painterResource(id = R.drawable.defaultprofile)
                }

                Image(
                    painter = painter,
                    contentDescription = stringResource(id = R.string.profile_picture),
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = username,
                    textAlign = TextAlign.Center,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(26.dp))
                if(userIsAdmin) {
                    Button(
                        onClick = { navController.navigate("tagsscreen") },
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text(stringResource(id = R.string.manage_tags), style = ButtonTextStyle)
                    }
                    Spacer(modifier = Modifier.height(36.dp))

                    Button(onClick = { navController.navigate("moderate") }) {
                        Text(
                            text = stringResource(id = R.string.moderate_parkings),
                            style = ButtonTextStyle
                        )
                    }
                    Spacer(modifier = Modifier.height(36.dp))
                }
                Button(onClick = { navController.navigate("updateusername") }){
                    Text(text = stringResource(id = R.string.change_username), style = ButtonTextStyle)
                }

            }
        }
    }
}
