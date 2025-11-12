package com.example.bbunifor

import android.app.Application
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class BBUniforApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar Firebase App Check
        try {
            // Sempre usar Debug em desenvolvimento para evitar bloqueios
            Firebase.appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            Log.d("BBUniforApplication", "Firebase App Check (Debug) inicializado")
        } catch (e: Exception) {
            Log.e("BBUniforApplication", "Erro ao inicializar App Check: ${e.message}", e)
        }
    }
}

