package com.transitwallet.transit_wallet.service.impl;

import com.transitwallet.transit_wallet.exception.SolicitudRecargaInvalidaException;
import com.transitwallet.transit_wallet.exception.SolicitudRecargaNoEncontradaException;
import com.transitwallet.transit_wallet.model.Movimiento;
import com.transitwallet.transit_wallet.model.SolicitudRecarga;
import com.transitwallet.transit_wallet.model.Tarjeta;
import com.transitwallet.transit_wallet.model.enums.EstadoRecarga;
import com.transitwallet.transit_wallet.model.enums.EstadoTarjeta;
import com.transitwallet.transit_wallet.model.enums.MetodoPago;
import com.transitwallet.transit_wallet.model.enums.TipoMovimiento;
import com.transitwallet.transit_wallet.repository.MovimientoRepository;
import com.transitwallet.transit_wallet.repository.SolicitudRecargaRepository;
import com.transitwallet.transit_wallet.repository.TarjetaRepository;
import com.transitwallet.transit_wallet.service.SolicitudRecargaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SolicitudRecargaServiceImpl implements SolicitudRecargaService {

    private final SolicitudRecargaRepository solicitudRecargaRepository;
    private final TarjetaRepository tarjetaRepository;
    private  final MovimientoRepository movimientoRepository;

    public SolicitudRecargaServiceImpl(MovimientoRepository movimientoRepository, SolicitudRecargaRepository solicitudRecargaRepository, TarjetaRepository tarjetaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.solicitudRecargaRepository = solicitudRecargaRepository;
        this.tarjetaRepository = tarjetaRepository;
    }

    @Override
    public SolicitudRecarga iniciar(Tarjeta tarjeta, BigDecimal monto, MetodoPago metodoPago){
        StringBuilder numero = new StringBuilder(6);
        for(int i = 0; i < 6; i++){
            numero.append(ThreadLocalRandom.current().nextInt(10));
        }
        String n = numero.toString();

        SolicitudRecarga solicitudRecarga = new SolicitudRecarga();
        solicitudRecarga.setTarjeta(tarjeta);
        solicitudRecarga.setMonto(monto);
        solicitudRecarga.setMetodoPago(metodoPago);
        solicitudRecarga.setCodigoTemporal(n);
        solicitudRecarga.setEstado(EstadoRecarga.PENDIENTE);
        return solicitudRecargaRepository.save(solicitudRecarga);

    }

    @Override
    @Transactional
    public BigDecimal confirmar(String codigoTemporal){
        SolicitudRecarga solicitud = solicitudRecargaRepository.findByCodigoTemporal(codigoTemporal).
                orElseThrow(() -> new SolicitudRecargaNoEncontradaException("Código Inválido"));
        if(solicitud.getEstado() != EstadoRecarga.PENDIENTE){
            throw new SolicitudRecargaInvalidaException("Esta solicitud ya fue procesada");
        }
        if(LocalDateTime.now().isAfter(solicitud.getFechaExpiracion())){
            throw new SolicitudRecargaInvalidaException("El código venció");
        }
        Tarjeta tarjeta = solicitud.getTarjeta();
        tarjeta.setSaldo(tarjeta.getSaldo().add(solicitud.getMonto()));
        if(tarjeta.getEstado() == EstadoTarjeta.BLOQUEADA){
            tarjeta.setEstado(EstadoTarjeta.ACTIVA);
        }
        tarjetaRepository.save(tarjeta);

        solicitud.setEstado(EstadoRecarga.CONFIRMADA);
        solicitudRecargaRepository.save(solicitud);

        Movimiento movimiento = new Movimiento();
        movimiento.setTarjeta(tarjeta);
        movimiento.setTipo(TipoMovimiento.RECARGA);
        movimiento.setMonto(solicitud.getMonto());
        movimiento.setSaldoResultante(tarjeta.getSaldo());
        movimientoRepository.save(movimiento);
        return tarjeta.getSaldo();
    }


}
