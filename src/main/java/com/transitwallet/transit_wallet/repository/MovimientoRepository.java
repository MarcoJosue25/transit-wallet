package com.transitwallet.transit_wallet.repository;

import com.transitwallet.transit_wallet.model.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    Page<Movimiento> findByTarjetaId(Long tarjetaId, Pageable pageable);



}
