# Sistema de Gestión de Biblioteca — API REST

Proyecto académico del curso **Desarrollo de los Componentes del Negocio (IV ciclo, IDAT)**. Implementado como backend REST para administrar usuarios, libros y préstamos de una biblioteca.

**Repositorio:** https://github.com/Omar3496/gestion-biblioteca  
**Rama de entrega:** `main`.

## Funcionalidades implementadas

- **Usuarios:** crear, listar, consultar por ID, actualizar y eliminar; asociación a un rol existente.
- **Libros:** crear, listar, consultar por ID o ISBN, actualizar y eliminar; categoría y cantidad de ejemplares disponibles.
- **Préstamos:** registrar, listar, buscar, modificar, eliminar, consultar pendientes/por lector y registrar devolución.
- **Persistencia:** MySQL con Spring Data JPA y validación del esquema al iniciar (`ddl-auto=validate`).

El proyecto es **una API backend**: las respuestas se devuelven como JSON; no incluye una interfaz web para usuarios finales.

## Tecnologías

- Java **21** como nivel de compilación (`pom.xml`; se probó localmente también con JDK 25).
- Spring Boot **4.1.1**, Spring Web MVC, Spring Data JPA, Bean Validation.
- MySQL **8.x** y MySQL Connector/J; Maven Wrapper (`mvnw.cmd` o `./mvnw`).
- Lombok, JUnit y Mockito para pruebas.

## Instalación y ejecución (Windows, PowerShell)

### 1. Requisitos previos

Tener instalados **JDK 21 o superior** y **MySQL 8.x** con un servidor local funcionando en el puerto `3306`. Se recomienda IntelliJ IDEA y MySQL Workbench. El puerto HTTP de la API es `8080`.

### 2. Clonar y abrir el proyecto

```powershell
git clone https://github.com/Omar3496/gestion-biblioteca.git
cd gestion-biblioteca
```

Abrir la carpeta `gestion-biblioteca` en IntelliJ IDEA y permitir la importación del proyecto Maven (`pom.xml`).

### 3. Crear el esquema de MySQL

En **MySQL Workbench**, abrir y ejecutar el archivo del repositorio:

`bd_biblioteca_integrada.sql`

Este script crea `bd_biblioteca` y las tablas `roles`, `usuarios`, `categorias`, `libros`, `lectores` y `prestamos`. **No inserta usuarios, libros, categorías, roles ni lectores de ejemplo.** Para probar las relaciones es necesario tener registros de referencia válidos. No ejecutar el script repetidamente sobre una base antigua esperando que migre columnas: `CREATE TABLE IF NOT EXISTS` no hace migraciones.

Para verificar:

```sql
SHOW TABLES FROM bd_biblioteca;
```

### 4. Configurar credenciales sin subir contraseñas a GitHub

En **la misma terminal PowerShell** donde ejecutarás Maven:

```powershell
$env:DB_USER = "root"
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host "Contraseña de MySQL" -AsSecureString)).Password
```

La contraseña se solicita de forma oculta. La variable dura solamente en esa sesión de PowerShell; no debes incluir la contraseña real en `application.properties` ni en un commit.

