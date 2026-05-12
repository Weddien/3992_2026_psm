// ui/screens/vault/AddPasswordScreen.kt
package com.example.myapplication.ui.screens.vault

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.AppButton
import com.example.myapplication.ui.components.AppPasswordField
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPasswordScreen(
    onSaveClick: (title: String, url: String, username: String, password: String) -> Unit = { _, _, _, _ -> },
    onBackClick: () -> Unit = {},
    initialData: PasswordEntry? = null
) {
    var title by remember { mutableStateOf(initialData?.title ?: "") }
    var url by remember { mutableStateOf(initialData?.url ?: "") }
    var username by remember { mutableStateOf(initialData?.username ?: "") }
    var password by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    // Генерация пароля (заглушка)
    fun generatePassword(): String {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()"
        return (1..16).map { chars.random() }.joinToString("")
    }

    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(2000)
            showSuccess = false
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (initialData != null) "Редактировать" else "Добавить пароль") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Название
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // URL
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL (сайт или приложение)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Логин
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Логин / Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Пароль с генератором
                AppPasswordField(
                    value = password,
                    onValueChange = { password =    it },
                    label = "Пароль"
                )

                // Кнопка генерации
                TextButton(
                    onClick = { password = generatePassword() }
                ) {
                    Text("Сгенерировать надежный пароль")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Кнопка сохранения
                AppButton(
                    text = "Сохранить",
                    onClick = { onSaveClick(title, url, username, password) },
                    enabled = title.isNotBlank() && password.isNotBlank()
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Сообщение об успехе
            if (showSuccess) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("Пароль сохранен!")
                }
            }
        }
    }
}