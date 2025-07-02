# 📱 PoggTech - Aplicação Android

PoggTech é uma aplicação Android desenvolvida como parte de uma Prova de Aptidão Profissional (PAP).  
Permite a gestão de produtos, contas de utilizador, compras, mensagens e muito mais, utilizando uma API REST construída em PHP.

---

## 🚀 Funcionalidades principais

- 🔐 Autenticação via Firebase
- 🛍️ Listagem e criação de produtos
- 📷 Upload de imagens
- 💬 Sistema de chat entre compradores e vendedores
- 🛒 Carrinho e pedidos
- ⭐ Avaliações de produtos
- 🧾 Histórico de encomendas
- 📡 Comunicação com servidor remoto via Retrofit

---

## ⚙️ Tecnologias utilizadas

- **Java (Android)**
- **Retrofit + Gson** (para requisições HTTP)
- **Firebase Authentication**
- **Glide** (para carregar imagens)
- **SharedPreferences** (para guardar configurações)
- **Google Places API** (preenchimento automático de endereços)
- **Material Components**

---

## 📦 Requisitos

- Android Studios (melhor opção)
- Android 9 (API 28) ou superior
- Ligação à internet (preferencialmente à mesma rede do servidor)
- Servidor com a [API PoggTech](https://github.com/KauanFortunato/PoggTechAPIs) (ver abaixo)

---

## 🔗 API necessária

Este app depende da [API PoggTech](https://github.com/KauanFortunato/PoggTechAPIs), construída em PHP + MySQL.

**Para que o app funcione corretamente:**
- O servidor PHP deve estar ativo.
- A base de dados MySQL deve estar configurada.
- O IP do servidor deve ser ajustado no app (ver abaixo).

---

## ▶️ Como usar

Este projeto pode ser executado localmente num computador com **Android Studio** instalado.

---

### 1. 📥 Instalar o Android Studio

1. Aceda ao site oficial: https://developer.android.com/studio
2. Clique em **"Download Android Studio"** e siga os passos da instalação.
3. Durante a instalação, **deixe todas as opções por padrão** (SDK, emulador, etc.).

---


### 2. 📥 Instalar Git

1. Aceda ao site oficial: https://git-scm.com/downloads
2. Clique em **"Download for Windows"** e siga os passos da instalação.
3. Durante a instalação, **deixe todas as opções por padrão**.

---

### ✅ 3 Clonar o repositório
1. Abra o Android Studio
2. No ecrã principal, selecione **"Clone Repository"**
3. No campo *URL*, cole o link do repositório:
    ```
    https://github.com/KauanFortunato/PoggTech
    ```

## 🧠 Configuração do IP do Servidor

O IP do servidor pode ser alterado diretamente no código ou através de uma interface (se incluída).

### 📍 Alteração manual:
Em `AppConfig.java`, altera a linha:


```java
String ip = prefs.getString(KEY_IP, "poggers.ddns.net");
```

---

### 🔑 GOOGLE_API_KEY

Você pode adquirir gratuitamente em **[Places API](https://console.cloud.google.com/marketplace/product/google/places-backend.googleapis.com)**.

> Na raiz do projeto Android, adiciona um ficheiro `local.properties`:

```env
GOOGLE_API_KEY=sua_api_key
```
