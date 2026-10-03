package com.transitwallet.transit_wallet.scheduler;

import com.transitwallet.transit_wallet.model.Movimiento;
import com.transitwallet.transit_wallet.model.Tarjeta;
import com.transitwallet.transit_wallet.model.enums.EstadoTarjeta;
import com.transitwallet.transit_wallet.model.enums.TipoMovimiento;
import com.transitwallet.transit_wallet.repository.MovimientoRepository;
import com.transitwallet.transit_wallet.repository.TarjetaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DescuentoAutomatico {

    private static final BigDecimal TARIFA = new BigDecimal("2.45");
    private static final BigDecimal UMBRAL_SALDO_BAJO = new BigDecimal(("5.00"));

    private final TarjetaRepository tarjetaRepository;
    private final MovimientoRepository movimientoRepository;

    public DescuentoAutomatico(MovimientoRepository movimientoRepository, TarjetaRepository tarjetaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.tarjetaRepository = tarjetaRepository;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void slot1() {procesarSlot(1);}

    @Scheduled(cron = "0 0 13 * * *")
    public void slot2() {procesarSlot(2);}

    @Scheduled(cron = "0 0 18 * * *")
    public void slot3() {procesarSlot(3);}

    @Transactional
    void procesarSlot(int slot){
        List<Tarjeta> tarjetas = tarjetaRepository.findByEstadoAndUsosPorDiaGreaterThanEqual(EstadoTarjeta.ACTIVA, slot);
        for(Tarjeta tarjeta: tarjetas){
            if(tarjeta.getSaldo().compareTo(TARIFA) >= 0){
                tarjeta.setSaldo(tarjeta.getSaldo().subtract(TARIFA));
                if(tarjeta.getSaldo().compareTo(UMBRAL_SALDO_BAJO) < 0){
                    tarjeta.setAlertaSaldoBajo(true);
                }
                tarjetaRepository.save(tarjeta);

                Movimiento movimiento = new Movimiento();
                movimiento.setTarjeta(tarjeta);
                movimiento.setTipo(TipoMovimiento.CONSUMO);
                movimiento.setMonto(TARIFA);
                movimiento.setSaldoResultante(tarjeta.getSaldo());
                movimientoRepository.save(movimiento);
            }else{
                tarjeta.setEstado(EstadoTarjeta.BLOQUEADA);
                tarjetaRepository.save(tarjeta);
            }
        }
    }
}
