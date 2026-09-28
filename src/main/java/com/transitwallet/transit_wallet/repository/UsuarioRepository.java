package com.transitwallet.transit_wallet.repository;

import com.transitwallet.transit_wallet.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository <Usuario, Long> {
     Optional<Usuario> findByEmail(String Email);
}
