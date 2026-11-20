package com.mycompany.tienda3a.repositorio.mysql;

import com.mycompany.tienda3a.modelo.Usuario;
import com.mycompany.tienda3a.repositorio.memoria.RepositorioUsuariosMemoria;

public class RepositorioUsuariosMySQL extends RepositorioUsuariosMemoria {
    @Override
    public void guardar(Usuario u) {
        ConexionMySQL.enTransaccion(c -> {
            if (u.getIdUsuario() == 0) {
                u.setIdUsuario(ConexionMySQL.insertar(c,
                        "INSERT INTO usuario (nombre, nombre_acceso, clave_hash, rol, intentos_fallidos, bloqueado_hasta, activo, creado_en) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        u.getNombre(), u.getNombreAcceso(), u.getClaveHash(), u.getRol(), u.getIntentosFallidos(),
                        u.getBloqueadoHasta(), u.isActivo(), u.getCreadoEn()));
            } else {
                ConexionMySQL.actualizar(c,
                        "UPDATE usuario SET nombre = ?, nombre_acceso = ?, clave_hash = ?, rol = ?, intentos_fallidos = ?, "
                        + "bloqueado_hasta = ?, activo = ? WHERE usuario_id = ?",
                        u.getNombre(), u.getNombreAcceso(), u.getClaveHash(), u.getRol(), u.getIntentosFallidos(),
                        u.getBloqueadoHasta(), u.isActivo(), u.getIdUsuario());
            }
        });
        super.guardar(u);
    }
}
