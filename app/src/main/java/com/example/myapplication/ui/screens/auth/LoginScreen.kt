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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Вход") })
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
                text = "Password Manager",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Войдите в аккаунт для доступа к паролям",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                onValueChange = { password = it }
            )

            // Ошибка
            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопка входа
            AppButton(
                text = "Войти",
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        error = null
                        try {
                            // Вход через Firebase
                            auth.signInWithEmailAndPassword(email, password).await()
                            onLoginSuccess()
                        } catch (e: Exception) {
                            error = when {
                                e.message?.contains("no user record") == true ->
                                    "Пользователь не найден"
                                e.message?.contains("wrong password") == true ->
                                    "Неверный пароль"
                                e.message?.contains("invalid email") == true ->
                                    "Некорректный email"
                                else -> "Ошибка входа: ${e.message}"
                            }
                        } finally {
                            isLoading = false
                        }
                    }
                },
                isLoading = isLoading,
                enabled = email.isNotBlank() && password.isNotBlank() && !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ссылка на регистрацию
            TextButton(onClick = onNavigateToRegister) {
                Text("Нет аккаунта? Зарегистрироваться")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}