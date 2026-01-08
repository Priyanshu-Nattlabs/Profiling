# Docker Compose Build Fixes Applied

## Issues Fixed:

1. **Frontend Port Configuration**: Configured EXPOSE to 4000 in Dockerfile to match nginx.conf
2. **API URL Configuration**: Changed VITE_API_BASE_URL from localhost to backend service name
3. **Backend Build**: Removed test execution from Docker build (tests run in CI/CD, not Docker builds)
4. **Package.json**: Fixed duplicate "scripts" key
5. **MongoDB Port**: Configured MongoDB port to 57017

## To Build and Run:

```bash
# Stop any running containers
docker compose down

# Remove old images (optional, for clean build)
docker compose down --rmi all

# Build and start services
docker compose up --build

# Or run in detached mode
docker compose up --build -d

# View logs
docker compose logs -f

# View specific service logs
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f mongodb
```

## Troubleshooting:

If you still encounter errors:

1. **Check logs**: `docker compose logs backend` or `docker compose logs frontend`
2. **Verify MongoDB is running**: `docker compose ps`
3. **Check network connectivity**: Services should be on `profiling-network`
4. **Verify environment variables**: Check docker-compose.yml for all required env vars
5. **Clear Docker cache**: `docker compose build --no-cache`

## Service URLs:

- Frontend: http://localhost:4000
- Backend API: http://localhost:9090
- MongoDB: localhost:57017
