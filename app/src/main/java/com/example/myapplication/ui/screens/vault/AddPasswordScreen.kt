package com.example.myapplication.ui.screens.vault

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import java.util.UUID

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class SyncPushRequest(
    val encryptedData: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPasswordScreen(
    onPasswordSaved: () -> Unit,
    onNavigateBack: () -> Unit,
    existingPasswords: List<PasswordEntry> = emptyList()
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val client = remember { HttpClient() }
    val coroutineScope = rememberCoroutineScope()
    val json = Json { ignoreUnknownKeys = true }

    fun generatePassword(): String {
        val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lower = "abcdefghijklmnopqrstuvwxyz"
        val digits = "0123456789"
        val special = "!@#$%^&*()_+-="
        val all = upper + lower + digits + special

        return buildString {
            append(upper.random())
            append(lower.random())
            append(digits.random())
            append(special.random())
            repeat(12) { append(all.random()) }
        }.toList().shuffled().joinToString("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить пароль") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL (сайт или приложение)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Логин / Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Пароль"
            )

            TextButton(onClick = { password = generatePassword() }) {
                Text("Сгенерировать надежный пароль")
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                text = "Сохранить",
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        error = null
                        try {
                            // Валидация
                            if (title.isBlank()) {
                                throw Exception("Введите название")
                            }
                            if (password.isBlank()) {
                                throw Exception("Введите пароль")
                            }

                            val newEntry = PasswordEntry(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                username = username,
                                url = url,
                                password = password
                            )

                            val updatedList = existingPasswords + newEntry

                            val encryptedData = json.encodeToString(
                                ListSerializer(PasswordEntry.serializer()),
                                updatedList
                            )

                            val token = auth.currentUser?.getIdToken(false)?.await()?.token
                                ?: throw Exception("Не авторизован")

                            val requestBody = SyncPushRequest(encryptedData = encryptedData)
                            val jsonBody = json.encodeToString(SyncPushRequest.serializer(), requestBody)

                            println("Sending request: $jsonBody")

                            val response: HttpResponse = client.post("http://10.0.2.2:8080/api/sync/push") {
                                header("Authorization", "Bearer $token")
                                contentType(ContentType.Application.Json)
                                setBody(jsonBody)
                            }

                            println("Response status: ${response.status}")
                            println("Response body: ${response.bodyAsText()}")

                            if (response.status == HttpStatusCode.OK) {
                                onPasswordSaved()
                            } else {
                                error = "Ошибка сохранения: ${response.status}"
                            }
                        } catch (e: Exception) {
                            error = "Ошибка: ${e.message}"
                            println("Error: ${e.message}")
                            e.printStackTrace()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                isLoading = isLoading,
                enabled = title.isNotBlank() && password.isNotBlank() && !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}