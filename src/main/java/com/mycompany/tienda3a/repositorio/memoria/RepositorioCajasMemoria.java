package com.mycompany.tienda3a.repositorio.memoria;

import com.mycompany.tienda3a.modelo.Caja;
import com.mycompany.tienda3a.modelo.CierreCaja;
import com.mycompany.tienda3a.modelo.Pago;
import com.mycompany.tienda3a.modelo.Venta;
import com.mycompany.tienda3a.repositorio.RepositorioCajas;
import java.util.ArrayList;
import java.util.List;

public class RepositorioCajasMemoria implements RepositorioCajas {
    protected final List<Caja> cajas = new ArrayList<>();
    protected final List<CierreCaja> cierres = new ArrayList<>();
    private int ultimaCaja;
    private int ultimoCierre;

    @Override
    public List<Caja> listarCajas() {
        return new ArrayList<>(cajas);
    }

    @Override
    public List<CierreCaja> listarCierres() {
        return new ArrayList<>(cierres);
    }

    public void agregarCaja(Caja caja) {
        if (caja.getIdCaja() == 0) {
            caja.setIdCaja(++ultimaCaja);
        }
        ultimaCaja = Math.max(ultimaCaja, caja.getIdCaja());
        cajas.add(caja);
    }

    @Override
    public void guardarCierre(CierreCaja cierre, List<Venta> ventas, List<Pago> devoluciones) {
        if (cierre.getIdCierreCaja() == 0) {
            cierre.setIdCierreCaja(++ultimoCierre);
        }
        ultimoCierre = Math.max(ultimoCierre, cierre.getIdCierreCaja());
        for (Venta v : ventas) {
            v.setCierreCaja(cierre);
        }
        for (Pago p : devoluciones) {
            p.asignarCierreReversion(cierre);
        }
        if (!cierres.contains(cierre)) {
            cierres.add(cierre);
        }
    }

    public void cargarCierre(CierreCaja cierre) {
        cierres.add(cierre);
        ultimoCierre = Math.max(ultimoCierre, cierre.getIdCierreCaja());
    }
}
