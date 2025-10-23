package com.example.autoapply.model
import androidx.annotation.StringRes
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val email: String,
    val password: String,
)
