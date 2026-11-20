package com.mycompany.tienda3a.repositorio;

import com.mycompany.tienda3a.modelo.Auditoria;
import java.util.List;

public interface RepositorioAuditoria {
    void registrar(Auditoria auditoria);

    List<Auditoria> listar();
}
