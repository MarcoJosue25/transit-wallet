# Diagrama de la DB

- Estructura de la Base de Datos del proyecto transit-wallet

```mermaid
erDiagram
    usuarios {
        bigint id PK
        varchar(100) nombre
        varchar(100) email UK
        varchar(255) password
    }
    tarjetas {
        bigint id PK
        bigint usuario_id FK
        varchar(16) numero UK
        decimal(10,2) saldo "CHECK: saldo >= 0"
        enum estado "ACTIVA | BLOQUEADA"
        int usos_por_dia "CHECK: entre 1 y 3"
        bit alerta_saldo_bajo
        datetime fecha_creacion
    }
    movimientos {
        bigint id PK
        bigint tarjeta_id FK
        enum tipo "CONSUMO | RECARGA"
        decimal(10,2) monto
        decimal(10,2) saldo_resultante
        datetime fecha
    }
    solicitud_recarga {
        bigint id PK
        bigint tarjeta_id FK
        decimal(10,2) monto
        enum metodo_pago "TARJETA | YAPE"
        varchar(6) codigo_temporal
        enum estado "CONFIRMADA | EXPIRADA | PENDIENTE"
        datetime fecha_creacion
        datetime fecha_expiracion
    }
```
