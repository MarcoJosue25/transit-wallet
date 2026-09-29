package com.transitwallet.transit_wallet.service.impl;

import com.transitwallet.transit_wallet.model.Tarjeta;
import com.transitwallet.transit_wallet.model.Usuario;
import com.transitwallet.transit_wallet.repository.TarjetaRepository;
import com.transitwallet.transit_wallet.service.TarjetaService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class TarjetaServiceImpl implements TarjetaService{

    private static final int LONGITUD_NUMERO = 16;
    //Guardamos la clase en un objeto para poder usar sus métodos
    private final TarjetaRepository tarjetaRepository;

    public TarjetaServiceImpl(TarjetaRepository tarjetaRepository) {
        this.tarjetaRepository = tarjetaRepository;
    }

    @Override
    public Tarjeta crearParaUsuario(Usuario usuario){
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setUsuario(usuario);
        tarjeta.setNumero(generarNumeroUnico());
        tarjeta.setUsosPorDia(ThreadLocalRandom.current().nextInt(1,4));
        return tarjetaRepository.save(tarjeta);
    }

    private String generarNumeroUnico(){
        String numero;
        do {
            numero = generarNumero();
        }while (tarjetaRepository.existsByNumero(numero));
        return numero;
    }

    private String generarNumero(){
        StringBuilder numero = new StringBuilder(LONGITUD_NUMERO);
        for (int i=0; i < LONGITUD_NUMERO; i++){
            numero.append(ThreadLocalRandom.current().nextInt(10));
        }return numero.toString();
    }

    @Override
    public Tarjeta obtenerPorUsuario(Usuario usuario){
        return tarjetaRepository.findByUsuarioId(usuario.getId()).getFirst();
    }
}
