package com.transitwallet.transit_wallet.service.impl;

import com.transitwallet.transit_wallet.exception.CredencialesInvalidasException;
import com.transitwallet.transit_wallet.model.Tarjeta;
import com.transitwallet.transit_wallet.model.Usuario;
import com.transitwallet.transit_wallet.model.enums.EstadoTarjeta;
import com.transitwallet.transit_wallet.repository.UsuarioRepository;
import com.transitwallet.transit_wallet.security.JwtService;
import com.transitwallet.transit_wallet.service.TarjetaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private TarjetaService tarjetaService;

    @InjectMocks
    private UsuarioServiceImpl service;

    @Test
    void obtenerPorEmailDevuelveElUsuarioSiEstaActivo(){
        Usuario usuario = new Usuario();
        usuario.setEmail("ema@gmail.com");

        when(usuarioRepository.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        Usuario resultado = service.obtenerPorEmail(usuario.getEmail());
        assertEquals(usuario, resultado);
    }

    @Test
    void obtenerPorEmailLanzaExcepcionSiElEmailNoExiste(){
        when(usuarioRepository.findByEmail("email"))
                .thenReturn(Optional.empty());
        assertThrows(CredencialesInvalidasException.class, () ->
                service.obtenerPorEmail("email"));
    }

    @Test
    void obtenerPorEmailLanzaExcepcionSiLaCuentaEstaInactiva(){
        Usuario usuario = new Usuario();
        usuario.setActivo(false);

        when(usuarioRepository.findByEmail("email"))
                .thenReturn(Optional.of(usuario));
        assertThrows(CredencialesInvalidasException.class,
                () -> service.obtenerPorEmail("email"));
    }

    @Test
    void actualizarCambiaElNombre(){
        Usuario usuario = new Usuario();
        usuario.setNombre("Nombre anterior");
        when(usuarioRepository.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        Usuario resultado = service.actualizar(usuario.getEmail(), "Gregorio");

        assertEquals("Gregorio", resultado.getNombre());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void darDeBajaDescativaElUsuarioYBloqueaLaTarjeta(){
        Usuario usuario = new Usuario();
        when(usuarioRepository.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        service.darDeBaja(usuario.getEmail());
        assertFalse(usuario.isActivo());
        verify(usuarioRepository).save(usuario);
        verify(tarjetaService).bloquear(usuario);
    }

    @Test
    void unLoginCorrectoDevuelveUnToken(){
        Usuario usuario = new Usuario();
        usuario.setEmail("marco@gmail.com");
        usuario.setPassword("marco123");
        when(usuarioRepository.findByEmail("marco@gmail.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("clave123","marco123"))
                .thenReturn(true);
        when(jwtService.generarToken("marco@gmail.com")).thenReturn("tokenPrueba");
        String token  = service.login("marco@gmail.com", "clave123");
        assertEquals("tokenPrueba", token);
    }

    @Test
    void loginLanzaExcepcionSiElPasswordEsIncorrecto(){
        Usuario usuario = new Usuario();
        usuario.setEmail("marco@gmail.com");
        usuario.setPassword("marco123");
        when(usuarioRepository.findByEmail("marco@gmail.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("clave","marco123")).thenReturn(false);
        assertThrows(CredencialesInvalidasException.class,
                () -> service.login("marco@gmail.com", "marco123"));
        verify(jwtService,never()).generarToken(anyString());
    }

    @Test
    void loginLanzaExcepcionSiLaCuentaEstaInactiva(){
        Usuario usuario = new Usuario();
        usuario.setEmail("marco@gmail.com");
        usuario.setPassword("marco123");
        usuario.setActivo(false);

        when(usuarioRepository.findByEmail("marco@gmail.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("clave123", "marco123")).thenReturn(true);
        assertThrows(CredencialesInvalidasException.class,
                () -> service.login("marco@gmail.com", "clave123"));
    }

}
