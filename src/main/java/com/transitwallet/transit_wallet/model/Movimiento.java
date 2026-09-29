package com.transitwallet.transit_wallet.model;

import com.transitwallet.transit_wallet.model.enums.TipoMovimiento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "movimientos")
@Getter @Setter
@NoArgsConstructor
@Entity
public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tarjeta_id", nullable = false)
    private Tarjeta tarjeta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipo;

    @Column(nullable = false, precision = 10, scale = 2, updatable = false)
    private BigDecimal monto;

    @Column(nullable = false, precision = 10, scale = 2, updatable = false)
    private BigDecimal saldoResultante;

    @Column(updatable = false, nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    void crear() {this.fecha = LocalDateTime.now();}
}
