package com.transitwallet.transit_wallet.dto;

import com.transitwallet.transit_wallet.model.enums.MetodoPago;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class IniciarRecargaRequest {
    private BigDecimal monto;
    private MetodoPago metodoPago;
}
