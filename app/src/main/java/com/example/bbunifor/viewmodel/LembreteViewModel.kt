package com.example.bbunifor.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bbunifor.model.Book
import com.example.bbunifor.model.Lembrete
import com.example.bbunifor.service.NotificationService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class LembreteViewModel(
    private val notificationService: NotificationService? = null
) : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val database = FirebaseDatabase.getInstance()

    private val _lembretes = MutableStateFlow<List<Lembrete>>(emptyList())
    val lembretes = _lembretes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun adicionarLembrete(livro: Book, dataEntrega: Long, service: NotificationService? = null) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _error.update { "Usuário não autenticado" }
            return
        }

        _isLoading.update { true }
        _error.update { null }

        viewModelScope.launch {
            try {
                val lembreteId = UUID.randomUUID().toString()
                val lembrete = Lembrete(
                    id = lembreteId,
                    livro = livro,
                    dataEntrega = dataEntrega,
                    userId = userId
                )

                // Converte para Map para salvar no Firebase
                val lembreteMap = hashMapOf<String, Any>(
                    "id" to lembreteId,
                    "dataEntrega" to dataEntrega,
                    "userId" to userId,
                    "notificadoUmDiaAntes" to false,
                    "notificadoNoDia" to false,
                    "livro" to hashMapOf(
                        "title" to (livro.title ?: ""),
                        "authors" to (livro.authors ?: emptyList()),
                        "description" to (livro.description ?: ""),
                        "publisher" to (livro.publisher ?: ""),
                        "pageCount" to (livro.pageCount ?: 0),
                        "thumbnail" to (livro.thumbnail ?: "")
                    )
                )

                val lembreteRef = database.reference
                    .child("lembretes")
                    .child(userId)
                    .child(lembreteId)

                Log.d("LembreteViewModel", "Salvando lembrete no Firebase: $lembreteMap")
                
                lembreteRef.setValue(lembreteMap)
                    .addOnSuccessListener {
                        Log.d("LembreteViewModel", "✅ Lembrete salvo com sucesso no Firebase!")
                        // Agenda notificações após salvar com sucesso
                        val serviceToUse = service ?: notificationService
                        serviceToUse?.agendarNotificacoes(lembrete)
                    }
                    .addOnFailureListener { e ->
                        Log.e("LembreteViewModel", "❌ Erro ao salvar lembrete: ${e.message}", e)
                    }
                    .await()

                carregarLembretes()

                _isLoading.update { false }
            } catch (e: Exception) {
                Log.e("LembreteViewModel", "❌ Exceção ao adicionar lembrete: ${e.message}", e)
                _error.update { "Erro ao adicionar lembrete: ${e.message}" }
                _isLoading.update { false }
            }
        }
    }

    fun carregarLembretes() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _lembretes.update { emptyList() }
            return
        }

        _isLoading.update { true }

        viewModelScope.launch {
            try {
                val snapshot = database.reference
                    .child("lembretes")
                    .child(userId)
                    .get()
                    .await()

                val lembretesList = snapshot.children.mapNotNull { child ->
                    val data = child.value as? Map<*, *>
                    if (data != null) {
                        try {
                            val livroMap = data["livro"] as? Map<*, *>
                            val livro = if (livroMap != null) {
                                Book(
                                    title = livroMap["title"] as? String ?: "",
                                    authors = (livroMap["authors"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                                    description = livroMap["description"] as? String,
                                    publisher = livroMap["publisher"] as? String,
                                    pageCount = (livroMap["pageCount"] as? Number)?.toInt(),
                                    thumbnail = livroMap["thumbnail"] as? String
                                )
                            } else {
                                null
                            }

                            if (livro != null) {
                                Lembrete(
                                    id = data["id"] as? String,
                                    livro = livro,
                                    dataEntrega = (data["dataEntrega"] as? Number)?.toLong() ?: 0L,
                                    userId = data["userId"] as? String,
                                    notificadoUmDiaAntes = data["notificadoUmDiaAntes"] as? Boolean ?: false,
                                    notificadoNoDia = data["notificadoNoDia"] as? Boolean ?: false
                                )
                            } else {
                                null
                            }
                        } catch (e: Exception) {
                            null
                        }
                    } else {
                        null
                    }
                }

                _lembretes.update { lembretesList }
                _isLoading.update { false }
            } catch (e: Exception) {
                _error.update { "Erro ao carregar lembretes: ${e.message}" }
                _isLoading.update { false }
            }
        }
    }

    fun removerLembrete(lembreteId: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) return

        viewModelScope.launch {
            try {
                database.reference
                    .child("lembretes")
                    .child(userId)
                    .child(lembreteId)
                    .removeValue()
                    .await()

                notificationService?.cancelarNotificacoes(lembreteId)

                carregarLembretes()
            } catch (e: Exception) {
                _error.update { "Erro ao remover lembrete: ${e.message}" }
            }
        }
    }

    init {
        carregarLembretes()
    }
}

