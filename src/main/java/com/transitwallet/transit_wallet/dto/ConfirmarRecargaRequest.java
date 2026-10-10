package com.transitwallet.transit_wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmarRecargaRequest {

    //Deja pasar solo números del y que sean exactamente 6
    @NotBlank @Pattern(regexp = "\\d{6}")
    private String codigoTemporal;
}
