package com.mycompany.tienda3a.datos;

import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.repositorio.Repositorios;
import com.mycompany.tienda3a.repositorio.memoria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class DatosIniciales {
    public static final String CLAVE_PRUEBA = "Tienda3A*";
    public static final int DIAS_DE_HISTORIA = 6;

    private DatosIniciales() {
    }

    public static Repositorios crear() {
        RepositorioUsuariosMemoria usuarios = new RepositorioUsuariosMemoria();
        RepositorioCatalogoMemoria catalogo = new RepositorioCatalogoMemoria();
        RepositorioClientesMemoria clientes = new RepositorioClientesMemoria();
        RepositorioCajasMemoria cajas = new RepositorioCajasMemoria();
        RepositorioVentasMemoria ventas = new RepositorioVentasMemoria();
        RepositorioAuditoriaMemoria auditoria = new RepositorioAuditoriaMemoria();
        Random azar = new Random(20261120L);
        LocalDate hoy = LocalDate.now();

        Caja c1 = new Caja(0, "CAJA-1", "B001", "F001");
        Caja c2 = new Caja(0, "CAJA-2", "B002", "F002");
        Caja c3 = new Caja(0, "CAJA-3", "B003", "F003");
        cajas.agregarCaja(c1);
        cajas.agregarCaja(c2);
        cajas.agregarCaja(c3);

        LocalDateTime alta = hoy.minusDays(60).atTime(9, 0);
        Administrador admin = new Administrador(0, "Rosa Medina", "admin", null, true, alta);
        Cajero ana = new Cajero(0, "Ana Torres", "atorres", null, true, alta);
        Cajero luis = new Cajero(0, "Luis Quispe", "lquispe", null, true, alta);
        Cajero marco = new Cajero(0, "Marco Rojas", "mrojas", null, true, alta.plusDays(20));
        Cajero carla = new Cajero(0, "Carla Flores", "cflores", null, false, alta);
        for (Usuario u : List.of(admin, ana, luis, marco, carla)) {
            u.cambiarClave(CLAVE_PRUEBA);
            usuarios.guardar(u);
        }

        Categoria abarrotes = categoria(catalogo, "Abarrotes", "Arroz, azúcar, aceite, fideos y conservas");
        Categoria bebidas = categoria(catalogo, "Bebidas", "Gaseosas, aguas, jugos y rehidratantes");
        Categoria lacteos = categoria(catalogo, "Lácteos y huevos", "Leche, yogur, queso y huevos");
        Categoria snacks = categoria(catalogo, "Snacks y golosinas", "Galletas, chocolates y piqueos");
        Categoria limpieza = categoria(catalogo, "Limpieza", "Detergentes, lejía y papel");
        Categoria personal = categoria(catalogo, "Cuidado personal", "Jabón, champú y pasta dental");
        Categoria granel = categoria(catalogo, "Frutas y verduras", "Productos a granel vendidos por peso");

        List<Producto> ps = new ArrayList<>();
        ps.add(producto(catalogo, abarrotes, "7750100000011", "Arroz extra 1 kg", UnidadMedida.UNIDAD, 4.90, 180));
        ps.add(producto(catalogo, abarrotes, "7750100000028", "Azúcar rubia 1 kg", UnidadMedida.UNIDAD, 4.20, 150));
        ps.add(producto(catalogo, abarrotes, "7750100000035", "Aceite vegetal 900 ml", UnidadMedida.UNIDAD, 9.50, 90));
        ps.add(producto(catalogo, abarrotes, "7750100000042", "Fideos spaghetti 500 g", UnidadMedida.UNIDAD, 3.40, 140));
        ps.add(producto(catalogo, abarrotes, "7750100000059", "Atún en trozos 170 g", UnidadMedida.UNIDAD, 6.20, 110));
        ps.add(producto(catalogo, abarrotes, "7750100000066", "Avena en hojuelas 500 g", UnidadMedida.UNIDAD, 5.80, 70));
        ps.add(producto(catalogo, bebidas, "7750200000010", "Gaseosa 1.5 L", UnidadMedida.UNIDAD, 6.90, 160));
        ps.add(producto(catalogo, bebidas, "7750200000027", "Agua sin gas 625 ml", UnidadMedida.UNIDAD, 1.80, 260));
        ps.add(producto(catalogo, bebidas, "7750200000034", "Jugo de naranja 1 L", UnidadMedida.UNIDAD, 5.50, 80));
        ps.add(producto(catalogo, bebidas, "7750200000041", "Bebida rehidratante 500 ml", UnidadMedida.UNIDAD, 3.20, 120));
        ps.add(producto(catalogo, lacteos, "7750300000019", "Leche evaporada 400 g", UnidadMedida.UNIDAD, 4.10, 220));
        ps.add(producto(catalogo, lacteos, "7750300000026", "Yogur de fresa 1 L", UnidadMedida.UNIDAD, 7.30, 60));
        ps.add(producto(catalogo, lacteos, "7750300000033", "Huevos x 15", UnidadMedida.UNIDAD, 11.90, 45));
        ps.add(producto(catalogo, lacteos, "7750300000040", "Queso fresco 500 g", UnidadMedida.UNIDAD, 13.50, 30));
        ps.add(producto(catalogo, snacks, "7750400000018", "Galletas de soda x 6", UnidadMedida.UNIDAD, 4.50, 130));
        ps.add(producto(catalogo, snacks, "7750400000025", "Chocolate de leche 30 g", UnidadMedida.UNIDAD, 2.20, 200));
        ps.add(producto(catalogo, snacks, "7750400000032", "Papas fritas 45 g", UnidadMedida.UNIDAD, 2.50, 150));
        ps.add(producto(catalogo, snacks, "7750400000049", "Maní salado 100 g", UnidadMedida.UNIDAD, 3.00, 90));
        ps.add(producto(catalogo, limpieza, "7750500000017", "Detergente en polvo 800 g", UnidadMedida.UNIDAD, 10.90, 75));
        ps.add(producto(catalogo, limpieza, "7750500000024", "Lejía 1 L", UnidadMedida.UNIDAD, 3.60, 95));
        ps.add(producto(catalogo, limpieza, "7750500000031", "Papel higiénico x 4", UnidadMedida.UNIDAD, 6.40, 110));
        ps.add(producto(catalogo, limpieza, "7750500000048", "Lavavajilla en crema 450 g", UnidadMedida.UNIDAD, 4.80, 6));
        ps.add(producto(catalogo, personal, "7750600000016", "Jabón de tocador 90 g", UnidadMedida.UNIDAD, 2.40, 150));
        ps.add(producto(catalogo, personal, "7750600000023", "Champú 400 ml", UnidadMedida.UNIDAD, 14.90, 40));
        ps.add(producto(catalogo, personal, "7750600000030", "Pasta dental 90 g", UnidadMedida.UNIDAD, 5.20, 85));
        ps.add(producto(catalogo, granel, "2000000000015", "Plátano de seda (kg)", UnidadMedida.KG, 3.50, 60));
        ps.add(producto(catalogo, granel, "2000000000022", "Manzana delicia (kg)", UnidadMedida.KG, 6.90, 45));
        ps.add(producto(catalogo, granel, "2000000000039", "Tomate (kg)", UnidadMedida.KG, 4.20, 50));
        ps.add(producto(catalogo, granel, "2000000000046", "Limón (kg)", UnidadMedida.KG, 5.90, 35));
        Producto papa = producto(catalogo, granel, "2000000000053", "Papa amarilla (kg)", UnidadMedida.KG, 4.80, 40);
        papa.desactivar();

        promocion(catalogo, ps.get(6), admin, "Gaseosa a precio de oferta", 5.90, hoy.minusDays(10), hoy.plusDays(10));
        promocion(catalogo, ps.get(14), admin, "Semana de galletas", 3.90, hoy.minusDays(3), hoy.plusDays(4));
        promocion(catalogo, ps.get(18), admin, "Detergente en oferta", 9.50, hoy.minusDays(1), hoy.plusDays(14));
        promocion(catalogo, ps.get(10), admin, "Leche: oferta de lanzamiento", 3.70, hoy.minusDays(40), hoy.minusDays(20));

        List<Cliente> conDni = new ArrayList<>();
        List<Cliente> conRuc = new ArrayList<>();
        conDni.add(cliente(clientes, TipoDocumento.DNI, "45871236", "María Fernández Ruiz", ""));
        conDni.add(cliente(clientes, TipoDocumento.DNI, "70214589", "José Huamán Ccori", ""));
        conDni.add(cliente(clientes, TipoDocumento.DNI, "41025877", "Lucía Paredes Soto", ""));
        conDni.add(cliente(clientes, TipoDocumento.DNI, "72569014", "Diego Salazar Vega", ""));
        conRuc.add(cliente(clientes, TipoDocumento.RUC, "20601234561", "Inversiones Los Olivos S.A.C.", "Av. Universitaria 1234, Los Olivos"));
        conRuc.add(cliente(clientes, TipoDocumento.RUC, "10456789012", "Pedro Castillo Ramos", "Jr. Huáscar 455, Jesús María"));
        conRuc.add(cliente(clientes, TipoDocumento.RUC, "20555888991", "Comercial Rímac E.I.R.L.", "Av. Alcázar 310, Rímac"));

        Caja[] cajaDe = {c1, c2, c3};
        Cajero[] cajeros = {ana, luis, marco};
        int[] correlativoB = new int[3];
        int[] correlativoF = new int[3];
        for (int dia = DIAS_DE_HISTORIA; dia >= 1; dia--) {
            LocalDate fecha = hoy.minusDays(dia);
            List<Object[]> plan = new ArrayList<>();
            for (int k = 0; k < 3; k++) {
                if (k == 2 && dia % 2 == 1) {
                    continue;
                }
                int n = 6 + azar.nextInt(5) - k;
                for (int i = 0; i < n; i++) {
                    plan.add(new Object[]{k, LocalTime.of(8, 0).plusMinutes(azar.nextInt(13 * 60 + 30))});
                }
            }
            plan.sort(Comparator.comparing(o -> (LocalTime) o[1]));
            for (Object[] o : plan) {
                int k = (int) o[0];
                auditar(auditoria, cajeros[k], fecha.atTime(7, 50 + k), "LOGIN", "usuario", cajeros[k].getIdUsuario(),
                        ResultadoOperacion.EXITO, "CAJERO", true);
            }
            for (Object[] o : plan) {
                int k = (int) o[0];
                LocalDateTime t = fecha.atTime((LocalTime) o[1]);
                Venta v = new Venta(0, cajaDe[k], cajeros[k], UUID.randomUUID().toString(), t);
                int lineas = 1 + azar.nextInt(5);
                for (int l = 0; l < lineas; l++) {
                    Producto p = ps.get(azar.nextInt(ps.size()));
                    double cant = p.getUnidadMedida() == UnidadMedida.KG
                            ? Math.round((0.4 + azar.nextDouble() * 1.6) * 1000) / 1000.0 : 1 + azar.nextInt(3);
                    if (p.hayStock(cant + 5)) {
                        v.agregarProducto(p, cant);
                    }
                }
                if (v.getDetalles().isEmpty()) {
                    continue;
                }
                int r = azar.nextInt(100);
                TipoComprobante tipo = r < 68 ? TipoComprobante.VENTA_SIMPLE : r < 90 ? TipoComprobante.BOLETA : TipoComprobante.FACTURA;
                if (tipo == TipoComprobante.BOLETA) {
                    v.asignarCliente(conDni.get(azar.nextInt(conDni.size())));
                } else if (tipo == TipoComprobante.FACTURA) {
                    v.asignarCliente(conRuc.get(azar.nextInt(conRuc.size())));
                }
                int m = azar.nextInt(100);
                Pago pago;
                LocalDateTime tPago = t.plusMinutes(2);
                if (m < 55) {
                    pago = new PagoEfectivo(0, v, cajeros[k], v.getTotal(), tPago, redondearEntrega(v.getTotal(), azar));
                } else if (m < 80) {
                    pago = new PagoTarjeta(0, v, cajeros[k], v.getTotal(), tPago, String.valueOf(100000 + azar.nextInt(899999)));
                } else {
                    pago = new PagoBilletera(0, v, cajeros[k], v.getTotal(), tPago, String.valueOf(10000000 + azar.nextInt(89999999)));
                }
                v.confirmar(pago);
                for (MovimientoInventario mv : v.getMovimientos()) {
                    mv.cargarDatos(tPago, mv.getSaldoResultante());
                }
                String serie = cajaDe[k].obtenerSerie(tipo);
                int corr = tipo == TipoComprobante.FACTURA ? ++correlativoF[k] : ++correlativoB[k];
                Comprobante c = switch (tipo) {
                    case VENTA_SIMPLE -> new VentaSimpleComprobante(0, v, serie, corr, tPago);
                    case BOLETA -> new Boleta(0, v, serie, corr, tPago);
                    case FACTURA -> new Factura(0, v, serie, corr, tPago);
                };
                c.cargarEstado(EstadoComprobante.ACEPTADO, 1, tPago, tPago.plusSeconds(3), "Comprobante aceptado.", null);
                v.setComprobante(c);
                ventas.guardarVentaConfirmada(v);
                auditar(auditoria, cajeros[k], tPago, "CONFIRMAR_VENTA", "venta", v.getIdVenta(), ResultadoOperacion.EXITO,
                        c.getNumero() + String.format(" · S/ %.2f", v.getTotal()), false);
            }
        }

        List<Venta> todas = ventas.listar();
        Comprobante pend1 = todas.get(todas.size() - 3).getComprobante();
        pend1.cargarEstado(EstadoComprobante.PENDIENTE, 3, pend1.getFechaEmision().plusMinutes(10), null,
                "Sin respuesta (intento 3).", null);
        Comprobante pend2 = todas.get(todas.size() - 12).getComprobante();
        pend2.cargarEstado(EstadoComprobante.PENDIENTE, 3, pend2.getFechaEmision().plusMinutes(10), null,
                "Sin respuesta (intento 3).", null);
        for (Venta v : todas) {
            if (v.getComprobante().getTipo() == TipoComprobante.FACTURA && v.getFechaHora().toLocalDate().equals(hoy.minusDays(2))) {
                v.getComprobante().cargarEstado(EstadoComprobante.RECHAZADO, 1, v.getComprobante().getFechaEmision(),
                        v.getComprobante().getFechaEmision().plusSeconds(4),
                        "El simulador rechazó el comprobante: revise los datos del cliente.", null);
                break;
            }
        }

        anular(todas.get(5), "Cliente devolvió los productos sellados", auditoria, ventas);
        anular(todas.get(todas.size() / 2), "Se cobró dos veces el mismo producto; el cliente devolvió todo", auditoria, ventas);

        for (int dia = DIAS_DE_HISTORIA; dia >= 1; dia--) {
            LocalDate fecha = hoy.minusDays(dia);
            for (int k = 0; k < 3; k++) {
                List<Venta> delDia = new ArrayList<>();
                for (Venta v : ventas.listar()) {
                    if (v.getCajero() == cajeros[k] && v.getFechaHora().toLocalDate().equals(fecha)) {
                        delDia.add(v);
                    }
                }
                if (delDia.isEmpty()) {
                    continue;
                }
                List<Pago> devoluciones = new ArrayList<>();
                for (Venta v : ventas.listar()) {
                    Pago p = v.getPago();
                    if (p.estaRevertido() && p.getCajeroReversion() == cajeros[k]
                            && p.getFechaReversion().toLocalDate().equals(fecha)) {
                        devoluciones.add(p);
                    }
                }
                CierreCaja cierre = new CierreCaja(0, cajeros[k], cajaDe[k], fecha, 150.00);
                cierre.calcularResumen(delDia, devoluciones);
                double faltante = (k == 1 && dia == 3) ? 2.50 : 0;
                cierre.registrarContado(cierre.getEfectivoEsperado() - faltante);
                String obs = faltante > 0 ? "Faltaron S/ 2.50: vuelto mal entregado en una venta en efectivo." : "";
                cierre.confirmar(obs);
                LocalDateTime hora = fecha.atTime(21, 40 + k * 5);
                cierre.cargarDatos(hora, cierre.getCantidadVentas(), cierre.getCantidadAnuladas(), cierre.getTotalEfectivo(),
                        cierre.getTotalTarjeta(), cierre.getTotalBilletera(), cierre.getDevueltoEfectivo(),
                        cierre.getEfectivoEsperado(), cierre.getEfectivoContado(), cierre.getDiferencia(), obs);
                cajas.guardarCierre(cierre, delDia, devoluciones);
                auditar(auditoria, cajeros[k], hora, "CERRAR_CAJA", "cierre_caja", cierre.getIdCierreCaja(),
                        ResultadoOperacion.EXITO, String.format("%s · esperado S/ %.2f · diferencia S/ %.2f",
                                cajaDe[k].getCodigo(), cierre.getEfectivoEsperado(), cierre.getDiferencia()), false);
            }
        }

        auditar(auditoria, admin, hoy.minusDays(DIAS_DE_HISTORIA).atTime(8, 5), "REGISTRAR_PROMOCION", "promocion", 1,
                ResultadoOperacion.EXITO, "Gaseosa a precio de oferta", false);
        auditar(auditoria, marco, hoy.minusDays(4).atTime(7, 58), "LOGIN", "usuario", marco.getIdUsuario(),
                ResultadoOperacion.FALLIDO, "Intento fallido 1", false);
        auditar(auditoria, null, hoy.minusDays(2).atTime(13, 20), "LOGIN", "usuario", null,
                ResultadoOperacion.FALLIDO, "Nombre de acceso no registrado", false);
        auditar(auditoria, admin, hoy.minusDays(1).atTime(18, 30), "CAMBIAR_PRECIO", "producto", ps.get(2).getIdProducto(),
                ResultadoOperacion.EXITO, "Aceite vegetal 900 ml: S/ 9.90 → S/ 9.50", false);

        return new Repositorios(usuarios, catalogo, clientes, cajas, ventas, auditoria, "memoria");
    }

    private static void anular(Venta v, String motivo, RepositorioAuditoriaMemoria auditoria, RepositorioVentasMemoria ventas) {
        Cajero cajero = v.getCajero();
        LocalDateTime t = v.getFechaHora().plusHours(1);
        v.anular(motivo, cajero);
        if (v.getPago() instanceof PagoElectronico pe) {
            pe.registrarReferenciaReversion("DEV-" + pe.getReferenciaDigital());
        }
        v.cargarDatos(EstadoVenta.ANULADA, v.getCliente(), v.getPago(), v.getComprobante(), t, motivo, cajero);
        v.getPago().cargarReversion(EstadoPago.REVERTIDO, t, cajero);
        for (MovimientoInventario m : v.getMovimientos()) {
            m.cargarDatos(t, m.getSaldoResultante());
        }
        ventas.guardarAnulacion(v);
        auditar(auditoria, cajero, t, "ANULAR_VENTA", "venta", v.getIdVenta(), ResultadoOperacion.EXITO,
                v.getComprobante().getNumero() + " · " + motivo, false);
    }

    private static double redondearEntrega(double total, Random azar) {
        int[] billetes = {10, 20, 50, 100, 200};
        if (azar.nextInt(4) == 0) {
            return total;
        }
        for (int b : billetes) {
            if (b >= total) {
                return b;
            }
        }
        return Math.ceil(total / 100.0) * 100;
    }

    private static void auditar(RepositorioAuditoriaMemoria a, Usuario u, LocalDateTime t, String accion, String entidad,
                                Object id, ResultadoOperacion r, String detalle, boolean unaVezPorDia) {
        if (unaVezPorDia) {
            for (Auditoria x : a.listar()) {
                if (x.getUsuario() == u && x.getAccion().equals(accion) && x.getFechaHora().toLocalDate().equals(t.toLocalDate())) {
                    return;
                }
            }
        }
        a.registrar(new Auditoria(0, u, t, accion, entidad, id == null ? null : String.valueOf(id), r, detalle));
    }

    private static Categoria categoria(RepositorioCatalogoMemoria repo, String nombre, String descripcion) {
        Categoria c = new Categoria(0, nombre, descripcion);
        repo.guardarCategoria(c);
        return c;
    }

    private static Producto producto(RepositorioCatalogoMemoria repo, Categoria cat, String codigo, String nombre,
                                     UnidadMedida unidad, double precio, double stock) {
        Producto p = new Producto(0, cat, codigo, nombre, unidad, precio, stock);
        repo.guardarProducto(p);
        return p;
    }

    private static void promocion(RepositorioCatalogoMemoria repo, Producto p, Administrador admin, String nombre,
                                  double precio, LocalDate inicio, LocalDate fin) {
        Promocion promo = new Promocion(0, p, admin, nombre, precio, inicio, fin);
        p.agregarPromocion(promo);
        repo.guardarPromocion(promo);
    }

    private static Cliente cliente(RepositorioClientesMemoria repo, TipoDocumento tipo, String numero, String nombre,
                                   String direccion) {
        Cliente c = new Cliente(0, tipo, numero, nombre, direccion);
        repo.guardar(c);
        return c;
    }
}
