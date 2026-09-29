package com.transitwallet.transit_wallet.service.impl;

import com.transitwallet.transit_wallet.exception.CredencialesInvalidasException;
import com.transitwallet.transit_wallet.exception.EmailYaRegistradoException;
import com.transitwallet.transit_wallet.model.Usuario;
import com.transitwallet.transit_wallet.repository.UsuarioRepository;
import com.transitwallet.transit_wallet.security.JwtService;
import com.transitwallet.transit_wallet.service.TarjetaService;
import com.transitwallet.transit_wallet.service.UsuarioService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TarjetaService tarjetaService;

    public UsuarioServiceImpl(JwtService jwtService, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, TarjetaService tarjetaService) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tarjetaService = tarjetaService;
    }

    @Override
    @Transactional
    public Usuario registrar(String nombre, String email, String password) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new EmailYaRegistradoException("Ese email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));

        Usuario guardado = usuarioRepository.save(usuario);
        tarjetaService.crearParaUsuario(guardado);
        return guardado;
    }

    @Override
    public String login(String email, String password){
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new
                CredencialesInvalidasException("Credenciales inválidas"));
        if(!passwordEncoder.matches(password, usuario.getPassword())){
            throw new IllegalArgumentException(("Credenciales inválidas"));
        }
        return jwtService.generarToken(usuario.getEmail());
    }

    @Override
    public Usuario obtenerPorEmail(String email){
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new
                CredencialesInvalidasException("Credenciales inválidas"));
        return usuario;
    }
}