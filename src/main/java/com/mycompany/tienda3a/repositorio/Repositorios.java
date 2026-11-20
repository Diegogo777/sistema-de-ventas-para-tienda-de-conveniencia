package com.mycompany.tienda3a.repositorio;

public record Repositorios(RepositorioUsuarios usuarios, RepositorioCatalogo catalogo,
                           RepositorioClientes clientes, RepositorioCajas cajas,
                           RepositorioVentas ventas, RepositorioAuditoria auditoria,
                           String origen) {
}
