
# TP TACS

## Links

### Alumnos/Grupos
[https://docs.google.com/spreadsheets/d/e/2PACX-1vSReiaYjc97RflWQ5IlHoeRb2lrDqMi3n9YwexC2KBf7_fqZ1Am_Et7CZ5V5K5b0vy_0n4DeWvXZzPD/pubhtml?gid=347228570&single=true
](https://docs.google.com/spreadsheets/d/e/2PACX-1vSReiaYjc97RflWQ5IlHoeRb2lrDqMi3n9YwexC2KBf7_fqZ1Am_Et7CZ5V5K5b0vy_0n4DeWvXZzPD/pubhtml?gid=347228570&single=true)

### Planificacion
[https://docs.google.com/spreadsheets/d/e/2PACX-1vSa2Emm5Rjl2zmFTbxOxp6nH19FbL80dM3T9-7Ae1EGK9laBI_4D-3FLcUuk9pmSOv9FiCEXfFt0he-/pubhtml?gid=0&single=true
](https://docs.google.com/spreadsheets/d/e/2PACX-1vSa2Emm5Rjl2zmFTbxOxp6nH19FbL80dM3T9-7Ae1EGK9laBI_4D-3FLcUuk9pmSOv9FiCEXfFt0he-/pubhtml?gid=0&single=true)

### Consigna
[https://docs.google.com/document/d/e/2PACX-1vRKgz7eEA1fIByKMtXKxA6-Vs1rSst8cwUeTkMnZyYrDPkzkUECyK7WXqXWFSh5jwnxJMdanffdyWzB/pub
](https://docs.google.com/document/d/e/2PACX-1vRKgz7eEA1fIByKMtXKxA6-Vs1rSst8cwUeTkMnZyYrDPkzkUECyK7WXqXWFSh5jwnxJMdanffdyWzB/pub)

### Pag general
[https://www.tacs-utn.com.ar/
](https://www.tacs-utn.com.ar/)

### Jira
[https://frba-team-hj2c1r3q.atlassian.net/jira/software/projects/MBA/boards/1](https://frba-team-hj2c1r3q.atlassian.net/jira/software/projects/MBA/boards/1)

## Instrucciones para levantar el sistema
(Desde la raiz del repositorio)

### Con Docker Automático
1. Abrir Docker Desktop si no esta abierto



2. Ejecutar el script setup.sh
    ```bash
    ./setup.sh [modulos]
    ```
    Si se deja vacío se ejecutan todos los modulos. Los modulos posibles son:
     - servidor
     - telegrambot


3. Introducir las variables de entorno por consola:
   - EVENTOS_TELEGRAM_BOT_TOKEN
   - EVENTOS_TELEGRAM_BOT_USERNAME
   - EVENTOS_SERVER_SECRET_KEY

### Con Docker Manual:
1. Abrir Docker Desktop si no está abierto.


2. Crear un archivo .env con las variables de entorno:
    - EVENTOS_TELEGRAM_BOT_TOKEN
    - EVENTOS_TELEGRAM_BOT_USERNAME
    - EVENTOS_SERVER_SECRET_KEY



3. Construir y ejecutar red de contenedores:

```bash
docker-compose up --build
```

### Sin Docker:
1. Crear las variables de entorno en el sistema:
   - EVENTOS_TELEGRAM_BOT_TOKEN
   - EVENTOS_TELEGRAM_BOT_USERNAME
   - EVENTOS_SERVER_SECRET_KEY


2. Compilar el proyecto
```bash
mvn clean package
```
3. Ejecutar servidor
```bash
java -jar Servidor/target/Servidor-1.0.jar
```
4. Ejecutar bot de telegram
```bash
java -jar TelegramBot/target/TelegramBot-1.0.jar
```
--- 


Tras eso, el servidor estará escuchando peticiones en el puerto 8080.
Se puede comprobar haciendo alguna de las peticiones de ejemplo que se muestran abajo.

---
## Posibles problemas
### 1.  "Error removing old webhook"
Esto se debe a que el bot utiliza long polling para conectarse a los servidores de telegram, y si hay un webhook existente, puede causar errores.
En la ultima version, el bot se encarga de eliminarlo automaticamente, pero si aun asi no funciona, puede ser necesario eliminarlo de manera manual.
Para esto se puede utilizar:
```bash
curl https://api.telegram.org/bot<TOKEN>/setwebhook

# En windows, si no anda el anterior
curl --ssl-no-revoke https://api.telegram.org/bot<TOKEN>/setwebhook

# O entrar por un navegador a https://api.telegram.org/bot<TOKEN>/setwebhook
```
(Se debe completar \<TOKEN\> con el token del bot.)

---
## API Endpoints

> ⚠️ Importante: todos los `accountId`, `eventId` y `registrationId` que se envían en las requests deben haber sido creados previamente.

---

### 1️⃣ Accounts

#### Crear cuenta

```
POST /accounts
Content-Type: application/json
```

**Request**

```json
{
  "username": "TestingUser"
}
```

**Response**

```json
{
  "id": "fcf41137-d0ef-434d-a170-8bb815b67809",
  "username": "TestingUser"
}
```

#### Obtener inscripciones de un usuario

```
GET /accounts/{accountId}/registrations
```

**Response**

```json
[
  {
    "registrationId": "44daf0a2-ffce-4366-bf93-18da55f6f24e",
    "eventId": "9a15c50f-c056-4023-90f1-2a94e863e977",
    "accountId": "fcf41137-d0ef-434d-a170-8bb815b67809",
    "state": "CONFIRMED",
    "dateTime": "2025-09-01T17:24:32"
  }
]
```

#### Obtener eventos organizados por un usuario

```
GET /accounts/{accountId}/organized-events
```

**Response**

```json
[
  {
    "id": "9a15c50f-c056-4023-90f1-2a94e863e977",
    "title": "Concierto de Jazz",
    "description": "Noche de jazz en vivo",
    "location": "Teatro Colón",
    "startDateTime": "2025-09-10T20:00:00",
    "durationMinutes": 120,
    "maxParticipants": 200,
    "price": 1500
  }
]
```

---

### 2️⃣ Events

#### Crear evento (general)

```
POST /events
Content-Type: application/json
```

**Request**

```json
{
  "title": "Concierto de Jazz",
  "description": "Noche de jazz en vivo",
  "location": "Teatro Colón",
  "startDateTime": "2025-09-10T20:00:00",
  "durationMinutes": 120,
  "maxParticipants": 200,
  "price": 1500,
  "organizerId": "fcf41137-d0ef-434d-a170-8bb815b67809"
}
```

#### Obtener evento por ID

```
GET /events/{id}
```

**Response**

```json
{
  "id": "9a15c50f-c056-4023-90f1-2a94e863e977",
  "title": "Concierto de Jazz",
  "description": "Noche de jazz en vivo",
  "location": "Teatro Colón",
  "startDateTime": "2025-09-10T20:00:00",
  "durationMinutes": 120,
  "maxParticipants": 200,
  "price": 1500
}
```

#### Buscar eventos con filtros

```
GET /events?titleContains=Jazz&minPrice=1000&maxPrice=2000
```

**Response**

```json
[
  {
    "id": "9a15c50f-c056-4023-90f1-2a94e863e977",
    "title": "Concierto de Jazz",
    "description": "Noche de jazz en vivo",
    "location": "Teatro Colón",
    "startDateTime": "2025-09-10T20:00:00",
    "durationMinutes": 120,
    "maxParticipants": 200,
    "price": 1500
  }
]
```

#### Registrar usuario a evento

```
POST /events/registration
Content-Type: application/json
```

**Request**

```json
{
  "eventId": "9a15c50f-c056-4023-90f1-2a94e863e977",
  "accountId": "fcf41137-d0ef-434d-a170-8bb815b67809"
}
```

---

### 3️⃣ Organizer

#### Obtener participantes de un evento

```
GET /accounts/{accountId}/organized-events/{eventId}/participants
```
**Response**

```json
[
  {
    "registrationId": "44daf0a2-ffce-4366-bf93-18da55f6f24e",
    "accountId": "fcf41137-d0ef-434d-a170-8bb815b67809",
    "username": "TestingUser",
    "state": "CONFIRMED",
    "dateTime": "2025-09-01T17:24:32"
  }
]
```

#### Obtener waitlist de un evento

```
GET /accounts/{accountId}/organized-events/{eventId}/waitlist
```

**Response**

```json
[
  {
    "registrationId": "55aaa0a2-ffce-4366-bf93-18da55f6f24e",
    "accountId": "11111137-d0ef-434d-a170-8bb815b67809",
    "username": "UserInWaitlist",
    "state": "WAITLIST",
    "dateTime": "2025-09-01T17:24:32"
  }
]
```

#### Cerrar inscripciones de un evento

```
POST /accounts/{accountId}/organized-events/{eventId}/close
```

**Response**

```
"Las inscripciones al evento fueron cerradas correctamente."
```

---

### 4️⃣ Registration (usuario)

#### Obtener inscripción específica

```
GET /accounts/{accountId}/registrations/{registrationId}
```

**Response**

```json
{
  "registrationId": "44daf0a2-ffce-4366-bf93-18da55f6f24e",
  "eventId": "9a15c50f-c056-4023-90f1-2a94e863e977",
  "accountId": "fcf41137-d0ef-434d-a170-8bb815b67809",
  "state": "CONFIRMED",
  "dateTime": "2025-09-01T17:24:32"
}
```

#### Cancelar inscripción

```
DELETE /accounts/{accountId}/registrations/{registrationId}
```

**Response**

```json
{
  "canceled": true
}
```
---

### 5️⃣ Admin

#### Crear evento

```
POST /admin/event
Content-Type: application/json
```

**Request**

```json
{
  "title": "Concierto de Rock",
  "description": "Rock en vivo",
  "location": "Estadio River",
  "startDateTime": "2025-09-15T21:00:00",
  "durationMinutes": 150,
  "maxParticipants": 300,
  "price": 2000,
  "organizerId": "fcf41137-d0ef-434d-a170-8bb815b67809"
}
```

**Response**

```json
{
  "id": "c1b9b8f9-2a8e-4f02-96c7-1234567890ab",
  "title": "Concierto de Rock",
  "description": "Rock en vivo",
  "location": "Estadio River",
  "startDateTime": "2025-09-15T21:00:00",
  "durationMinutes": 150,
  "maxParticipants": 300,
  "price": 2000,
  "organizerId": "fcf41137-d0ef-434d-a170-8bb815b67809",
  "state": "EVENT_OPEN"
}
```

#### Registrar usuario a un evento

```
POST /admin/register
Content-Type: application/json
```

**Request**

```json
{
  "eventId": "9a15c50f-c056-4023-90f1-2a94e863e977",
  "accountId": "fcf41137-d0ef-434d-a170-8bb815b67809"
}
```

**Response**

```
"CONFIRMED" O "WAITLIST"
```

#### Obtener estadísticas

```
GET /admin/stats
```

**Response**

```json
{
  "totalEvents": 10,
  "totalRegistrations": 50,
  "waitlistPromotions": 5,
  "conversionRate": 0.33
}
```



