package com.example.bbunifor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bbunifor.datasource.BookApiDataSource
import com.example.bbunifor.model.Home
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val bookApiDataSource: BookApiDataSource = BookApiDataSource()
) : ViewModel() {
    
    val _homeState = MutableStateFlow(
        Home(
            codigoLivro = ""
        )
    )
    val homeState = _homeState.asStateFlow()

    fun updateCodigo(codigoLivro: String) {
        this._homeState.update { 
            it.copy(
                codigoLivro = codigoLivro,
                error = null  // Limpa erro ao digitar
            ) 
        }
    }

    fun buscarLivro() {
        val isbn = _homeState.value.codigoLivro.trim()
        
        if (isbn.isEmpty()) {
            _homeState.update { 
                it.copy(error = "Por favor, digite um código ISBN")
            }
            return
        }

        _homeState.update { 
            it.copy(
                isLoading = true,
                error = null,
                livro = null
            )
        }

        viewModelScope.launch {
            bookApiDataSource.getBookByISBN(isbn)
                .onSuccess { book ->
                    _homeState.update {
                        it.copy(
                            isLoading = false,
                            livro = book,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    _homeState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Erro ao buscar livro",
                            livro = null
                        )
                    }
                }
        }
    }
}