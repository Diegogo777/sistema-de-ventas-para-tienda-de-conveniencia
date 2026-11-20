package com.mycompany.tienda3a.repositorio.mysql;

import com.mycompany.tienda3a.modelo.Categoria;
import com.mycompany.tienda3a.modelo.Producto;
import com.mycompany.tienda3a.modelo.Promocion;
import com.mycompany.tienda3a.repositorio.memoria.RepositorioCatalogoMemoria;

public class RepositorioCatalogoMySQL extends RepositorioCatalogoMemoria {
    @Override
    public void guardarCategoria(Categoria k) {
        ConexionMySQL.enTransaccion(c -> {
            if (k.getIdCategoria() == 0) {
                k.setIdCategoria(ConexionMySQL.insertar(c,
                        "INSERT INTO categoria (nombre, descripcion, activo) VALUES (?, ?, ?)",
                        k.getNombre(), k.getDescripcion(), k.isActivo()));
            } else {
                ConexionMySQL.actualizar(c, "UPDATE categoria SET nombre = ?, descripcion = ?, activo = ? WHERE categoria_id = ?",
                        k.getNombre(), k.getDescripcion(), k.isActivo(), k.getIdCategoria());
            }
        });
        super.guardarCategoria(k);
    }

    @Override
    public void guardarProducto(Producto p) {
        ConexionMySQL.enTransaccion(c -> {
            if (p.getIdProducto() == 0) {
                p.setIdProducto(ConexionMySQL.insertar(c,
                        "INSERT INTO producto (categoria_id, codigo, nombre, unidad_medida, precio_venta, stock_actual, activo) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                        p.getCategoria().getIdCategoria(), p.getCodigo(), p.getNombre(), p.getUnidadMedida(),
                        p.getPrecioVenta(), p.getStockActual(), p.isActivo()));
            } else {
                ConexionMySQL.actualizar(c,
                        "UPDATE producto SET categoria_id = ?, codigo = ?, nombre = ?, unidad_medida = ?, precio_venta = ?, "
                        + "stock_actual = ?, activo = ? WHERE producto_id = ?",
                        p.getCategoria().getIdCategoria(), p.getCodigo(), p.getNombre(), p.getUnidadMedida(),
                        p.getPrecioVenta(), p.getStockActual(), p.isActivo(), p.getIdProducto());
            }
        });
        super.guardarProducto(p);
    }

    @Override
    public void guardarPromocion(Promocion p) {
        ConexionMySQL.enTransaccion(c -> {
            if (p.getIdPromocion() == 0) {
                p.setIdPromocion(ConexionMySQL.insertar(c,
                        "INSERT INTO promocion (producto_id, creado_por, nombre, precio_promocional, fecha_inicio, fecha_fin, activo) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                        p.getProducto().getIdProducto(),
                        p.getCreadoPor() == null ? null : p.getCreadoPor().getIdUsuario(),
                        p.getNombre(), p.getPrecioPromocional(), p.getFechaInicio(), p.getFechaFin(), p.isActivo()));
            } else {
                ConexionMySQL.actualizar(c, "UPDATE promocion SET activo = ? WHERE promocion_id = ?",
                        p.isActivo(), p.getIdPromocion());
            }
        });
        super.guardarPromocion(p);
    }
}
