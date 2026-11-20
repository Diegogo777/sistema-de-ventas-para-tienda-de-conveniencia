package com.mycompany.tienda3a.modelo;

import com.mycompany.tienda3a.config.Configuracion;
import com.mycompany.tienda3a.config.Empresa;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Comprobante implements Imprimible {
    protected static final int ANCHO = 42;
    protected static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    protected int idComprobante;
    protected Venta venta;
    protected String serie;
    protected int correlativo;
    protected LocalDateTime fechaEmision;
    protected EstadoComprobante estado;
    protected int intentosEnvio;
    protected LocalDateTime fechaUltimoEnvio;
    protected LocalDateTime fechaRespuesta;
    protected String observacionSunat;
    protected String observacionRevision;

    public Comprobante(int idComprobante, Venta venta, String serie, int correlativo, LocalDateTime fechaEmision) {
        this.idComprobante = idComprobante;
        this.venta = venta;
        this.serie = serie;
        this.correlativo = correlativo;
        this.fechaEmision = fechaEmision == null ? LocalDateTime.now() : fechaEmision;
        this.estado = EstadoComprobante.PENDIENTE;
    }

    public abstract TipoComprobante getTipo();

    public abstract boolean validarDatos();

    public String getNumero() {
        return serie + "-" + String.format("%08d", correlativo);
    }

    public void registrarEnvio(LocalDateTime fechaHora) {
        intentosEnvio++;
        fechaUltimoEnvio = fechaHora;
    }

    public void registrarRespuesta(RespuestaSunat respuesta, String mensaje) {
        fechaRespuesta = LocalDateTime.now();
        observacionSunat = mensaje;
        switch (respuesta) {
            case ACEPTADO -> estado = EstadoComprobante.ACEPTADO;
            case RECHAZADO -> estado = EstadoComprobante.RECHAZADO;
            case SIN_RESPUESTA -> estado = EstadoComprobante.PENDIENTE;
        }
    }

    public boolean puedeReintentar(LocalDateTime ahora) {
        if (estado != EstadoComprobante.PENDIENTE || intentosEnvio >= Configuracion.getMaxIntentosEnvio()) {
            return false;
        }
        return fechaUltimoEnvio == null
                || !ahora.isBefore(fechaUltimoEnvio.plusMinutes(Configuracion.getMinutosReintentoEnvio()));
    }

    public void anular() {
        estado = EstadoComprobante.ANULADO;
    }

    public void registrarRevision(String observacion) {
        if (observacion == null || observacion.isBlank()) {
            throw new IllegalArgumentException("Escribe la observación de la revisión.");
        }
        this.observacionRevision = observacion.trim();
    }

    @Override
    public String generarImpresion() {
        return armarTicket(getTipo().getEtiqueta().toUpperCase(), null);
    }

    protected String armarTicket(String titulo, String datosCliente) {
        StringBuilder t = new StringBuilder();
        String linea = "-".repeat(ANCHO) + "\n";
        t.append(centrar(Empresa.NOMBRE_COMERCIAL)).append(centrar(Empresa.RAZON_SOCIAL + " · RUC " + Empresa.RUC))
         .append(centrar(Empresa.DIRECCION)).append(linea)
         .append(centrar(titulo)).append(centrar(getNumero()))
         .append("Fecha : ").append(fechaEmision.format(FECHA)).append("\n")
         .append("Caja  : ").append(venta.getCaja().getCodigo())
         .append("   Cajero: ").append(venta.getCajero().getNombre()).append("\n");
        if (datosCliente != null) {
            t.append(datosCliente);
        }
        t.append(linea).append(String.format("%-6s%-26s%10s%n", "CANT", "DESCRIPCION", "IMPORTE"));
        for (DetalleVenta d : venta.getDetalles()) {
            String nombre = d.getProducto().getNombre();
            if (nombre.length() > 25) {
                nombre = nombre.substring(0, 25);
            }
            String cant = d.getProducto().getUnidadMedida() == UnidadMedida.KG
                    ? String.format("%.3f", d.getCantidad()) : String.format("%.0f", d.getCantidad());
            t.append(String.format("%-6s%-26s%10.2f%n", cant, nombre, d.getImporte()));
            t.append(String.format("      P.U. %.2f%s%n", d.getPrecioUnitario(),
                    d.tienePromocion() ? "  (promo, antes " + String.format("%.2f", d.getPrecioLista()) + ")" : ""));
        }
        t.append(linea)
         .append(fila("OP. GRAVADA", venta.getSubtotal()))
         .append(fila(String.format("IGV %.0f%%", Configuracion.getTasaIgv() * 100), venta.getIgv()))
         .append(fila("TOTAL", venta.getTotal()));
        if (venta.getAhorroTotal() > 0) {
            t.append(fila("Ahorro en promociones", venta.getAhorroTotal()));
        }
        Pago p = venta.getPago();
        if (p != null) {
            t.append(linea).append(fila("Pago " + p.getMedio().getEtiqueta(), p instanceof PagoEfectivo e
                    ? e.getMontoEntregado() : p.getMonto()));
            if (p instanceof PagoEfectivo e) {
                t.append(fila("Vuelto", e.getVuelto()));
            } else if (p instanceof PagoElectronico pe) {
                t.append("N° operación: ").append(pe.getReferenciaDigital()).append("\n");
            }
        }
        t.append(linea).append(centrar("Estado SUNAT: " + estado.getEtiqueta().toUpperCase()))
         .append(centrar("Gracias por su compra"));
        return t.toString();
    }

    private static String centrar(String texto) {
        int espacios = Math.max(0, (ANCHO - texto.length()) / 2);
        return " ".repeat(espacios) + texto + "\n";
    }

    private static String fila(String etiqueta, double monto) {
        return String.format("%-28sS/ %11.2f%n", etiqueta, monto);
    }

    public void cargarEstado(EstadoComprobante estado, int intentosEnvio, LocalDateTime fechaUltimoEnvio,
                             LocalDateTime fechaRespuesta, String observacionSunat, String observacionRevision) {
        this.estado = estado;
        this.intentosEnvio = intentosEnvio;
        this.fechaUltimoEnvio = fechaUltimoEnvio;
        this.fechaRespuesta = fechaRespuesta;
        this.observacionSunat = observacionSunat;
        this.observacionRevision = observacionRevision;
    }

    public int getIdComprobante() { return idComprobante; }
    public void setIdComprobante(int idComprobante) { this.idComprobante = idComprobante; }
    public Venta getVenta() { return venta; }
    public String getSerie() { return serie; }
    public int getCorrelativo() { return correlativo; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public EstadoComprobante getEstado() { return estado; }
    public int getIntentosEnvio() { return intentosEnvio; }
    public LocalDateTime getFechaUltimoEnvio() { return fechaUltimoEnvio; }
    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public String getObservacionSunat() { return observacionSunat; }
    public String getObservacionRevision() { return observacionRevision; }
}
