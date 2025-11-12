package com.example.bbunifor.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.bbunifor.MainActivity

class LembreteNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("LembreteNotificationReceiver", "Notificação recebida!")
        val titulo = intent.getStringExtra("titulo") ?: "Lembrete de Entrega"
        val mensagem = intent.getStringExtra("mensagem") ?: "Não esqueça de devolver o livro!"

        Log.d("LembreteNotificationReceiver", "Título: $titulo")
        Log.d("LembreteNotificationReceiver", "Mensagem: $mensagem")

        criarCanalNotificacao(context)
        mostrarNotificacao(context, titulo, mensagem)
    }

    private fun criarCanalNotificacao(context: Context) {
        val channelId = "lembrete_entrega_channel"
        val channelName = "Lembretes de Entrega"
        val channelDescription = "Notificações sobre lembretes de entrega de livros"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
            enableVibration(true)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun mostrarNotificacao(context: Context, titulo: String, mensagem: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "lembrete_entrega_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensagem))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
        Log.d("LembreteNotificationReceiver", "Notificação exibida com ID: $notificationId")
    }
}

