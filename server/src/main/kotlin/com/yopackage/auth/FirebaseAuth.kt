package com.yopackage.auth

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.io.File

data class FirebasePrincipal(val uid: String, val email: String?) : Principal

fun Application.installSecurity() {
    try {
        val serviceAccountPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
            ?: "firebase-service-account.json"

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(File(serviceAccountPath).inputStream()))
            .build()

        FirebaseApp.initializeApp(options)
    } catch (e: Exception) {
        println("Ошибка: Firebase не инициализирован - ${e.message}")
    }

    install(Authentication) {
        bearer("firebase-auth") {
            realm = "Password Manager API"

            authenticate { tokenCredential ->
                try {
                    val token = tokenCredential.token
                    val decodedToken = FirebaseAuth.getInstance().verifyIdTokenAsync(token).get()

                    if (decodedToken != null) {
                        FirebasePrincipal(decodedToken.uid, decodedToken.email)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}