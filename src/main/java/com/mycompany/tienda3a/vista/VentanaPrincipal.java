package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.modelo.Caja;
import com.mycompany.tienda3a.modelo.Cajero;
import com.mycompany.tienda3a.modelo.Usuario;
import com.mycompany.tienda3a.vista.componentes.Aviso;
import com.mycompany.tienda3a.vista.componentes.Tema;
import java.awt.AWTEvent;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class VentanaPrincipal extends JFrame {
    private final Contexto ctx;
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel raiz = new JPanel(tarjetas);
    private final PanelLogin login;
    private JPanel actual;
    private EspacioTrabajo trabajo;
    private LocalDateTime ultimaActividad = LocalDateTime.now();

    public VentanaPrincipal(Contexto ctx) {
        super("Tiendas 3A · Sistema de ventas en caja · " + ctx.origenDatos);
        this.ctx = ctx;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 740));
        setSize(1366, 800);
        setLocationRelativeTo(null);
        raiz.setBackground(Tema.FONDO);
        login = new PanelLogin(ctx, this::alIngresar);
        raiz.add(login, "login");
        setContentPane(raiz);
        tarjetas.show(raiz, "login");

        Toolkit.getDefaultToolkit().addAWTEventListener(e -> ultimaActividad = LocalDateTime.now(),
                AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
        Timer vigilante = new Timer(20000, e -> {
            if (ctx.getUsuario() != null && ctx.acceso.sesionExpirada(ultimaActividad)) {
                cerrarSesion(true);
            }
        });
        vigilante.start();
        login.reiniciar(null, null);
    }

    private void alIngresar(Usuario u) {
        ctx.setUsuario(u);
        ctx.setCaja(null);
        ultimaActividad = LocalDateTime.now();
        if (u instanceof Cajero) {
            mostrarPanel(new PanelSeleccionCaja(ctx, this::alElegirCaja, () -> cerrarSesion(false)));
        } else {
            abrirEspacio(List.of(new PanelInicio(ctx), new PanelVentas(ctx), new PanelReportes(ctx),
                    new PanelProductos(ctx), new PanelPromociones(ctx), new PanelUsuarios(ctx),
                    new PanelComprobantes(ctx), new PanelBitacora(ctx)));
        }
    }

    private void alElegirCaja(Caja caja) {
        ctx.setCaja(caja);
        abrirEspacio(List.of(new PanelVenta(ctx), new PanelConsultaStock(ctx), new PanelAnulacion(ctx),
                new PanelCierreCaja(ctx)));
    }

    private void abrirEspacio(List<Pantalla> pantallas) {
        trabajo = new EspacioTrabajo(ctx, pantallas, () -> cerrarSesion(false));
        mostrarPanel(trabajo);
    }

    private void mostrarPanel(JPanel p) {
        if (actual != null) {
            raiz.remove(actual);
        }
        actual = p;
        raiz.add(p, "trabajo");
        tarjetas.show(raiz, "trabajo");
        raiz.revalidate();
        raiz.repaint();
    }

    void cerrarSesion(boolean expirada) {
        ctx.acceso.cerrarSesion(ctx.getUsuario());
        ctx.setUsuario(null);
        ctx.setCaja(null);
        if (trabajo != null) {
            trabajo.detener();
            trabajo = null;
        }
        if (actual != null) {
            raiz.remove(actual);
            actual = null;
        }
        tarjetas.show(raiz, "login");
        login.reiniciar(expirada ? "Tu sesión se cerró por 15 minutos de inactividad. La venta que estabas preparando "
                + "se conservó: al volver a ingresar la retomas." : null, Aviso.Tipo.INFO);
    }

    public void irA(String tituloPantalla) {
        if (trabajo != null) {
            trabajo.irA(tituloPantalla);
        }
    }

    public void ingresarComo(String usuario, String clave, String codigoCaja) {
        alIngresar(ctx.acceso.iniciarSesion(usuario, clave));
        if (codigoCaja != null) {
            for (Caja c : ctx.caja.listarCajas()) {
                if (c.getCodigo().equals(codigoCaja)) {
                    alElegirCaja(c);
                }
            }
        }
    }
}
