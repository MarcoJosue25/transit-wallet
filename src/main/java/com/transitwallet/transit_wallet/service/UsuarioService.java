package com.transitwallet.transit_wallet.service;

import com.transitwallet.transit_wallet.model.Usuario;
import com.transitwallet.transit_wallet.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(String nombre, String email, String password) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Ese email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));

        return usuarioRepository.save(usuario);
    }
}