// ui/screens/vault/VaultScreen.kt
package com.example.myapplication.ui.screens.vault

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.PasswordItemCard

// Заглушка данных
data class PasswordEntry(
    val id: String,
    val title: String,
    val username: String,
    val url: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    passwords: List<PasswordEntry> = emptyList(),
    onAddPasswordClick: () -> Unit = {},
    onPasswordClick: (PasswordEntry) -> Unit = {},
    onLockClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои пароли") },
                actions = {
                    IconButton(onClick = onLockClick) {
                        Icon(Icons.Default.Lock, contentDescription = "Заблокировать")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPasswordClick
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить пароль")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Поиск
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Поиск паролей...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                singleLine = true
            )

            // Список паролей
            if (passwords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет сохраненных паролей",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(passwords) { password ->
                        PasswordItemCard(
                            title = password.title,
                            subtitle = password.username,
                            onClick = { onPasswordClick(password) }
                        )
                    }
                }
            }
        }
    }
}