// ui/screens/biometric/BiometricPromptScreen.kt
package com.example.myapplication.ui.screens.biometric

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.AppButton

@Composable
fun BiometricPromptScreen(
    onBiometricSuccess: () -> Unit = {},
    onUsePinCode: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Иконка биометрии
        Icon(
            imageVector = Icons.Default.Fingerprint,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Подтвердите личность",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Используйте отпечаток пальца или Face ID для разблокировки доступа к паролям",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Кнопка биометрии (заглушка)
        AppButton(
            text = "Использовать биометрию",
            onClick = onBiometricSuccess
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Альтернативный вход
        OutlinedButton(
            onClick = onUsePinCode,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.LockOpen,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Использовать PIN-код")
        }
    }
}