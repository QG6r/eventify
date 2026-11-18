#!/bin/bash

echo "🚀 Starting Event Booking Frontend in Docker with mock data..."

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

# Start with mock data
echo "🐳 Starting with mock data..."
docker-compose --profile docker-mocks up --build

echo "✅ Application started! Open http://localhost:3000" 