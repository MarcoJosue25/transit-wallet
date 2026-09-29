package com.transitwallet.transit_wallet.service;

import com.transitwallet.transit_wallet.model.Movimiento;
import com.transitwallet.transit_wallet.model.Tarjeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MovimientoService {

    Page<Movimiento> obtenerPorTarjeta(Tarjeta tarjeta, Pageable pageable);

}
