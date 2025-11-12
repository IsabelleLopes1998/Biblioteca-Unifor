package com.example.bbunifor.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.bbunifor.model.Lembrete
import com.example.bbunifor.receiver.LembreteNotificationReceiver
import java.util.Calendar

class NotificationService(private val context: Context? = null) {

    private fun getContext(): Context {
        return context ?: throw IllegalStateException("Context não fornecido")
    }

    fun agendarNotificacoes(lembrete: Lembrete) {
        val ctx = getContext()
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val dataEntrega = Calendar.getInstance().apply {
            timeInMillis = lembrete.dataEntrega
        }

        // Notificação 1 dia antes
        val umDiaAntes = Calendar.getInstance().apply {
            timeInMillis = lembrete.dataEntrega
            add(Calendar.DAY_OF_YEAR, -1)
        }

        Log.d("NotificationService", "Agendando notificações para lembrete: ${lembrete.id}")
        Log.d("NotificationService", "Data entrega: ${dataEntrega.time}")
        Log.d("NotificationService", "1 dia antes: ${umDiaAntes.time}")

        // Só agenda se a data não passou
        if (umDiaAntes.timeInMillis > System.currentTimeMillis()) {
            Log.d("NotificationService", "Agendando notificação 1 dia antes")
            agendarNotificacao(
                alarmManager = alarmManager,
                lembrete = lembrete,
                dataNotificacao = umDiaAntes.timeInMillis,
                tipo = "um_dia_antes",
                titulo = "Lembrete: Entrega de livro amanhã",
                mensagem = "Não esqueça de devolver \"${lembrete.livro.title}\" amanhã!"
            )
        } else {
            Log.d("NotificationService", "Data de 1 dia antes já passou, não agendando")
        }

        // Notificação no dia da entrega
        if (dataEntrega.timeInMillis > System.currentTimeMillis()) {
            Log.d("NotificationService", "Agendando notificação no dia")
            agendarNotificacao(
                alarmManager = alarmManager,
                lembrete = lembrete,
                dataNotificacao = dataEntrega.timeInMillis,
                tipo = "no_dia",
                titulo = "Lembrete: Entrega de livro hoje",
                mensagem = "Hoje é o dia de devolver \"${lembrete.livro.title}\"!"
            )
        } else {
            Log.d("NotificationService", "Data de entrega já passou, não agendando")
        }
    }

    private fun agendarNotificacao(
        alarmManager: AlarmManager,
        lembrete: Lembrete,
        dataNotificacao: Long,
        tipo: String,
        titulo: String,
        mensagem: String
    ) {
        val ctx = getContext()
        val intent = Intent(ctx, LembreteNotificationReceiver::class.java).apply {
            putExtra("lembrete_id", lembrete.id)
            putExtra("titulo", titulo)
            putExtra("mensagem", mensagem)
            putExtra("tipo", tipo)
        }

        val requestCode = "${lembrete.id}_$tipo".hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            ctx,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ requer permissão especial
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        dataNotificacao,
                        pendingIntent
                    )
                    Log.d("NotificationService", "Notificação agendada com sucesso: $tipo para ${java.util.Date(dataNotificacao)}")
                } else {
                    // Fallback para alarme inexato
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        dataNotificacao,
                        pendingIntent
                    )
                    Log.d("NotificationService", "Notificação agendada (inexata) por falta de permissão: $tipo")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    dataNotificacao,
                    pendingIntent
                )
                Log.d("NotificationService", "Notificação agendada com sucesso: $tipo")
            }
        } catch (e: Exception) {
            Log.e("NotificationService", "Erro ao agendar notificação: ${e.message}", e)
            // Tenta agendar como alarme inexato como fallback
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, dataNotificacao, pendingIntent)
                Log.d("NotificationService", "Notificação agendada como fallback (inexata)")
            } catch (e2: Exception) {
                Log.e("NotificationService", "Erro ao agendar notificação (fallback): ${e2.message}", e2)
            }
        }
    }

    fun cancelarNotificacoes(lembreteId: String) {
        val ctx = getContext()
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Cancela notificação 1 dia antes
        val intent1 = Intent(ctx, LembreteNotificationReceiver::class.java)
        val requestCode1 = "${lembreteId}_um_dia_antes".hashCode()
        val pendingIntent1 = PendingIntent.getBroadcast(
            ctx,
            requestCode1,
            intent1,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent1)

        // Cancela notificação no dia
        val intent2 = Intent(ctx, LembreteNotificationReceiver::class.java)
        val requestCode2 = "${lembreteId}_no_dia".hashCode()
        val pendingIntent2 = PendingIntent.getBroadcast(
            ctx,
            requestCode2,
            intent2,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent2)
    }
}

