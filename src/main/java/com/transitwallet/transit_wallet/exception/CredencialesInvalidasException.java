package com.transitwallet.transit_wallet.exception;

public class CredencialesInvalidasException extends RuntimeException{
    public CredencialesInvalidasException (String mensaje){
        super (mensaje);
    }

}
