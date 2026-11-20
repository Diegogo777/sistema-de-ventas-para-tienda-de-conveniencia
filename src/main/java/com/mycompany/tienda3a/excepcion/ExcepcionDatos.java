package com.mycompany.tienda3a.excepcion;

public class ExcepcionDatos extends RuntimeException {
    public ExcepcionDatos(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
