package com.transitwallet.transit_wallet.service;

import com.transitwallet.transit_wallet.model.Usuario;
import com.transitwallet.transit_wallet.repository.UsuarioRepository;
import com.transitwallet.transit_wallet.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

    public String login(String email, String password){
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new
                IllegalArgumentException("Credenciales inválidas"));
        if(!passwordEncoder.matches(password, usuario.getPassword())){
            throw new IllegalArgumentException(("Credenciales inválidas"));
        }
        return jwtService.generarToken(usuario.getEmail());
    }

}