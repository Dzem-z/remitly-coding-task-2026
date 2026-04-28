#!/bin/bash
# Usage: ./start_app.sh <PORT> [REPLICAS]
# Example: ./start_app.sh 8080 2

set -e

if [ -z "$1" ]; then
  echo "Usage: $0 <PORT> [REPLICAS]"
  exit 1
fi

PORT="$1"
REPLICAS="${2:-3}"

# Export variables for docker-compose
export APP_PORT="$PORT"
export APP_REPLICAS="$REPLICAS"

echo "Starting application on port $APP_PORT with $APP_REPLICAS replica(s)..."
docker compose up --scale app=$APP_REPLICAS --build -d

echo "Application started. Access it at http://localhost:$APP_PORT/"
