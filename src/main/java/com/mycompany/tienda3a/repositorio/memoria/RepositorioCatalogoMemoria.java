package com.mycompany.tienda3a.repositorio.memoria;

import com.mycompany.tienda3a.modelo.Categoria;
import com.mycompany.tienda3a.modelo.Producto;
import com.mycompany.tienda3a.modelo.Promocion;
import com.mycompany.tienda3a.repositorio.RepositorioCatalogo;
import java.util.ArrayList;
import java.util.List;

public class RepositorioCatalogoMemoria implements RepositorioCatalogo {
    protected final List<Categoria> categorias = new ArrayList<>();
    protected final List<Producto> productos = new ArrayList<>();
    protected final List<Promocion> promociones = new ArrayList<>();
    private int ultimaCategoria;
    private int ultimoProducto;
    private int ultimaPromocion;

    @Override
    public List<Categoria> listarCategorias() {
        return new ArrayList<>(categorias);
    }

    @Override
    public List<Producto> listarProductos() {
        return new ArrayList<>(productos);
    }

    @Override
    public Producto buscarProductoPorCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        for (Producto p : productos) {
            if (p.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void guardarCategoria(Categoria categoria) {
        if (categoria.getIdCategoria() == 0) {
            categoria.setIdCategoria(++ultimaCategoria);
        }
        ultimaCategoria = Math.max(ultimaCategoria, categoria.getIdCategoria());
        if (!categorias.contains(categoria)) {
            categorias.add(categoria);
        }
    }

    @Override
    public void guardarProducto(Producto producto) {
        if (producto.getIdProducto() == 0) {
            producto.setIdProducto(++ultimoProducto);
        }
        ultimoProducto = Math.max(ultimoProducto, producto.getIdProducto());
        if (!productos.contains(producto)) {
            productos.add(producto);
        }
    }

    @Override
    public List<Promocion> listarPromociones() {
        return new ArrayList<>(promociones);
    }

    @Override
    public void guardarPromocion(Promocion promocion) {
        if (promocion.getIdPromocion() == 0) {
            promocion.setIdPromocion(++ultimaPromocion);
        }
        ultimaPromocion = Math.max(ultimaPromocion, promocion.getIdPromocion());
        if (!promociones.contains(promocion)) {
            promociones.add(promocion);
        }
    }

    public void cargarCategoria(Categoria categoria) {
        categorias.add(categoria);
        ultimaCategoria = Math.max(ultimaCategoria, categoria.getIdCategoria());
    }

    public void cargarProducto(Producto producto) {
        productos.add(producto);
        ultimoProducto = Math.max(ultimoProducto, producto.getIdProducto());
    }

    public void cargarPromocion(Promocion promocion) {
        promociones.add(promocion);
        ultimaPromocion = Math.max(ultimaPromocion, promocion.getIdPromocion());
    }
}
