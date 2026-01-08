# Credentials Setup Guide

## 🔒 Security Notice

Credentials have been moved from `docker-compose.yml` to a `.env` file for security. The `.env` file is automatically ignored by Git.

## 📋 Setup Instructions

### 1. Create `.env` file

Copy the example file and fill in your credentials:

```bash
cp .env.example .env
```

### 2. Edit `.env` file

Open `.env` and replace the placeholder values with your actual credentials:

```env
# OpenAI API Configuration
OPENAI_API_KEY=your-actual-openai-api-key-here

# JWT Configuration
JWT_SECRET=your-actual-jwt-secret-key-here-minimum-32-characters-long

# Google OAuth2 Configuration
GOOGLE_CLIENT_ID=your-actual-google-client-id-here.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-actual-google-client-secret-here
```

### 3. Copy docker-compose.yml

If you don't have `docker-compose.yml`, copy from the example:

```bash
cp docker-compose.yml.example docker-compose.yml
```

The `docker-compose.yml` file is now also ignored by Git for security.

## 🔑 Where to Get Credentials

### OpenAI API Key
1. Go to https://platform.openai.com/api-keys
2. Sign in or create an account
3. Create a new API key
4. Copy the key and paste it in `.env`

### JWT Secret
Generate a secure random string (minimum 32 characters):
```bash
# On Linux/Mac:
openssl rand -hex 32

# On Windows PowerShell:
-join ((48..57) + (65..90) + (97..122) | Get-Random -Count 32 | % {[char]$_})
```

### Google OAuth2 Credentials
1. Go to https://console.cloud.google.com/apis/credentials
2. Create a new OAuth 2.0 Client ID
3. Copy the Client ID and Client Secret
4. Paste them in `.env`

## ✅ Verification

After setting up `.env`, verify it works:

```bash
docker compose up --build
```

## 🚨 Important Security Notes

- **Never commit `.env` file** - It's already in `.gitignore`
- **Never commit `docker-compose.yml`** - It's now in `.gitignore`
- **Rotate credentials** if they were ever committed to Git
- **Use different credentials** for development and production
- **Share `.env.example`** with team members, not `.env`

## 📝 For New Team Members

When cloning the repository:
1. Copy `.env.example` to `.env`
2. Fill in the actual credentials
3. Copy `docker-compose.yml.example` to `docker-compose.yml`
4. Run `docker compose up --build`
