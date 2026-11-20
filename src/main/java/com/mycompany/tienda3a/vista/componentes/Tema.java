package com.mycompany.tienda3a.vista.componentes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

public final class Tema {
    public static final Color PETROLEO = new Color(0x0F4C5C);
    public static final Color PETROLEO_OSCURO = new Color(0x0A3540);
    public static final Color PETROLEO_MEDIO = new Color(0x13606F);
    public static final Color PETROLEO_SUAVE = new Color(0xE3EEF0);

    public static final Color AMARILLO = new Color(0xF2B705);
    public static final Color AMARILLO_SUAVE = new Color(0xFFF4CC);
    public static final Color AMARILLO_TEXTO = new Color(0x7A5A00);

    public static final Color VERDE = new Color(0x1B7F4B);
    public static final Color VERDE_OSCURO = new Color(0x156A3E);
    public static final Color VERDE_SUAVE = new Color(0xE3F2E9);
    public static final Color ROJO = new Color(0xC0392B);
    public static final Color ROJO_OSCURO = new Color(0xA13023);
    public static final Color ROJO_SUAVE = new Color(0xFBE8E6);
    public static final Color AMBAR = new Color(0xB45309);
    public static final Color AMBAR_SUAVE = new Color(0xFDF0E1);
    public static final Color AMBAR_OSCURO = new Color(0x92400E);

    public static final Color FONDO = new Color(0xF3F5F4);
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color FILA_ALTERNA = new Color(0xF8FAF9);
    public static final Color BORDE = new Color(0xDCE3E1);
    public static final Color BORDE_FUERTE = new Color(0xB9C5C2);
    public static final Color TEXTO = new Color(0x1D2B30);
    public static final Color TEXTO_SUAVE = new Color(0x5E6E73);
    public static final Color TEXTO_TENUE = new Color(0x6F7F84);
    public static final Color SELECCION = new Color(0xD3E7EB);

    public static final int RADIO = 12;

    private static Font regular = new Font("SansSerif", Font.PLAIN, 13);
    private static Font medium = new Font("SansSerif", Font.PLAIN, 13);
    private static Font bold = new Font("SansSerif", Font.BOLD, 13);
    private static Font mono = new Font("Monospaced", Font.PLAIN, 13);
    private static Font monoBold = new Font("Monospaced", Font.BOLD, 13);

    private static final DecimalFormat SOLES;
    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    static {
        DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.US);
        SOLES = new DecimalFormat("#,##0.00", s);
    }

    private Tema() {
    }

    public static void aplicar() {
        regular = cargar("Poppins-Regular.ttf", regular);
        medium = cargar("Poppins-Medium.ttf", medium);
        bold = cargar("Poppins-Bold.ttf", bold);
        mono = cargar("LiberationMono-Regular.ttf", mono);
        monoBold = cargar("LiberationMono-Bold.ttf", monoBold);

        FontUIResource base = new FontUIResource(texto(13));
        for (String k : new String[]{"Label.font", "Button.font", "TextField.font", "PasswordField.font",
                "TextArea.font", "ComboBox.font", "List.font", "Table.font", "TableHeader.font", "ToolTip.font",
                "OptionPane.messageFont", "OptionPane.buttonFont", "CheckBox.font", "RadioButton.font",
                "Spinner.font", "FormattedTextField.font", "MenuItem.font", "PopupMenu.font"}) {
            UIManager.put(k, base);
        }
        UIManager.put("Panel.background", new ColorUIResource(FONDO));
        UIManager.put("OptionPane.background", new ColorUIResource(SUPERFICIE));
        UIManager.put("Label.foreground", new ColorUIResource(TEXTO));
        UIManager.put("TextField.selectionBackground", new ColorUIResource(SELECCION));
        UIManager.put("TextField.selectionForeground", new ColorUIResource(TEXTO));
        UIManager.put("TextField.inactiveForeground", new ColorUIResource(TEXTO_TENUE));
        UIManager.put("ToolTip.background", new ColorUIResource(TEXTO));
        UIManager.put("ToolTip.foreground", new ColorUIResource(Color.WHITE));
        UIManager.put("ToolTip.border", BorderFactory.createEmptyBorder(6, 10, 6, 10));
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("ScrollPane.background", new ColorUIResource(SUPERFICIE));
        UIManager.put("Viewport.background", new ColorUIResource(SUPERFICIE));
        UIManager.put("List.selectionBackground", new ColorUIResource(SELECCION));
        UIManager.put("List.selectionForeground", new ColorUIResource(TEXTO));
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(SELECCION));
        UIManager.put("ComboBox.selectionForeground", new ColorUIResource(TEXTO));
        UIManager.put("ComboBox.background", new ColorUIResource(SUPERFICIE));
        UIManager.put("CheckBox.background", new ColorUIResource(SUPERFICIE));
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(BORDE));
        UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
        UIManager.put("CheckBox.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
    }

    private static Font cargar(String archivo, Font respaldo) {
        try (InputStream in = Tema.class.getResourceAsStream("/com/mycompany/tienda3a/fuentes/" + archivo)) {
            if (in == null) {
                return respaldo;
            }
            Font f = Font.createFont(Font.TRUETYPE_FONT, in);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
            return f.deriveFont(13f);
        } catch (IOException | FontFormatException e) {
            System.err.println("No se pudo cargar la fuente " + archivo + "; se usa la del sistema.");
            return respaldo;
        }
    }

    public static Font texto(float tam) { return regular.deriveFont(Font.PLAIN, tam); }
    public static Font medio(float tam) { return medium.deriveFont(Font.PLAIN, tam); }
    public static Font negrita(float tam) { return bold.deriveFont(Font.PLAIN, tam); }
    public static Font cifra(float tam) { return mono.deriveFont(Font.PLAIN, tam); }
    public static Font cifraNegrita(float tam) { return monoBold.deriveFont(Font.PLAIN, tam); }

    public static String soles(double monto) {
        return "S/ " + SOLES.format(monto);
    }

    public static String numero(double monto) {
        return SOLES.format(monto);
    }

    public static Color tinte(Color c, double cantidad) {
        int r = (int) (255 - (255 - c.getRed()) * cantidad);
        int g = (int) (255 - (255 - c.getGreen()) * cantidad);
        int b = (int) (255 - (255 - c.getBlue()) * cantidad);
        return new Color(r, g, b);
    }
}
