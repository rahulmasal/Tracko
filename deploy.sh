#!/bin/bash
set -euo pipefail

# Tracko Deployment Script
# Usage: ./deploy.sh [production|staging]

ENVIRONMENT="${1:-production}"
COMPOSE_FILE="docker-compose.yml"

if [ "$ENVIRONMENT" = "production" ]; then
  echo "Deploying to PRODUCTION..."
  if [ ! -f .env ]; then
    echo "ERROR: .env file not found. Copy .env.example to .env and configure it."
    exit 1
  fi
elif [ "$ENVIRONMENT" = "staging" ]; then
  echo "Deploying to STAGING..."
  COMPOSE_FILE="docker-compose.staging.yml"
  if [ ! -f .env.staging ]; then
    echo "ERROR: .env.staging file not found."
    exit 1
  fi
else
  echo "Usage: ./deploy.sh [production|staging]"
  exit 1
fi

echo "==> Pulling latest images..."
docker compose -f "$COMPOSE_FILE" pull --ignore-buildable 2>/dev/null || true

echo "==> Building services..."
docker compose -f "$COMPOSE_FILE" build --no-cache

echo "==> Stopping existing containers..."
docker compose -f "$COMPOSE_FILE" down --remove-orphans

echo "==> Starting services..."
docker compose -f "$COMPOSE_FILE" up -d

echo "==> Waiting for health checks..."
sleep 10

echo "==> Checking service status..."
docker compose -f "$COMPOSE_FILE" ps

echo ""
echo "==> Deployment complete!"
echo "    Manager Portal: http://localhost/manager/"
echo "    Admin Portal:   http://localhost/admin/"
echo "    API:            http://localhost/api/"
echo ""
echo "==> To view logs: docker compose -f $COMPOSE_FILE logs -f"
