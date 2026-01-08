# Quick Start Guide - Docker Compose

## 🚀 Build and Run

```powershell
# Navigate to project
cd "c:\Users\PriyanshuPandey\OneDrive - NATT Labs\Desktop\Profile 12\Profile main\Profi"

# Clean start (removes old containers and images)
docker compose down --rmi all

# Build and start
docker compose up --build

# Or in background
docker compose up --build -d
```

## 📋 What Was Fixed

✅ Frontend port configured to 4000  
✅ Backend tests removed from Docker build  
✅ Package.json duplicate scripts key fixed  
✅ MongoDB port configured to 57017  
✅ Service healthchecks added  
✅ Proper service dependencies configured  

## 🔍 Verify Services

```powershell
# Check all services are running
docker compose ps

# Check logs
docker compose logs -f

# Test endpoints
# Frontend: http://localhost:4000
# Backend: http://localhost:9090/api/templates/all
```

## 🐛 If Issues Persist

1. **Check logs**: `docker compose logs backend`
2. **Rebuild without cache**: `docker compose build --no-cache`
3. **Check ports**: Ensure 4000, 9090, 57017 are free
4. **Docker resources**: Ensure Docker has 4GB+ RAM allocated

## 📝 Service Health

- MongoDB: Healthcheck with mongosh ping
- Backend: Healthcheck with netcat (port 9090)
- Frontend: Depends on backend being healthy

All services will wait for dependencies before starting! ✅
