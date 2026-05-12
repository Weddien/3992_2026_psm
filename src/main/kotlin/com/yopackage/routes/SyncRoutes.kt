package com.yopackage.routes

import com.yopackage.auth.FirebasePrincipal
import com.yopackage.models.Users
import com.yopackage.models.Vaults
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

@Serializable
data class VaultResponse(
    val encryptedData: String?,
    val updatedAt: String?
)

@Serializable
data class PushRequest(
    val encryptedData: String
)

@Serializable
data class StatusResponse(
    val success: Boolean,
    val error: String? = null
)

fun Route.syncRoutes() {
    authenticate("firebase-auth") {
        route("/api/sync") {

            post("/pull") {
                val principal = call.principal<FirebasePrincipal>()!!
                val firebaseUid = principal.uid

                val result = try {
                    val vaultData = transaction {
                        val existingUser = Users.select { Users.firebaseUid eq firebaseUid }.singleOrNull()
                        val userRow = if (existingUser == null) {
                            Users.insert {
                                it[Users.firebaseUid] = firebaseUid
                                it[Users.email] = principal.email
                                it[Users.createdAt] = LocalDateTime.now()
                            }
                            Users.select { Users.firebaseUid eq firebaseUid }.single()
                        } else {
                            existingUser
                        }

                        val userId = userRow[Users.id]
                        val vaultRow = Vaults.select { Vaults.userId eq userId }.singleOrNull()

                        if (vaultRow != null) {
                            VaultResponse(
                                encryptedData = vaultRow[Vaults.encryptedData],
                                updatedAt = vaultRow[Vaults.updatedAt].toString()
                            )
                        } else {
                            VaultResponse(null, null)
                        }
                    }
                    vaultData
                } catch (e: Exception) {
                    StatusResponse(false, "Failed to pull: ${e.message}")
                }

                call.respond(result)
            }

            post("/push") {
                val principal = call.principal<FirebasePrincipal>()!!
                val firebaseUid = principal.uid
                val request = call.receive<PushRequest>()

                val result = try {
                    transaction {
                        val existingUser = Users.select { Users.firebaseUid eq firebaseUid }.singleOrNull()
                        val userRow = if (existingUser == null) {
                            Users.insert {
                                it[Users.firebaseUid] = firebaseUid
                                it[Users.email] = principal.email
                                it[Users.createdAt] = LocalDateTime.now()
                            }
                            Users.select { Users.firebaseUid eq firebaseUid }.single()
                        } else {
                            existingUser
                        }

                        val userId = userRow[Users.id]
                        val existingVault = Vaults.select { Vaults.userId eq userId }.singleOrNull()

                        if (existingVault != null) {
                            Vaults.update({ Vaults.userId eq userId }) {
                                it[Vaults.encryptedData] = request.encryptedData
                                it[Vaults.updatedAt] = LocalDateTime.now()
                            }
                        } else {
                            Vaults.insert {
                                it[Vaults.userId] = userId
                                it[Vaults.encryptedData] = request.encryptedData
                                it[Vaults.updatedAt] = LocalDateTime.now()
                            }
                        }
                    }
                    StatusResponse(true)
                } catch (e: Exception) {
                    StatusResponse(false, "Failed to push: ${e.message}")
                }

                call.respond(result)
            }
        }
    }
}