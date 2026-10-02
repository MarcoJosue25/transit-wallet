package com.transitwallet.transit_wallet.repository;

import com.transitwallet.transit_wallet.model.Tarjeta;
import com.transitwallet.transit_wallet.model.enums.EstadoTarjeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarjetaRepository extends JpaRepository<Tarjeta, Long> {

    boolean existsByNumero(String numero);
    List<Tarjeta> findByUsuarioId(Long UsuarioId);

    //Devuelve solo tarjetas activas que cumplan con el uso requerido
    List<Tarjeta> findByEstadoAndUsosPorDiaGreaterThanEqual(EstadoTarjeta estado, int usosPorDia);
}
