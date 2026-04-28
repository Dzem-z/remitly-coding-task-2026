#!/bin/bash
# Usage: ./stop_app.sh
# Stops all running containers and removes them (including networks, volumes, images built by compose)

set -e

echo "Stopping and removing all containers..."
docker compose down --volumes --remove-orphans

echo "All containers stopped and removed."
