package com.mycompany.tienda3a.repositorio;

import com.mycompany.tienda3a.modelo.Cliente;
import com.mycompany.tienda3a.modelo.TipoDocumento;
import java.util.List;

public interface RepositorioClientes {
    List<Cliente> listar();

    Cliente buscarPorDocumento(TipoDocumento tipo, String numero);

    void guardar(Cliente cliente);
}
