# Tiendas 3A · Sistema de ventas en caja

Proyecto **Java con Maven** para Apache NetBeans. Es una aplicación de escritorio en **Swing** con arquitectura **MVC** y base de datos **MySQL**. Implementa el documento de requisitos *revision_tiendas3a* (versión 1.3) y el diagrama de clases corregido (v3).

## Cómo ejecutarlo

**Requisito:** JDK 17 o superior.

1. En NetBeans, abre la carpeta del proyecto con **File → Open Project** (la carpeta que contiene `pom.xml`).
2. Presiona **Run (▶)**. La clase principal es `com.mycompany.tienda3a.Main`.

La primera vez, Maven descarga el driver de MySQL, así que se necesita internet.

## Base de datos MySQL

1. En MySQL Workbench, abre `bd/tiendas3a.sql` y ejecútalo completo (botón del rayo). El script crea la base `tiendas3a` con las 13 tablas del diccionario de datos (sección 8.2.1) y carga datos de prueba de los últimos 6 días.
2. Abre `mysql.properties` y escribe tu usuario y tu clave de MySQL.
3. Ejecuta el proyecto. En la barra superior debe aparecer **MySQL conectado**.

Si MySQL no responde, el sistema funciona igual con datos de ejemplo en memoria. En ese caso la barra superior muestra **Datos en memoria** y los cambios se pierden al cerrar. La salida de NetBeans indica el motivo, por ejemplo una clave incorrecta o el servicio detenido.

Para volver a los datos iniciales, ejecuta otra vez `bd/tiendas3a.sql`.

## Usuarios de prueba

La contraseña de todos es `Tienda3A*`.

| Usuario | Rol | Qué ve |
|---|---|---|
| `atorres` | Cajero (Ana Torres) | Nueva venta, Consultar productos, Anular venta, Cierre de caja |
| `lquispe` | Cajero (Luis Quispe) | Lo mismo |
| `mrojas` | Cajero (Marco Rojas) | Lo mismo |
| `cflores` | Cajero inactivo | No puede ingresar (prueba de RF-19) |
| `admin` | Administrador (Rosa Medina) | Inicio, Ventas, Reportes, Productos, Promociones, Usuarios, Comprobantes, Bitácora |

Después de iniciar sesión, el cajero elige su caja (CAJA-1, CAJA-2 o CAJA-3). Sus ventas usan las series de esa caja.

## Pantallas y casos de uso

| Pantalla | Caso de uso | Qué hace |
|---|---|---|
| Inicio de sesión | CU-01 | Usuario y contraseña. Bloquea 15 min tras 5 intentos y cierra la sesión tras 15 min sin actividad |
| Nueva venta | CU-02 a CU-06 | Escanear o buscar productos, promociones automáticas, comprobante (venta simple, boleta o factura), pago (efectivo, tarjeta o billetera), ticket y envío al simulador de SUNAT |
| Consultar productos | CU-07 | Precio, promoción vigente y stock |
| Anular venta | CU-08 | Motivo obligatorio. Reingresa el stock, revierte el pago y anula el comprobante |
| Cierre de caja | CU-09 | Cobros por medio de pago, efectivo esperado, arqueo y diferencia |
| Inicio, Ventas y Reportes | CU-10 | Indicadores, consulta de ventas y reportes por producto, caja y cajero |
| Productos y Promociones | CU-11 | Registrar, modificar, cambiar precio, desactivar; promociones con vigencia |
| Usuarios | CU-12 | Registrar, modificar, desactivar, desbloquear y cambiar contraseña |
| Comprobantes | CU-13 | Pendientes y rechazados: reenviar y registrar la revisión |
| Bitácora | RF-20 | Accesos, ventas, anulaciones, cierres y cambios |

Atajos en Nueva venta: **F2** buscar, **Enter** agregar, **flechas** elegir una sugerencia, **Supr** quitar la línea seleccionada y **F9** cobrar.

## Estructura (MVC)

```
tienda3a/
├─ pom.xml                 Java 17, driver MySQL
├─ mysql.properties        Usuario y clave de MySQL
├─ bd/tiendas3a.sql        13 tablas (diccionario 8.2.1) y datos de prueba
└─ src/main/java/com/mycompany/tienda3a/
   ├─ Main.java            Clase principal
   ├─ App.java             Arma repositorios → controladores → vista
   ├─ config/              Configuracion (IGV, fondo de caja, límites; static con get/set) y Empresa
   ├─ modelo/              MODELO: clases del diagrama de clases v3
   ├─ controlador/         CONTROLADORES: reglas de negocio (RNF-14)
   ├─ vista/               VISTA: pantallas Swing (solo capturan datos y muestran resultados)
   │  └─ componentes/      Tema (colores y fuentes), botones, campos, tablas, íconos, gráficos
   ├─ repositorio/         Interfaces (QUÉ se guarda)
   │  ├─ memoria/          Implementación en memoria (datos de ejemplo)
   │  └─ mysql/            Implementación JDBC (CÓMO se guarda en MySQL)
   ├─ datos/               Carga desde MySQL o datos iniciales de ejemplo
   ├─ sunat/               AdaptadorSunat (interfaz) y SimuladorSunat
   └─ excepcion/           ExcepcionNegocio y ExcepcionDatos
```

