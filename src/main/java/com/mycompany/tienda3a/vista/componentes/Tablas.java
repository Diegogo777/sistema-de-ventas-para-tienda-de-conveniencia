package com.mycompany.tienda3a.vista.componentes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public final class Tablas {
    private Tablas() {
    }

    public static JTable crear(String... columnas) {
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable t = new JTable(modelo) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int fila, int col) {
                Component c = super.prepareRenderer(r, fila, col);
                if (!isRowSelected(fila)) {
                    c.setBackground(fila % 2 == 0 ? Tema.SUPERFICIE : Tema.FILA_ALTERNA);
                }
                return c;
            }
        };
        t.setRowHeight(38);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(new Color(0xEDF1F0));
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setSelectionBackground(Tema.SELECCION);
        t.setSelectionForeground(Tema.TEXTO);
        t.setForeground(Tema.TEXTO);
        t.setBackground(Tema.SUPERFICIE);
        t.setFont(Tema.texto(13));
        t.setFocusable(true);
        t.getTableHeader().setReorderingAllowed(false);
        t.setAutoCreateRowSorter(false);
        JTableHeader h = t.getTableHeader();
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tb, v, false, false, r, c);
                l.setFont(Tema.medio(12));
                l.setForeground(Tema.TEXTO_SUAVE);
                l.setBackground(Tema.FONDO);
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDE),
                        BorderFactory.createEmptyBorder(0, 12, 0, 12)));
                TableColumn col = tb.getColumnModel().getColumn(c);
                l.setHorizontalAlignment(col.getCellRenderer() instanceof RenderCifra ? SwingConstants.RIGHT : SwingConstants.LEFT);
                return l;
            }
        });
        h.setPreferredSize(new Dimension(0, 36));
        t.setDefaultRenderer(Object.class, new RenderTexto());
        return t;
    }

    public static DefaultTableModel modelo(JTable t) {
        return (DefaultTableModel) t.getModel();
    }

    public static void ancho(JTable t, int columna, int ancho, boolean fijo) {
        TableColumn c = t.getColumnModel().getColumn(columna);
        c.setPreferredWidth(ancho);
        if (fijo) {
            c.setMinWidth(ancho);
            c.setMaxWidth(ancho);
        }
    }

    public static void cifra(JTable t, int... columnas) {
        for (int c : columnas) {
            t.getColumnModel().getColumn(c).setCellRenderer(new RenderCifra());
        }
    }

    public static void insignia(JTable t, int columna, Function<Object, Insignia.Tipo> tipo) {
        t.getColumnModel().getColumn(columna).setCellRenderer(new DefaultTableCellRenderer() {
            private final Insignia ins = new Insignia("", Insignia.Tipo.NEUTRO);
            private final JPanel panel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));

            {
                panel.add(ins);
            }

            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                ins.setText(v == null ? "" : v.toString());
                ins.setVisible(v != null && !v.toString().isEmpty());
                ins.setTipo(tipo.apply(v));
                panel.setBackground(s ? Tema.SELECCION : r % 2 == 0 ? Tema.SUPERFICIE : Tema.FILA_ALTERNA);
                panel.setBorder(BorderFactory.createEmptyBorder((tb.getRowHeight() - ins.getPreferredSize().height) / 2, 0, 0, 0));
                return panel;
            }
        });
    }

    public static JComponent envolver(JTable t) {
        JScrollPane sp = desplazable(t);
        JPanel marco = new JPanel(new java.awt.BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Tema.SUPERFICIE);
                g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g.setColor(Tema.BORDE);
                g.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 2, getHeight() - 2, 12, 12));
                g.dispose();
            }
        };
        marco.setOpaque(false);
        marco.setBorder(BorderFactory.createEmptyBorder(1, 1, 2, 2));
        marco.add(sp);
        return marco;
    }

    public static JScrollPane desplazable(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Tema.SUPERFICIE);
        sp.getVerticalScrollBar().setUI(new BarraDelgada());
        sp.getHorizontalScrollBar().setUI(new BarraDelgada());
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(10, 10));
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension(10, 10));
        sp.getVerticalScrollBar().setUnitIncrement(18);
        return sp;
    }

    public static class RenderTexto extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, false, r, c);
            l.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            l.setFont(Tema.texto(13));
            l.setForeground(Tema.TEXTO);
            return l;
        }
    }

    public static class RenderCifra extends RenderTexto {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, false, r, c);
            l.setHorizontalAlignment(SwingConstants.RIGHT);
            l.setFont(Tema.cifra(13));
            return l;
        }
    }

    public static class BarraDelgada extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = Tema.BORDE_FUERTE;
            trackColor = Tema.SUPERFICIE;
        }

        @Override
        protected javax.swing.JButton createDecreaseButton(int o) {
            return cero();
        }

        @Override
        protected javax.swing.JButton createIncreaseButton(int o) {
            return cero();
        }

        private javax.swing.JButton cero() {
            javax.swing.JButton b = new javax.swing.JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, java.awt.Rectangle r) {
            g.setColor(c.getParent() == null ? Tema.SUPERFICIE : c.getParent().getBackground());
            g.fillRect(r.x, r.y, r.width, r.height);
        }

        @Override
        protected void paintThumb(Graphics g0, JComponent c, java.awt.Rectangle r) {
            if (r.isEmpty()) {
                return;
            }
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(isThumbRollover() ? Tema.TEXTO_TENUE : Tema.BORDE_FUERTE);
            boolean vertical = ((JScrollBar) c).getOrientation() == JScrollBar.VERTICAL;
            if (vertical) {
                g.fill(new RoundRectangle2D.Double(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 6, 6));
            } else {
                g.fill(new RoundRectangle2D.Double(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 6, 6));
            }
            g.dispose();
        }
    }
}
