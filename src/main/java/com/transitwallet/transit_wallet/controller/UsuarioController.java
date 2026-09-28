package com.transitwallet.transit_wallet.controller;

import com.transitwallet.transit_wallet.dto.RegistroRequest;
import com.transitwallet.transit_wallet.dto.UsuarioResponse;
import com.transitwallet.transit_wallet.model.Usuario;
import com.transitwallet.transit_wallet.service.UsuarioService;
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
                request.getEmail(), request.getEmail(), request.getPassword());
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail());
    }

}
