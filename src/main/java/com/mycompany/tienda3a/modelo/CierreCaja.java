package com.mycompany.tienda3a.modelo;

import com.mycompany.tienda3a.config.Configuracion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CierreCaja {
    private int idCierreCaja;
    private Cajero cajero;
    private Caja caja;
    private LocalDate fecha;
    private LocalDateTime fechaHoraCierre;
    private double fondoInicial;
    private int cantidadVentas;
    private int cantidadAnuladas;
    private double totalEfectivo;
    private double totalTarjeta;
    private double totalBilletera;
    private double devueltoEfectivo;
    private double efectivoEsperado;
    private double efectivoContado;
    private double diferencia;
    private String observacion;

    public CierreCaja(int idCierreCaja, Cajero cajero, Caja caja, LocalDate fecha, double fondoInicial) {
        this.idCierreCaja = idCierreCaja;
        this.cajero = cajero;
        this.caja = caja;
        this.fecha = fecha;
        this.fondoInicial = fondoInicial;
    }

    public void calcularResumen(List<Venta> ventas, List<Pago> devoluciones) {
        cantidadVentas = 0;
        cantidadAnuladas = 0;
        totalEfectivo = 0;
        totalTarjeta = 0;
        totalBilletera = 0;
        devueltoEfectivo = 0;
        for (Venta v : ventas) {
            if (v.getEstado() == EstadoVenta.ANULADA) {
                cantidadAnuladas++;
            } else if (v.getEstado() == EstadoVenta.CONFIRMADA) {
                cantidadVentas++;
            } else {
                continue;
            }
            Pago p = v.getPago();
            if (p == null) {
                continue;
            }
            switch (p.getMedio()) {
                case EFECTIVO -> totalEfectivo += p.getMonto();
                case TARJETA -> totalTarjeta += p.getMonto();
                case BILLETERA -> totalBilletera += p.getMonto();
            }
        }
        for (Pago p : devoluciones) {
            if (p.getMedio() == MedioPago.EFECTIVO) {
                devueltoEfectivo += p.getMonto();
            }
        }
        totalEfectivo = Venta.redondear2(totalEfectivo);
        totalTarjeta = Venta.redondear2(totalTarjeta);
        totalBilletera = Venta.redondear2(totalBilletera);
        devueltoEfectivo = Venta.redondear2(devueltoEfectivo);
        efectivoEsperado = calcularEfectivoEsperado();
    }

    public double calcularEfectivoEsperado() {
        return Venta.redondear2(fondoInicial + totalEfectivo - devueltoEfectivo);
    }

    public double registrarContado(double efectivoContado) {
        if (efectivoContado < 0) {
            throw new IllegalArgumentException("El efectivo contado no puede ser negativo.");
        }
        this.efectivoContado = Venta.redondear2(efectivoContado);
        this.diferencia = Venta.redondear2(this.efectivoContado - efectivoEsperado);
        return diferencia;
    }

    public void confirmar(String observacion) {
        if (Math.abs(diferencia) >= 0.005 && (observacion == null || observacion.isBlank())) {
            throw new IllegalArgumentException("Hay una diferencia de S/ " + String.format("%.2f", diferencia)
                    + ". Escribe una observación para registrar el cierre.");
        }
        this.observacion = observacion == null ? "" : observacion.trim();
        this.fechaHoraCierre = LocalDateTime.now();
    }

    public double getTotalCobrado() {
        return Venta.redondear2(totalEfectivo + totalTarjeta + totalBilletera);
    }

    public void cargarDatos(LocalDateTime fechaHoraCierre, int cantidadVentas, int cantidadAnuladas,
                            double totalEfectivo, double totalTarjeta, double totalBilletera,
                            double devueltoEfectivo, double efectivoEsperado, double efectivoContado,
                            double diferencia, String observacion) {
        this.fechaHoraCierre = fechaHoraCierre;
        this.cantidadVentas = cantidadVentas;
        this.cantidadAnuladas = cantidadAnuladas;
        this.totalEfectivo = totalEfectivo;
        this.totalTarjeta = totalTarjeta;
        this.totalBilletera = totalBilletera;
        this.devueltoEfectivo = devueltoEfectivo;
        this.efectivoEsperado = efectivoEsperado;
        this.efectivoContado = efectivoContado;
        this.diferencia = diferencia;
        this.observacion = observacion;
    }

    public int getIdCierreCaja() { return idCierreCaja; }
    public void setIdCierreCaja(int idCierreCaja) { this.idCierreCaja = idCierreCaja; }
    public Cajero getCajero() { return cajero; }
    public Caja getCaja() { return caja; }
    public LocalDate getFecha() { return fecha; }
    public LocalDateTime getFechaHoraCierre() { return fechaHoraCierre; }
    public double getFondoInicial() { return fondoInicial; }
    public int getCantidadVentas() { return cantidadVentas; }
    public int getCantidadAnuladas() { return cantidadAnuladas; }
    public double getTotalEfectivo() { return totalEfectivo; }
    public double getTotalTarjeta() { return totalTarjeta; }
    public double getTotalBilletera() { return totalBilletera; }
    public double getDevueltoEfectivo() { return devueltoEfectivo; }
    public double getEfectivoEsperado() { return efectivoEsperado; }
    public double getEfectivoContado() { return efectivoContado; }
    public double getDiferencia() { return diferencia; }
    public String getObservacion() { return observacion; }

    public static double fondoPorDefecto() {
        return Configuracion.getFondoInicialCaja();
    }
}
