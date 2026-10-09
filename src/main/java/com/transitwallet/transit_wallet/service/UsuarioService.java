package com.transitwallet.transit_wallet.service;

import com.transitwallet.transit_wallet.model.Usuario;

public interface UsuarioService {

    Usuario registrar(String nombre, String email, String password);
    String login (String email, String password);
    Usuario obtenerPorEmail(String email);
    Usuario actualizar(String email, String nNombre);
    void darDeBaja(String email);
}
