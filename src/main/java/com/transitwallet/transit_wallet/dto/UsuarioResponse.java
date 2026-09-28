package com.transitwallet.transit_wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String email;
}
