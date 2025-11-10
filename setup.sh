#!/bin/bash

# 1. Pedir variables de entorno
echo "Deje uno o mas campos vacios para usar el .env existente"
read -p "Ingresa el TOKEN del bot (EVENTOS_TELEGRAM_BOT_TOKEN): " BOT_TOKEN
read -p "Ingresa el USERNAME del bot (EVENTOS_TELEGRAM_BOT_USERNAME): " BOT_USERNAME
read -p "Ingresa la SECRET KEY del server (EVENTOS_SERVER_SECRET_KEY): " SECRET_KEY
read -p "Ingresa el USERNAME del admin-default (ADMIN_USERNAME): " ADMIN_USERNAME
read -p "Ingresa la PASSWORD del admin-default (ADMIN_PASSWORD): " ADMIN_PASSWORD

# 2. Crear archivo .env en la raíz del proyecto
ENV_FILE=".env"
if [ -n "$BOT_TOKEN" ] && [ -n "$BOT_USERNAME" ] && [ -n "$SECRET_KEY" ] && [ -n "$ADMIN_USERNAME" ] && [ -n "$ADMIN_PASSWORD" ]; then
  echo "Creando archivo $ENV_FILE en la raíz del proyecto..."

cat > "$ENV_FILE" <<EOF
EVENTOS_TELEGRAM_BOT_TOKEN=$BOT_TOKEN
EVENTOS_TELEGRAM_BOT_USERNAME=$BOT_USERNAME
EVENTOS_SERVER_SECRET_KEY=$SECRET_KEY
ADMIN_USERNAME=$ADMIN_USERNAME
ADMIN_PASSWORD=$ADMIN_PASSWORD
EOF

echo "Archivo $ENV_FILE creado correctamente."
else
  echo "No se creo archivo .env"
fi


# 3. Esperar a que se abra docker
read -p "Asegúrese que docker este iniciado "


# 4. Ejecutar docker-compose
# Toma argumentos para solo iniciar ciertos servicios
if [ "$#" -gt 0 ]; then
  echo "Ejecutando docker-compose para servicios: $@"
  docker-compose up --build "$@"
else
  echo "Ejecutando docker-compose para todos los servicios..."
  docker-compose up --build
fi