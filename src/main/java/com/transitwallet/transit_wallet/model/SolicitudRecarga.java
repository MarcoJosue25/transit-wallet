package com.transitwallet.transit_wallet.model;

import com.transitwallet.transit_wallet.model.enums.EstadoRecarga;
import com.transitwallet.transit_wallet.model.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name="solicitud_recarga")

public class SolicitudRecarga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name= "tarjeta_id", nullable = false)
    private Tarjeta tarjeta;

    @Column(nullable = false,updatable = false,precision = 10,scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,updatable = false)
    private MetodoPago metodoPago;

    @Column(length = 6,nullable = false,updatable = false)
    private String codigoTemporal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRecarga estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaExpiracion;

    @PrePersist void alCrear(){
        this.fechaCreacion = LocalDateTime.now();
        this.fechaExpiracion = fechaCreacion.plusMinutes(5);
    }
}
