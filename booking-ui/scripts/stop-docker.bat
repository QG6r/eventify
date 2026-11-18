@echo off
echo 🛑 Stopping Docker containers...

REM Stop all containers
docker-compose down

echo ✅ Containers stopped!
pause 