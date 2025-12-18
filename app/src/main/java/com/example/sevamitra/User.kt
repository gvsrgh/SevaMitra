package com.example.sevamitra

data class User(
    val name: String = "",
    val email: String = "",
    val role: String = "", // SEEKER or PARTNER
    val createdAt: Long = System.currentTimeMillis()
)
