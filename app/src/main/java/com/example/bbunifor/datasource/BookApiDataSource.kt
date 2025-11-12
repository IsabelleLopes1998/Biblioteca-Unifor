package com.example.bbunifor.datasource

import com.example.bbunifor.datasource.entity.toBook
import com.example.bbunifor.datasource.retrofit.BookApiService
import com.example.bbunifor.model.Book
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BookApiDataSource {

    private val apiKey: String by lazy {
        try {

            val buildConfigClass = Class.forName("com.example.bbunifor.BuildConfig")
            val apiKeyField = buildConfigClass.getField("GOOGLE_BOOKS_API_KEY")
            apiKeyField.get(null) as? String ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private val bookApiService: BookApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookApiService = retrofit.create(BookApiService::class.java)
    }

    suspend fun getBookByISBN(isbn: String): Result<Book> {
        return try {
            val query = "isbn:$isbn"
            val response = bookApiService.currentByISBN(query, apiKey)
            
            android.util.Log.d("BookApiDataSource", "Response code: ${response.code()}")
            android.util.Log.d("BookApiDataSource", "Response isSuccessful: ${response.isSuccessful}")
            
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                android.util.Log.d("BookApiDataSource", "Total items: ${apiResponse.totalItems}")

                if (apiResponse.totalItems != null && apiResponse.totalItems!! > 0 && apiResponse.items != null && apiResponse.items!!.isNotEmpty()) {
                    val volumeItem = apiResponse.items!![0]
                    android.util.Log.d("BookApiDataSource", "VolumeItem: $volumeItem")
                    
                    val volumeInfo = volumeItem.volumeInfo
                    val volumeId = volumeItem.id
                    android.util.Log.d("BookApiDataSource", "VolumeInfo: $volumeInfo")
                    android.util.Log.d("BookApiDataSource", "VolumeId: $volumeId")
                    
                    if (volumeInfo != null) {
                        android.util.Log.d("BookApiDataSource", "VolumeInfo.imageLinks: ${volumeInfo.imageLinks}")
                        // Passa o volumeId para gerar URL de fallback se imageLinks for null
                        Result.success(volumeInfo.toBook(volumeId))
                    } else {
                        Result.failure(Exception("Informações do livro não encontradas"))
                    }
                } else {
                    Result.failure(Exception("Nenhum livro encontrado com este ISBN"))
                }
            } else {
                android.util.Log.e("BookApiDataSource", "Erro na resposta: ${response.code()}, ${response.message()}")
                Result.failure(Exception("Erro na resposta da API: ${response.code()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("BookApiDataSource", "Exceção ao buscar livro: ${e.message}", e)
            Result.failure(e)
        }
    }
}