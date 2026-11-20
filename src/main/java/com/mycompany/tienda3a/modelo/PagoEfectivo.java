package com.mycompany.tienda3a.modelo;

import java.time.LocalDateTime;

public class PagoEfectivo extends Pago {
    private double montoEntregado;
    private double vuelto;

    public PagoEfectivo(int idPago, Venta venta, Cajero cajero, double monto, LocalDateTime fechaHora,
                        double montoEntregado) {
        super(idPago, venta, cajero, monto, fechaHora);
        this.montoEntregado = Venta.redondear2(montoEntregado);
        this.vuelto = calcularVuelto();
    }

    @Override
    public boolean validar() {
        return montoEntregado + 1e-9 >= monto;
    }

    public double calcularVuelto() {
        return Math.max(0, Venta.redondear2(montoEntregado - monto));
    }

    @Override
    public MedioPago getMedio() {
        return MedioPago.EFECTIVO;
    }

    public double getMontoEntregado() { return montoEntregado; }
    public double getVuelto() { return vuelto; }
}
