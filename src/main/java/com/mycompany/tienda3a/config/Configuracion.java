package com.mycompany.tienda3a.config;

public class Configuracion {
    private static double tasaIgv = 0.18;
    private static double fondoInicialCaja = 150.00;
    private static double montoMaximoVentaSimple = 700.00;
    private static int minutosReintentoEnvio = 5;
    private static int maxIntentosEnvio = 3;
    private static int maxIntentosLogin = 5;
    private static int minutosBloqueo = 15;
    private static int minutosInactividad = 15;

    private Configuracion() {
    }

    public static double getTasaIgv() { return tasaIgv; }

    public static void setTasaIgv(double tasa) {
        if (tasa < 0 || tasa >= 1) {
            throw new IllegalArgumentException("La tasa de IGV debe estar entre 0 y 1.");
        }
        tasaIgv = tasa;
    }

    public static double getFondoInicialCaja() { return fondoInicialCaja; }
    public static void setFondoInicialCaja(double monto) { fondoInicialCaja = monto; }

    public static double getMontoMaximoVentaSimple() { return montoMaximoVentaSimple; }
    public static void setMontoMaximoVentaSimple(double monto) { montoMaximoVentaSimple = monto; }

    public static int getMinutosReintentoEnvio() { return minutosReintentoEnvio; }
    public static void setMinutosReintentoEnvio(int minutos) { minutosReintentoEnvio = minutos; }

    public static int getMaxIntentosEnvio() { return maxIntentosEnvio; }
    public static void setMaxIntentosEnvio(int intentos) { maxIntentosEnvio = intentos; }

    public static int getMaxIntentosLogin() { return maxIntentosLogin; }
    public static void setMaxIntentosLogin(int intentos) { maxIntentosLogin = intentos; }

    public static int getMinutosBloqueo() { return minutosBloqueo; }
    public static void setMinutosBloqueo(int minutos) { minutosBloqueo = minutos; }

    public static int getMinutosInactividad() { return minutosInactividad; }
    public static void setMinutosInactividad(int minutos) { minutosInactividad = minutos; }
}
