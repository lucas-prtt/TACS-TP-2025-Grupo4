
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


#
Tras eso, el servidor estará escuchando peticiones en el puerto 8080.
Se puede comprobar haciendo alguna de las peticiones de ejemplo que se muestran abajo.


---
## Configuraciones adicionales
Dentro de cada módulo se encuentra el archivo

>src/main/resources/config.properties

Este archivo se puede modificar para alterar el comportamiento de los modulos, permitiendo definir el tamaño máximo y minimo de las páginas en la API y el tiempo de expiración del token de seguridad, entre otras cuestiones.

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
# API Endpoints

---
## Admin Controller

### Crear una nueva categoría

```
POST /admin/categories
```

***Request Body***

```json
{
  "title": "WORKSHOP"
}
```

***Response***

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "title": "WORKSHOP"
}
```

### Eliminar una categoría

```
DELETE /admin/categories/{titulo}
```

***Response***

```
204 No Content
```

---

### Obtener estadísticas

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
---
## Auth Controller

### Registro de usuario
```
POST /auth/register
```

***Request Body*** 
```json
{
   "username": "usuario123",
   "password": "claveSegura"
}
```


***Response***
```json
{
   "uuid": "550e8400-e29b-41d4-a716-446655440000",
   "username": "usuario123"
}
```

### Login

```
POST /auth/login
```
***Request Body***

```json
{
   "username": "usuario123",
   "password": "claveSegura"
}
```


***Response***

```json
{
   "username": "usuario123",
   "id": "550e8400-e29b-41d4-a716-446655440000",
   "roles": ["USER"],
   "token": "jwt.token.aquí"
}
```

### Crear código de un solo uso

```
POST /auth/oneTimeCode
```

***Response***

```json
{
   "cosaDelLogueo": {
   "username": "usuario123",
   "id": "550e8400-e29b-41d4-a716-446655440000",
   "roles": ["USER"],
   "token": "jwt.token.aquí"
   },
   "code": "palabra1-palabra2-palabra3-palabra4-palabra5-palabra6"
}
```

#### Obtener token con código de un solo uso

```
GET /auth/oneTimeCode?username=usuario123&code=palabra1-palabra2-palabra3...
```
***Response***

```json
{
   "username": "usuario123",
   "id": "550e8400-e29b-41d4-a716-446655440000",
   "roles": ["USER"],
   "token": "jwt.token.aquí"
}
```

---
## Event Controller

### Crear un nuevo evento

```
POST /events
```
***Request Body***

```json
{
   "title": "Nombre del evento",
   "description": "Descripción del evento",
   "startDateTime": "2025-10-01T18:00:00",
   "durationMinutes": 90,
   "location": "Ciudad X",
   "maxParticipants": 100,
   "minParticipants": 10,
   "price": 50.00,
   "category": "CONFERENCE",
   "tags": ["TECH", "INNOVATION"]
}
```


***Response***

```json
{
   "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
   "title": "Nombre del evento",
   "description": "Descripción del evento",
   "usernameOrganizer": "organizador123",
   "startDateTime": "2025-10-01T18:00:00",
   "durationMinutes": 90,
   "location": "Ciudad X",
   "maxParticipants": 100,
   "minParticipants": 10,
   "price": 50.00,
   "category": "CONFERENCE",
   "tags": ["TECH", "INNOVATION"],
   "state": "EVENT_OPEN"
}
```

### Obtener eventos organizados por el usuario autenticado

```
GET /events/organized-events
```
***Response***

```json
[
   {
      "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "title": "Nombre del evento",
      "description": "Descripción",
      "usernameOrganizer": "organizador123",
      "startDateTime": "2025-10-01T18:00:00",
      "durationMinutes": 90,
      "location": "Ciudad X",
      "maxParticipants": 100,
      "minParticipants": 10,
      "price": 50.00,
      "category": "CONFERENCE",
      "tags": ["TECH"],
      "state": "EVENT_OPEN"
   }
]
```

### Obtener un evento por ID

```
GET /events/{id}
```
***Response***

```json
{
   "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
   "title": "Nombre del evento",
   "description": "Descripción",
   "usernameOrganizer": "organizador123",
   "startDateTime": "2025-10-01T18:00:00",
   "durationMinutes": 90,
   "location": "Ciudad X",
   "maxParticipants": 100,
   "minParticipants": 10,
   "price": 50.00,
   "category": "CONFERENCE",
   "tags": ["TECH"],
   "state": "EVENT_OPEN"
}
```

### Buscar eventos con filtros

```
GET /events
```
***Query Params (opcionales)***

- title: string exacto 
- titleContains: subcadena en el título 
- minDate: ISO datetime 
- maxDate: ISO datetime 
- category: categoría del evento 
- tags: lista de tags 
- minPrice: precio mínimo 
- maxPrice: precio máximo 
- page: número de página 
- limit: cantidad por página

***Response***

```json
[
      {
      "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "title": "Nombre del evento",
      "description": "Descripción",
      "usernameOrganizer": "organizador123",
      "startDateTime": "2025-10-01T18:00:00",
      "durationMinutes": 90,
      "location": "Ciudad X",
      "maxParticipants": 100,
      "minParticipants": 10,
      "price": 50.00,
      "category": "CONFERENCE",
      "tags": ["TECH"],
      "state": "EVENT_OPEN"
   }
]
```

### Registrarse a un evento

```
POST /events/{id}/registrations
```
***Response***

```json
{
   "registrationId": "a1b2c3d4-e5f6-6890-abcd-ef1234567890",
   "eventId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
   "accountId": "a1b2c3d4-e5f6-7890-gbcd-ef1234567890",
   "title": "Nombre del evento",
   "description": "Descripción",
   "state": "CONFIRMED",
   "dateTime": "2025-09-16T12:30:00"
}
```

### Modificar evento

```
PATCH /events/{id}
```
***Request Body***

```json
{
   "title": "Nuevo título",
   "description": "Nueva descripción",
   "price": 75.00,
   "category": "WORKSHOP",
   "location": "Otra ciudad",
   "tags": ["ART"],
   "startDateTime": "2025-11-01T10:00:00",
   "durationMinutes": 120,
   "state": "EVENT_CLOSED"
}
```


***Response***

```json
{
   "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
   "title": "Nuevo título",
   "description": "Nueva descripción",
   "usernameOrganizer": "organizador123",
   "startDateTime": "2025-11-01T10:00:00",
   "durationMinutes": 120,
   "location": "Otra ciudad",
   "maxParticipants": 100,
   "minParticipants": 10,
   "price": 75.00,
   "category": "WORKSHOP",
   "tags": ["ART"],
   "state": "EVENT_CLOSED"
}
```

### Obtener inscripciones de un evento

```
GET /events/{eventId}/registrations
```
***Query Params***

- page: número de página
- limit: cantidad por página
- registrationType: CONFIRMED | WAITLIST | CANCELED (Opcional)

***Response***

```json
[
   {
      "registrationId": "11112222-3333-4444-5555-666677778888",
      "eventId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "accountId": "99998888-7777-6666-5555-444433332222",
      "title": "Nombre del evento",
      "description": "Descripción",
      "state": "CONFIRMED",
      "dateTime": "2025-09-16T12:30:00"
   }
]
```
---
## Registration Controller
### Obtener inscripciones del usuario autenticado

```
GET /registrations
```
***Query Params (opcionales)***
- page: número de página
- limit: cantidad de resultados por página
- registrationState: CONFIRMED | WAITLIST | CANCELED (opcional)

***Response***

```json
[
{
   "registrationId": "11112222-3333-4444-5555-666677778888",
   "eventId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
   "accountId": "99998888-7777-6666-5555-444433332222",
   "title": "Evento de ejemplo",
   "description": "Descripción del evento",
   "state": "CONFIRMED",
   "dateTime": "2025-09-16T12:30:00"
}
]
```

### Obtener una inscripción por ID (del usuario autenticado)

```
GET /registrations/{registrationId}
```
***Response***

```json
{
   "registrationId": "11112222-3333-4444-5555-666677778888",
   "eventId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
   "accountId": "99998888-7777-6666-5555-444433332222",
   "title": "Evento de ejemplo",
   "description": "Descripción del evento",
   "state": "WAITLIST",
   "dateTime": "2025-09-16T12:30:00"
}
```

### Cancelar una inscripción

```
PATCH /registrations/{registrationId}
```
***Request Body***

```json
{
   "state": "CANCELED"
}
```

***Response***

```
204 No Content
```

### Obtener todas las categorías (con paginación y filtro)

```
GET /events/categories?page=0&limit=10&startsWith=C
```

***Query Params (opcionales)***

- **page**: número de página (por defecto 0)
- **limit**: cantidad de categorías por página (por defecto configurado en config.properties)
- **startsWith**: filtrar categorías que comienzan con este texto (case-insensitive, opcional)

***Response***

```json
[
  {
    "title": "CONFERENCE"
  },
  {
    "title": "CONCERT"
  },
  {
    "title": "COURSE"
  }
]
```

---