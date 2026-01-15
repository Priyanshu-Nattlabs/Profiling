# Production Deployment Guide - SomethingX

This guide will help you securely deploy SomethingX on your VPS.

## Prerequisites

- VPS with Docker and Docker Compose installed
- Domain name (optional but recommended for HTTPS)
- SSL certificate (Let's Encrypt recommended)
- Basic knowledge of Linux/server administration

## Security Checklist

Before deploying, ensure you have:

- [ ] Generated a strong JWT_SECRET (32+ characters)
- [ ] Set strong passwords for admin user (if using)
- [ ] Configured MongoDB authentication (recommended)
- [ ] Set up SSL/TLS certificates
- [ ] Configured firewall rules
- [ ] Set secure environment variables

## Step 1: Clone and Prepare

```bash
# Clone your repository
git clone <your-repo-url>
cd Profiling

# Copy the example environment file
cp .env.example .env
```

## Step 2: Configure Environment Variables

Edit the `.env` file with your production values:

```bash
nano .env
```

### Required Variables

1. **JWT_SECRET**: Generate a secure secret key
   ```bash
   openssl rand -hex 32
   ```
   Copy the output and set it as `JWT_SECRET` in your `.env` file.

2. **FRONTEND_URL**: Your production frontend URL
   ```
   FRONTEND_URL=https://yourdomain.com
   ```

3. **MONGODB_URI**: Your MongoDB connection string
   - For Docker: `mongodb://mongodb:27017/profiling_db`
   - For external MongoDB: `mongodb://user:pass@host:27017/profiling_db?authSource=admin`

4. **VITE_API_BASE_URL**: Your backend API URL
   ```
   VITE_API_BASE_URL=https://api.yourdomain.com
   # or if using same domain: https://yourdomain.com
   ```

### Optional but Recommended

5. **Admin User**: Set up an admin user on first startup
   ```
   ADMIN_EMAIL=admin@yourdomain.com
   ADMIN_PASSWORD=your-secure-password
   ADMIN_NAME=Administrator
   ```

6. **OpenAI API Key**: If using AI features
   ```
   OPENAI_API_KEY=sk-your-key-here
   ```

7. **Google OAuth**: If using Google login
   ```
   GOOGLE_CLIENT_ID=your-client-id
   GOOGLE_CLIENT_SECRET=your-client-secret
   GOOGLE_REDIRECT_URI=https://yourdomain.com/login/oauth2/code/google
   ```

8. **MongoDB Authentication** (Recommended for production):
   - Uncomment MongoDB auth lines in `docker-compose.yml`
   - Set `MONGO_ROOT_USERNAME` and `MONGO_ROOT_PASSWORD` in `.env`
   - Update `MONGODB_URI` to include credentials

## Step 3: Secure Your Server

### Firewall Configuration

```bash
# Allow SSH (adjust port if needed)
sudo ufw allow 22/tcp

# Allow HTTP and HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# If you need direct access to backend (not recommended)
# sudo ufw allow 9090/tcp

# Enable firewall
sudo ufw enable
```

### Remove MongoDB Port Exposure (Recommended)

For production, MongoDB should not be exposed to the internet. Edit `docker-compose.yml` and remove or comment out the MongoDB port mapping:

```yaml
mongodb:
  # ports:
  #   - "${MONGO_PORT:-57017}:27017"  # Remove this line
```

## Step 4: SSL/TLS Setup (Recommended)

### Using Nginx Reverse Proxy with Let's Encrypt

1. Install Nginx and Certbot:
```bash
sudo apt update
sudo apt install nginx certbot python3-certbot-nginx
```

2. Configure Nginx reverse proxy:
Create `/etc/nginx/sites-available/somethingx`:

```nginx
server {
    listen 80;
    server_name yourdomain.com;

    location / {
        proxy_pass http://localhost:4000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Optional: Proxy backend API
    location /api/ {
        proxy_pass http://localhost:9090;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

3. Enable the site:
```bash
sudo ln -s /etc/nginx/sites-available/somethingx /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

4. Get SSL certificate:
```bash
sudo certbot --nginx -d yourdomain.com
```

## Step 5: Build and Deploy

```bash
# Build and start services
docker-compose up -d --build

# Check logs
docker-compose logs -f

# Check service status
docker-compose ps
```

## Step 6: Verify Deployment

1. Check that all services are running:
   ```bash
   docker-compose ps
   ```

2. Test the frontend:
   - Visit `https://yourdomain.com` (or `http://your-vps-ip:4000`)

3. Test the backend:
   - Visit `https://yourdomain.com/api/auth/me` (should return 401 if not authenticated)

4. Check logs for errors:
   ```bash
   docker-compose logs backend
   docker-compose logs frontend
   docker-compose logs mongodb
   ```

## Step 7: Ongoing Maintenance

### Viewing Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
```

### Updating the Application
```bash
git pull
docker-compose down
docker-compose up -d --build
```

### Backup MongoDB
```bash
# Create backup
docker-compose exec mongodb mongodump --out /data/backup

# Restore backup
docker-compose exec mongodb mongorestore /data/backup
```

### Monitoring
- Set up log rotation
- Monitor disk space
- Set up monitoring alerts (optional)
- Regularly update dependencies and security patches

## Security Best Practices

1. **Never commit `.env` file** - It's already in `.gitignore`
2. **Use strong passwords** - For admin user and MongoDB
3. **Keep dependencies updated** - Regularly update Docker images
4. **Monitor logs** - Check for suspicious activity
5. **Use HTTPS** - Always use SSL/TLS in production
6. **Limit port exposure** - Only expose necessary ports
7. **Regular backups** - Backup your MongoDB database regularly
8. **Firewall rules** - Configure UFW or iptables properly

## Troubleshooting

### Services won't start
- Check `.env` file is properly configured
- Check logs: `docker-compose logs`
- Verify all required environment variables are set

### Can't connect to database
- Check MongoDB is running: `docker-compose ps mongodb`
- Verify `MONGODB_URI` in `.env` is correct
- Check MongoDB logs: `docker-compose logs mongodb`

### CORS errors
- Verify `FRONTEND_URL` matches your actual frontend URL
- Check that frontend and backend URLs are correctly configured

### OAuth not working
- Verify `GOOGLE_REDIRECT_URI` matches your Google OAuth configuration
- Check that `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` are correct

## Support

For issues and questions, please refer to the project documentation or create an issue in the repository.
