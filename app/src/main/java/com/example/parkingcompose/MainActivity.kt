package com.example.parkingcompose

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parkingcompose.data.LocationRepository
import com.example.parkingcompose.data.MapViewModelFactory
import com.example.parkingcompose.domain.GoogleAuthUiClient
import com.example.parkingcompose.screens.CrearParkingScreen
import com.example.parkingcompose.screens.ForgotPasswordScreen
import com.example.parkingcompose.screens.LoginScreen
import com.example.parkingcompose.screens.MapScreen
import com.example.parkingcompose.screens.ParkingListScreen
import com.example.parkingcompose.viewmodels.SignInGoogleViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.example.parkingcompose.screens.ProfileScreen
import com.example.parkingcompose.screens.RegisterScreen
import com.example.parkingcompose.screens.ReviewScreen
import com.example.parkingcompose.ui.theme.DaleComposeTheme
import com.example.parkingcompose.viewmodels.CrearParkingViewModel
import com.example.parkingcompose.viewmodels.LoginMailViewModel
import com.example.parkingcompose.viewmodels.MapViewModel
import com.example.parkingcompose.viewmodels.ParkingViewModel
import com.example.parkingcompose.viewmodels.RegisterViewModel
import com.example.parkingcompose.viewmodels.ReviewViewModel

import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val signInViewModel: SignInGoogleViewModel by viewModels()
        val loginViewModel: LoginMailViewModel by viewModels()
        val parkingViewModel: ParkingViewModel by viewModels()
        val registerViewModel: RegisterViewModel by viewModels()
        val crearParkingViewModel: CrearParkingViewModel by viewModels()
        val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()
        val locationRepository = LocationRepository(this)
        val reviewViewModel: ReviewViewModel by viewModels()
        val mapViewModel: MapViewModel by viewModels { MapViewModelFactory(locationRepository) }
        setContent {
            DaleComposeTheme{
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "sign_in") {
                        composable("sign_in") {
                            val state by signInViewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = Unit) {
                                if(googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate("mapa")
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if(result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            signInViewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )

                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if(state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("mapa")
                                    signInViewModel.resetState()
                                }
                            }

                            LoginScreen(
                                navHostController = navController,
                                state = state,
                                loginViewModel = loginViewModel,
                                onLogin = { email, password ->
                                    lifecycleScope.launch {
                                        loginViewModel.login(this@MainActivity, email, password)
                                    }
                                }

                            ,
                                onRegister = {navController.navigate("register")},
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )


                        }
                        composable("profile") {
                            ProfileScreen(
                                userData = googleAuthUiClient.getSignedInUser(),
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Signed out",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        navController.navigate("sign_in")
                                    }
                                },
                                navController = navController
                            )
                        }

                        composable("mapa") {

                            MapScreen(mapViewModel,navController)
                        }

                        composable("parkingList") {
                            ParkingListScreen(parkingViewModel,navController)
                        }
                        composable("register"){
                            RegisterScreen(registerViewModel,navController)
                        }
                        composable("crearparking"){
                            CrearParkingScreen(crearParkingViewModel)
                        }
                        composable("forgotpassword"){
                            ForgotPasswordScreen(forgotPasswordViewModel)
                        }
                        composable("review"){
                            ReviewScreen(reviewViewModel)
                        }
                    }
                }
            }
        }
    }
}
