package com.transitwallet.transit_wallet.controller;

import com.transitwallet.transit_wallet.dto.*;
import com.transitwallet.transit_wallet.model.*;
import com.transitwallet.transit_wallet.service.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/tarjetas")
public class TarjetaController {

    private final UsuarioService usuarioService;
    private final TarjetaService tarjetaService;
    private final SolicitudRecargaService solicitudRecargaService;
    private final MovimientoService movimientoService;
    private final SolicitudConsumoService solicitudConsumoService;

    public TarjetaController(SolicitudRecargaService solicitudRecargaService, UsuarioService usuarioService, TarjetaService tarjetaService,
                             MovimientoService movimientoService, SolicitudConsumoService solicitudConsumoService) {
        this.solicitudRecargaService = solicitudRecargaService;
        this.usuarioService = usuarioService;
        this.tarjetaService = tarjetaService;
        this.movimientoService = movimientoService;
        this.solicitudConsumoService = solicitudConsumoService;
    }

    @PostMapping("/recargas/iniciar")
    public IniciarRecargaResponse iniciar (@Valid  @RequestBody IniciarRecargaRequest request,
                                           Authentication authentication){
        Usuario usuario = usuarioService.obtenerPorEmail(authentication.getName());
        Tarjeta tarjeta  = tarjetaService.obtenerPorUsuario(usuario);
        SolicitudRecarga solicitud = solicitudRecargaService.iniciar(tarjeta, request.getMonto(), request.getMetodoPago());
        return new IniciarRecargaResponse(solicitud.getCodigoTemporal(), solicitud.getFechaExpiracion());
    }

    @PostMapping("/recargas/confirmar")
    public ConfirmarRecargaResponse confirmar (@Valid @RequestBody ConfirmarRecargaRequest request){
        BigDecimal saldo = solicitudRecargaService.confirmar(request.getCodigoTemporal());
        return new ConfirmarRecargaResponse(saldo);
    }
    @PostMapping("/consumo")
    public ConsumoResponse consumo (Authentication authentication){
        Usuario usuario = usuarioService.obtenerPorEmail(authentication.getName());
        Tarjeta tarjeta = tarjetaService.obtenerPorUsuario(usuario);
        SolicitudConsumo consumo = solicitudConsumoService.consumir(tarjeta);
        return new ConsumoResponse(consumo.getEstado(), tarjeta.getSaldo());
    }

    @GetMapping("/mi-tarjeta")
    public TarjetaResponse miTarjeta(Authentication authentication){
        Usuario usuario = usuarioService.obtenerPorEmail(authentication.getName());
        Tarjeta tarjeta = tarjetaService.obtenerPorUsuario(usuario);
        return new TarjetaResponse(tarjeta.getNumero(), tarjeta.getSaldo(), tarjeta.getEstado(), tarjeta.isAlertaSaldoBajo());
    }

    @GetMapping("/movimientos")
    public Page<MovimientoResponse> movimientos(Authentication authentication, @PageableDefault(size = 10,
            sort ="fecha", direction = Sort.Direction.DESC)Pageable pageable){
        Usuario usuario = usuarioService.obtenerPorEmail(authentication.getName());
        Tarjeta tarjeta = tarjetaService.obtenerPorUsuario(usuario);
        return movimientoService.obtenerPorTarjeta(tarjeta, pageable).map(m -> new MovimientoResponse(m.getTipo(), m.getMonto(), m.getSaldoResultante(),m.getFecha()));
    }
}
