package com.transitwallet.transit_wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class IniciarRecargaResponse {
    private String codigoTemporal;
    private LocalDateTime fechaExpiracion;
}
