package com.mycompany.tienda3a.controlador;

import com.mycompany.tienda3a.modelo.Auditoria;
import com.mycompany.tienda3a.modelo.ResultadoOperacion;
import com.mycompany.tienda3a.modelo.Usuario;
import com.mycompany.tienda3a.repositorio.RepositorioAuditoria;
import java.time.LocalDateTime;

public class Bitacora {
    private final RepositorioAuditoria repositorio;

    public Bitacora(RepositorioAuditoria repositorio) {
        this.repositorio = repositorio;
    }

    public void registrar(Usuario usuario, String accion, String entidad, Object registroId,
                          ResultadoOperacion resultado, String detalle) {
        repositorio.registrar(new Auditoria(0, usuario, LocalDateTime.now(), accion, entidad,
                registroId == null ? null : String.valueOf(registroId), resultado, detalle));
    }
}
