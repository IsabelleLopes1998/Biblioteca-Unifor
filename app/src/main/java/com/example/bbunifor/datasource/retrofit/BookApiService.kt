package com.example.bbunifor.datasource.retrofit

import com.example.bbunifor.datasource.entity.BookApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BookApiService {
    //https://www.googleapis.com/books/v1/volumes?q=isbn:YOUR_ISBN&key=YOUR_API_KEY`N}
    @GET("books/v1/volumes")
    suspend fun currentByISBN(
        @Query("q") isbn: String,
        @Query("key") apiKey: String
    ): Response<BookApiResponse>
}