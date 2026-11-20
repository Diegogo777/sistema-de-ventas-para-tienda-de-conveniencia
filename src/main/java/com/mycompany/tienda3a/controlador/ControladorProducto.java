package com.mycompany.tienda3a.controlador;

import com.mycompany.tienda3a.excepcion.ExcepcionNegocio;
import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.repositorio.RepositorioCatalogo;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ControladorProducto {
    private final RepositorioCatalogo catalogo;
    private final Bitacora bitacora;

    public ControladorProducto(RepositorioCatalogo catalogo, Bitacora bitacora) {
        this.catalogo = catalogo;
        this.bitacora = bitacora;
    }

    public List<Producto> buscarProductos(String codigoONombre) {
        return buscar(codigoONombre, false);
    }

    public List<Producto> buscar(String texto, boolean incluirInactivos) {
        String t = normalizar(texto);
        List<Producto> lista = new ArrayList<>();
        for (Producto p : catalogo.listarProductos()) {
            if (!incluirInactivos && !p.isActivo()) {
                continue;
            }
            if (t.isEmpty() || p.getCodigo().toLowerCase().startsWith(t) || normalizar(p.getNombre()).contains(t)
                    || normalizar(p.getCategoria().getNombre()).contains(t)) {
                lista.add(p);
            }
        }
        lista.sort(Comparator.comparing(Producto::getNombre));
        return lista;
    }

    public List<Categoria> listarCategorias() {
        return catalogo.listarCategorias();
    }

    public Producto registrarProducto(Categoria categoria, String codigo, String nombre, UnidadMedida unidad,
                                      double precio, double stockInicial, Usuario administrador) {
        if (codigo != null && catalogo.buscarProductoPorCodigo(codigo.trim()) != null) {
            throw new ExcepcionNegocio("Ya existe un producto con el código " + codigo.trim() + ".");
        }
        Producto p;
        try {
            p = new Producto(0, categoria, codigo, nombre, unidad, precio, stockInicial);
        } catch (IllegalArgumentException e) {
            throw new ExcepcionNegocio(e.getMessage());
        }
        catalogo.guardarProducto(p);
        bitacora.registrar(administrador, "REGISTRAR_PRODUCTO", "producto", p.getIdProducto(), ResultadoOperacion.EXITO,
                p.getCodigo() + " · " + p.getNombre());
        return p;
    }

    public void modificarProducto(Producto producto, Categoria categoria, String nombre, UnidadMedida unidad,
                                  Usuario administrador) {
        try {
            producto.setCategoria(categoria);
            producto.setNombre(nombre);
            producto.setUnidadMedida(unidad);
        } catch (IllegalArgumentException e) {
            throw new ExcepcionNegocio(e.getMessage());
        }
        catalogo.guardarProducto(producto);
        bitacora.registrar(administrador, "MODIFICAR_PRODUCTO", "producto", producto.getIdProducto(),
                ResultadoOperacion.EXITO, producto.getNombre());
    }

    public void cambiarPrecio(Producto producto, double nuevoPrecio, Usuario administrador) {
        double anterior = producto.getPrecioVenta();
        try {
            producto.cambiarPrecio(nuevoPrecio);
        } catch (IllegalArgumentException e) {
            throw new ExcepcionNegocio(e.getMessage());
        }
        catalogo.guardarProducto(producto);
        bitacora.registrar(administrador, "CAMBIAR_PRECIO", "producto", producto.getIdProducto(), ResultadoOperacion.EXITO,
                String.format("%s: S/ %.2f → S/ %.2f", producto.getNombre(), anterior, nuevoPrecio));
    }

    public void desactivarProducto(Producto producto, Usuario administrador) {
        producto.desactivar();
        catalogo.guardarProducto(producto);
        bitacora.registrar(administrador, "DESACTIVAR_PRODUCTO", "producto", producto.getIdProducto(),
                ResultadoOperacion.EXITO, producto.getNombre());
    }

    public void reactivarProducto(Producto producto, Usuario administrador) {
        producto.setActivo(true);
        catalogo.guardarProducto(producto);
        bitacora.registrar(administrador, "REACTIVAR_PRODUCTO", "producto", producto.getIdProducto(),
                ResultadoOperacion.EXITO, producto.getNombre());
    }

    public Promocion registrarPromocion(Producto producto, String nombre, double precio, LocalDate inicio,
                                        LocalDate fin, Administrador administrador) {
        Promocion promo;
        try {
            promo = new Promocion(0, producto, administrador, nombre, precio, inicio, fin);
            producto.agregarPromocion(promo);
        } catch (IllegalArgumentException e) {
            throw new ExcepcionNegocio(e.getMessage());
        }
        catalogo.guardarPromocion(promo);
        bitacora.registrar(administrador, "REGISTRAR_PROMOCION", "promocion", promo.getIdPromocion(),
                ResultadoOperacion.EXITO, String.format("%s · %s a S/ %.2f", promo.getNombre(), producto.getNombre(), precio));
        return promo;
    }

    public void desactivarPromocion(Promocion promocion, Usuario administrador) {
        promocion.desactivar();
        catalogo.guardarPromocion(promocion);
        bitacora.registrar(administrador, "DESACTIVAR_PROMOCION", "promocion", promocion.getIdPromocion(),
                ResultadoOperacion.EXITO, promocion.getNombre());
    }

    public List<Promocion> listarPromociones() {
        List<Promocion> lista = catalogo.listarPromociones();
        lista.sort(Comparator.comparing(Promocion::getFechaInicio).reversed());
        return lista;
    }

    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto.trim().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}
