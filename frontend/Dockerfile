# Use Node 18 as base image
FROM node:18 AS builder

# Set working directory
WORKDIR /app

# Accept build argument for API URL
ARG VITE_API_BASE_URL=http://localhost:9090
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

# Copy package files
COPY package.json package-lock.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Production stage with nginx
FROM nginx:alpine

# Copy built files from builder stage to nginx html directory
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 4000
EXPOSE 4000

# Start nginx
CMD ["nginx", "-g", "daemon off;"]

