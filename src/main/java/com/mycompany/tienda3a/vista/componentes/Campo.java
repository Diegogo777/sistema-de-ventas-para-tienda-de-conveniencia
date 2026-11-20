package com.mycompany.tienda3a.vista.componentes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.text.JTextComponent;

public class Campo extends JTextField {
    private final String ayuda;
    private Icono icono;
    private boolean error;

    public Campo(String ayuda) {
        this(ayuda, 0);
    }

    public Campo(String ayuda, int columnas) {
        super(columnas);
        this.ayuda = ayuda;
        configurar(this);
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    public Campo conIcono(Icono.Tipo tipo) {
        icono = new Icono(tipo, 18, Tema.TEXTO_TENUE);
        setBorder(new BordeCampo(this, 38));
        return this;
    }

    public void setError(boolean error) {
        this.error = error;
        repaint();
    }

    public boolean isError() {
        return error;
    }

    static void configurar(JTextComponent c) {
        c.setFont(Tema.texto(13));
        c.setForeground(Tema.TEXTO);
        c.setCaretColor(Tema.PETROLEO);
        c.setOpaque(false);
        c.setBorder(new BordeCampo(c, 12));
        c.setSelectionColor(Tema.SELECCION);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, Math.max(40, d.height));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(isEnabled() && isEditable() ? Tema.SUPERFICIE : Tema.FONDO);
        g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
        g.dispose();
        super.paintComponent(g0);
        if (getText().isEmpty() && ayuda != null) {
            Graphics2D h = (Graphics2D) g0.create();
            h.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            h.setColor(Tema.TEXTO_TENUE);
            h.setFont(getFont());
            Insets i = getInsets();
            int y = (getHeight() + h.getFontMetrics().getAscent() - h.getFontMetrics().getDescent()) / 2;
            h.drawString(ayuda, i.left, y);
            h.dispose();
        }
        if (icono != null) {
            icono.paintIcon(this, g0, 12, (getHeight() - 18) / 2);
        }
    }

    static class BordeCampo extends AbstractBorder {
        private final JTextComponent campo;
        private final int izquierda;

        BordeCampo(JTextComponent campo, int izquierda) {
            this.campo = campo;
            this.izquierda = izquierda;
        }

        @Override
        public void paintBorder(Component c, Graphics g0, int x, int y, int w, int h) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean err = campo instanceof Campo cc && cc.error || campo instanceof CampoClave k && k.isError();
            Color color = err ? Tema.ROJO : campo.hasFocus() ? Tema.PETROLEO : Tema.BORDE_FUERTE;
            g.setColor(color);
            g.draw(new RoundRectangle2D.Double(x + 0.5, y + 0.5, w - 2, h - 2, 10, 10));
            if (campo.hasFocus() || err) {
                g.setColor(Tema.tinte(color, 0.25));
                g.draw(new RoundRectangle2D.Double(x + 1.5, y + 1.5, w - 4, h - 4, 9, 9));
            }
            g.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, izquierda, 8, 12);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets i) {
            i.set(8, izquierda, 8, 12);
            return i;
        }
    }

    public static class CampoClave extends JPasswordField {
        private final String ayuda;
        private boolean error;

        public CampoClave(String ayuda) {
            this.ayuda = ayuda;
            configurar(this);
            setEchoChar('•');
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }

        public void setError(boolean error) {
            this.error = error;
            repaint();
        }

        public boolean isError() {
            return error;
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, Math.max(40, d.height));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Tema.SUPERFICIE);
            g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
            g.dispose();
            super.paintComponent(g0);
            if (getPassword().length == 0 && ayuda != null) {
                Graphics2D h = (Graphics2D) g0.create();
                h.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                h.setColor(Tema.TEXTO_TENUE);
                h.setFont(Tema.texto(13));
                Insets i = getInsets();
                int y = (getHeight() + h.getFontMetrics().getAscent() - h.getFontMetrics().getDescent()) / 2;
                h.drawString(ayuda, i.left, y);
                h.dispose();
            }
        }
    }
}
