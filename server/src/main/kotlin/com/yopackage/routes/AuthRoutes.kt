package com.yopackage.routes

import com.yopackage.auth.FirebasePrincipal
import com.yopackage.models.Users
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

@Serializable
data class UserInfo(
    val uid: String,
    val email: String?,
    val createdAt: String
)

@Serializable
data class AuthStatus(
    val registered: Boolean,
    val user: UserInfo? = null,
    val error: String? = null
)

fun Route.authRoutes() {
    authenticate("firebase-auth") {

        // Регистрация нового пользователя
        post("/api/auth/register") {
            val principal = call.principal<FirebasePrincipal>()!!

            val result = try {
                transaction {
                    val existingUser = Users.select { Users.firebaseUid eq principal.uid }.singleOrNull()

                    if (existingUser != null) {
                        AuthStatus(
                            registered = false,
                            error = "User already exists"
                        )
                    } else {
                        val now = LocalDateTime.now()
                        Users.insert {
                            it[Users.firebaseUid] = principal.uid
                            it[email] = principal.email
                            it[createdAt] = now
                        }

                        AuthStatus(
                            registered = true,
                            user = UserInfo(
                                uid = principal.uid,
                                email = principal.email,
                                createdAt = now.toString()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                AuthStatus(
                    registered = false,
                    error = "Registration failed: ${e.message}"
                )
            }

            call.respond(result)
        }

        // Проверка статуса пользователя
        get("/api/auth/me") {
            val principal = call.principal<FirebasePrincipal>()!!

            val result = try {
                transaction {
                    val user = Users.select { Users.firebaseUid eq principal.uid }.singleOrNull()

                    if (user != null) {
                        AuthStatus(
                            registered = true,
                            user = UserInfo(
                                uid = principal.uid,
                                email = principal.email,
                                createdAt = user[Users.createdAt].toString()
                            )
                        )
                    } else {
                        AuthStatus(registered = false)
                    }
                }
            } catch (e: Exception) {
                AuthStatus(
                    registered = false,
                    error = "Failed to check status: ${e.message}"
                )
            }

            call.respond(result)
        }
    }
}