package com.mycompany.tienda3a.vista.componentes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public class Boton extends JButton {
    public enum Estilo {
        PRIMARIO,

        EXITO,

        PELIGRO,

        SECUNDARIO,

        PELIGRO_SUAVE,

        FANTASMA
    }

    private Estilo estilo;
    private boolean encima;
    private boolean presionado;
    private int alto;

    public Boton(String texto, Estilo estilo) {
        this(texto, estilo, null);
    }

    public Boton(String texto, Estilo estilo, Icono.Tipo icono) {
        super(texto);
        this.estilo = estilo;
        this.alto = 40;
        setFont(Tema.medio(13));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        setIconTextGap(8);
        if (icono != null) {
            setIcon(new Icono(icono, 18, colorTexto()));
        }
        setForeground(colorTexto());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                presionado = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                presionado = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                presionado = false;
                repaint();
            }
        });
    }

    public Boton alto(int alto) {
        this.alto = alto;
        if (alto >= 50) {
            setFont(Tema.negrita(15));
        }
        revalidate();
        return this;
    }

    public void setEstilo(Estilo estilo) {
        this.estilo = estilo;
        setForeground(colorTexto());
        if (getIcon() instanceof Icono i) {
            i.setColor(colorTexto());
        }
        repaint();
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        setForeground(b ? colorTexto() : Tema.TEXTO_TENUE);
        if (getIcon() instanceof Icono i) {
            i.setColor(b ? colorTexto() : Tema.TEXTO_TENUE);
        }
        setCursor(Cursor.getPredefinedCursor(b ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(Math.max(d.width, 44), alto);
    }

    private Color colorTexto() {
        return switch (estilo) {
            case PRIMARIO, EXITO, PELIGRO -> Color.WHITE;
            case SECUNDARIO -> Tema.TEXTO;
            case PELIGRO_SUAVE -> Tema.ROJO;
            case FANTASMA -> Tema.PETROLEO;
        };
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        RoundRectangle2D forma = new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, 10, 10);
        Color fondo;
        Color borde = null;
        boolean activo = isEnabled();
        switch (estilo) {
            case PRIMARIO -> fondo = !activo ? Tema.BORDE : presionado ? Tema.PETROLEO_OSCURO : encima ? Tema.PETROLEO_MEDIO : Tema.PETROLEO;
            case EXITO -> fondo = !activo ? Tema.BORDE : presionado || encima ? Tema.VERDE_OSCURO : Tema.VERDE;
            case PELIGRO -> fondo = !activo ? Tema.BORDE : presionado || encima ? Tema.ROJO_OSCURO : Tema.ROJO;
            case SECUNDARIO -> {
                fondo = activo && encima ? Tema.PETROLEO_SUAVE : Tema.SUPERFICIE;
                borde = Tema.BORDE_FUERTE;
            }
            case PELIGRO_SUAVE -> {
                fondo = activo && encima ? Tema.ROJO_SUAVE : Tema.SUPERFICIE;
                borde = activo ? Tema.tinte(Tema.ROJO, 0.55) : Tema.BORDE;
            }
            default -> fondo = activo && encima ? Tema.PETROLEO_SUAVE : new Color(0, 0, 0, 0);
        }
        g.setColor(fondo);
        g.fill(forma);
        if (borde != null) {
            g.setColor(borde);
            g.draw(forma);
        }
        if (isFocusOwner() && activo) {
            g.setColor(Tema.tinte(Tema.PETROLEO, 0.45));
            g.draw(new RoundRectangle2D.Double(1.5, 1.5, w - 3, h - 3, 9, 9));
        }
        g.dispose();
        super.paintComponent(g0);
    }
}
