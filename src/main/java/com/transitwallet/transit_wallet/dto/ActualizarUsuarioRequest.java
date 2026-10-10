package com.transitwallet.transit_wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ActualizarUsuarioRequest {

    @NotBlank @Size(max = 100)
    private String nombre;
}
