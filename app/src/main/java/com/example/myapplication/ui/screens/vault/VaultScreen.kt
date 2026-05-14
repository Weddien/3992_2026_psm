package com.example.myapplication.ui.screens.vault

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.PasswordItemCard
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class VaultResponse(
    val encryptedData: String?,
    val updatedAt: String?
)

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class PasswordEntry(
    val id: String,
    val title: String,
    val username: String,
    val url: String,
    val password: String = ""
)

private val json = Json { ignoreUnknownKeys = true }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    onNavigateToAddPassword: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLock: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var passwords by remember { mutableStateOf<List<PasswordEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedPassword by remember { mutableStateOf<PasswordEntry?>(null) }

    val auth = FirebaseAuth.getInstance()
    val client = remember { HttpClient() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val token = auth.currentUser?.getIdToken(false)?.await()?.token
            if (token != null) {
                val response: HttpResponse = client.post("http://10.0.2.2:8080/api/sync/pull") {
                    header("Authorization", "Bearer $token")
                }
                if (response.status == HttpStatusCode.OK) {
                    val body = response.bodyAsText()
                    val vaultResponse = json.decodeFromString<VaultResponse>(body)
                    if (vaultResponse.encryptedData != null) {
                        val jsonString = String(
                            android.util.Base64.decode(vaultResponse.encryptedData, android.util.Base64.NO_WRAP)
                        )
                        passwords = json.decodeFromString(jsonString)
                    }
                }
            }
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои пароли") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                    IconButton(onClick = onLock) {
                        Icon(Icons.Default.Lock, contentDescription = "Заблокировать")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddPassword) {
                Icon(Icons.Default.Add, contentDescription = "Добавить пароль")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Поиск паролей...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (passwords.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Нет сохраненных паролей", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                val filtered = passwords.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                            it.username.contains(searchQuery, ignoreCase = true)
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered) { password ->
                        PasswordItemCard(
                            title = password.title,
                            subtitle = password.username,
                            onClick = { selectedPassword = password }
                        )
                    }
                }
            }
        }
    }

    selectedPassword?.let { entry ->
        AlertDialog(
            onDismissRequest = { selectedPassword = null },
            title = { Text(entry.title) },
            text = {
                Column {
                    Text("Логин: ${entry.username}")
                    Text("URL: ${entry.url}")
                    Text("Пароль: ${entry.password}")
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedPassword = null }) {
                    Text("Копировать пароль")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPassword = null }) {
                    Text("Закрыть")
                }
            }
        )
    }
}