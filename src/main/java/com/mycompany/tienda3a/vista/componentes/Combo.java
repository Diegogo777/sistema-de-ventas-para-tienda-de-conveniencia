package com.mycompany.tienda3a.vista.componentes;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

public class Combo<T> extends JComboBox<T> {
    public Combo(List<T> elementos, Function<T, String> texto) {
        for (T e : elementos) {
            addItem(e);
        }
        setFont(Tema.texto(13));
        setForeground(Tema.TEXTO);
        setBackground(Tema.SUPERFICIE);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g0) {
                        Graphics2D g = (Graphics2D) g0.create();
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g.setColor(Tema.TEXTO_SUAVE);
                        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        int cx = getWidth() / 2 - 4;
                        int cy = getHeight() / 2;
                        Path2D p = new Path2D.Double();
                        p.moveTo(cx - 4, cy - 2);
                        p.lineTo(cx, cy + 2);
                        p.lineTo(cx + 4, cy - 2);
                        g.draw(p);
                        g.dispose();
                    }
                };
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setContentAreaFilled(false);
                b.setFocusPainted(false);
                b.setOpaque(false);
                return b;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, java.awt.Rectangle bounds, boolean hasFocus) {
            }

            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup p = new BasicComboPopup(comboBox);
                p.setBorder(BorderFactory.createLineBorder(Tema.BORDE_FUERTE));
                return p;
            }
        });
        setRenderer(new DefaultListCellRenderer() {
            @Override
            @SuppressWarnings("unchecked")
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean foco) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, sel, false);
                String txt;
                try {
                    txt = texto.apply((T) value);
                } catch (NullPointerException e) {
                    txt = "";
                }
                l.setText(txt);
                l.setFont(Tema.texto(13));
                l.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
                l.setOpaque(index >= 0 || sel);
                if (index < 0) {
                    l.setOpaque(false);
                    l.setBackground(Tema.SUPERFICIE);
                    l.setForeground(Tema.TEXTO);
                    l.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
                } else {
                    l.setBackground(sel ? Tema.SELECCION : Tema.SUPERFICIE);
                    l.setForeground(Tema.TEXTO);
                }
                return l;
            }
        });
    }

    @SuppressWarnings("unchecked")
    public T seleccionado() {
        return (T) getSelectedItem();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width + 8, 40);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(isEnabled() ? Tema.SUPERFICIE : Tema.FONDO);
        g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
        g.setColor(hasFocus() || isPopupVisible() ? Tema.PETROLEO : Tema.BORDE_FUERTE);
        g.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 2, getHeight() - 2, 10, 10));
        g.dispose();
        super.paintComponent(g0);
    }
}
