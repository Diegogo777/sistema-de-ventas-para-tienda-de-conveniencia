package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.vista.componentes.Icono;
import javax.swing.JComponent;

public interface Pantalla {
    String titulo();

    String descripcion();

    Icono.Tipo icono();

    String casoUso();

    JComponent vista();

    default void alMostrar() {
    }
}
