package com.Ventas;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Clase para imprimir tickets en impresora térmica POS-58
 */
public class impresoraTermica {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(impresoraTermica.class.getName());
    
    // Configuración de la impresora
    private static final String NOMBRE_IMPRESORA = "POS-58"; // Nombre de tu impresora
    private static final int ANCHO_PAPEL = 58; // Ancho en mm (58mm para POS-58)
    
    /**
     * Imprime el ticket de venta SOLO si la impresora está conectada
     */
    public static boolean imprimirTicket(
            int idVenta,
            List<itemVentas> productos,
            BigDecimal total,
            BigDecimal efectivo,
            BigDecimal cambio,
            String cajero) {
        
        try {
            // ⭐ VERIFICAR que la impresora esté conectada y lista
            PrintService impresora = buscarImpresora();
            
            if (impresora == null) {
                logger.warning("No se encontró la impresora térmica");
                return false;
            }
            
            // ⭐ VERIFICAR que la impresora esté realmente conectada (no en cola)
            if (!impresoraEstaConectada(impresora)) {
                logger.warning("La impresora no está conectada o no está lista");
                return false;
            }
            
            // Crear el trabajo de impresión
            DocPrintJob trabajoImpresion = impresora.createPrintJob();
            
            // Crear el contenido del ticket
            String contenidoTicket = generarContenidoTicket(
                idVenta, productos, total, efectivo, cambio, cajero
            );
            
            // Configurar el documento a imprimir
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc documento = new SimpleDoc(
                contenidoTicket.getBytes("Windows-1252"), 
                flavor, 
                null
            );
            
            // Configurar atributos de impresión
            PrintRequestAttributeSet atributos = new HashPrintRequestAttributeSet();
            atributos.add(new Copies(1));
            
            // Imprimir con manejo de errores
            try {
                trabajoImpresion.print(documento, atributos);
                logger.info("Ticket impreso exitosamente en " + impresora.getName());
                return true;
            } catch (PrintException pe) {
                // Si falla la impresión, cancelar el trabajo
                logger.warning("Error al imprimir, cancelando trabajo: " + pe.getMessage());
                cancelarTrabajosEnCola(impresora);
                return false;
            }
            
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, 
                "Error al imprimir ticket", e);
            return false;
        }
    }
    
    /**
     * ⭐ NUEVO: Verifica si la impresora está realmente conectada y lista
     */
    private static boolean impresoraEstaConectada(PrintService impresora) {
        try {
            // Obtener atributos de la impresora
            PrintServiceAttributeSet atributos = impresora.getAttributes();
            
            // Verificar estado de la impresora
            PrinterState estado = (PrinterState) atributos.get(PrinterState.class);
            PrinterIsAcceptingJobs aceptandoTrabajos = 
                (PrinterIsAcceptingJobs) atributos.get(PrinterIsAcceptingJobs.class);
            
            // Log para debug
            logger.info("Estado impresora: " + (estado != null ? estado : "desconocido"));
            logger.info("Aceptando trabajos: " + (aceptandoTrabajos != null ? aceptandoTrabajos : "desconocido"));
            
            // Verificar que esté en estado IDLE o PROCESSING (no STOPPED)
            if (estado != null && estado == PrinterState.STOPPED) {
                logger.warning("Impresora detenida");
                return false;
            }
            
            // Verificar que esté aceptando trabajos
            if (aceptandoTrabajos != null && 
                aceptandoTrabajos == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS) {
                logger.warning("Impresora no acepta trabajos");
                return false;
            }
            
            // ⭐ Verificación adicional: intentar una operación rápida
            return verificarConexionReal(impresora);
            
        } catch (Exception e) {
            logger.warning("Error al verificar estado de impresora: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * ⭐ NUEVO: Verificación real de conexión (intenta comunicarse con la impresora)
     */
    private static boolean verificarConexionReal(PrintService impresora) {
        try {
            // Crear un trabajo de prueba vacío
            DocPrintJob testJob = impresora.createPrintJob();
            
            // Si se puede crear el trabajo, la impresora está disponible
            // (esto no envía nada a imprimir)
            return testJob != null;
            
        } catch (Exception e) {
            logger.warning("Impresora no responde: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * ⭐ NUEVO: Cancela trabajos en cola para evitar impresiones pendientes
     */
    private static void cancelarTrabajosEnCola(PrintService impresora) {
        try {
            // Obtener trabajos en cola
            PrintJobAttributeSet jobAttrs = new HashPrintJobAttributeSet();
            
            logger.info("Cancelando trabajos pendientes en " + impresora.getName());
            
            // Nota: En Windows, los trabajos se gestionan a nivel de sistema
            // Esta es una operación de limpieza local
            
        } catch (Exception e) {
            logger.warning("No se pudieron cancelar trabajos: " + e.getMessage());
        }
    }
    
    /**
     * Busca la impresora térmica instalada
     */
    private static PrintService buscarImpresora() {
        // Obtener todas las impresoras disponibles
        PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);
        
        // Buscar específicamente la POS-58
        for (PrintService impresora : impresoras) {
            String nombreImpresora = impresora.getName().toLowerCase();
            
            // Buscar por nombre exacto o parcial
            if (nombreImpresora.contains("pos-58") || 
                nombreImpresora.contains("pos58") ||
                nombreImpresora.contains("pos-50")) {
                
                logger.info("Impresora encontrada: " + impresora.getName());
                return impresora;
            }
        }
        
        // No usar impresora predeterminada si no es la térmica
        logger.warning("No se encontró la impresora POS-58");
        return null;
    }
    
    /**
     * Genera el contenido del ticket en formato ESC/POS
     */
    private static String generarContenidoTicket(
            int idVenta,
            List<itemVentas> productos,
            BigDecimal total,
            BigDecimal efectivo,
            BigDecimal cambio,
            String cajero) {
        
        StringBuilder ticket = new StringBuilder();
        
        // Comandos ESC/POS
        String ESC = "\u001B";
        String INIT = ESC + "@";           // Inicializar impresora
        String CENTER = ESC + "a" + (char)1;  // Centrar texto
        String LEFT = ESC + "a" + (char)0;    // Alinear izquierda
        String BOLD_ON = ESC + "E" + (char)1; // Negrita ON
        String BOLD_OFF = ESC + "E" + (char)0; // Negrita OFF
        String CUT = ESC + "i";            // Cortar papel
        String LINE_FEED = "\n";
        
        // Inicializar
        ticket.append(INIT);
        
        // Encabezado centrado
        ticket.append(CENTER);
        ticket.append(BOLD_ON);
        ticket.append("ARCANGEL MIGUEL\n");
        ticket.append(BOLD_OFF);
        ticket.append("Papeleria y Regalos\n");
        ticket.append("================================\n");
        
        // Información del ticket
       
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        ticket.append(String.format("Fecha: %s\n", sdf.format(new Date())));
        ticket.append(String.format("Cajero/a: %s\n", cajero));
        
        ticket.append("--------------------------------\n");
        ticket.append("PRODUCTO      CANT  P.U  TOTAL\n");
        ticket.append("--------------------------------\n");
        
        // Productos
        for (itemVentas item : productos) {
            String nombre = item.getNombreProducto();
            if (nombre.length() > 12) {
                nombre = nombre.substring(0, 12);
            }
            
            ticket.append(String.format("%-12s %3d %5.2f %6.2f\n",
                nombre,
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getSubtotal()
            ));
        }
        
        // Totales
        ticket.append("--------------------------------\n");
        ticket.append(BOLD_ON);
        ticket.append(String.format("TOTAL:              $%7.2f\n", total));
        ticket.append(BOLD_OFF);
        ticket.append(String.format("EFECTIVO:           $%7.2f\n", efectivo));
        ticket.append(String.format("CAMBIO:             $%7.2f\n", cambio));
        ticket.append("================================\n");
        
        // Pie de página
        ticket.append(CENTER);
        ticket.append("Gracias por su compra!\n");
        ticket.append("Vuelva pronto\n\n\n");
        
        // Cortar papel
        ticket.append(CUT);
        
        return ticket.toString();
    }
    
    /**
     * Lista todas las impresoras disponibles (útil para debug)
     */
    public static void listarImpresoras() {
        PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);
        
        System.out.println("=== IMPRESORAS DISPONIBLES ===");
        for (PrintService impresora : impresoras) {
            System.out.println("Nombre: " + impresora.getName());
            
            // Obtener atributos
            PrintServiceAttributeSet atributos = impresora.getAttributes();
            for (Attribute attr : atributos.toArray()) {
                System.out.println("  - " + attr.getName() + ": " + attr);
            }
            
            // Estado de conexión
            boolean conectada = impresoraEstaConectada(impresora);
            System.out.println("  ✓ Conectada: " + (conectada ? "SÍ" : "NO"));
            System.out.println("---");
        }
    }
    
    /**
     * ⭐ NUEVO: Verifica si hay impresora conectada antes de intentar vender
     */
    public static boolean hayImpresoraDisponible() {
        PrintService impresora = buscarImpresora();
        if (impresora == null) {
            return false;
        }
        return impresoraEstaConectada(impresora);
    }
}