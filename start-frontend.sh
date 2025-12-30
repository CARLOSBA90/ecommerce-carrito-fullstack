#!/bin/bash

echo "===================================="
echo " Iniciando Frontend Angular"
echo "===================================="
 
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

FRONTEND_DIR="$ROOT_DIR/carrito-frontend"
 
if [ ! -d "$FRONTEND_DIR" ]; then
  echo "Error: No se encontró la carpeta carrito-frontend"
  exit 1
fi

cd "$FRONTEND_DIR" || exit

echo "Instalando dependencias..."
npm install

echo "Levantando servidor Angular..."
ng serve
