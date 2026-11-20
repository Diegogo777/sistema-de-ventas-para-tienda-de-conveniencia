package com.mycompany.tienda3a.repositorio;

import com.mycompany.tienda3a.modelo.Comprobante;
import com.mycompany.tienda3a.modelo.MovimientoInventario;
import com.mycompany.tienda3a.modelo.Venta;
import java.util.List;

public interface RepositorioVentas {
    List<Venta> listar();

    List<MovimientoInventario> listarMovimientos();

    int siguienteCorrelativo(String serie);

    void guardarVentaConfirmada(Venta venta);

    void guardarAnulacion(Venta venta);

    void actualizarComprobante(Comprobante comprobante);
}
