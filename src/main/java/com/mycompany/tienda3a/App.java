package com.mycompany.tienda3a;

import com.mycompany.tienda3a.controlador.*;
import com.mycompany.tienda3a.datos.CargaMySQL;
import com.mycompany.tienda3a.datos.DatosIniciales;
import com.mycompany.tienda3a.excepcion.ExcepcionDatos;
import com.mycompany.tienda3a.repositorio.Repositorios;
import com.mycompany.tienda3a.repositorio.mysql.ConexionMySQL;
import com.mycompany.tienda3a.sunat.SimuladorSunat;
import com.mycompany.tienda3a.vista.Contexto;
import com.mycompany.tienda3a.vista.VentanaPrincipal;
import com.mycompany.tienda3a.vista.componentes.Tema;
import javax.swing.Timer;

public final class App {
    private App() {
    }

    public static VentanaPrincipal iniciar() {
        Tema.aplicar();
        Repositorios repos = cargarRepositorios();
        System.out.println("Datos: " + repos.origen());

        Bitacora bitacora = new Bitacora(repos.auditoria());
        ControladorComprobante comprobante = new ControladorComprobante(repos.ventas(), new SimuladorSunat(), bitacora);
        ControladorVenta venta = new ControladorVenta(repos.catalogo(), repos.clientes(), repos.ventas(), comprobante, bitacora);
        Contexto ctx = new Contexto(
                new ControladorAcceso(repos.usuarios(), bitacora),
                venta,
                comprobante,
                new ControladorCaja(repos.cajas(), repos.ventas(), venta, bitacora),
                new ControladorProducto(repos.catalogo(), bitacora),
                new ControladorConsulta(repos.ventas(), repos.auditoria()),
                new ControladorUsuario(repos.usuarios(), repos.ventas(), bitacora),
                repos.origen());

        Timer reintentos = new Timer(60_000, e -> {
            try {
                comprobante.reintentarPendientes();
            } catch (RuntimeException ex) {
                System.err.println("Reintento de comprobantes: " + ex.getMessage());
            }
        });
        reintentos.start();

        VentanaPrincipal ventana = new VentanaPrincipal(ctx);
        ventana.setVisible(true);
        return ventana;
    }

    private static Repositorios cargarRepositorios() {
        if (ConexionMySQL.probar()) {
            try {
                return CargaMySQL.cargar();
            } catch (ExcepcionDatos e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.err.println("MySQL no responde (" + ConexionMySQL.getUltimoError() + "). Se usan datos de ejemplo.");
        }
        return DatosIniciales.crear();
    }
}