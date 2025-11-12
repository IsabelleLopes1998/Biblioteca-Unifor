package com.example.bbunifor.model


data class Book(
    val title: String,
    val authors: List<String>,
    val description: String?,
    val publisher: String?,
    val pageCount: Int?,
    val thumbnail: String? // URL da capa (pode ser null)
)

