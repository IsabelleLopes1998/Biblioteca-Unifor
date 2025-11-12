package com.example.bbunifor.model

data class Home(
    val codigoLivro: String,
    val isLoading: Boolean = false,
    val error: String? = null,
    val livro: Book? = null
)
