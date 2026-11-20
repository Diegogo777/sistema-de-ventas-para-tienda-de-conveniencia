package com.mycompany.tienda3a.repositorio.memoria;

import com.mycompany.tienda3a.modelo.Cliente;
import com.mycompany.tienda3a.modelo.TipoDocumento;
import com.mycompany.tienda3a.repositorio.RepositorioClientes;
import java.util.ArrayList;
import java.util.List;

public class RepositorioClientesMemoria implements RepositorioClientes {
    protected final List<Cliente> clientes = new ArrayList<>();
    private int ultimoId;

    @Override
    public List<Cliente> listar() {
        return new ArrayList<>(clientes);
    }

    @Override
    public Cliente buscarPorDocumento(TipoDocumento tipo, String numero) {
        for (Cliente c : clientes) {
            if (c.getTipoDocumento() == tipo && c.getNumeroDocumento().equals(numero)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void guardar(Cliente cliente) {
        if (cliente.getIdCliente() == 0) {
            cliente.setIdCliente(++ultimoId);
        }
        ultimoId = Math.max(ultimoId, cliente.getIdCliente());
        if (!clientes.contains(cliente)) {
            clientes.add(cliente);
        }
    }

    public void cargar(Cliente cliente) {
        clientes.add(cliente);
        ultimoId = Math.max(ultimoId, cliente.getIdCliente());
    }
}
