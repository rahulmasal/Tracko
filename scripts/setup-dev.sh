#!/bin/bash
# Tracko Development Setup Script (Linux/Mac)
# =============================================

set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Tracko - Development Setup${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Check prerequisites
echo "[1/6] Checking prerequisites..."

if command -v java &> /dev/null; then
    echo -e "${GREEN}[OK]${NC} Java found"
else
    echo -e "${YELLOW}[WARNING]${NC} Java not found. Install JDK 17+"
fi

if command -v node &> /dev/null; then
    echo -e "${GREEN}[OK]${NC} Node.js $(node -v) found"
else
    echo -e "${RED}[ERROR]${NC} Node.js not found. Install Node.js 20+"
    exit 1
fi

if command -v npm &> /dev/null; then
    echo -e "${GREEN}[OK]${NC} npm found"
else
    echo -e "${RED}[ERROR]${NC} npm not found"
    exit 1
fi

if command -v docker &> /dev/null; then
    echo -e "${GREEN}[OK]${NC} Docker found"
else
    echo -e "${YELLOW}[WARNING]${NC} Docker not found"
fi

echo ""

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_DIR"

# Copy environment file
echo "[2/6] Setting up environment configuration..."
if [ ! -f ".env" ]; then
    cp .env.example .env
    echo -e "${GREEN}[OK]${NC} Created .env from .env.example"
else
    echo -e "${YELLOW}[SKIP]${NC} .env already exists"
fi

# Start Docker services
echo "[3/6] Starting Docker services (PostgreSQL, Redis, MinIO)..."
docker-compose up -d tracko-db tracko-redis tracko-minio 2>/dev/null && \
    echo -e "${GREEN}[OK]${NC} Docker services started" || \
    echo -e "${YELLOW}[WARNING]${NC} Failed to start Docker services"

# Install npm dependencies
echo "[4/6] Installing Manager Portal dependencies..."
cd web/manager-portal
npm install --silent
echo -e "${GREEN}[OK]${NC} Manager Portal dependencies installed"

echo "[5/6] Installing Admin Portal dependencies..."
cd ../admin-portal
npm install --silent
echo -e "${GREEN}[OK]${NC} Admin Portal dependencies installed"

cd "$PROJECT_DIR"

echo ""
echo "[6/6] Setup Complete!"
echo ""
echo -e "${GREEN}========================================${NC}"
echo "  Services starting on:"
echo "  -------------------------"
echo "  Manager Portal:  http://localhost:3000"
echo "  Admin Portal:    http://localhost:3001"
echo "  API:             http://localhost:8080/api"
echo "  PostgreSQL:      localhost:5432"
echo "  Redis:           localhost:6379"
echo "  MinIO Console:   http://localhost:9001"
echo "  -------------------------"
echo "  Default Login:   admin@tracko.com / Admin@123"
echo -e "${GREEN}========================================${NC}"
echo ""
echo "To start the web servers in development mode:"
echo "  cd web/manager-portal && npm run dev"
echo "  cd web/admin-portal && npm run dev"
echo ""
