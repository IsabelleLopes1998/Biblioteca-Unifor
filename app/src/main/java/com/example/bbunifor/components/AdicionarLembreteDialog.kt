package com.example.bbunifor.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.bbunifor.model.Book
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdicionarLembreteDialog(
    livro: Book,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    val datePickerState = rememberDatePickerState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Adicionar Lembrete",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = livro.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Selecione a data de entrega:",
                    style = MaterialTheme.typography.bodyMedium
                )

                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { dateMillis ->
                                onConfirm(dateMillis)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = datePickerState.selectedDateMillis != null
                    ) {
                        Text("Adicionar")
                    }
                }
            }
        }
    }
}

