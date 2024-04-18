package com.example.parkingcompose


import ReviewViewModel
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
import com.example.parkingcompose.dao.ReviewDaoImpl
import com.example.parkingcompose.data.LocationRepository
import com.example.parkingcompose.data.MapViewModelFactory
import com.example.parkingcompose.screens.CreateParkingScreen
import com.example.parkingcompose.screens.CreateReviewScreen
import com.example.parkingcompose.screens.ForgotPasswordScreen
import com.example.parkingcompose.screens.ListReviewScreen
import com.example.parkingcompose.screens.LoginScreen
import com.example.parkingcompose.screens.MapScreen
import com.example.parkingcompose.screens.ModerateScreen
import com.example.parkingcompose.screens.ParkingDetailsScreen
import com.example.parkingcompose.screens.ParkingListScreen
import com.example.parkingcompose.screens.ProfileScreen
import com.example.parkingcompose.screens.RegisterScreen
import com.example.parkingcompose.screens.SelectLocationScreen
import com.example.parkingcompose.screens.UpdateUsernameScreen
import com.example.parkingcompose.ui.theme.DaleComposeTheme
import com.example.parkingcompose.util.GoogleAuthUiClient
import com.example.parkingcompose.viewmodels.CreateParkingViewModel
import com.example.parkingcompose.viewmodels.LoginMailViewModel
import com.example.parkingcompose.viewmodels.MapViewModel
import com.example.parkingcompose.viewmodels.ModerateViewModel
import com.example.parkingcompose.viewmodels.ParkingViewModel
import com.example.parkingcompose.viewmodels.RegisterViewModel
import com.example.parkingcompose.viewmodels.SelectLocationViewModel
import com.example.parkingcompose.viewmodels.SignInGoogleViewModel
import com.example.parkingcompose.viewmodels.UpdateUsernameViewModel
import com.google.android.gms.auth.api.identity.Identity
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
            // Crear una instancia de ReviewDao
            val reviewDao = ReviewDaoImpl()

            // Crear una instancia de ReviewViewModel con la instancia de ReviewDao
            val reviewViewModel = ReviewViewModel(reviewDao)
            val signInViewModel: SignInGoogleViewModel by viewModels()
            val loginViewModel: LoginMailViewModel by viewModels()
            val parkingViewModel: ParkingViewModel by viewModels()
            val registerViewModel: RegisterViewModel by viewModels()
            val createParkingViewModel: CreateParkingViewModel by viewModels()
            val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()
            val updateUsernameViewModel: UpdateUsernameViewModel by viewModels()
            val selectLocationScreen: SelectLocationViewModel by viewModels()
            val moderateViewModel: ModerateViewModel by viewModels()
            val locationRepository = LocationRepository(this)


            val mapViewModel: MapViewModel by viewModels { MapViewModelFactory(locationRepository) }
            setContent {
                DaleComposeTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = "sign_in") {
                            composable("sign_in") {
                                val state by signInViewModel.state.collectAsStateWithLifecycle()

                                LaunchedEffect(key1 = Unit) {
                                    if (googleAuthUiClient.getSignedInUser() != null) {
                                        navController.navigate("mapa")
                                    }
                                }

                                val launcher = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                                    onResult = { result ->
                                        if (result.resultCode == RESULT_OK) {
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
                                    if (state.isSignInSuccessful) {

                                        Toast.makeText(
                                            applicationContext,
                                            "Sign in successful",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        RegisterViewModel().registerUser(googleAuthUiClient)
                                        navController.navigate("mapa")
                                        signInViewModel.resetState()
                                    }
                                }

                                LoginScreen(
                                    navHostController = navController,
                                    state = state,
                                    loginViewModel = loginViewModel,
                                    registerViewModel = registerViewModel,
                                    googleAuthUiClient = googleAuthUiClient,
                                    onLogin = { email, password ->
                                        lifecycleScope.launch {
                                            loginViewModel.login(this@MainActivity, email, password)
                                        }
                                    },
                                    onRegister = { navController.navigate("register") },
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

                                MapScreen(createParkingViewModel, mapViewModel, navController)
                            }

                            composable("parkingList") {
                                ParkingListScreen(
                                    parkingViewModel,
                                    createParkingViewModel,
                                    navController
                                )
                            }
                            composable("register") {
                                RegisterScreen(registerViewModel, navController, googleAuthUiClient)
                            }
                            composable("crearparking") {
                                CreateParkingScreen(
                                    createParkingViewModel,
                                    selectLocationScreen,
                                    navController
                                )
                            }
                            composable("forgotpassword") {
                                ForgotPasswordScreen(forgotPasswordViewModel)
                            }
                            composable("updateusername") {
                                UpdateUsernameScreen(updateUsernameViewModel, navController)
                            }
                            composable("parkingDetailsScreen") {
                                ParkingDetailsScreen(
                                    parkingId = it.arguments?.getString("parkingId") ?: "",
                                    navController,
                                    parkingViewModel
                                )
                            }
                            composable("selectLocation") { backStackEntry ->
                                SelectLocationScreen(selectLocationScreen, navController)
                            }
                            composable("moderate") {
                                ModerateScreen(parkingViewModel, navController)
                            }

                            composable("createReview") {
                                CreateReviewScreen(
                                    navController = navController,
                                    viewModel = reviewViewModel
                                )
                            }
                            composable("listReviews") {
                                ListReviewScreen(viewModel = reviewViewModel)
                            }
                        }
                    }
                }
            }
        }
    }

