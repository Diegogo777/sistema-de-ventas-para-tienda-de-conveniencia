package com.mycompany.tienda3a.repositorio.mysql;

import com.mycompany.tienda3a.excepcion.ExcepcionDatos;
import com.mycompany.tienda3a.modelo.Auditoria;
import com.mycompany.tienda3a.repositorio.memoria.RepositorioAuditoriaMemoria;

public class RepositorioAuditoriaMySQL extends RepositorioAuditoriaMemoria {
    @Override
    public void registrar(Auditoria a) {
        try {
            ConexionMySQL.enTransaccion(c -> a.setIdAuditoria(ConexionMySQL.insertar(c,
                    "INSERT INTO auditoria (usuario_id, fecha_hora, accion, entidad, registro_id, resultado, detalle) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    a.getUsuario() == null ? null : a.getUsuario().getIdUsuario(), a.getFechaHora(), a.getAccion(),
                    a.getEntidad(), a.getRegistroId(), a.getResultado(), a.getDetalle())));
        } catch (ExcepcionDatos e) {
            System.err.println("Bitácora: " + e.getMessage());
        }
        super.registrar(a);
    }
}
