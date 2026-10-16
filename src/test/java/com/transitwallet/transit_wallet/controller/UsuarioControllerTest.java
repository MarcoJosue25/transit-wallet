package com.transitwallet.transit_wallet.controller;

import com.transitwallet.transit_wallet.config.Seguridad;
import com.transitwallet.transit_wallet.model.Usuario;
import com.transitwallet.transit_wallet.security.JwtService;
import com.transitwallet.transit_wallet.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import(Seguridad.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;
    @MockitoBean
    private JwtService jwtService;

    @Test
    void registroValidoDevuelveElUsuarioSinPassword() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Ana");
        usuario.setEmail("ana@correo.com");
        when(usuarioService.registrar("Ana", "ana@correo.com", "clave12345"))
                .thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Ana", "email": "ana@correo.com", "password": "clave12345"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("ana@correo.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void registroConDatosInvalidosDevuelve400ConElDetalle() throws Exception {
        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "", "email": "no-es-email", "password": "123"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Datos inválidos"))
                .andExpect(jsonPath("$.errores.nombre").exists())
                .andExpect(jsonPath("$.errores.email").exists())
                .andExpect(jsonPath("$.errores.password").exists());
    }

    @Test
    void loginCorrectoDevuelveElToken() throws Exception {
        when(usuarioService.login("ana@correo.com", "clave12345"))
                .thenReturn("token-falso");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "ana@correo.com", "password": "clave12345"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-falso"));
    }

    @Test
    void perfilSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/usuarios/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "ana@correo.com")
    void darDeBajaDevuelve204YPideLaBajaDelUsuarioAutenticado() throws Exception {
        mockMvc.perform(delete("/api/usuarios/me"))
                .andExpect(status().isNoContent());

        verify(usuarioService).darDeBaja("ana@correo.com");
    }
}