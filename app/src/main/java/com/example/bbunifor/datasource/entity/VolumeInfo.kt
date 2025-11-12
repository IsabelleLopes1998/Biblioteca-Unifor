package com.example.bbunifor.datasource.entity

import android.util.Log
import com.example.bbunifor.model.Book
import com.google.gson.annotations.SerializedName

data class VolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    @SerializedName("industryIdentifiers")
    val industryIdentifiers: List<IndustryIdentifier>?,
    val pageCount: Int?,
    val printType: String?,
    val categories: List<String>?,
    val averageRating: Double?,
    val ratingsCount: Int?,
    val maturityRating: String?,
    val allowAnonLogging: Boolean?,
    val contentVersion: String?,
    @SerializedName("imageLinks")
    val imageLinks: ImageLinks?,
    val language: String?,
    val previewLink: String?,
    val infoLink: String?,
    val canonicalVolumeLink: String?
)

// Extension function para converter VolumeInfo em Book
fun VolumeInfo.toBook(volumeId: String? = null): Book {
    // Debug: verifica se imageLinks existe
    Log.d("VolumeInfo", "imageLinks: ${imageLinks}")
    
    // tenta pegar a melhor thumbnail disponível (ordem de preferência)
    val thumb = imageLinks?.thumbnail
        ?: imageLinks?.medium
        ?: imageLinks?.large
        ?: imageLinks?.small
        ?: imageLinks?.smallThumbnail
        ?: imageLinks?.extraLarge
        ?: null

    Log.d("VolumeInfo", "URL original da imagem: $thumb")

    // Ajusta a URL da imagem: remove parâmetros desnecessários e converte http para https
    var processedThumb = thumb?.let { url ->
        val processed = url
            .replace("&edge=curl", "") // Remove parâmetro edge=curl que pode causar problemas
            .replace("http://", "https://") // Converte HTTP para HTTPS (Android bloqueia HTTP por padrão)
            .replace("=zoom=1", "=zoom=2") // Melhora a qualidade da imagem
            .replace("=zoom=0", "=zoom=2") // Melhora a qualidade da imagem
        
        Log.d("VolumeInfo", "URL processada da imagem: $processed")
        processed.ifEmpty { null }
    }

    // Fallback: se não houver imageLinks, tenta gerar URL baseada no volumeId
    if (processedThumb == null && volumeId != null) {
        // Tenta construir URL da imagem usando o padrão do Google Books
        processedThumb = "https://books.google.com/books/publisher/content/images/frontcover/$volumeId?fife=w400-h600&source=gbs_api"
        Log.d("VolumeInfo", "Usando URL de fallback: $processedThumb")
    }

    return Book(
        title = this.title ?: "Título desconhecido",
        authors = this.authors ?: emptyList(),
        description = this.description,
        publisher = this.publisher,
        pageCount = this.pageCount,
        thumbnail = processedThumb
    )
}
