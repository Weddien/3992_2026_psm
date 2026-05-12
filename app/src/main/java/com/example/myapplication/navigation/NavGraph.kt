package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.auth.LoginScreen
import com.example.myapplication.ui.screens.auth.RegisterScreen
import com.example.myapplication.ui.screens.biometric.BiometricPromptScreen
import com.example.myapplication.ui.screens.vault.VaultScreen
import com.example.myapplication.ui.screens.vault.AddPasswordScreen
import com.example.myapplication.ui.screens.settings.SettingsScreen
import com.google.firebase.auth.FirebaseAuth

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val BIOMETRIC = "biometric"
    const val VAULT = "vault"
    const val ADD_PASSWORD = "add_password"
    const val SETTINGS = "settings"
}

@Composable
fun NavGraph(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val isLoggedIn = remember { mutableStateOf(auth.currentUser != null) }

    // Определяем стартовый экран
    val startDestination = if (isLoggedIn.value) Routes.BIOMETRIC else Routes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    isLoggedIn.value = true
                    navController.navigate(Routes.BIOMETRIC) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.BIOMETRIC) {
            BiometricPromptScreen(
                onBiometricSuccess = {
                    navController.navigate(Routes.VAULT) {
                        popUpTo(Routes.BIOMETRIC) { inclusive = true }
                    }
                },
                onLogout = {
                    auth.signOut()
                    isLoggedIn.value = false
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.VAULT) {
            VaultScreen(
                onNavigateToAddPassword = {
                    navController.navigate(Routes.ADD_PASSWORD)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onLock = {
                    navController.navigate(Routes.BIOMETRIC) {
                        popUpTo(Routes.BIOMETRIC) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ADD_PASSWORD) {
            AddPasswordScreen(
                onPasswordSaved = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onLogout = {
                    auth.signOut()
                    isLoggedIn.value = false
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEnableAutofill = {
                    // Открыть настройки автозаполнения
                }
            )
        }
    }
}