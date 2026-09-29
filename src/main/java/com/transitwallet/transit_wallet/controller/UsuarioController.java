package com.transitwallet.transit_wallet.controller;

import com.transitwallet.transit_wallet.dto.LoginRequest;
import com.transitwallet.transit_wallet.dto.LoginResponse;
import com.transitwallet.transit_wallet.dto.RegistroRequest;
import com.transitwallet.transit_wallet.dto.UsuarioResponse;
import com.transitwallet.transit_wallet.model.Usuario;
import com.transitwallet.transit_wallet.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public UsuarioResponse registrar(@RequestBody RegistroRequest request){
        Usuario usuario = usuarioService.registrar(
                request.getNombre(), request.getEmail(), request.getPassword());
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail());
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        String token = usuarioService.login(request.getEmail(), request.getPassword());
        return new LoginResponse(token);
    }

    @GetMapping("/yo")
    public String yo(Authentication authentication){
        return authentication.getName();
    }
}
