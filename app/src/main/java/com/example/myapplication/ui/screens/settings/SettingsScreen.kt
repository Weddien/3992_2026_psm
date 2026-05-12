// ui/screens/settings/SettingsScreen.kt
package com.example.myapplication.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogoutClick: () -> Unit = {},
    onEnableAutofillClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Настройки") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Секция безопасность
            Text(
                text = "Безопасность",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ListItem(
                headlineContent = { Text("Настроить автозаполнение") },
                supportingContent = { Text("Включить автозаполнение паролей в приложениях") },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { onEnableAutofillClick() }
            )

            ListItem(
                headlineContent = { Text("Изменить мастер-пароль") },
                supportingContent = { Text("Обновить пароль для входа в приложение") },
                leadingContent = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { onChangePasswordClick() }
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            // Секция аккаунт
            Text(
                text = "Аккаунт",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ListItem(
                headlineContent = { Text("Выйти из аккаунта") },
                supportingContent = { Text("Вы будете перенаправлены на экран входа") },
                leadingContent = { Icon(Icons.Default.Logout, contentDescription = null) },
                modifier = Modifier.clickable { showLogoutDialog = true }
            )

            ListItem(
                headlineContent = { Text("Удалить аккаунт") },
                supportingContent = { Text("Все данные будут безвозвратно удалены") },
                leadingContent = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                modifier = Modifier.clickable { showDeleteDialog = true }
            )

            // Информация о приложении
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Password Manager v1.0.0",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    // Диалог выхода
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Выход из аккаунта") },
            text = { Text("Вы уверены, что хотите выйти?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogoutClick()
                }) {
                    Text("Выйти")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    // Диалог удаления
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удаление аккаунта") },
            text = { Text("Это действие нельзя отменить. Все ваши пароли будут удалены.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteAccountClick()
                }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}