| Capa | Qué hace |
|---|---|
| **Vista** | Dibuja la pantalla, lee los campos y muestra resultados y mensajes. No tiene reglas de negocio |
| **Controlador** | Aplica las reglas (RN-01 a RN-15) y coordina el modelo y los repositorios |
| **Modelo** | Las clases del negocio con sus validaciones propias |
| **Repositorio** | Guarda y lee los datos (MySQL o memoria) |

## Programación orientada a objetos

| Concepto | Dónde se ve |
|---|---|
| **Herencia** | `Usuario` (abstracta) → `Cajero`, `Administrador`. `Pago` (abstracta) → `PagoEfectivo`, `PagoTarjeta`, `PagoBilletera`. `Comprobante` (abstracta) → `VentaSimpleComprobante`, `Boleta`, `Factura`. Los repositorios MySQL heredan de los de memoria |
| **Atributos protegidos** | Los atributos de estado de las clases padre (`Usuario`, `Pago`, `Comprobante`) son `protected` |
| **Interfaces** | `PagoElectronico` (la implementan `PagoTarjeta` y `PagoBilletera`), `Imprimible`, `AdaptadorSunat` y los 6 repositorios |
| **Polimorfismo** | `usuario.puedeAcceder()` decide qué pantallas ve cada rol. `pago.validar()` y `pago.getMedio()` responden según el medio. `comprobante.validarDatos()` y `generarImpresion()` según el tipo |
| **Encapsulamiento** | Atributos privados con get y set; la validación está dentro del set |
| **Atributos estáticos** | `Configuracion` (tasa de IGV, fondo de caja, monto máximo de venta simple, reintentos), con get y set (RNF-06) |

## Principios SOLID y patrones

| Principio o patrón | Dónde se aplica |
|---|---|
| **S · Responsabilidad única** | Cada capa tiene una sola tarea: la vista muestra, el controlador aplica reglas, el modelo valida sus datos y el repositorio guarda |
| **O · Abierto/cerrado** | Un medio de pago nuevo es otra clase hija de `Pago`; un comprobante nuevo, otra hija de `Comprobante`. La confirmación de la venta no cambia |
| **D · Inversión de dependencias** | Los controladores reciben interfaces (`RepositorioVentas`, `AdaptadorSunat`…). Solo `App` decide si se usa MySQL o memoria |
| **Adaptador** | `AdaptadorSunat` aísla el envío a SUNAT; hoy lo implementa `SimuladorSunat` |
| **Repositorio** | Las 6 interfaces de `repositorio` separan las reglas del acceso a datos |
| **Método plantilla** | `Comprobante.armarTicket()` arma el ticket común y cada tipo agrega su título y sus datos del cliente |

## Reglas destacadas

- **Confirmación única (RF-10, RNF-11):** el pago, el descuento de stock, los movimientos de inventario y el comprobante se guardan en una sola transacción. Si algo falla, la venta sigue en curso y no se descuenta nada.
- **Promociones (RN-03, RN-13):** se aplican solas al agregar el producto. El precio de la línea ya no cambia aunque cambie la promoción.
- **Comprobantes (RN-05, RN-06):** la venta simple solo procede hasta S/ 700. La boleta exige DNI y la factura, RUC. La serie depende de la caja y el correlativo es consecutivo por serie.
- **Longitud del DNI y del RUC:** es una regla de negocio, por eso se valida en `ControladorVenta` y no en la clase `Cliente`.
- **SUNAT simulado (RN-15):** si el simulador no responde, el comprobante queda PENDIENTE y se reenvía cada 5 minutos, hasta 3 veces.
- **Contraseñas (RNF-08):** se guardan solo como hash PBKDF2 con sal.

## Diseño de la interfaz

Los colores y las fuentes están en `vista/componentes/Tema.java`. Las fuentes (Poppins y Liberation Mono, licencia SIL OFL) están en `src/main/resources/com/mycompany/tienda3a/fuentes`.

| Color | Uso |
|---|---|
| Petróleo `#0F4C5C` | Marca, menú lateral, botones principales |
| Amarillo `#F2B705` | Acento de marca y promociones |
| Verde `#1B7F4B` | Cobrar, confirmar y estados correctos |
| Rojo `#C0392B` | Anular, errores y faltantes |
| Ámbar `#B45309` | Pendientes y stock bajo |

Ningún estado depende solo del color: las insignias siempre llevan ícono y texto.
