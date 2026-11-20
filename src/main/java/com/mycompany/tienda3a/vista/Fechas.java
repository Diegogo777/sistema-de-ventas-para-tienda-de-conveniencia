package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.excepcion.ExcepcionNegocio;
import com.mycompany.tienda3a.vista.componentes.Campo;
import com.mycompany.tienda3a.vista.componentes.Tema;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

final class Fechas {
    private Fechas() {
    }

    static LocalDate leer(Campo campo, String nombre) {
        String t = campo.getText().trim();
        if (t.isEmpty()) {
            return null;
        }
        try {
            campo.setError(false);
            return LocalDate.parse(t, Tema.FECHA);
        } catch (DateTimeParseException e) {
            campo.setError(true);
            throw new ExcepcionNegocio("Escribe la fecha " + nombre + " como dd/mm/aaaa, por ejemplo 20/11/2026.");
        }
    }

    static String texto(LocalDate f) {
        return f == null ? "" : f.format(Tema.FECHA);
    }
}
