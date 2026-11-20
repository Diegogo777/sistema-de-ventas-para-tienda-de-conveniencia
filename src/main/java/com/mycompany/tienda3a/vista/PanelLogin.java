package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.datos.DatosIniciales;
import com.mycompany.tienda3a.excepcion.ExcepcionNegocio;
import com.mycompany.tienda3a.modelo.Usuario;
import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.function.Consumer;
import javax.swing.*;

class PanelLogin extends JPanel {
    private final Contexto ctx;
    private final Campo usuario = new Campo("Ej.: atorres");
    private final Campo.CampoClave clave = new Campo.CampoClave("Tu contraseña");
    private final Aviso aviso = new Aviso();
    private final Boton ingresar = new Boton("Ingresar", Boton.Estilo.PRIMARIO);
    private final Consumer<Usuario> alIngresar;

    PanelLogin(Contexto ctx, Consumer<Usuario> alIngresar) {
        super(new GridLayout(1, 2));
        this.ctx = ctx;
        this.alIngresar = alIngresar;
        add(panelMarca());
        add(panelFormulario());
    }

    void reiniciar(String mensaje, Aviso.Tipo tipo) {
        clave.setText("");
        usuario.setError(false);
        clave.setError(false);
        if (mensaje == null) {
            aviso.limpiar();
        } else {
            aviso.mostrar(tipo, mensaje);
        }
        SwingUtilities.invokeLater(() -> (usuario.getText().isEmpty() ? usuario : clave).requestFocusInWindow());
    }

    private JComponent panelMarca() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setPaint(new GradientPaint(0, 0, Tema.PETROLEO, getWidth(), getHeight(), Tema.PETROLEO_OSCURO));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(255, 255, 255, 14));
                g.fill(new Ellipse2D.Double(getWidth() - 260, -140, 420, 420));
                g.fill(new Ellipse2D.Double(-120, getHeight() - 220, 320, 320));
                g.setColor(new Color(242, 183, 5, 40));
                g.fill(new Ellipse2D.Double(getWidth() - 150, getHeight() - 170, 120, 120));
                g.dispose();
            }
        };
        p.setBorder(BorderFactory.createEmptyBorder(48, 56, 40, 56));
        p.add(new Logo(46, true, Color.WHITE), BorderLayout.NORTH);

        JPanel centro = Textos.transparente(null);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("<html>Cobra rápido,<br>cuadra tu caja<br><span style='color:#F2B705'>sin errores.</span></html>");
        titulo.setFont(Tema.negrita(38));
        titulo.setForeground(Color.WHITE);
        centro.add(titulo);
        centro.add(Box.createVerticalStrut(16));
        JLabel sub = new JLabel("<html><div style='width:330px'>Sistema de ventas en caja de Tiendas 3A: "
                + "venta, comprobante, stock y cierre en un solo lugar.</div></html>");
        sub.setFont(Tema.texto(14));
        sub.setForeground(new Color(0xCFE2E6));
        centro.add(sub);
        centro.add(Box.createVerticalStrut(30));
        centro.add(beneficio(Icono.Tipo.PROMO, "Promociones aplicadas solas al escanear"));
        centro.add(Box.createVerticalStrut(12));
        centro.add(beneficio(Icono.Tipo.COMPROBANTE, "Boleta, factura o venta simple con la serie de tu caja"));
        centro.add(Box.createVerticalStrut(12));
        centro.add(beneficio(Icono.Tipo.CIERRE, "Cierre de caja con el efectivo esperado calculado"));
        JPanel envoltura = Textos.transparente(new GridBagLayout());
        envoltura.add(centro);
        p.add(envoltura, BorderLayout.CENTER);

        JLabel pie = new JLabel("Tiendas 3A · Sistema de ventas en caja · Datos: " + ctx.origenDatos);
        pie.setFont(Tema.texto(12));
        pie.setForeground(new Color(0xA9C6CC));
        p.add(pie, BorderLayout.SOUTH);
        return p;
    }

    private JComponent beneficio(Icono.Tipo icono, String texto) {
        JLabel l = new JLabel(texto);
        l.setIcon(new Icono(icono, 20, Tema.AMARILLO));
        l.setIconTextGap(12);
        l.setFont(Tema.texto(14));
        l.setForeground(Color.WHITE);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JComponent panelFormulario() {
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(Tema.FONDO);

        Tarjeta t = new Tarjeta();
        t.setBorder(BorderFactory.createEmptyBorder(34, 36, 30, 36));
        JPanel f = Textos.transparente(null);
        f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        JLabel tit = Textos.titulo("Iniciar sesión");
        f.add(izq(tit));
        f.add(izq(Textos.ayuda("Ingresa con tu usuario de Tiendas 3A.")));
        f.add(Box.createVerticalStrut(22));
        usuario.conIcono(Icono.Tipo.USUARIO);
        f.add(izq(Textos.conEtiqueta("Usuario", usuario)));
        f.add(Box.createVerticalStrut(14));
        f.add(izq(Textos.conEtiqueta("Contraseña", clave)));
        f.add(Box.createVerticalStrut(14));
        f.add(izq(aviso));
        f.add(Box.createVerticalStrut(8));
        ingresar.alto(46);
        ingresar.setFont(Tema.medio(14));
        f.add(izq(ingresar));
        f.add(Box.createVerticalStrut(18));
        JLabel prueba = new JLabel("<html><div style='width:300px'>Usuarios de prueba: <b>atorres</b> y <b>lquispe</b> (cajeros), "
                + "<b>admin</b> (administrador). Contraseña: <b>" + DatosIniciales.CLAVE_PRUEBA + "</b></div></html>");
        prueba.setFont(Tema.texto(11.5f));
        prueba.setForeground(Tema.TEXTO_TENUE);
        f.add(izq(prueba));
        t.con(f);
        fondo.add(Textos.ancho(t, 420));

        ingresar.addActionListener(e -> ingresar());
        clave.addActionListener(e -> ingresar());
        usuario.addActionListener(e -> clave.requestFocusInWindow());
        return fondo;
    }

    private static JComponent izq(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height + (c instanceof Aviso ? 40 : 0)));
        return c;
    }

    private void ingresar() {
        aviso.limpiar();
        usuario.setError(usuario.getText().isBlank());
        clave.setError(clave.getPassword().length == 0);
        if (usuario.isError() || clave.isError()) {
            aviso.mostrar(Aviso.Tipo.ERROR, "Escribe tu usuario y tu contraseña.");
            return;
        }
        try {
            Usuario u = ctx.acceso.iniciarSesion(usuario.getText(), new String(clave.getPassword()));
            clave.setText("");
            alIngresar.accept(u);
        } catch (ExcepcionNegocio ex) {
            clave.setText("");
            clave.setError(true);
            aviso.mostrar(Aviso.Tipo.ERROR, ex.getMessage());
            clave.requestFocusInWindow();
        }
    }
}
