# transit-wallet

Backend de una billetera virtual para una tarjeta de transporte público. Lo hice para practicar Java y Spring Boot con un problema que me resulta cercano: en el transporte público de Lima cuesta saber cuánto saldo tienes, recargar no siempre es cómodo y casi nunca ves en qué se fue cada descuento. Aquí el saldo, la recarga y el historial se consultan con claridad.

El nombre es genérico a propósito: no pretende replicar ninguna tarjeta ni empresa real.

## Qué hace

- Registro e inicio de sesión con JWT, sin sesiones en el servidor y con contraseñas guardadas con BCrypt. El usuario puede ver su perfil, editarlo y darse de baja.
- Al registrarte se crea tu tarjeta virtual, con un número único de 16 dígitos.
- Recarga en dos pasos: se pide (`YAPE` o `TARJETA`) y el sistema devuelve un código de 6 dígitos que vence a los 5 minutos; luego se confirma con ese código y se suma el saldo. Si la tarjeta estaba sin saldo, se reactiva; si está bloqueada, la recarga se rechaza.
- Descuento automático a las 8:00, 13:00 y 18:00: cobra 2,45 a cada tarjeta activa según sus usos diarios (de 1 a 3, asignados al azar al crearla). Si el saldo no alcanza, la tarjeta queda en `SALDO_INSUFICIENTE` hasta la próxima recarga.
- Alerta de saldo bajo (menos de 5,00) e historial de movimientos paginado, del más reciente al más antiguo.

## Tecnologías

Java 21, Spring Boot 4.1.1, Spring Data JPA (Hibernate), Spring Security con JWT, Bean Validation, MySQL 8.4, Maven, Lombok, JUnit 5 y Mockito, Swagger (springdoc OpenAPI), Docker y Docker Compose.

## Cómo ejecutarlo

### Con Docker

Solo necesitas Docker instalado.

```bash
git clone https://github.com/MarcoJosue25/transit-wallet.git
cd transit-wallet
cp .env.example .env
```

Edita `.env` y reemplaza los valores de ejemplo por los tuyos. Para `JWT_SECRET` usa una cadena larga y aleatoria, por ejemplo con `openssl rand -base64 48`. Después:

```bash
docker compose up --build
```

La API queda en `http://localhost:8081`. Para apagarla: `docker compose down` (conserva los datos) o `docker compose down -v` (borra también la base).

### Sin Docker

Necesitas Java 21 y un MySQL con una base `db_transit_wallet` (conviene crearla con la colación `utf8mb4_0900_as_ci`, para que distinga tildes). Define las variables `DB_USER`, `DB_PASSWORD` y `JWT_SECRET` y ejecuta `./mvnw spring-boot:run`.

## API

Con la aplicación en marcha, la documentación interactiva está en `http://localhost:8081/swagger-ui/index.html`. Ahí puedes probar todos los endpoints: haz login, copia el token y pulsa **Authorize**.

| Método | Ruta | Token | Qué hace |
|---|---|---|---|
| POST | `/api/usuarios/registro` | No | Crea el usuario y su tarjeta |
| POST | `/api/usuarios/login` | No | Devuelve el token JWT |
| GET | `/api/usuarios/me` | Sí | Perfil del usuario |
| PUT | `/api/usuarios/me` | Sí | Actualiza el nombre |
| DELETE | `/api/usuarios/me` | Sí | Baja del usuario (204) |
| POST | `/api/tarjetas/recargas/iniciar` | Sí | Genera el código temporal de recarga |
| POST | `/api/tarjetas/recargas/confirmar` | Sí | Confirma la recarga y suma el saldo |
| GET | `/api/tarjetas/mi-tarjeta` | Sí | Número, saldo, estado y alerta de la tarjeta |
| GET | `/api/tarjetas/movimientos` | Sí | Historial paginado (`?page=0&size=10`) |

El token se envía en el encabezado `Authorization: Bearer <token>`.

Los errores responden con un cuerpo `{"mensaje": "..."}` y el código que corresponde: 400 si los datos no son válidos (con el detalle por campo en `errores`) o si el código de recarga venció o ya se usó, 401 por credenciales inválidas o cuenta inactiva, 404 si el código no existe y 409 si el email ya está registrado.

## Decisiones de diseño

- El dinero va en `BigDecimal`, con columnas `decimal(10,2)`. Una restricción en la base impide que el saldo sea negativo.
- Confirmar una recarga toca tres tablas (tarjeta, solicitud y movimiento) y ocurre dentro de una sola transacción: se guarda completo o no se guarda nada.
- El proyecto se organiza en controladores, servicios (con su interfaz) y repositorios. Las respuestas usan DTOs para no exponer las entidades ni la contraseña, y los datos de entrada se validan con Bean Validation.
- La baja de usuario es lógica: conserva el historial, bloquea la tarjeta para congelar el saldo y rechaza el login y las recargas de esa cuenta.

## Pruebas

```bash
./mvnw test
```

Son 30 pruebas con JUnit 5 y Mockito, más un test de arranque de Spring. 21 son unitarias y no usan base de datos: recargas (errores, saldo, reactivación, alerta y cuenta inactiva), consumo (pasaje cobrado y denegado) y usuarios (consulta, edición, cambio de contraseña, baja y login). Las otras 9 prueban los controllers con MockMvc: validación de datos (400), acceso sin token (401) y respuestas como el login, el consumo o la baja (204).

El test de arranque necesita la base de datos y las variables de entorno. Para correr todo lo demás sin ellas: `./mvnw test -Dtest='!TransitWalletApplicationTests'`.

## Limitaciones conocidas

- Faltan pruebas del descuento automático, de la capa JWT, de los servicios de tarjetas y movimientos, y de integración con la base de datos.
- La concurrencia no está controlada: dos confirmaciones simultáneas del mismo código podrían procesarse a la vez.
- El descuento automático guarda cada cambio por separado, sin una transacción por tarjeta.
- Los códigos temporales de 6 dígitos no se comprueban como únicos.
- El esquema de la base se genera con `ddl-auto: update`. En un entorno real usaría migraciones versionadas.
