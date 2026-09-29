package com.transitwallet.transit_wallet.repository;

import com.transitwallet.transit_wallet.model.SolicitudRecarga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SolicitudRecargaRepository extends JpaRepository<SolicitudRecarga, Long> {

    Optional<SolicitudRecarga> findByCodigoTemporal(String codigo);

}
