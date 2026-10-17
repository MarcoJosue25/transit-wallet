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
        usuario.setEmail("email");
        when(usuarioRepository.findByEmail("email"))
                .thenReturn(Optional.of(usuario));
        Usuario encontrado = service.obtenerPorEmail("email");
        assertEquals(usuario,encontrado);
    }


    @Test
    void obtenerPorEmailLanzaExcepcionSiElEmailNoExiste(){
        when(usuarioRepository.findByEmail("email"))
                .thenReturn(Optional.empty());
        assertThrows(CredencialesInvalidasException.class,
                () -> service.obtenerPorEmail("email"));
    }

    @Test
    void obtenerPorEmailLanzaExcepcionSiLaCuentaEstaInactiva(){
        Usuario usuario = new Usuario();
        usuario.setActivo(false);
        usuario.setEmail("email");

        when(usuarioRepository.findByEmail("email"))
                .thenReturn(Optional.of(usuario));
        assertThrows(CredencialesInvalidasException.class,
                () -> service.obtenerPorEmail("email"));
    }

    @Test
    void actualizarCambiaElNombre(){
        Usuario usuario = new Usuario();
        usuario.setNombre("marco");
        usuario.setEmail("email");

        when(usuarioRepository.findByEmail("email"))
                .thenReturn(Optional.of(usuario));
        Usuario actualizado = service.actualizarNombre("email", "mario");
        assertEquals("mario",actualizado.getNombre());
        verify(usuarioRepository).save(actualizado);
    }

    @Test
    void darDeBajaDescativaElUsuarioYBloqueaLaTarjeta(){
        Usuario usuario = new Usuario();
        usuario.setEmail("email");
        usuario.setActivo(true);
        when(usuarioRepository.findByEmail("email"))
                .thenReturn(Optional.of(usuario));
        service.darDeBaja("email");
        assertFalse(usuario.isActivo());
        verify(usuarioRepository).save(usuario);
        verify(tarjetaService).bloquear(usuario);
    }


    @Test
    void unLoginCorrectoDevuelveUnToken(){
        Usuario usuario = new Usuario();
        usuario.setEmail("email");
        usuario.setPassword("marco123");
        when(usuarioRepository.findByEmail("email"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("clave123", "marco123"))
                .thenReturn(true);
        when(jwtService.generarToken("email"))
                .thenReturn("ddfasdsfasds");
        String token = service.login("email", "clave123");
        assertEquals("ddfasdsfasds", token);
    }

    @Test
    void loginLanzaExcepcionSiElPasswordEsIncorrecto(){
        Usuario usuario = new Usuario();
        usuario.setEmail("email");
        usuario.setPassword("marco123");
        when(usuarioRepository.findByEmail("email"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("clave123", "marco123"))
                .thenReturn(false);
        assertThrows(CredencialesInvalidasException.class,
                () -> service.login("email","clave123"));
        verify(jwtService, never()).generarToken(anyString());
    }

    @Test
    void loginLanzaExcepcionSiLaCuentaEstaInactiva(){
        Usuario usuario = new Usuario();
        usuario.setEmail("email");
        usuario.setPassword("marco123");
        usuario.setActivo(false);
        when(usuarioRepository.findByEmail("email"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("clave123","marco123"))
                .thenReturn(true);
        assertThrows(CredencialesInvalidasException.class,
                () -> service.login("email","clave123"));
    }

    @Test
    void cambiaLaContraseñaSiLaActualEsCorrecta(){
        Usuario usuario = new Usuario();
        usuario.setEmail("email");
        usuario.setPassword("marco123");
        when(usuarioRepository.findByEmail("email"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("clave123","marco123"))
                .thenReturn(true);
        when(passwordEncoder.encode("marco1234"))
                .thenReturn("clave1234");
        service.actualizarContraseña("email", "clave123", "marco1234");
        assertEquals("clave1234", usuario.getPassword());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void cambiarLaContraseñaLanzaExcepcionSiLosDatosNoCoinciden(){
        Usuario usuario = new Usuario();
        usuario.setEmail("email");
        usuario.setPassword("marco123");
        when(usuarioRepository.findByEmail("email"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("clave123","marco123"))
                .thenReturn(false);
        assertThrows(CredencialesInvalidasException.class,
                () -> service.actualizarContraseña("email", "clave123", "marco1234"));
        assertEquals("marco123",usuario.getPassword());
        verify(usuarioRepository,never()).save(any(Usuario.class));
    }
}
