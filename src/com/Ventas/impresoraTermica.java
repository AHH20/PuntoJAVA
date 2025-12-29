package com.Ventas;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

public class impresoraTermica {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(impresoraTermica.class.getName());
    
    private static final String NOMBRE_IMPRESORA = "POS-58";
    private static final int ANCHO_PAPEL = 58;
    
    public static boolean imprimirTicket(
            int idVenta,
            List<itemVentas> productos,
            BigDecimal total,
            BigDecimal efectivo,
            BigDecimal cambio,
            String cajero) {
        
        try {
            PrintService impresora = buscarImpresora();
            
            if (impresora == null) {
                logger.warning("No se encontró la impresora térmica");
                return false;
            }
            
            if (!impresoraEstaConectada(impresora)) {
                logger.warning("La impresora no está conectada o no está lista");
                return false;
            }
            
            DocPrintJob trabajoImpresion = impresora.createPrintJob();
            
            String contenidoTicket = generarContenidoTicket(
                idVenta, productos, total, efectivo, cambio, cajero
            );
            
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc documento = new SimpleDoc(
                contenidoTicket.getBytes("Windows-1252"), 
                flavor, 
                null
            );
            
            PrintRequestAttributeSet atributos = new HashPrintRequestAttributeSet();
            atributos.add(new Copies(1));
            
            try {
                trabajoImpresion.print(documento, atributos);
                logger.info("Ticket impreso exitosamente en " + impresora.getName());
                return true;
            } catch (PrintException pe) {
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
    
    private static boolean impresoraEstaConectada(PrintService impresora) {
        try {
            PrintServiceAttributeSet atributos = impresora.getAttributes();
            
            PrinterState estado = (PrinterState) atributos.get(PrinterState.class);
            PrinterIsAcceptingJobs aceptandoTrabajos = 
                (PrinterIsAcceptingJobs) atributos.get(PrinterIsAcceptingJobs.class);
            
            logger.info("Estado impresora: " + (estado != null ? estado : "desconocido"));
            logger.info("Aceptando trabajos: " + (aceptandoTrabajos != null ? aceptandoTrabajos : "desconocido"));
            
            if (estado != null && estado == PrinterState.STOPPED) {
                logger.warning("Impresora detenida");
                return false;
            }
            
            if (aceptandoTrabajos != null && 
                aceptandoTrabajos == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS) {
                logger.warning("Impresora no acepta trabajos");
                return false;
            }
            
            return verificarConexionReal(impresora);
            
        } catch (Exception e) {
            logger.warning("Error al verificar estado de impresora: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean verificarConexionReal(PrintService impresora) {
        try {
            DocPrintJob testJob = impresora.createPrintJob();
            return testJob != null;
        } catch (Exception e) {
            logger.warning("Impresora no responde: " + e.getMessage());
            return false;
        }
    }
    
    private static void cancelarTrabajosEnCola(PrintService impresora) {
        try {
            PrintJobAttributeSet jobAttrs = new HashPrintJobAttributeSet();
            logger.info("Cancelando trabajos pendientes en " + impresora.getName());
        } catch (Exception e) {
            logger.warning("No se pudieron cancelar trabajos: " + e.getMessage());
        }
    }
    
    private static PrintService buscarImpresora() {
        PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);
        
        for (PrintService impresora : impresoras) {
            String nombreImpresora = impresora.getName().toLowerCase();
            
            if (nombreImpresora.contains("pos-58") || 
                nombreImpresora.contains("pos58") ||
                nombreImpresora.contains("pos-50")) {
                
                logger.info("Impresora encontrada: " + impresora.getName());
                return impresora;
            }
        }
        
        logger.warning("No se encontró la impresora POS-58");
        return null;
    }
    
    // ⭐ CORREGIDO: Cambio de %3d a %.1f para cantidades decimales
    private static String generarContenidoTicket(
            int idVenta,
            List<itemVentas> productos,
            BigDecimal total,
            BigDecimal efectivo,
            BigDecimal cambio,
            String cajero) {
        
        StringBuilder ticket = new StringBuilder();
        
        String ESC = "\u001B";
        String INIT = ESC + "@";
        String CENTER = ESC + "a" + (char)1;
        String LEFT = ESC + "a" + (char)0;
        String BOLD_ON = ESC + "E" + (char)1;
        String BOLD_OFF = ESC + "E" + (char)0;
        String CUT = ESC + "i";
        
        ticket.append(INIT);
        
        ticket.append(CENTER);
        ticket.append(BOLD_ON);
        ticket.append("ARCANGEL MIGUEL\n");
        ticket.append(BOLD_OFF);
        ticket.append("Papeleria y Regalos\n");
        ticket.append("================================\n");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        ticket.append(String.format("Fecha: %s\n", sdf.format(new Date())));
        ticket.append(String.format("Cajero/a: %s\n", cajero));
        
        ticket.append("--------------------------------\n");
        ticket.append("PRODUCTO      CANT  P.U  TOTAL\n");
        ticket.append("--------------------------------\n");
        
        ticket.append(LEFT);
        
        for (itemVentas item : productos) {
            String nombre = item.getNombreProducto();
            if (nombre.length() > 12) {
                nombre = nombre.substring(0, 12);
            }
            
            // ✅ CORRECCIÓN: Usar %.1f en lugar de %3d para cantidad decimal
            ticket.append(String.format("%-12s %.1f %5.2f %6.2f\n",
                nombre,
                item.getCantidad(),  // ⭐ Ahora acepta double
                item.getPrecioUnitario(),
                item.getSubtotal()
            ));
        }
        
        ticket.append("--------------------------------\n");
        ticket.append(BOLD_ON);
        ticket.append(String.format("TOTAL:              $%7.2f\n", total));
        ticket.append(BOLD_OFF);
        ticket.append(String.format("EFECTIVO:           $%7.2f\n", efectivo));
        ticket.append(String.format("CAMBIO:             $%7.2f\n", cambio));
        ticket.append("================================\n");
        
        ticket.append(CENTER);
        ticket.append("Gracias por su compra!\n");
        ticket.append("Vuelva pronto\n\n\n");
        
        ticket.append(CUT);
        
        return ticket.toString();
    }
    
    public static void listarImpresoras() {
        PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);
        
        System.out.println("=== IMPRESORAS DISPONIBLES ===");
        for (PrintService impresora : impresoras) {
            System.out.println("Nombre: " + impresora.getName());
            
            PrintServiceAttributeSet atributos = impresora.getAttributes();
            for (Attribute attr : atributos.toArray()) {
                System.out.println("  - " + attr.getName() + ": " + attr);
            }
            
            boolean conectada = impresoraEstaConectada(impresora);
            System.out.println("  ✓ Conectada: " + (conectada ? "SÍ" : "NO"));
            System.out.println("---");
        }
    }
    
    public static boolean hayImpresoraDisponible() {
        PrintService impresora = buscarImpresora();
        if (impresora == null) {
            return false;
        }
        return impresoraEstaConectada(impresora);
    }
}