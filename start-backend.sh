#!/bin/bash

# Load environment variables from .env if it exists
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
  echo "Loaded environment variables from .env"
else
  echo ".env file not found. Using default application properties."
fi

echo "Starting Backend..."
cd carrito-backend
./mvnw spring-boot:run
