#!/bin/bash
# Script simples para obter SHA-256

KEYSTORE="$HOME/.android/debug.keystore"

echo "=== Obtendo SHA-256 do certificado de debug ==="
echo ""

if [ ! -f "$KEYSTORE" ]; then
    echo "❌ Keystore não encontrado em: $KEYSTORE"
    exit 1
fi

echo "✅ Keystore encontrado!"
echo ""
echo "Tentando obter SHA-256..."
echo ""

# Tenta vários caminhos possíveis para o keytool
KEYTOOL_PATHS=(
    "keytool"
    "/usr/bin/keytool"
    "$(find /usr/lib/jvm -name keytool 2>/dev/null | head -1)"
    "$(find /opt -name keytool 2>/dev/null | head -1)"
    "$JAVA_HOME/bin/keytool"
)

KEYTOOL=""
for path in "${KEYTOOL_PATHS[@]}"; do
    if [ -n "$path" ] && command -v "$path" >/dev/null 2>&1; then
        KEYTOOL="$path"
        break
    fi
done

if [ -z "$KEYTOOL" ]; then
    echo "⚠️  keytool não encontrado automaticamente."
    echo ""
    echo "📋 INSTRUÇÕES MANUAIS:"
    echo ""
    echo "1. No Android Studio:"
    echo "   - Clique no ícone de sincronização (elefante com seta) na barra superior"
    echo "   - Aguarde a sincronização terminar"
    echo "   - Vá em: Gradle panel → BBUnifor → app → Tasks → android"
    echo "   - Execute: signingReport"
    echo "   - Procure por 'SHA256:' na saída"
    echo ""
    echo "2. Ou use este comando no terminal (se tiver Java instalado):"
    echo "   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android | grep SHA256"
    exit 1
fi

echo "✅ keytool encontrado: $KEYTOOL"
echo ""
echo "Executando comando..."
echo ""

OUTPUT=$($KEYTOOL -list -v -keystore "$KEYSTORE" -alias androiddebugkey -storepass android -keypass android 2>&1)

if [ $? -eq 0 ]; then
    SHA256=$(echo "$OUTPUT" | grep -i "SHA256" | sed 's/.*SHA256: *//' | tr -d ' ' | tr -d ':')
    
    if [ -n "$SHA256" ]; then
        echo "✅ SHA-256 encontrado:"
        echo ""
        echo "$SHA256"
        echo ""
        echo "📋 Copie este valor e cole no Firebase Console!"
    else
        echo "⚠️  SHA-256 não encontrado na saída."
        echo ""
        echo "Saída completa:"
        echo "$OUTPUT"
    fi
else
    echo "❌ Erro ao executar keytool"
    echo ""
    echo "Saída do erro:"
    echo "$OUTPUT"
fi


