#!/bin/bash

echo "🚀 Starting Event Booking Frontend in Docker with a backend..."

# Check that Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Install Docker from https://docs.docker.com/get-docker/"
    exit 1
fi

# Check that Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Install Docker Compose from https://docs.docker.com/compose/install/"
    exit 1
fi

# Stop existing containers
echo "🛑 Stopping existing containers..."
docker-compose down

# Start with the backend
echo "🐳 Starting with the backend..."
docker-compose --profile docker-backend up --build

echo "✅ Application started!"
echo "🌐 Frontend: http://localhost:3000"
echo "🔧 Backend: http://localhost:8080 (your backend is expected to be running)" 