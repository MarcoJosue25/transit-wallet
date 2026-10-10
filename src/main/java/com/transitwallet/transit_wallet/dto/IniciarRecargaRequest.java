package com.transitwallet.transit_wallet.dto;

import com.transitwallet.transit_wallet.model.enums.MetodoPago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class IniciarRecargaRequest {

    @NotNull @Positive
    private BigDecimal monto;

    @NotNull
    private MetodoPago metodoPago;
}
