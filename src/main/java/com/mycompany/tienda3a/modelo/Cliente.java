package com.mycompany.tienda3a.modelo;

public class Cliente {
    private int idCliente;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String nombreRazonSocial;
    private String direccion;
    private boolean activo;

    public Cliente(int idCliente, TipoDocumento tipoDocumento, String numeroDocumento,
                   String nombreRazonSocial, String direccion) {
        if (tipoDocumento == null) {
            throw new IllegalArgumentException("Elige el tipo de documento.");
        }
        this.idCliente = idCliente;
        this.tipoDocumento = tipoDocumento;
        setNumeroDocumento(numeroDocumento);
        setNombreRazonSocial(nombreRazonSocial);
        this.direccion = direccion == null ? "" : direccion.trim();
        this.activo = true;
    }

    public void desactivar() {
        activo = false;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }

    public void setNumeroDocumento(String numeroDocumento) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new IllegalArgumentException("El número de documento es obligatorio.");
        }
        this.numeroDocumento = numeroDocumento.trim();
    }

    public String getNombreRazonSocial() { return nombreRazonSocial; }

    public void setNombreRazonSocial(String nombreRazonSocial) {
        if (nombreRazonSocial == null || nombreRazonSocial.isBlank()) {
            throw new IllegalArgumentException("El nombre o la razón social es obligatorio.");
        }
        this.nombreRazonSocial = nombreRazonSocial.trim();
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion == null ? "" : direccion.trim(); }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombreRazonSocial + " · " + tipoDocumento + " " + numeroDocumento;
    }
}
