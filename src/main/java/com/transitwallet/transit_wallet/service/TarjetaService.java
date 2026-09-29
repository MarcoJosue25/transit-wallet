package com.transitwallet.transit_wallet.service;

import com.transitwallet.transit_wallet.model.Tarjeta;
import com.transitwallet.transit_wallet.model.Usuario;

public interface TarjetaService {
    Tarjeta crearParaUsuario(Usuario usuario);

    Tarjeta obtenerPorUsuario(Usuario usuario);
}

