package com.transitwallet.transit_wallet.service.impl;

import com.transitwallet.transit_wallet.model.Movimiento;
import com.transitwallet.transit_wallet.model.SolicitudConsumo;
import com.transitwallet.transit_wallet.model.Tarjeta;
import com.transitwallet.transit_wallet.model.enums.EstadoConsumo;
import com.transitwallet.transit_wallet.model.enums.EstadoTarjeta;
import com.transitwallet.transit_wallet.repository.MovimientoRepository;
import com.transitwallet.transit_wallet.repository.SolicitudConsumoRepository;
import com.transitwallet.transit_wallet.repository.TarjetaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SolicitudConsumoServiceImplTest {

    @Mock
    private SolicitudConsumoRepository solicitudConsumoRepository;

    @Mock
    private TarjetaRepository tarjetaRepository;

    @Mock
    private MovimientoRepository movimientoRepository;

    @InjectMocks
    private SolicitudConsumoServiceImpl service;

    @Test
    void consumoConfirmado(){
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setEstado(EstadoTarjeta.ACTIVA);
        tarjeta.setSaldo(new BigDecimal("10.00"));

        SolicitudConsumo consumo = service.consumir(tarjeta);

        assertEquals(EstadoConsumo.CONFIRMADO, consumo.getEstado());
        assertEquals(new BigDecimal("7.55"), tarjeta.getSaldo());
        verify(tarjetaRepository).save(tarjeta);
        verify(movimientoRepository).save(any(Movimiento.class));

    }
    @Test
    void consumirDeniegaSiElSaldoNoAlcanza(){
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setEstado(EstadoTarjeta.ACTIVA);
        tarjeta.setSaldo(new BigDecimal("1.00"));

        SolicitudConsumo consumo = service.consumir(tarjeta);

        assertEquals(EstadoConsumo.DENEGADO, consumo.getEstado());
        assertEquals(new BigDecimal("1.00"), tarjeta.getSaldo());
        verify(solicitudConsumoRepository).save(consumo);
        verify(tarjetaRepository, never()).save(any(Tarjeta.class));
        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }

    @Test
    void consumirConfirmaSiElSaldoEsExactamenteElPasaje(){
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setEstado(EstadoTarjeta.ACTIVA);
        tarjeta.setSaldo(new BigDecimal("2.45"));

        SolicitudConsumo consumo = service.consumir(tarjeta);

        assertEquals(EstadoConsumo.CONFIRMADO, consumo.getEstado());
        assertEquals(new BigDecimal("0.00"), tarjeta.getSaldo());
    }

}
