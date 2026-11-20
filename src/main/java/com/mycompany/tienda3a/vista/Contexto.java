package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.controlador.*;
import com.mycompany.tienda3a.modelo.Caja;
import com.mycompany.tienda3a.modelo.Usuario;

public class Contexto {
    public final ControladorAcceso acceso;
    public final ControladorVenta venta;
    public final ControladorComprobante comprobante;
    public final ControladorCaja caja;
    public final ControladorProducto producto;
    public final ControladorConsulta consulta;
    public final ControladorUsuario usuarios;
    public final String origenDatos;

    private Usuario usuario;
    private Caja cajaActual;

    public Contexto(ControladorAcceso acceso, ControladorVenta venta, ControladorComprobante comprobante,
                    ControladorCaja caja, ControladorProducto producto, ControladorConsulta consulta,
                    ControladorUsuario usuarios, String origenDatos) {
        this.acceso = acceso;
        this.venta = venta;
        this.comprobante = comprobante;
        this.caja = caja;
        this.producto = producto;
        this.consulta = consulta;
        this.usuarios = usuarios;
        this.origenDatos = origenDatos;
    }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Caja getCaja() { return cajaActual; }
    public void setCaja(Caja caja) { this.cajaActual = caja; }
}
