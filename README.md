# squirtle-squad-chat-service

Para clonar y levantar el entorno local de desarrollo con un solo comando, sigue estos pasos:

1. **Clonar el repositorio y entrar a la carpeta del microservicio**:
   ```bash
   git clone <URL_DEL_REPO>
   cd squirtle-squad-chat-service
   ```

2. **Cambiar a la rama de desarrollo (develop)**:
   ```bash
   git checkout develop
   ```
   *(Nota: Si quieres probar esta feature específica antes de integrarla, usa `git checkout feature/setup-devops`)*

3. **Levantar los servicios con Docker Compose**:
   ```bash
   docker-compose up -d --build
   ```
   Este comando levantará:
   - El microservicio en el puerto `8082`.
   - PostgreSQL 15 en el puerto `5432`.
   - Redis en el puerto `6379`.
   - RabbitMQ en el puerto `5672` (y la consola web de administración en `15672`).

Para detener los servicios, simplemente ejecuta:
```bash
docker-compose down
```

---

## Tabla de Contenido

1. [Descripción del Módulo](#1-descripción-del-módulo)
2. [Análisis de Tecnologías y Decisiones de Diseño Arquitectónico](#2-análisis-de-tecnologías-y-decisiones-de-diseño-arquitectónico)
    - 2.1 [Lenguaje de Programación](#21-lenguaje-de-programación)
    - 2.2 [Framework](#22-framework)
    - 2.3 [Base de Datos](#23-base-de-datos)
    - 2.4 [Protocolo de Comunicación en Tiempo Real](#24-protocolo-de-comunicación-en-tiempo-real)
    - 2.5 [Herramientas de Desarrollo](#25-herramientas-de-desarrollo)
3. [Diagrama de Contexto del Módulo](#3-diagrama-de-contexto-del-módulo)
4. [Diagrama de Datos](#4-diagrama-de-datos)
5. [Diagrama de Clases](#5-diagrama-de-clases)
6. [Planteamiento de la Arquitectura](#6-planteamiento-de-la-arquitectura)
7. [Diagrama de Componentes](#7-diagrama-de-componentes)
8. [Diagrama de Despliegue](#8-diagrama-de-despliegue)
9. [Funcionalidades](#9-funcionalidades)
    - 9.1 [RF07 — Gestión de Conexiones](#91-rf07--gestión-de-conexiones)
    - 9.2 [RF10 — Chat en Tiempo Real](#92-rf10--chat-en-tiempo-real)
10. [Manejo de Errores](#10-manejo-de-errores)
11. [Fuentes Bibliográficas](#11-fuentes-bibliográficas)
12. [Historial de Revisión](#12-historial-de-revisión)

---

## 1. Descripción del Módulo

El **Chat Service** es el microservicio de PATRICIA — ECI Social Campus encargado de gestionar la comunicación directa entre estudiantes. Cubre dos responsabilidades principales: la **mensajería instantánea dentro de los parches** y la **gestión de conexiones de amistad** entre usuarios de la plataforma.

El servicio provee un canal de comunicación en tiempo real mediante WebSockets, permitiendo que los miembros de un parche intercambien mensajes de texto y multimedia ligera de forma instantánea. Adicionalmente, expone endpoints REST para la consulta paginada del historial de mensajes y el control del estado de conexión entre usuarios (enviar, aceptar, rechazar y listar solicitudes de amistad).

El acceso al canal de chat de cada parche está restringido exclusivamente a sus miembros activos. La verificación de membresía se realiza mediante integración con el **Parche Service** (Equipo 3) a través de Feign Client, y toda solicitud es autenticada contra el **Auth Service** (Equipo 1) mediante JWT.

Este microservicio forma parte del módulo de Comunicación (Equipo 3) y se despliega de forma independiente, garantizando que su eventual fallo no afecte el funcionamiento de los módulos core de la plataforma.

**Requerimientos que cubre:**
- `RF07` — Gestión de Conexiones: enviar, aceptar, rechazar y listar solicitudes de amistad.
- `RF10` — Chat en Tiempo Real: mensajería instantánea dentro de cada parche con soporte para texto y multimedia ligera.

---

## 2. Análisis de Tecnologías y Decisiones de Diseño Arquitectónico

### 2.1 Lenguaje de Programación

| Criterio | <!-- tecnología A --> | <!-- tecnología B --> |
|---|---|---|
| <!-- criterio --> | | |

> **Selección:** <!-- tecnología seleccionada -->
> **Razón:** <!-- justificación -->

---

### 2.2 Framework

| Criterio | <!-- tecnología A --> | <!-- tecnología B --> |
|---|---|---|
| <!-- criterio --> | | |

> **Selección:** <!-- tecnología seleccionada -->
> **Razón:** <!-- justificación -->

---

### 2.3 Base de Datos

| Criterio | <!-- tecnología A --> | <!-- tecnología B --> |
|---|---|---|
| <!-- criterio --> | | |

> **Selección:** <!-- tecnología seleccionada -->
> **Razón:** <!-- justificación -->

---

### 2.4 Protocolo de Comunicación en Tiempo Real

| Criterio | <!-- tecnología A --> | <!-- tecnología B --> |
|---|---|---|
| <!-- criterio --> | | |

> **Selección:** <!-- tecnología seleccionada -->
> **Razón:** <!-- justificación -->

---

### 2.5 Herramientas de Desarrollo

| Herramienta | Uso |
|---|---|
| <!-- herramienta --> | <!-- uso --> |

---

## 3. Diagrama de Contexto del Módulo

> _Diagrama por agregar._

<!-- Descripción del diagrama -->

---

## 4. Diagrama de Datos

> _Diagrama por agregar._

### Documentos / Colecciones

| Documento | Descripción |
|---|---|
| <!-- documento --> | <!-- descripción --> |

---

## 5. Diagrama de Clases

> _Diagrama por agregar._

<!-- Descripción de la arquitectura y patrones utilizados -->

---

## 6. Planteamiento de la Arquitectura

<!-- Descripción de la arquitectura limpia aplicada al módulo -->

---

## 7. Diagrama de Componentes

> _Diagrama por agregar._

<!-- Descripción de la interacción con otros módulos -->

---

## 8. Diagrama de Despliegue

> _Diagrama por agregar._

<!-- Descripción del despliegue -->

---

## 9. Funcionalidades

### 9.1 RF07 — Gestión de Conexiones

> _Diagrama de secuencia por agregar._

<!-- Descripción del flujo -->

#### Endpoints

##### `POST /api/connections/request`

**Request Body:**

| Campo | Tipo | Restricciones | Obligatorio |
|---|---|---|---|
| <!-- campo --> | <!-- tipo --> | <!-- restricciones --> | <!-- sí/no --> |

**Response `201`:**

| Campo | Tipo | Descripción |
|---|---|---|
| <!-- campo --> | <!-- tipo --> | <!-- descripción --> |

---

##### `PATCH /api/connections/{connectionId}`

**Request Body:**

| Campo | Tipo | Restricciones | Obligatorio |
|---|---|---|---|
| <!-- campo --> | <!-- tipo --> | <!-- restricciones --> | <!-- sí/no --> |

**Response `200`:**

| Campo | Tipo | Descripción |
|---|---|---|
| <!-- campo --> | <!-- tipo --> | <!-- descripción --> |

---

##### `GET /api/connections`

**Response `200`:**

| Campo | Tipo | Descripción |
|---|---|---|
| <!-- campo --> | <!-- tipo --> | <!-- descripción --> |

---

### 9.2 RF10 — Chat en Tiempo Real

> _Diagrama de secuencia por agregar._

<!-- Descripción del flujo -->

#### WebSocket

##### `CONNECT /ws/chat`

<!-- Descripción de la conexión WebSocket -->

##### `SEND /app/parches/{parcheId}/messages`

**Payload:**

| Campo | Tipo | Restricciones | Obligatorio |
|---|---|---|---|
| <!-- campo --> | <!-- tipo --> | <!-- restricciones --> | <!-- sí/no --> |

**Broadcast `/topic/parches/{parcheId}`:**

| Campo | Tipo | Descripción |
|---|---|---|
| <!-- campo --> | <!-- tipo --> | <!-- descripción --> |

---

#### REST

##### `GET /api/parches/{parcheId}/messages`

**Query Params:**

| Parámetro | Tipo | Descripción |
|---|---|---|
| page | Integer | Número de página |
| size | Integer | Tamaño de página |

**Response `200`:**

| Campo | Tipo | Descripción |
|---|---|---|
| <!-- campo --> | <!-- tipo --> | <!-- descripción --> |

---

## 10. Manejo de Errores

| Código HTTP | Tipo | Escenario | Excepción |
|---|---|---|---|
| <!-- código --> | <!-- tipo --> | <!-- escenario --> | <!-- excepción --> |

---

## 11. Fuentes Bibliográficas

- <!-- fuente -->

---

## 12. Historial de Revisión

| Versión | Fecha | Autor | Descripción |
|---|---|---|---|
| 1.0.0 | <!-- fecha --> | <!-- autor --> | Creación inicial del documento |