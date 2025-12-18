package com.example.sevamitra

data class Service(
    val id: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val category: String = "",
    val title: String = "",
    val area: String = "",
    val priceRange: String = "",
    val rating: Float = 0f,
    val phone: String = "",
    val allowCall: Boolean = true,
    val allowWhatsapp: Boolean = true,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
