# BBUnifor - Biblioteca Unifor

Aplicativo Android para gerenciamento de lembretes de entrega de livros da biblioteca.

## Configuração

### 1. Clone o repositório

```bash
git clone <url-do-repositorio>
cd BBUnifor
```

### 2. Configure as chaves de API

Copie o arquivo de exemplo e adicione suas chaves:

```bash
cp local.properties.example local.properties
```

Edite o arquivo `local.properties` e adicione sua chave da Google Books API:

```properties
GOOGLE_BOOKS_API_KEY=sua_chave_aqui
```

**Importante:** O arquivo `local.properties` já está no `.gitignore` e não será commitado.

### 3. Obter a Google Books API Key

1. Acesse [Google Cloud Console](https://console.cloud.google.com/)
2. Crie um novo projeto ou selecione um existente
3. Ative a API "Books API"
4. Vá em "Credenciais" e crie uma chave de API
5. Copie a chave e adicione no `local.properties`

### 4. Configure o Firebase

1. Adicione o arquivo `google-services.json` na pasta `app/`
2. Configure o Firebase Authentication (Email/Password)
3. Configure o Firebase Realtime Database
4. Configure o Firebase App Check

## Tecnologias

- Kotlin
- Jetpack Compose
- Firebase (Authentication, Realtime Database, App Check)
- Retrofit (Google Books API)
- Material Design 3

## Estrutura do Projeto

```
app/src/main/java/com/example/bbunifor/
├── components/          # Componentes reutilizáveis
├── datasource/          # Fontes de dados (API, Firebase)
├── model/               # Modelos de dados
├── receiver/            # BroadcastReceivers
├── screen/              # Telas da aplicação
├── service/             # Serviços (notificações)
└── viewmodel/           # ViewModels
```

## Funcionalidades

- 🔐 Autenticação com Firebase
- 📚 Busca de livros por ISBN (Google Books API)
- 🔔 Lembretes de entrega com notificações
- 📅 Notificações 1 dia antes e no dia da entrega
- 💾 Persistência de dados no Firebase Realtime Database

## Licença

Este projeto é privado e pertence à Universidade de Fortaleza (Unifor).

