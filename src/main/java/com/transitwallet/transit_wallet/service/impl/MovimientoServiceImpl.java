package com.transitwallet.transit_wallet.service.impl;

import com.transitwallet.transit_wallet.model.Movimiento;
import com.transitwallet.transit_wallet.model.Tarjeta;
import com.transitwallet.transit_wallet.repository.MovimientoRepository;
import com.transitwallet.transit_wallet.service.MovimientoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;;


@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;

    public MovimientoServiceImpl(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }



    @Override
    public Page<Movimiento> obtenerPorTarjeta(Tarjeta tarjeta, Pageable pageable){
        return movimientoRepository.findByTarjetaId(tarjeta.getId(), pageable);
    }
}
