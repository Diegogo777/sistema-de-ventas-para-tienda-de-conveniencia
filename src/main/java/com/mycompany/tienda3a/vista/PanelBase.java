package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.excepcion.ExcepcionNegocio;
import com.mycompany.tienda3a.vista.componentes.Aviso;
import com.mycompany.tienda3a.vista.componentes.Icono;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

abstract class PanelBase extends JPanel implements Pantalla {
    protected final Contexto ctx;
    protected final Aviso aviso = new Aviso();
    private final String titulo;
    private final String descripcion;
    private final Icono.Tipo icono;
    private final String casoUso;

    PanelBase(Contexto ctx, String titulo, String descripcion, Icono.Tipo icono, String casoUso) {
        super(new BorderLayout(0, 14));
        this.ctx = ctx;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.icono = icono;
        this.casoUso = casoUso;
        setOpaque(false);
        JPanel arriba = new JPanel(new BorderLayout());
        arriba.setOpaque(false);
        arriba.add(aviso);
        arriba.setBorder(BorderFactory.createEmptyBorder());
        add(arriba, BorderLayout.NORTH);
    }

    protected boolean ejecutar(Runnable accion) {
        try {
            accion.run();
            return true;
        } catch (ExcepcionNegocio e) {
            aviso.mostrar(Aviso.Tipo.ERROR, e.getMessage());
            return false;
        } catch (RuntimeException e) {
            aviso.mostrar(Aviso.Tipo.ERROR, "Ocurrió un error inesperado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    protected void exito(String mensaje) {
        aviso.mostrar(Aviso.Tipo.EXITO, mensaje);
    }

    @Override
    public String titulo() { return titulo; }

    @Override
    public String descripcion() { return descripcion; }

    @Override
    public Icono.Tipo icono() { return icono; }

    @Override
    public String casoUso() { return casoUso; }

    @Override
    public JComponent vista() { return this; }
}
