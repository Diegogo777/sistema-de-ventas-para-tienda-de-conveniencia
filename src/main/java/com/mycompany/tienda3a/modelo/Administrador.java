package com.mycompany.tienda3a.modelo;

import java.time.LocalDateTime;

public class Administrador extends Usuario {
    public static final String[] CASOS_PERMITIDOS = {"CU-01", "CU-10", "CU-11", "CU-12", "CU-13"};

    public Administrador(int idUsuario, String nombre, String nombreAcceso, String claveHash,
                         boolean activo, LocalDateTime creadoEn) {
        super(idUsuario, nombre, nombreAcceso, claveHash, activo, creadoEn);
    }

    @Override
    public Rol getRol() {
        return Rol.ADMINISTRADOR;
    }

    @Override
    public boolean puedeAcceder(String casoUso) {
        return contiene(CASOS_PERMITIDOS, casoUso);
    }

    public boolean puedeDesactivar(Usuario usuario) {
        return usuario != null && usuario.getIdUsuario() != this.idUsuario;
    }
}
