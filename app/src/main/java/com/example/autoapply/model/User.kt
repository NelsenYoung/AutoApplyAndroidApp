package com.example.autoapply.model

data class User(
    val id: Int,
    val createdAt: java.time.OffsetDateTime,
    val email: String,
    val password: String,
)
