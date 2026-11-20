package com.mycompany.tienda3a.modelo;

import com.mycompany.tienda3a.config.Configuracion;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public abstract class Usuario {
    private static final int ITERACIONES = 120_000;

    protected int idUsuario;
    protected String nombre;
    protected String nombreAcceso;
    protected String claveHash;
    protected int intentosFallidos;
    protected LocalDateTime bloqueadoHasta;
    protected boolean activo;
    protected LocalDateTime creadoEn;

    public Usuario(int idUsuario, String nombre, String nombreAcceso, String claveHash,
                   boolean activo, LocalDateTime creadoEn) {
        this.idUsuario = idUsuario;
        setNombre(nombre);
        setNombreAcceso(nombreAcceso);
        this.claveHash = claveHash;
        this.activo = activo;
        this.creadoEn = creadoEn == null ? LocalDateTime.now() : creadoEn;
    }

    public boolean autenticar(String clave) {
        if (clave == null || claveHash == null) {
            return false;
        }
        String[] partes = claveHash.split("\\$");
        if (partes.length != 4) {
            return false;
        }
        byte[] sal = Base64.getDecoder().decode(partes[2]);
        String calculado = generarHash(clave, sal, Integer.parseInt(partes[1]));
        return MessageDigest.isEqual(calculado.getBytes(), claveHash.getBytes());
    }

    public void registrarIntentoFallido() {
        intentosFallidos++;
        if (intentosFallidos >= Configuracion.getMaxIntentosLogin()) {
            bloqueadoHasta = LocalDateTime.now().plusMinutes(Configuracion.getMinutosBloqueo());
        }
    }

    public void reiniciarIntentos() {
        intentosFallidos = 0;
        bloqueadoHasta = null;
    }

    public boolean estaBloqueado() {
        return bloqueadoHasta != null && LocalDateTime.now().isBefore(bloqueadoHasta);
    }

    public void desbloquear() {
        reiniciarIntentos();
    }

    public void desactivar() {
        activo = false;
    }

    public void cambiarClave(String clave) {
        if (clave == null || clave.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }
        this.claveHash = generarHash(clave);
    }

    public abstract Rol getRol();

    public abstract boolean puedeAcceder(String casoUso);

    private String generarHash(String clave) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        return generarHash(clave, sal, ITERACIONES);
    }

    private String generarHash(String clave, byte[] sal, int iteraciones) {
        try {
            PBEKeySpec spec = new PBEKeySpec(clave.toCharArray(), sal, iteraciones, 256);
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            spec.clearPassword();
            return "pbkdf2$" + iteraciones + "$" + Base64.getEncoder().encodeToString(sal)
                    + "$" + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña.", e);
        }
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public String getNombreAcceso() { return nombreAcceso; }

    public void setNombreAcceso(String nombreAcceso) {
        if (nombreAcceso == null || !nombreAcceso.trim().matches("[a-zA-Z0-9._]{3,30}")) {
            throw new IllegalArgumentException("El nombre de acceso debe tener de 3 a 30 letras, números, punto o guion bajo.");
        }
        this.nombreAcceso = nombreAcceso.trim().toLowerCase();
    }

    public String getClaveHash() { return claveHash; }
    public int getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(int intentos) { this.intentosFallidos = Math.max(0, intentos); }
    public LocalDateTime getBloqueadoHasta() { return bloqueadoHasta; }
    public void setBloqueadoHasta(LocalDateTime bloqueadoHasta) { this.bloqueadoHasta = bloqueadoHasta; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public LocalDateTime getCreadoEn() { return creadoEn; }

    public String getIniciales() {
        String[] p = nombre.split("\\s+");
        String ini = p[0].substring(0, 1) + (p.length > 1 ? p[p.length - 1].substring(0, 1) : "");
        return ini.toUpperCase();
    }

    protected static boolean contiene(String[] casos, String casoUso) {
        return Arrays.asList(casos).contains(casoUso);
    }

    @Override
    public String toString() {
        return nombre + " (" + nombreAcceso + ")";
    }
}
