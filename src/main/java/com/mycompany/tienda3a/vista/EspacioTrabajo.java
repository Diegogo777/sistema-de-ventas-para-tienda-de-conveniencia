package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.modelo.Usuario;
import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;

class EspacioTrabajo extends JPanel {
    private final Contexto ctx;
    private final List<Pantalla> pantallas;
    private final List<ItemMenu> items = new ArrayList<>();
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final JLabel titulo = new JLabel();
    private final JLabel descripcion = new JLabel();
    private final JLabel reloj = new JLabel();
    private final Timer timerReloj;

    EspacioTrabajo(Contexto ctx, List<Pantalla> pantallas, Runnable alSalir) {
        super(new BorderLayout());
        this.ctx = ctx;
        this.pantallas = new ArrayList<>();
        for (Pantalla p : pantallas) {
            if (ctx.acceso.verificarPermiso(ctx.getUsuario(), p.casoUso())) {
                this.pantallas.add(p);
            }
        }
        add(menuLateral(alSalir), BorderLayout.WEST);
        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setBackground(Tema.FONDO);
        derecha.add(barraSuperior(), BorderLayout.NORTH);
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 24, 22, 24));
        for (Pantalla p : this.pantallas) {
            contenido.add(p.vista(), p.titulo());
        }
        derecha.add(contenido, BorderLayout.CENTER);
        add(derecha, BorderLayout.CENTER);
        timerReloj = new Timer(15000, e -> actualizarReloj());
        timerReloj.start();
        actualizarReloj();
        if (!this.pantallas.isEmpty()) {
            mostrar(this.pantallas.get(0));
        }
    }

    void irA(String tituloPantalla) {
        for (Pantalla p : pantallas) {
            if (p.titulo().equals(tituloPantalla)) {
                mostrar(p);
            }
        }
    }

    void detener() {
        timerReloj.stop();
    }

    void mostrar(Pantalla p) {
        if (!ctx.acceso.verificarPermiso(ctx.getUsuario(), p.casoUso())) {
            return;
        }
        titulo.setText(p.titulo());
        descripcion.setText(p.descripcion());
        for (ItemMenu i : items) {
            i.setActivo(i.pantalla == p);
        }
        p.alMostrar();
        tarjetas.show(contenido, p.titulo());
    }

    private void actualizarReloj() {
        String t = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE d 'de' MMM · HH:mm", new Locale("es", "PE")));
        reloj.setText(Character.toUpperCase(t.charAt(0)) + t.substring(1));
    }

    private JComponent menuLateral(Runnable alSalir) {
        JPanel m = new JPanel(new BorderLayout());
        m.setBackground(Tema.PETROLEO_OSCURO);
        m.setPreferredSize(new Dimension(236, 0));
        JPanel arriba = Textos.transparente(null);
        arriba.setLayout(new BoxLayout(arriba, BoxLayout.Y_AXIS));
        arriba.setBorder(BorderFactory.createEmptyBorder(22, 18, 10, 14));
        Logo logo = new Logo(38, true, Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        arriba.add(logo);
        arriba.add(Box.createVerticalStrut(28));
        JLabel sec = new JLabel(ctx.getUsuario().getRol() == com.mycompany.tienda3a.modelo.Rol.CAJERO ? "CAJA" : "ADMINISTRACIÓN");
        sec.setFont(Tema.medio(11));
        sec.setForeground(new Color(0x7FA3AB));
        sec.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 0));
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        arriba.add(sec);
        for (Pantalla p : pantallas) {
            ItemMenu i = new ItemMenu(p);
            i.setAlignmentX(Component.LEFT_ALIGNMENT);
            items.add(i);
            arriba.add(i);
            arriba.add(Box.createVerticalStrut(4));
        }
        m.add(arriba, BorderLayout.NORTH);
        m.add(tarjetaUsuario(alSalir), BorderLayout.SOUTH);
        return m;
    }

    private JComponent tarjetaUsuario(Runnable alSalir) {
        Usuario u = ctx.getUsuario();
        JPanel p = Textos.transparente(new BorderLayout(10, 12));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x1C4E5A)),
                BorderFactory.createEmptyBorder(16, 18, 18, 16)));
        JComponent avatar = new JComponent() {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setColor(Tema.AMARILLO);
                g.fill(new Ellipse2D.Double(0, 0, 38, 38));
                g.setColor(Tema.PETROLEO_OSCURO);
                g.setFont(Tema.negrita(14));
                FontMetrics fm = g.getFontMetrics();
                String s = u.getIniciales();
                g.drawString(s, (38 - fm.stringWidth(s)) / 2, (38 + fm.getAscent() - fm.getDescent()) / 2);
                g.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(38, 38));
        p.add(avatar, BorderLayout.WEST);
        JPanel txt = Textos.transparente(new GridLayout(2, 1));
        JLabel n = new JLabel(u.getNombre());
        n.setFont(Tema.medio(13));
        n.setForeground(Color.WHITE);
        JLabel r = new JLabel(u.getRol().getEtiqueta() + (ctx.getCaja() == null ? "" : " · " + ctx.getCaja().getCodigo()));
        r.setFont(Tema.texto(12));
        r.setForeground(new Color(0x9FBFC6));
        txt.add(n);
        txt.add(r);
        p.add(txt, BorderLayout.CENTER);
        JButton salir = new JButton("Cerrar sesión", new Icono(Icono.Tipo.SALIR, 17, new Color(0xCFE2E6)));
        salir.setFont(Tema.medio(13));
        salir.setForeground(new Color(0xCFE2E6));
        salir.setContentAreaFilled(false);
        salir.setBorderPainted(false);
        salir.setFocusPainted(false);
        salir.setHorizontalAlignment(SwingConstants.LEFT);
        salir.setIconTextGap(10);
        salir.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 0));
        salir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        salir.addActionListener(e -> alSalir.run());
        p.add(salir, BorderLayout.SOUTH);
        return p;
    }

    private JComponent barraSuperior() {
        JPanel b = new JPanel(new BorderLayout());
        b.setBackground(Tema.SUPERFICIE);
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDE),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));
        JPanel izq = Textos.transparente(new GridLayout(2, 1));
        titulo.setFont(Tema.negrita(20));
        titulo.setForeground(Tema.TEXTO);
        descripcion.setFont(Tema.texto(12.5f));
        descripcion.setForeground(Tema.TEXTO_SUAVE);
        izq.add(titulo);
        izq.add(descripcion);
        b.add(izq, BorderLayout.WEST);
        JPanel der = Textos.transparente(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        if (ctx.getCaja() != null) {
            der.add(new Insignia(ctx.getCaja().getCodigo() + " · Boletas " + ctx.getCaja().getSerieBoleta()
                    + " · Facturas " + ctx.getCaja().getSerieFactura(), Insignia.Tipo.MARCA));
        }
        Insignia datos = new Insignia(ctx.origenDatos.equals("MySQL") ? "MySQL conectado" : "Datos en memoria",
                ctx.origenDatos.equals("MySQL") ? Insignia.Tipo.EXITO : Insignia.Tipo.ADVERTENCIA);
        datos.setToolTipText(ctx.origenDatos.equals("MySQL") ? "Los cambios se guardan en la base tiendas3a."
                : "MySQL no respondió: los cambios se pierden al cerrar el programa.");
        der.add(datos);
        reloj.setFont(Tema.medio(13));
        reloj.setForeground(Tema.TEXTO_SUAVE);
        reloj.setIcon(new Icono(Icono.Tipo.CALENDARIO, 17, Tema.TEXTO_SUAVE));
        reloj.setIconTextGap(7);
        reloj.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        der.add(reloj);
        b.add(der, BorderLayout.EAST);
        return b;
    }

    private class ItemMenu extends JComponent {
        private final Pantalla pantalla;
        private boolean activo;
        private boolean encima;

        ItemMenu(Pantalla p) {
            this.pantalla = p;
            setPreferredSize(new Dimension(200, 42));
            setMaximumSize(new Dimension(204, 42));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mostrar(pantalla);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    encima = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    encima = false;
                    repaint();
                }
            });
        }

        void setActivo(boolean a) {
            activo = a;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            if (activo || encima) {
                g.setColor(activo ? Tema.PETROLEO_MEDIO : new Color(255, 255, 255, 16));
                g.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
            }
            if (activo) {
                g.setColor(Tema.AMARILLO);
                g.fill(new RoundRectangle2D.Double(0, 10, 4, getHeight() - 20, 4, 4));
            }
            Color c = activo ? Color.WHITE : new Color(0xC3D8DC);
            new Icono(pantalla.icono(), 19, activo ? Tema.AMARILLO : c).paintIcon(this, g, 14, (getHeight() - 19) / 2);
            g.setFont(activo ? Tema.medio(13.5f) : Tema.texto(13.5f));
            g.setColor(c);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(pantalla.titulo(), 44, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g.dispose();
        }
    }
}
