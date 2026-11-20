package com.mycompany.tienda3a.vista.componentes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

public class Selector<T> extends JPanel {
    private final List<JToggleButton> botones = new ArrayList<>();
    private final List<T> valores = new ArrayList<>();
    private T seleccionado;
    private Consumer<T> alCambiar = v -> { };

    public Selector(List<T> opciones, List<String> textos, List<Icono.Tipo> iconos) {
        super(new GridLayout(1, opciones.size(), 4, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        for (int i = 0; i < opciones.size(); i++) {
            T valor = opciones.get(i);
            JToggleButton b = new JToggleButton(textos.get(i)) {
                @Override
                protected void paintComponent(Graphics g0) {
                    Graphics2D g = (Graphics2D) g0.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (isSelected()) {
                        g.setColor(Tema.SUPERFICIE);
                        g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                        g.setColor(Tema.tinte(Tema.PETROLEO, 0.45));
                        g.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 2, getHeight() - 2, 8, 8));
                    }
                    g.dispose();
                    super.paintComponent(g0);
                }
            };
            b.setFont(Tema.medio(13));
            b.setMargin(new java.awt.Insets(0, 2, 0, 2));
            b.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setOpaque(false);
            b.setHorizontalAlignment(SwingConstants.CENTER);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (iconos != null && iconos.get(i) != null) {
                b.setIcon(new Icono(iconos.get(i), 17, Tema.TEXTO_SUAVE));
                b.setIconTextGap(5);
            }
            b.addActionListener(e -> seleccionar(valor));
            botones.add(b);
            valores.add(valor);
            add(b);
        }
        seleccionar(opciones.get(0));
    }

    public void alCambiar(Consumer<T> accion) {
        this.alCambiar = accion;
    }

    public void seleccionar(T valor) {
        seleccionado = valor;
        for (int i = 0; i < botones.size(); i++) {
            boolean activo = valores.get(i).equals(valor);
            JToggleButton b = botones.get(i);
            b.setSelected(activo);
            Color c = activo ? Tema.PETROLEO : Tema.TEXTO_SUAVE;
            b.setForeground(c);
            if (b.getIcon() instanceof Icono ic) {
                ic.setColor(c);
            }
        }
        repaint();
        alCambiar.accept(valor);
    }

    public T getSeleccionado() {
        return seleccionado;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, 44);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Tema.FONDO);
        g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 11, 11));
        g.setColor(Tema.BORDE);
        g.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 2, getHeight() - 2, 11, 11));
        g.dispose();
    }
}
