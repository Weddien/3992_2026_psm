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
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val coroutineScope = rememberCoroutineScope()
    val client = remember { HttpClient() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Регистрация") })
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

            // Кнопка регистрации
            AppButton(
                text = "Зарегистрироваться",
                onClick = {
                    if (password != confirmPassword) {
                        passwordError = "Пароли не совпадают"
                        return@AppButton
                    }

                    coroutineScope.launch {
                        isLoading = true
                        error = null
                        try {
                            // 1. Регистрация в Firebase
                            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                            val token = authResult.user?.getIdToken(false)?.await()?.token

                            if (token != null) {
                                // 2. Регистрация на сервере
                                val response: HttpResponse = client.post("http://10.0.2.2:8080/api/auth/register") {
                                    header("Authorization", "Bearer $token")
                                    contentType(ContentType.Application.Json)
                                }

                                if (response.status == HttpStatusCode.OK) {
                                    onRegisterSuccess()
                                } else {
                                    error = "Ошибка регистрации на сервере"
                                }
                            }
                        } catch (e: Exception) {
                            error = when {
                                e.message?.contains("email already in use") == true ->
                                    "Email уже используется"
                                e.message?.contains("weak password") == true ->
                                    "Пароль слишком слабый"
                                else -> "Ошибка: ${e.message}"
                            }
                        } finally {
                            isLoading = false
                        }
                    }
                },
                isLoading = isLoading,
                enabled = email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank() && !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ссылка на вход
            TextButton(onClick = onNavigateToLogin) {
                Text("Уже есть аккаунт? Войти")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}