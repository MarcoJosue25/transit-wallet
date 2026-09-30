package com.transitwallet.transit_wallet.model;

import com.transitwallet.transit_wallet.model.enums.EstadoTarjeta;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarjetas")
@Check(name = "chk_tarjeta_saldo_no_negativo", constraints = "saldo >= 0")
@Getter
@Setter
@NoArgsConstructor
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 16)
    private String numero;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTarjeta estado = EstadoTarjeta.ACTIVA;

    @Min(1)
    @Max(3)
    @Column(nullable = false)
    private int usosPorDia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    //Agregado en el último paso
    @Column(name = "alerta_saldo_bajo",nullable = false)
    private boolean alertaSaldoBajo = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    void alCrear() {
        this.fechaCreacion = LocalDateTime.now();
    }
}