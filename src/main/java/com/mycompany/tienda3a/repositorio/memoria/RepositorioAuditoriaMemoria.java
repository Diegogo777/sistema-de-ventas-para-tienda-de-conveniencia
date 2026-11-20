package com.mycompany.tienda3a.repositorio.memoria;

import com.mycompany.tienda3a.modelo.Auditoria;
import com.mycompany.tienda3a.repositorio.RepositorioAuditoria;
import java.util.ArrayList;
import java.util.List;

public class RepositorioAuditoriaMemoria implements RepositorioAuditoria {
    protected final List<Auditoria> registros = new ArrayList<>();
    private int ultimoId;

    @Override
    public void registrar(Auditoria auditoria) {
        if (auditoria.getIdAuditoria() == 0) {
            auditoria.setIdAuditoria(++ultimoId);
        }
        ultimoId = Math.max(ultimoId, auditoria.getIdAuditoria());
        registros.add(auditoria);
    }

    @Override
    public List<Auditoria> listar() {
        return new ArrayList<>(registros);
    }

    public void cargar(Auditoria auditoria) {
        registros.add(auditoria);
        ultimoId = Math.max(ultimoId, auditoria.getIdAuditoria());
    }
}
