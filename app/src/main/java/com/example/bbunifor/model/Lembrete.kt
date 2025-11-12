package com.example.bbunifor.model

import java.util.Date

data class Lembrete(
    val id: String? = null,
    val livro: Book,
    val dataEntrega: Long, // Timestamp em milissegundos
    val userId: String? = null, // ID do usuário autenticado
    val notificadoUmDiaAntes: Boolean = false,
    val notificadoNoDia: Boolean = false
)


