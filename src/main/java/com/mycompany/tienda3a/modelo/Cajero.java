package com.mycompany.tienda3a.modelo;

import java.time.LocalDateTime;

public class Cajero extends Usuario {
    public static final String[] CASOS_PERMITIDOS = {
        "CU-01", "CU-02", "CU-03", "CU-04", "CU-05", "CU-06", "CU-07", "CU-08", "CU-09"
    };

    public Cajero(int idUsuario, String nombre, String nombreAcceso, String claveHash,
                  boolean activo, LocalDateTime creadoEn) {
        super(idUsuario, nombre, nombreAcceso, claveHash, activo, creadoEn);
    }

    @Override
    public Rol getRol() {
        return Rol.CAJERO;
    }

    @Override
    public boolean puedeAcceder(String casoUso) {
        return contiene(CASOS_PERMITIDOS, casoUso);
    }
}
