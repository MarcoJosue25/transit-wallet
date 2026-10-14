package com.transitwallet.transit_wallet.service;

import com.transitwallet.transit_wallet.model.SolicitudConsumo;
import com.transitwallet.transit_wallet.model.Tarjeta;

public interface SolicitudConsumoService {

    SolicitudConsumo consumir (Tarjeta tarjeta);
}
