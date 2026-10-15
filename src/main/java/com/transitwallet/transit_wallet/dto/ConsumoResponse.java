package com.transitwallet.transit_wallet.dto;

import com.transitwallet.transit_wallet.model.enums.EstadoConsumo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ConsumoResponse {
    private EstadoConsumo estado;
    private BigDecimal saldo;
}
