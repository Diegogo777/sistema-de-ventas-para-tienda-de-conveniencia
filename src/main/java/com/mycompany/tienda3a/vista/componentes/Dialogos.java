package com.mycompany.tienda3a.vista.componentes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public final class Dialogos {
    private Dialogos() {
    }

    public static boolean confirmar(Component padre, String titulo, String mensaje, String textoAceptar,
                                    Boton.Estilo estilo) {
        boolean[] ok = {false};
        JDialog d = base(padre, titulo);
        JPanel cuerpo = cuerpo(titulo, mensaje, estilo == Boton.Estilo.PELIGRO ? Icono.Tipo.ALERTA : Icono.Tipo.INFO,
                estilo == Boton.Estilo.PELIGRO ? Tema.ROJO : Tema.PETROLEO);
        Boton cancelar = new Boton("Cancelar", Boton.Estilo.SECUNDARIO);
        Boton aceptar = new Boton(textoAceptar, estilo);
        cancelar.addActionListener(e -> d.dispose());
        aceptar.addActionListener(e -> {
            ok[0] = true;
            d.dispose();
        });
        cuerpo.add(botones(cancelar, aceptar), BorderLayout.SOUTH);
        mostrar(d, cuerpo, aceptar);
        return ok[0];
    }

    public static String pedirTexto(Component padre, String titulo, String etiqueta, String inicial, String textoAceptar) {
        String[] r = {null};
        JDialog d = base(padre, titulo);
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(Tema.SUPERFICIE);
        p.setBorder(BorderFactory.createEmptyBorder(22, 24, 18, 24));
        JLabel t = new JLabel(titulo);
        t.setFont(Tema.negrita(17));
        p.add(t, BorderLayout.NORTH);
        Campo campo = new Campo(null, 22);
        campo.setText(inicial);
        campo.selectAll();
        p.add(Textos.conEtiqueta(etiqueta, campo), BorderLayout.CENTER);
        Boton cancelar = new Boton("Cancelar", Boton.Estilo.SECUNDARIO);
        Boton aceptar = new Boton(textoAceptar, Boton.Estilo.PRIMARIO);
        cancelar.addActionListener(e -> d.dispose());
        aceptar.addActionListener(e -> {
            r[0] = campo.getText();
            d.dispose();
        });
        p.add(botones(cancelar, aceptar), BorderLayout.SOUTH);
        mostrar(d, p, aceptar);
        return r[0];
    }

    public static void mostrarContenido(Component padre, String titulo, JComponent contenido, Boton... extra) {
        JDialog d = base(padre, titulo);
        JPanel p = Textos.transparente(new BorderLayout(0, 14));
        p.setOpaque(true);
        p.setBackground(Tema.SUPERFICIE);
        p.setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));
        JLabel t = new JLabel(titulo);
        t.setFont(Tema.negrita(17));
        p.add(t, BorderLayout.NORTH);
        p.add(contenido, BorderLayout.CENTER);
        Boton cerrar = new Boton("Cerrar", Boton.Estilo.PRIMARIO);
        cerrar.addActionListener(e -> d.dispose());
        Boton[] todos = new Boton[extra.length + 1];
        System.arraycopy(extra, 0, todos, 0, extra.length);
        todos[extra.length] = cerrar;
        for (Boton b : extra) {
            b.addActionListener(e -> d.dispose());
        }
        p.add(botones(todos), BorderLayout.SOUTH);
        mostrar(d, p, cerrar);
    }

    public static JComponent ticket(String texto) {
        JTextArea a = new JTextArea(texto);
        a.setFont(Tema.cifra(12.5f));
        a.setEditable(false);
        a.setForeground(Tema.TEXTO);
        a.setBackground(new java.awt.Color(0xFFFEF9));
        a.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        JComponent sp = Tablas.desplazable(a);
        sp.setBorder(BorderFactory.createLineBorder(Tema.BORDE));
        sp.setPreferredSize(new Dimension(420, Math.min(520, a.getPreferredSize().height + 6)));
        return sp;
    }

    private static JDialog base(Component padre, String titulo) {
        Window w = padre == null ? null : SwingUtilities.getWindowAncestor(padre);
        JDialog d = new JDialog(w, titulo, JDialog.ModalityType.APPLICATION_MODAL);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.getRootPane().registerKeyboardAction(e -> d.dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        return d;
    }

    private static JPanel cuerpo(String titulo, String mensaje, Icono.Tipo icono, java.awt.Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 18));
        p.setBackground(Tema.SUPERFICIE);
        p.setBorder(BorderFactory.createEmptyBorder(22, 24, 18, 24));
        JPanel arriba = Textos.transparente(new BorderLayout(14, 0));
        JLabel ic = new JLabel(new Icono(icono, 28, color));
        ic.setVerticalAlignment(JLabel.TOP);
        arriba.add(ic, BorderLayout.WEST);
        JPanel textos = Textos.transparente(new BorderLayout(0, 6));
        JLabel t = new JLabel(titulo);
        t.setFont(Tema.negrita(17));
        textos.add(t, BorderLayout.NORTH);
        JLabel m = new JLabel("<html><div style='width:330px'>" + mensaje + "</div></html>");
        m.setFont(Tema.texto(13));
        m.setForeground(Tema.TEXTO_SUAVE);
        textos.add(m, BorderLayout.CENTER);
        arriba.add(textos, BorderLayout.CENTER);
        p.add(arriba, BorderLayout.CENTER);
        return p;
    }

    private static JPanel botones(Boton... bs) {
        JPanel f = Textos.transparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        for (Boton b : bs) {
            f.add(b);
        }
        return f;
    }

    private static void mostrar(JDialog d, JPanel contenido, Boton porDefecto) {
        d.setContentPane(contenido);
        d.getRootPane().setDefaultButton(porDefecto);
        d.pack();
        d.setResizable(false);
        d.setLocationRelativeTo(d.getOwner());
        d.setVisible(true);
    }
}
