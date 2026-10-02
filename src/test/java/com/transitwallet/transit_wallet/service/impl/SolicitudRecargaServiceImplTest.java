package com.transitwallet.transit_wallet.service.impl;

import com.transitwallet.transit_wallet.exception.SolicitudRecargaInvalidaException;
import com.transitwallet.transit_wallet.exception.SolicitudRecargaNoEncontradaException;
import com.transitwallet.transit_wallet.model.Movimiento;
import com.transitwallet.transit_wallet.model.SolicitudRecarga;
import com.transitwallet.transit_wallet.model.Tarjeta;
import com.transitwallet.transit_wallet.model.enums.EstadoRecarga;
import com.transitwallet.transit_wallet.model.enums.EstadoTarjeta;
import com.transitwallet.transit_wallet.repository.MovimientoRepository;
import com.transitwallet.transit_wallet.repository.SolicitudRecargaRepository;
import com.transitwallet.transit_wallet.repository.TarjetaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//Activación de Mockito
@ExtendWith(MockitoExtension.class)
class SolicitudRecargaServiceImplTest {
    //Crea un doble falso
    @Mock
    private SolicitudRecargaRepository solicitudRecargaRepository;
    @Mock
    private TarjetaRepository tarjetaRepository;
    @Mock
    private MovimientoRepository movimientoRepository;
    //Crea una instancia real de la clase, inyecta los 3 mocks por su constructor.
    @InjectMocks
    private SolicitudRecargaServiceImpl service;

    //Probamos que el service lance la excepción correcta
    @Test
    void confirmarLanzaExcepcionSiElCodigoNoExiste() {
        when(solicitudRecargaRepository.findByCodigoTemporal("000000"))
                .thenReturn(Optional.empty());

        assertThrows(SolicitudRecargaNoEncontradaException.class,
                () -> service.confirmar("000000"));
    }


    @Test
    void confirmarLanzaExcepcionSiLaSolicitudYaFueProcesada(){
        SolicitudRecarga solicitud = new SolicitudRecarga();
        solicitud.setEstado(EstadoRecarga.CONFIRMADA);

        when(solicitudRecargaRepository.findByCodigoTemporal("123456"))
                .thenReturn(Optional.of(solicitud));
        assertThrows(SolicitudRecargaInvalidaException.class,
                () -> service.confirmar("123456"));
    }

    @Test
    void confirmarLanzaExcepcionSiLaSolicitudVencio() {
        SolicitudRecarga solicitud = new SolicitudRecarga();
        solicitud.setEstado(EstadoRecarga.PENDIENTE);
        solicitud.setFechaExpiracion(LocalDateTime.now().minusMinutes(10));

        when(solicitudRecargaRepository.findByCodigoTemporal("123456"))
                .thenReturn((Optional.of(solicitud)));
        assertThrows(SolicitudRecargaInvalidaException.class,
                () -> service.confirmar("123456"));
    }

    @Test
    void confirmarActualizaElSaldoCorrectamente(){
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setSaldo(new BigDecimal("10.00"));
        tarjeta.setEstado(EstadoTarjeta.ACTIVA);

        SolicitudRecarga solicitud = new SolicitudRecarga();
        solicitud.setEstado(EstadoRecarga.PENDIENTE);
        solicitud.setFechaExpiracion(LocalDateTime.now().plusMinutes(5));
        solicitud.setMonto(new BigDecimal("20.00"));
        solicitud.setTarjeta(tarjeta);

        when(solicitudRecargaRepository.findByCodigoTemporal("123456"))
                .thenReturn(Optional.of(solicitud));
        BigDecimal saldoResultante = service.confirmar("123456");

        assertEquals(new BigDecimal("30.00"),saldoResultante);
        verify(tarjetaRepository).save(tarjeta);
        verify(solicitudRecargaRepository).save(solicitud);
        verify(movimientoRepository).save(any(Movimiento.class));

    }


}
