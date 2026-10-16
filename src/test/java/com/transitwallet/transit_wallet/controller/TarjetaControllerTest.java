package com.transitwallet.transit_wallet.controller;

import com.transitwallet.transit_wallet.config.Seguridad;
import com.transitwallet.transit_wallet.model.SolicitudConsumo;
import com.transitwallet.transit_wallet.model.Tarjeta;
import com.transitwallet.transit_wallet.model.Usuario;
import com.transitwallet.transit_wallet.model.enums.EstadoConsumo;
import com.transitwallet.transit_wallet.security.JwtService;
import com.transitwallet.transit_wallet.service.MovimientoService;
import com.transitwallet.transit_wallet.service.SolicitudConsumoService;
import com.transitwallet.transit_wallet.service.SolicitudRecargaService;
import com.transitwallet.transit_wallet.service.TarjetaService;
import com.transitwallet.transit_wallet.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TarjetaController.class)
@Import(Seguridad.class)
class TarjetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;
    @MockitoBean
    private TarjetaService tarjetaService;
    @MockitoBean
    private SolicitudRecargaService solicitudRecargaService;
    @MockitoBean
    private MovimientoService movimientoService;
    @MockitoBean
    private SolicitudConsumoService solicitudConsumoService;
    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "ana@correo.com")
    void iniciarRecargaConMontoNegativoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/tarjetas/recargas/iniciar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"monto": -5.00, "metodoPago": "YAPE"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.monto").exists());
    }

    @Test
    @WithMockUser(username = "ana@correo.com")
    void confirmarRecargaConCodigoMalFormadoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/tarjetas/recargas/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"codigoTemporal": "12345"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.codigoTemporal").exists());
    }

    @Test
    void miTarjetaSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/tarjetas/mi-tarjeta"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "ana@correo.com")
    void consumoDevuelveElEstadoYElSaldo() throws Exception {
        Usuario usuario = new Usuario();
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setSaldo(new BigDecimal("7.55"));
        SolicitudConsumo consumo = new SolicitudConsumo();
        consumo.setEstado(EstadoConsumo.CONFIRMADO);

        when(usuarioService.obtenerPorEmail("ana@correo.com")).thenReturn(usuario);
        when(tarjetaService.obtenerPorUsuario(usuario)).thenReturn(tarjeta);
        when(solicitudConsumoService.consumir(tarjeta)).thenReturn(consumo);

        mockMvc.perform(post("/api/tarjetas/consumo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"))
                .andExpect(jsonPath("$.saldo").value(7.55));
    }
}