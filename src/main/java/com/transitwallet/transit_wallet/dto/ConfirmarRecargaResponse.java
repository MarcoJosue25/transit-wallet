package com.transitwallet.transit_wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ConfirmarRecargaResponse {
    private BigDecimal saldo;
}
