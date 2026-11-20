package com.mycompany.tienda3a;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el aspecto de Swing: " + e.getMessage());
        }
        SwingUtilities.invokeLater(App::iniciar);
    }
}
