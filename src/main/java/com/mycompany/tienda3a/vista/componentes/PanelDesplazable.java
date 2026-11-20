package com.mycompany.tienda3a.vista.componentes;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

public class PanelDesplazable extends JPanel implements Scrollable {
    public PanelDesplazable(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    public JScrollPane envolver() {
        JScrollPane sp = Tablas.desplazable(this);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return sp;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
        return 18;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
        return r.height - 40;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
