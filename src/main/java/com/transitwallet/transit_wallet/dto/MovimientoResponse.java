package com.transitwallet.transit_wallet.dto;

import com.transitwallet.transit_wallet.model.enums.TipoMovimiento;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MovimientoResponse {

    private TipoMovimiento tipo;
    private BigDecimal monto;
    private BigDecimal saldoResultante;
    private LocalDateTime fecha;

}
