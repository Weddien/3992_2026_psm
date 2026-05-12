// ui/screens/auth/RegisterScreen.kt
package com.example.myapplication.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.AppButton
import com.example.myapplication.ui.components.AppPasswordField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterClick: (email: String, password: String, confirmPassword: String) -> Unit = { _, _, _ -> },
    onLoginClick: () -> Unit = {},
    isLoading: Boolean = false,
    error: String? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Регистрация") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Создать аккаунт",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Поле email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Поле пароля
            AppPasswordField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = "Пароль"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Подтверждение пароля
            AppPasswordField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    passwordError = null
                },
                label = "Подтвердите пароль",
                isError = passwordError != null,
                errorMessage = passwordError
            )

            // Общая ошибка
            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопка регистрации
            AppButton(
                text = "Зарегистрироваться",
                onClick = {
                    if (password != confirmPassword) {
                        passwordError = "Пароли не совпадают"
                    } else {
                        onRegisterClick(email, password, confirmPassword)
                    }
                },
                isLoading = isLoading,
                enabled = email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ссылка на вход
            TextButton(
                onClick = onLoginClick
            ) {
                Text("Уже есть аккаунт? Войти")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}