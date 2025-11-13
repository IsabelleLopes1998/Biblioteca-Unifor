package com.example.bbunifor.components

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.bbunifor.model.Book
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

@Composable
fun AdicionarLembreteDialog(
    livro: Book,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var dataSelecionadaTexto by remember { mutableStateOf("") }
    
    // Horário pré-preenchido com 08:00
    var horario by remember { mutableStateOf("08:00") }
    var horarioError by remember { mutableStateOf(false) }
    var horarioErrorText by remember { mutableStateOf("") }
    
    // Padrão para validar formato HH:mm
    val horarioPattern = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
    
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    fun mostrarDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                selectedDateMillis = calendar.timeInMillis
                dataSelecionadaTexto = dateFormat.format(calendar.time)
            },
            year,
            month,
            day
        ).show()
    }

    fun validarHorario(horario: String): Boolean {
        return if (horario.isBlank()) {
            horarioError = true
            horarioErrorText = "Horário é obrigatório"
            false
        } else if (!horarioPattern.matcher(horario).matches()) {
            horarioError = true
            horarioErrorText = "Formato inválido. Use HH:mm (ex: 08:00, 19:10)"
            false
        } else {
            horarioError = false
            horarioErrorText = ""
            true
        }
    }

    fun combinarDataEHora(dataMillis: Long, horario: String): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dataMillis
        }
        
        val partes = horario.split(":")
        val hora = partes[0].toInt()
        val minuto = partes[1].toInt()
        
        calendar.set(Calendar.HOUR_OF_DAY, hora)
        calendar.set(Calendar.MINUTE, minuto)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        return calendar.timeInMillis
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
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

                OutlinedButton(
                    onClick = { mostrarDatePicker() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (dataSelecionadaTexto.isNotEmpty()) {
                            "Data selecionada: $dataSelecionadaTexto"
                        } else {
                            "Selecionar Data"
                        }
                    )
                }

                Text(
                    text = "Selecione o horário:",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = horario,
                    onValueChange = { novoHorario ->
                        horario = novoHorario
                        if (horarioError) {
                            validarHorario(novoHorario)
                        }
                    },
                    label = { Text("Horário (HH:mm)") },
                    placeholder = { Text("08:00") },
                    isError = horarioError,
                    supportingText = {
                        if (horarioError) {
                            Text(horarioErrorText)
                        } else {
                            Text("Exemplo: 08:00, 19:10")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
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
                            if (validarHorario(horario) && selectedDateMillis != null) {
                                val timestampCompleto = combinarDataEHora(selectedDateMillis!!, horario)
                                onConfirm(timestampCompleto)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedDateMillis != null && !horarioError && horario.isNotBlank()
                    ) {
                        Text("Adicionar")
                    }
                }
            }
        }
    }
}