La configuración incluida en el repositorio es:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bd_biblioteca?sslMode=REQUIRED&serverTimezone=America/Lima
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=validate
server.port=8080
```

**Nota:** `sslMode=REQUIRED` exige que el servidor MySQL admita TLS. Si el entorno del docente es distinto, deberá adecuar la conexión de forma consciente y segura; la base usada en la práctica fue `bd_biblioteca` (no `db_biblioteca`).

### 5. Ejecutar pruebas y servidor

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

No cerrar la terminal del servidor durante las peticiones. El mensaje esperado es `Tomcat started on port 8080` y `Started GestionBibliotecaApplication`.

En Linux/macOS, sustituir `mvnw.cmd` por `./mvnw` y configurar las variables en el shell correspondiente.

## Endpoints HTTP

| Módulo | Método | Ruta | Uso |
|---|---|---|---|
| Usuarios | GET | `/api/usuarios` | Listar |
| Usuarios | GET | `/api/usuarios/{id}` | Consultar por ID |
| Usuarios | POST | `/api/usuarios` | Crear |
| Usuarios | PUT | `/api/usuarios/{id}` | Actualizar |
| Usuarios | DELETE | `/api/usuarios/{id}` | Eliminar |
| Libros | GET | `/api/libros` | Listar |
| Libros | GET | `/api/libros/{id}` | Consultar por ID |
| Libros | GET | `/api/libros/isbn/{isbn}` | Consultar por ISBN |
| Libros | POST | `/api/libros` | Crear |
| Libros | PUT | `/api/libros/{id}` | Actualizar |
| Libros | DELETE | `/api/libros/{id}` | Eliminar |
| Préstamos | GET | `/api/prestamos` | Listar |
| Préstamos | GET | `/api/prestamos/pendientes` | Listar pendientes |
| Préstamos | GET | `/api/prestamos/lector/{idLector}` | Préstamos por lector |
| Préstamos | GET | `/api/prestamos/{id}` | Consultar por ID |
| Préstamos | POST | `/api/prestamos` | Registrar |
| Préstamos | PUT | `/api/prestamos/{id}` | Actualizar (si no fue devuelto) |
| Préstamos | PATCH | `/api/prestamos/{id}/devolver` | Marcar devuelto |
| Préstamos | DELETE | `/api/prestamos/{id}` | Eliminar |

### Ejemplo: consultar libros

Abrir en el navegador `http://localhost:8080/api/libros` o ejecutar:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/libros" -Method Get
```

### Ejemplo: registrar un libro

Requiere una categoría existente (en este ejemplo se usa el ID `1`):

```powershell
$libro = @{
    titulo = "Libro de prueba"
    autor = "Equipo Biblioteca"
    isbn = "DEMO-BIB-001"
    idCategoria = 1
    stock = 2
} | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/libros" -Method Post -ContentType "application/json" -Body $libro
```

### Ejemplo: registrar un préstamo

Requiere que el lector, el libro y el usuario existan en sus respectivas tablas:

```json
{
  "idLector": 2,
  "idLibro": 3,
  "idUsuario": 1,
  "fechaPrestamo": "2026-11-03",
  "fechaDevolucionPactada": "2026-11-10"
}
```

`POST /api/prestamos`. Para devolver: `PATCH /api/prestamos/{id}/devolver`.

## Estructura del código

```text
src/main/java/org/idat/biblioteca/
├── controller/    # Rutas HTTP y peticiones/respuestas
├── dto/           # Datos de entrada validados de préstamos
├── exception/     # Manejo de errores de préstamos
├── model/         # Entidades JPA y estados
├── repository/    # Acceso a base de datos
├── service/       # Lógica de negocio
└── GestionBibliotecaApplication.java
src/main/resources/application.properties
bd_biblioteca_integrada.sql
```

## Verificaciones realizadas (03/11/2026)

- `mvnw.cmd clean test`: **4 pruebas ejecutadas, 0 fallos, 0 errores**.
- Contexto de Spring Boot y conexión a `bd_biblioteca`: **correctos en el entorno de pruebas**.
- Libro: **GET, GET por ID, POST, PUT y DELETE** probados manualmente.
- Usuario: **GET de listado** respondió HTTP 200; operaciones de escritura no verificadas manualmente en esta sesión.
- Préstamo: **GET de listado, GET de pendientes, POST y PATCH de devolución** probados manualmente. Otras operaciones no verificadas manualmente en esta sesión.
- Integración a `main`: Pull Request **#4** fusionado el **03/11/2026**.

Estas verificaciones no sustituyen pruebas completas de seguridad, concurrencia o uso en producción.

## Limitaciones conocidas

- El servicio de usuarios todavía tiene pendiente cifrar contraseñas con BCrypt antes de almacenarlas; **no usar credenciales reales ni datos sensibles** en este proyecto de demostración. La propiedad `password` se oculta de la respuesta JSON pero **eso no equivale a cifrado en la base**.
- El préstamo y su devolución **no ajustan automáticamente** el número de ejemplares disponibles en `libros`.
- No se incluyen registros iniciales de demostración en el SQL integrado; se deben crear datos relacionados para probar altas.
- No se ha implementado una interfaz visual de usuario final.

## Equipo y contribuciones

- **Omar Daniel Chauca Pérez** (GitHub `Omar3496`): módulo Préstamo, configuración e integración final; comprobaciones de ejecución.
- **Integrante de Usuario** (GitHub `owenpiero123`): modelo/servicio/controlador de usuarios y roles.
- **Integrante de Libro** (GitHub `SiriusLLozano`): desarrollo inicial del módulo Libro, posteriormente adaptado al esquema integrado.

Los nombres completos de los otros dos integrantes pueden añadirse al informe y a este archivo según los datos oficiales del grupo.
