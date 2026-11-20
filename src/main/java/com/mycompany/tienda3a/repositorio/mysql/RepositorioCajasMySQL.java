package com.mycompany.tienda3a.repositorio.mysql;

import com.mycompany.tienda3a.modelo.CierreCaja;
import com.mycompany.tienda3a.modelo.Pago;
import com.mycompany.tienda3a.modelo.Venta;
import com.mycompany.tienda3a.repositorio.memoria.RepositorioCajasMemoria;
import java.util.List;

public class RepositorioCajasMySQL extends RepositorioCajasMemoria {
    @Override
    public void guardarCierre(CierreCaja k, List<Venta> ventas, List<Pago> devoluciones) {
        ConexionMySQL.enTransaccion(c -> {
            int id = ConexionMySQL.insertar(c,
                    "INSERT INTO cierre_caja (usuario_id, caja_id, fecha, fecha_hora_cierre, fondo_inicial, cantidad_ventas, "
                    + "cantidad_anuladas, total_efectivo, total_tarjeta, total_billetera, devuelto_efectivo, efectivo_esperado, "
                    + "efectivo_contado, diferencia, observacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    k.getCajero().getIdUsuario(), k.getCaja().getIdCaja(), k.getFecha(), k.getFechaHoraCierre(),
                    k.getFondoInicial(), k.getCantidadVentas(), k.getCantidadAnuladas(), k.getTotalEfectivo(),
                    k.getTotalTarjeta(), k.getTotalBilletera(), k.getDevueltoEfectivo(), k.getEfectivoEsperado(),
                    k.getEfectivoContado(), k.getDiferencia(), k.getObservacion());
            for (Venta v : ventas) {
                ConexionMySQL.actualizar(c, "UPDATE venta SET cierre_caja_id = ? WHERE venta_id = ?", id, v.getIdVenta());
            }
            for (Pago p : devoluciones) {
                ConexionMySQL.actualizar(c, "UPDATE pago SET cierre_reversion_id = ? WHERE pago_id = ?", id, p.getIdPago());
            }
            k.setIdCierreCaja(id);
        });
        super.guardarCierre(k, ventas, devoluciones);
    }
}
