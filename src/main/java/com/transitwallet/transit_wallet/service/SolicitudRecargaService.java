package com.transitwallet.transit_wallet.service;

import com.transitwallet.transit_wallet.model.SolicitudRecarga;
import com.transitwallet.transit_wallet.model.Tarjeta;
import com.transitwallet.transit_wallet.model.enums.MetodoPago;

import java.math.BigDecimal;

public interface SolicitudRecargaService {
    SolicitudRecarga iniciar(Tarjeta tarjeta, BigDecimal monto, MetodoPago metodoPago);

    BigDecimal confirmar(String codigoTemporal);
}
