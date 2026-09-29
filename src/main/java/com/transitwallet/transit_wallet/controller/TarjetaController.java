package com.transitwallet.transit_wallet.controller;

import com.transitwallet.transit_wallet.dto.ConfirmarRecargaRequest;
import com.transitwallet.transit_wallet.dto.ConfirmarRecargaResponse;
import com.transitwallet.transit_wallet.dto.IniciarRecargaRequest;
import com.transitwallet.transit_wallet.dto.IniciarRecargaResponse;
import com.transitwallet.transit_wallet.model.SolicitudRecarga;
import com.transitwallet.transit_wallet.model.Tarjeta;
import com.transitwallet.transit_wallet.model.Usuario;
import com.transitwallet.transit_wallet.service.SolicitudRecargaService;
import com.transitwallet.transit_wallet.service.TarjetaService;
import com.transitwallet.transit_wallet.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/tarjetas/recargas")
public class TarjetaController {

    private final UsuarioService usuarioService;
    private final TarjetaService tarjetaService;
    private final SolicitudRecargaService solicitudRecargaService;

    public TarjetaController(SolicitudRecargaService solicitudRecargaService, UsuarioService usuarioService, TarjetaService tarjetaService) {
        this.solicitudRecargaService = solicitudRecargaService;
        this.usuarioService = usuarioService;
        this.tarjetaService = tarjetaService;
    }

    @PostMapping("/iniciar")
    public IniciarRecargaResponse iniciar (@RequestBody IniciarRecargaRequest request,
                                           Authentication authentication){
        Usuario usuario = usuarioService.obtenerPorEmail(authentication.getName());
        Tarjeta tarjeta = tarjetaService.obtenerPorUsuario(usuario);
        SolicitudRecarga solicitud = solicitudRecargaService.iniciar(tarjeta, request.getMonto(), request.getMetodoPago());
        return new IniciarRecargaResponse(solicitud.getCodigoTemporal(),solicitud.getFechaExpiracion());
    }

    @PostMapping("/confirmar")
    public ConfirmarRecargaResponse confirmar (@RequestBody ConfirmarRecargaRequest request){
        BigDecimal saldo = solicitudRecargaService.confirmar(request.getCodigoTemporal());
        return new ConfirmarRecargaResponse(saldo);
    }
}
