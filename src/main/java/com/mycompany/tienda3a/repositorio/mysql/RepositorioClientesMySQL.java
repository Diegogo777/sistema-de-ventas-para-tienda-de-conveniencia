package com.mycompany.tienda3a.repositorio.mysql;

import com.mycompany.tienda3a.modelo.Cliente;
import com.mycompany.tienda3a.repositorio.memoria.RepositorioClientesMemoria;

public class RepositorioClientesMySQL extends RepositorioClientesMemoria {
    @Override
    public void guardar(Cliente k) {
        ConexionMySQL.enTransaccion(c -> {
            if (k.getIdCliente() == 0) {
                k.setIdCliente(ConexionMySQL.insertar(c,
                        "INSERT INTO cliente (tipo_documento, numero_documento, nombre_razon_social, direccion, activo) "
                        + "VALUES (?, ?, ?, ?, ?)",
                        k.getTipoDocumento(), k.getNumeroDocumento(), k.getNombreRazonSocial(), k.getDireccion(), k.isActivo()));
            } else {
                ConexionMySQL.actualizar(c,
                        "UPDATE cliente SET nombre_razon_social = ?, direccion = ?, activo = ? WHERE cliente_id = ?",
                        k.getNombreRazonSocial(), k.getDireccion(), k.isActivo(), k.getIdCliente());
            }
        });
        super.guardar(k);
    }
}
