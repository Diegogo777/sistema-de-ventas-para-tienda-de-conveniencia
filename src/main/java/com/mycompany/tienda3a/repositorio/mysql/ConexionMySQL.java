package com.mycompany.tienda3a.repositorio.mysql;

import com.mycompany.tienda3a.excepcion.ExcepcionDatos;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;

public final class ConexionMySQL {
    private static final String URL_POR_DEFECTO =
            "jdbc:mysql://localhost:3306/tiendas3a?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima&characterEncoding=UTF-8";

    private static String url = URL_POR_DEFECTO;
    private static String usuario = "root";
    private static String clave = "";
    private static String ultimoError = "";

    static {
        Properties p = new Properties();
        Path archivo = Path.of("mysql.properties");
        try (InputStream in = Files.exists(archivo) ? Files.newInputStream(archivo)
                : ConexionMySQL.class.getResourceAsStream("/mysql.properties")) {
            if (in != null) {
                p.load(in);
                url = p.getProperty("url", URL_POR_DEFECTO).trim();
                usuario = p.getProperty("usuario", "root").trim();
                clave = p.getProperty("clave", "").trim();
            }
        } catch (IOException e) {
            ultimoError = "No se pudo leer mysql.properties: " + e.getMessage();
        }
    }

    private ConexionMySQL() {
    }

    private static boolean cargarDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException e) {
            ultimoError = "No se encontró el driver de MySQL (mysql-connector-j). En NetBeans haz Clean and Build "
                    + "con internet para que Maven lo descargue.";
            return false;
        }
    }

    public static Connection obtener() throws SQLException {
        if (!cargarDriver()) {
            throw new SQLException(ultimoError);
        }
        DriverManager.setLoginTimeout(4);
        return DriverManager.getConnection(url, usuario, clave);
    }

    public static boolean probar() {
        try (Connection c = obtener(); Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM usuario")) {
            return rs.next();
        } catch (SQLException e) {
            if (!e.getMessage().startsWith("No se encontró el driver")) {
                ultimoError = e.getMessage();
            }
            return false;
        }
    }

    public static String getUltimoError() {
        return ultimoError;
    }

    @FunctionalInterface
    public interface Trabajo {
        void ejecutar(Connection c) throws SQLException;
    }

    public static void enTransaccion(Trabajo trabajo) {
        try (Connection c = obtener()) {
            c.setAutoCommit(false);
            try {
                trabajo.ejecutar(c);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new ExcepcionDatos("No se pudo guardar en la base de datos: " + e.getMessage(), e);
        }
    }

    public static int insertar(Connection c, String sql, Object... parametros) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, parametros);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("La base no devolvió el id generado.");
                }
                return rs.getInt(1);
            }
        }
    }

    public static void actualizar(Connection c, String sql, Object... parametros) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, parametros);
            ps.executeUpdate();
        }
    }

    private static void asignar(PreparedStatement ps, Object... parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            Object v = parametros[i];
            int n = i + 1;
            if (v == null) {
                ps.setNull(n, Types.NULL);
            } else if (v instanceof LocalDateTime t) {
                ps.setTimestamp(n, Timestamp.valueOf(t));
            } else if (v instanceof LocalDate d) {
                ps.setDate(n, java.sql.Date.valueOf(d));
            } else if (v instanceof Enum<?> e) {
                ps.setString(n, e.name());
            } else if (v instanceof Boolean b) {
                ps.setBoolean(n, b);
            } else {
                ps.setObject(n, v);
            }
        }
    }

    public static Integer idONulo(int id) {
        return id == 0 ? null : id;
    }
}
