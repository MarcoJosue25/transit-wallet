package com.transitwallet.transit_wallet.dto;

import com.transitwallet.transit_wallet.model.enums.EstadoTarjeta;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TarjetaResponse {

    private String numero;
    private BigDecimal saldo;
    private EstadoTarjeta estado;
}
