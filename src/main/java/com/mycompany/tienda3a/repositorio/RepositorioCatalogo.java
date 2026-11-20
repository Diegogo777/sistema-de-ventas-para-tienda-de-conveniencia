package com.mycompany.tienda3a.repositorio;

import com.mycompany.tienda3a.modelo.Categoria;
import com.mycompany.tienda3a.modelo.Producto;
import com.mycompany.tienda3a.modelo.Promocion;
import java.util.List;

public interface RepositorioCatalogo {
    List<Categoria> listarCategorias();

    List<Producto> listarProductos();

    Producto buscarProductoPorCodigo(String codigo);

    void guardarCategoria(Categoria categoria);

    void guardarProducto(Producto producto);

    List<Promocion> listarPromociones();

    void guardarPromocion(Promocion promocion);
}
