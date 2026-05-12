package com.yopackage.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Users : Table("users") {
    val id = uuid("id").autoGenerate()
    val firebaseUid = varchar("firebase_uid", 128).uniqueIndex()
    val email = varchar("email", 256).nullable()
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

object Vaults : Table("vaults") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id).uniqueIndex()
    val encryptedData = text("encrypted_data")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}