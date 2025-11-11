
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
     - interfazweb

3. Introducir las variables de entorno por consola:
   - EVENTOS_TELEGRAM_BOT_TOKEN
   - EVENTOS_TELEGRAM_BOT_USERNAME
   - EVENTOS_SERVER_SECRET_KEY
   - ADMIN_USERNAME
   - ADMIN_PASSWORD

### Con Docker Manual:
1. Abrir Docker Desktop si no está abierto.


2. Crear un archivo .env con las variables de entorno:
    - EVENTOS_TELEGRAM_BOT_TOKEN
    - EVENTOS_TELEGRAM_BOT_USERNAME
    - EVENTOS_SERVER_SECRET_KEY
    - ADMIN_USERNAME
    - ADMIN_PASSWORD



3. Construir y ejecutar red de contenedores:

```bash
docker-compose up --build
```
4. Comandos adicionales:

Para iniciar el sistema una vez ya fue compilado se debe usar:

```bash
docker-compose up
```

Para apagar el sistema se usa:

```bash
docker-compose down
```

Para apagar el sistema y borrar todos los datos del mismo se usa

```bash
docker-compose down -v
```

#
Tras eso, el servidor estará escuchando peticiones en el puerto 8080.
Se puede comprobar haciendo alguna de las peticiones de ejemplo que se muestran abajo.


---
## Configuraciones adicionales
### 1. Archivos config.properties 
Dentro de los modulos servidor y telegrambot se encuentra el archivo

>src/main/resources/config.properties

Este archivo se puede modificar para alterar el comportamiento de los modulos, permitiendo definir el tamaño máximo y minimo de las páginas en la API y el tiempo de expiración del token de seguridad, entre otras cuestiones.

### 2. Archivos translations.csv
Dentro de los modulos servidor y telegram ofrecemos soporte para i18n. Para esto utilizamos un archivo translations.csv donde se pueden modificar los mensajes de respuesta para cada idioma.

Estos archivos se encuentran en:

> \<Modulo\>/src/main/resources/translations.csv

