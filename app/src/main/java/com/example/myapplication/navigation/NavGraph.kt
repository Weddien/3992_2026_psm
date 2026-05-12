package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.auth.LoginScreen
import com.example.myapplication.ui.screens.auth.RegisterScreen
import com.example.myapplication.ui.screens.biometric.BiometricPromptScreen
import com.example.myapplication.ui.screens.vault.VaultScreen
import com.example.myapplication.ui.screens.vault.AddPasswordScreen
import com.example.myapplication.ui.screens.settings.SettingsScreen
import com.example.myapplication.ui.screens.vault.PasswordEntry

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
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = { _, _ ->
                    navController.navigate(Routes.BIOMETRIC)
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterClick = { _, _, _ ->
                    navController.navigate(Routes.LOGIN)
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.BIOMETRIC) {
            BiometricPromptScreen(
                onBiometricSuccess = {
                    navController.navigate(Routes.VAULT)
                },
                onUsePinCode = {
                    navController.navigate(Routes.VAULT)
                }
            )
        }

        composable(Routes.VAULT) {
            VaultScreen(
                passwords = listOf(
                    PasswordEntry("1", "Google", "user@gmail.com", "google.com"),
                    PasswordEntry("2", "GitHub", "dev", "github.com")
                ),
                onAddPasswordClick = {
                    navController.navigate(Routes.ADD_PASSWORD)
                },
                onPasswordClick = { _ -> },
                onLockClick = {
                    navController.navigate(Routes.BIOMETRIC)
                }
            )
        }

        composable(Routes.ADD_PASSWORD) {
            AddPasswordScreen(
                onSaveClick = { _, _, _, _ ->
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onLogoutClick = {
                    navController.navigate(Routes.LOGIN)
                },
                onEnableAutofillClick = {},
                onChangePasswordClick = {},
                onDeleteAccountClick = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }
    }
}