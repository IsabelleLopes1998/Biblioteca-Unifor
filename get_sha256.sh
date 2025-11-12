#!/bin/bash
# Script para obter SHA-256 do certificado de debug

KEYSTORE_PATH="$HOME/.android/debug.keystore"

if [ -f "$KEYSTORE_PATH" ]; then
    echo "Obtendo SHA-256 do certificado de debug..."
    echo ""
    
    # Tenta encontrar o keytool
    KEYTOOL=$(which keytool 2>/dev/null)
    
    if [ -z "$KEYTOOL" ]; then
        # Procura em locais comuns do Java
        KEYTOOL=$(find /usr/lib/jvm -name "keytool" 2>/dev/null | head -1)
    fi
    
    if [ -n "$KEYTOOL" ]; then
        $KEYTOOL -list -v -keystore "$KEYSTORE_PATH" -alias androiddebugkey -storepass android -keypass android 2>/dev/null | grep -A 1 "SHA256:" | head -2
    else
        echo "keytool não encontrado. Use o Android Studio:"
        echo "1. Abra o Gradle panel (lado direito)"
        echo "2. Vá em: app > Tasks > android > signingReport"
        echo "3. Procure por SHA256: na saída"
    fi
else
    echo "Keystore de debug não encontrado em: $KEYSTORE_PATH"
fi