En el [servidor / backend](https://github.com/lucas-prtt/TACS-TP-2025-Grupo4/blob/dev/Servidor/src/main/resources/translations.csv), estas traducciones son solo detalles de errores HTTP personalizados, siendo el idioma especificado en el header Accept-Language. Los archivos Json de respuesta no cambian según el idioma. En el [bot de telegram](https://github.com/lucas-prtt/TACS-TP-2025-Grupo4/blob/dev/TelegramBot/src/main/resources/translations.csv), dicta los idiomas que estarán disponibles desde el menu de idiomas y que afectarán la interfaz de todo el sistema para cada usuario particular, segun el idioma que tenga configurado. 

En cualquiera de los dos casos, es posible expandir los lenguajes admitidos (Actualmente hay 6 por defecto, traducidos por Google Translate) agregando más columnas al csv, sin que sea necesario modificar el código. El sistema detectará automaticamente el nuevo lenguaje y lo pondrá a disposición del usuario.

---

## Componentes / Contenedores del sistema

Para el funcionamiento completo del sistema se inician los siguientes componentes:
1. Servidor: El backend que se usa para gestionar todas las peticiones mediante una API rest. Se inicia con el JavaClient de Opentelemetry para proveer información sobre su funcionamiento.
2. Interfaz web: Permite al usuario gestionar sus eventos, inscribirse a eventos, ver inscripciones, descubrir eventos nuevos y cancelar sus inscripciones. Al mismo tiempo ofrece funcionalidades para los administradores, siendo estas la visualizacion de estadisticas y la creacion y eliminacion de categorias.
3. Bot de telegram: Permite lo mismo que la interfaz web, a través de un bot de telegram conectado mediante long-polling.
4. MongoDB (3 instancias): Utilizado por el backend para persistir la información necesaria para el funcionamiento del sistema. Al utilizar 3 instancias aumenta la fiabilidad del sistema, ya que en caso de que una se perdiera, el sistema podría recuperarse automáticamente.
5. Redis: Utilizado para la gestion de sesiones del bot de telegram. Permite distribuir la carga de mantener en memoria las sesiones a otro nodo, cuenta con persistencia ante fallos en el nodo y permitiría a futuro levantar multiples instancias para la ejecución del bot sin problemas de estado.
6. OpenTelemetry Collector: Utilizado para recopilar toda la información de telemetría aportada por el servidor backend. Actualmente solo integrado con Jaeger, pero puede integrarse con otros componentes.
7. Jaeger: Para ver los spans mandados por el servidor a través del OpenTelemetry collector. Utiliza una base de datos embebida provista por Badger con rotación de logs cada 72h. 
8. Mongo-init: Componente temporal utilizado para inicializar las instancias de mongo mediante un script de bash antes de iniciar el sistema. Tras su ejecución satisfactoria se cierra automáticamente.

Aclaración: Logramos conectar el OpenTelemetry collector con Prometheus y Grafana para exportar métricas y establecer alarmas, pero no lo supimos configurarlos a tiempo para la entrega. El progreso realizado fue dejado en otra branch (prometheus-y-grafana, commit [#a201b2c](https://github.com/lucas-prtt/TACS-TP-2025-Grupo4/tree/a201b2cb9da5c7309172a5351d198c3c4acfb2f5)).

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

## Estrategia de Concurrencia

### 1. Operaciones Atómicas (MongoTemplate)

Problema: La "condición de carrera" (Race Condition), como dos usuarios intentando tomar el último cupo (availableSeats) al mismo tiempo.

Solución: Usar una operación atómica de MongoTemplate (updateFirst con $inc: -1 y una condición availableSeats > 0).  Se evita el patrón peligroso de "Leer y luego Escribir". 

Mecanismo Detallado:

1. Arbitraje: Dos Instancias (A y B) envían el comando simultáneamente a MongoDB. La base de datos, al ser el árbitro, los serializa y le concede el Bloqueo Exclusivo (X) ✍️ a la Instancia A.

2. Ejecución de A (Ganador): La Instancia A verifica internamente que el filtro (availableSeats > 0) sea verdadero. Lo es. Ejecuta $inc: -1. El cupo en la BD se convierte en 0.

3. Ejecución de B (Perdedor): La Instancia B obtiene el Bloqueo. Verifica el filtro (availableSeats > 0) y encuentra que ahora es falso. La operación de actualización no se ejecuta.

4. Solo la Instancia A obtiene un modifiedCount: 1 y procede. La Instancia B obtiene modifiedCount: 0 y es enviada a la Waitlist.

Resultado: La base de datos actúa como un "portero" y solo permite que una operación (la primera que llega) tenga éxito. Es imposible sobrevender cupos.

### 2. Transacciones (@Transactional)

Problema: Asegurar que operaciones complejas sean " Todo o nada". Por ejemplo, al registrar, se debe crear una Registration y actualizar el array participants del Event.
Si una de esas dos falla, los datos quedan inconsistentes.

Solución: Usar @Transactional. Esto envuelve todas las operaciones de la base de datos en un "paquete" (transacción ACID).

Mecanismo: Si cualquier escritura falla (ej., un fallo de red o una excepción de la base de datos), Spring fuerza un ROLLBACK. Todos los cambios que la transacción hizo antes de fallar son deshechos, restaurando la base de datos a su estado inicial.

Resultado: Si cualquier parte del método falla (ej. una DuplicateKeyException), todo se deshace (Rollback). Se garantiza la consistencia total de los datos.


### 3. El Plan B: Reintentos (@Retryable)

Problema: Cuando dos transacciones (@Transactional) chocan al intentar modificar el mismo documento al mismo tiempo, la base de datos se protege abortando una de ellas (un deadlock o conflicto).

Solución: Usar @Retryable(retryFor = { TransientDataAccessException.class }).

Mecanismo:

1. Cuando MongoDB aborta una transacción, lanza la excepción TransientDataAccessException.

2. @Retryable captura esta excepción.

3. El framework espera un tiempo predefinido y vuelve a ejecutar todo el método automáticamente.

Resultado: La aplicación detecta ese aborto (que es un error temporal), espera un momento y reintenta automáticamente la operación. El usuario final no ve un error, solo un pequeño retraso.

---
# API Endpoints

---
## Admin Controller


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

