package com.example.bbunifor.components

import coil3.compose.AsyncImage
import com.example.bbunifor.model.Book

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color

@Composable
fun BookCard(
    book: Book,
    modifier: Modifier = Modifier,
    onLembrarClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (!book.thumbnail.isNullOrBlank()) {
                AsyncImage(
                    model = book.thumbnail,
                    contentDescription = "Capa de ${book.title}",
                    modifier = Modifier
                        .size(130.dp)
                        .background(Color.LightGray, RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(Color.LightGray, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sem capa",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                if (book.authors.isNotEmpty()) {
                    Text(
                        text = book.authors.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = (book.publisher ?: "Editora desconhecida"),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (book.pageCount != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${book.pageCount} pág.",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            if (!book.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = book.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Botão Lembrar
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onLembrarClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔔 Lembrar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookCardPreview() {
    val sample = Book(
        title = "Exemplo de Livro",
        authors = listOf("Autor Um", "Autor Dois"),
        description = "Descrição curta de exemplo. Aqui pode vir um trecho do resumo do livro para demonstrar.",
        publisher = "Editora Exemplo",
        pageCount = 312,
        thumbnail = null // ou coloque uma URL pública pra testar a imagem no preview
    )

    BookCard(book = sample, onLembrarClick = {})
}
