package com.transitwallet.transit_wallet.controller;

import com.transitwallet.transit_wallet.dto.*;
import com.transitwallet.transit_wallet.model.Usuario;
import com.transitwallet.transit_wallet.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }


    @PostMapping("/registro")
    public UsuarioResponse registrar(@Valid @RequestBody RegistroRequest request){
        Usuario usuario = usuarioService.registrar(request.getNombre(), request.getEmail(), request.getPassword());
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(),usuario.getEmail());
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid  @RequestBody LoginRequest request){
        String token = usuarioService.login(request.getEmail(), request.getPassword());
        return new LoginResponse(token);
    }

    @GetMapping("/me")
    public UsuarioResponse me(Authentication authentication){
        Usuario usuario = usuarioService.obtenerPorEmail(authentication.getName());
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail());
    }
    @PutMapping("/me")
    public UsuarioResponse actualizar(Authentication authentication,@Valid  @RequestBody ActualizarUsuarioRequest request){
        Usuario usuario = usuarioService.actualizar(authentication.getName(), request.getNombre());
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail());
    }
    @DeleteMapping("/me")
    public ResponseEntity<Void> darDeBaja(Authentication authentication){
        usuarioService.darDeBaja(authentication.getName());
        return ResponseEntity.noContent().build();
    }


}
