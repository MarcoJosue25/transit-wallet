# transit-wallet

Backend de una billetera virtual para una tarjeta de transporte público. Lo hice para practicar Java y Spring Boot con un problema que me resulta cercano: en el transporte público de Lima cuesta saber cuánto saldo tienes, recargar la tarjeta no siempre es cómodo y casi nunca puedes ver en qué se fue cada descuento. Aquí el saldo, la recarga y el historial de movimientos se pueden consultar con claridad en cualquier momento.

El nombre es genérico a propósito: parte de ese problema, pero no pretende replicar ninguna tarjeta ni empresa real.

## Qué hace

- Registro e inicio de sesión. La autenticación usa JWT, sin sesiones en el servidor.
- Al registrarte se crea tu tarjeta virtual, con un número único de 16 dígitos.
- Recarga en dos pasos. Primero se pide la recarga (método `YAPE` o `TARJETA`) y el sistema devuelve un código de 6 dígitos que vence a los 5 minutos. Luego se confirma con ese código y se suma el saldo. Si la tarjeta estaba bloqueada, se reactiva.
- Descuento automático. Tres veces al día (8:00, 13:00 y 18:00) un proceso programado cobra una tarifa de 2,45 a cada tarjeta activa, según cuántas veces al día se usa (de 1 a 3, un valor que se asigna al azar al crearla). Se cobra en las primeras franjas del día. Si el saldo no alcanza, la tarjeta se bloquea hasta la próxima recarga.
- Alerta de saldo bajo cuando quedan menos de 5,00.
- Consulta de saldo e historial de movimientos (recargas y consumos), paginado y del más reciente al más antiguo.

## Tecnologías
Java 21, Spring Boot 4.1.1, Spring Data JPA (Hibernate), Spring Security con JWT (JJWT), MySQL 8.4, Maven, Lombok, JUnit 5 y Mockito, Docker y Docker Compose.

## Cómo ejecutarlo

### Con Docker

Solo necesitas Docker instalado.

```bash
git clone https://github.com/MarcoJosue25/transit-wallet.git
cd transit-wallet
cp .env.example .env
```

Abre el archivo `.env` y reemplaza los valores de ejemplo por los tuyos. Para `JWT_SECRET` necesitas una cadena larga y aleatoria, por ejemplo:

```bash
openssl rand -base64 48
```

Después:

```bash
docker compose up --build
```

La API queda en `http://localhost:8081`. La primera vez tarda unos minutos, porque descarga las dependencias.

Para apagarlo:

```bash
docker compose down        # conserva los datos
docker compose down -v     # borra también la base de datos
```

### Sin Docker

Necesitas Java 21 y un MySQL con una base llamada `db_transit_wallet` (conviene crearla con la colación `utf8mb4_0900_as_ci`, para que distinga tildes). Define las variables de entorno `DB_USER`, `DB_PASSWORD` y `JWT_SECRET` y ejecuta:

```bash
./mvnw spring-boot:run
```

## Endpoints

| Método | Ruta | Requiere token | Qué hace |
|---|---|---|---|
| POST | `/api/usuarios/registro` | No | Crea el usuario y su tarjeta |
| POST | `/api/usuarios/login` | No | Devuelve el token JWT |
| POST | `/api/tarjetas/recargas/iniciar` | Sí | Genera el código temporal de recarga |
| POST | `/api/tarjetas/recargas/confirmar` | Sí | Confirma la recarga con el código y suma el saldo |
| GET | `/api/tarjetas/mi-tarjeta` | Sí | Número, saldo, estado y alerta de la tarjeta |
| GET | `/api/tarjetas/movimientos` | Sí | Historial paginado (`?page=0&size=10`) |

El token se envía en el encabezado `Authorization: Bearer <token>`.

Un ejemplo de flujo completo:

```bash
# 1. Registro
curl -X POST http://localhost:8081/api/usuarios/registro \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana","email":"ana@correo.com","password":"clave12345"}'

# 2. Login (devuelve {"token": "..."})
curl -X POST http://localhost:8081/api/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ana@correo.com","password":"clave12345"}'

# 3. Pedir una recarga (devuelve el código de 6 dígitos)
curl -X POST http://localhost:8081/api/tarjetas/recargas/iniciar \
  -H "Authorization: Bearer TU_TOKEN" -H "Content-Type: application/json" \
  -d '{"monto":50.00,"metodoPago":"YAPE"}'

# 4. Confirmarla con ese código
curl -X POST http://localhost:8081/api/tarjetas/recargas/confirmar \
  -H "Authorization: Bearer TU_TOKEN" -H "Content-Type: application/json" \
  -d '{"codigoTemporal":"123456"}'
```

Los errores responden con el código HTTP que corresponde y un cuerpo `{"mensaje": "..."}`: 409 si el email ya está registrado, 401 por credenciales inválidas, 404 si el código no existe y 400 si el código venció o ya se usó.

## Decisiones de diseño

- El dinero va en `BigDecimal`, con columnas `decimal(10,2)`. Una restricción en la base impide que el saldo sea negativo.
- Confirmar una recarga toca tres tablas (tarjeta, solicitud y movimiento). Todo ocurre dentro de una sola transacción: se guarda completo o no se guarda nada.
- El proyecto se organiza en controladores, servicios (con su interfaz) y repositorios. Las respuestas usan DTOs para no exponer las entidades ni la contraseña.
- Las contraseñas se guardan con BCrypt y la autenticación no usa sesión: cada petición lleva su token.
- El vencimiento de 5 minutos del código se calcula al crear la solicitud y se compara con la hora actual al confirmarla.

## Pruebas

```bash
./mvnw test -Dtest=SolicitudRecargaServiceImplTest
```

Son 6 pruebas unitarias del servicio de recargas, con JUnit 5 y Mockito y sin base de datos: código inexistente, solicitud ya procesada, código vencido, y tres confirmaciones exitosas (actualización del saldo, reactivación de una tarjeta bloqueada y apagado de la alerta de saldo bajo).

`./mvnw test` sin filtro también ejecuta un test de arranque de Spring que necesita la base de datos y las variables de entorno.

## Limitaciones conocidas

- Los DTOs de entrada no validan los datos.
- Solo está probado el servicio de recargas. Faltan pruebas de los demás servicios y de integración.
- La concurrencia no está controlada: dos confirmaciones simultáneas del mismo código podrían procesarse a la vez.
- Los códigos temporales de 6 dígitos no se comprueban como únicos.
- El esquema de la base se genera con `ddl-auto: update`. En un entorno real usaría migraciones versionadas.

