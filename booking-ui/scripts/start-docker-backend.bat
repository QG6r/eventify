@echo off
echo 🚀 Starting Event Booking Frontend in Docker with a backend...

REM Check that Docker is installed
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker is not installed. Install Docker from https://docs.docker.com/get-docker/
    pause
    exit /b 1
)

REM Check that Docker Compose is installed
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker Compose is not installed. Install Docker Compose from https://docs.docker.com/compose/install/
    pause
    exit /b 1
)

REM Stop existing containers
echo 🛑 Stopping existing containers...
docker-compose down

REM Start with the backend
echo 🐳 Starting with the backend...
docker-compose --profile docker-backend up --build

echo ✅ Application started!
echo 🌐 Frontend: http://localhost:3000
echo 🔧 Backend: http://localhost:8080
pause 