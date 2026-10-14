package com.transitwallet.transit_wallet.service.impl;

import com.transitwallet.transit_wallet.model.Movimiento;
import com.transitwallet.transit_wallet.model.SolicitudConsumo;
import com.transitwallet.transit_wallet.model.Tarjeta;
import com.transitwallet.transit_wallet.model.enums.EstadoConsumo;
import com.transitwallet.transit_wallet.model.enums.EstadoTarjeta;
import com.transitwallet.transit_wallet.model.enums.TipoMovimiento;
import com.transitwallet.transit_wallet.repository.MovimientoRepository;
import com.transitwallet.transit_wallet.repository.SolicitudConsumoRepository;
import com.transitwallet.transit_wallet.repository.TarjetaRepository;
import com.transitwallet.transit_wallet.service.SolicitudConsumoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class SolicitudConsumoServiceImpl implements SolicitudConsumoService{

    private static final BigDecimal PASAJE = new BigDecimal("2.45");
    private static final BigDecimal UMBRAL_SALDO_BAJO = new BigDecimal("5.00");

    private final SolicitudConsumoRepository solicitudConsumoRepository;
    private final TarjetaRepository tarjetaRepository;
    private final MovimientoRepository movimientoRepository;

    public SolicitudConsumoServiceImpl(SolicitudConsumoRepository solicitudConsumoRepository, TarjetaRepository tarjetaRepository,
                                       MovimientoRepository movimientoRepository) {
        this.solicitudConsumoRepository = solicitudConsumoRepository;
        this.tarjetaRepository = tarjetaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional
    @Override
    public SolicitudConsumo consumir (Tarjeta tarjeta){

        boolean puedePagar = tarjeta.getEstado() == EstadoTarjeta.ACTIVA
                && tarjeta.getSaldo().compareTo(PASAJE) >= 0;

            SolicitudConsumo consumo = new SolicitudConsumo();
            consumo.setTarjeta(tarjeta);
            consumo.setMonto(PASAJE);
            consumo.setEstado(puedePagar ? EstadoConsumo.CONFIRMADO : EstadoConsumo.DENEGADO);
            solicitudConsumoRepository.save(consumo);

            if(!puedePagar){
                return consumo;
            }

            tarjeta.setSaldo(tarjeta.getSaldo().subtract(PASAJE));
            if(tarjeta.getSaldo().compareTo(UMBRAL_SALDO_BAJO) < 0){
                tarjeta.setAlertaSaldoBajo(true);
            }

            tarjetaRepository.save(tarjeta);

            Movimiento movimiento = new Movimiento();
            movimiento.setTarjeta(tarjeta);
            movimiento.setTipo(TipoMovimiento.CONSUMO);
            movimiento.setMonto(PASAJE);
            movimiento.setSaldoResultante(tarjeta.getSaldo());
            movimientoRepository.save(movimiento);

            return consumo;
    }
}
