package com.respaldo;

import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.*;
import com.bd.Conexion;

/**
 * Sistema de Respaldo para Base de Datos SQLite
 * Permite crear, restaurar y gestionar respaldos de la base de datos
 */
public class Respaldo {
    
    private static final int DIAS_RECORDATORIO = 7;
    private static final String PREFIJO_RESPALDO = "respaldo_";
    private static final String EXTENSION_DB = ".db";
    private static final String EXTENSION_ZIP = ".zip";
    
    // Obtener rutas desde la clase Conexion
    private static String getArchivoBD() {
        return Conexion.getRutaBD();
    }
    
    private static String getCarpetaRespaldos() {
        return Conexion.getDirectorioBD() + File.separator + "Respaldos";
    }
    
    /**
     * Crear respaldo de la base de datos SQLite
     */
    public static void crearRespaldo() {
        try {
            String archivoBD = getArchivoBD();
            String carpetaRespaldos = getCarpetaRespaldos();
            
            // Validar que existe el archivo de BD
            if (!validarArchivoBD(archivoBD)) {
                return;
            }
            
            // Crear carpeta de respaldos
            crearCarpetaRespaldos(carpetaRespaldos);
            
            // Generar nombre con fecha y hora
            String fecha = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String nombreRespaldo = carpetaRespaldos + File.separator + PREFIJO_RESPALDO + fecha + EXTENSION_DB;
            
            // Copiar archivo
            Files.copy(
                Paths.get(archivoBD),
                Paths.get(nombreRespaldo),
                StandardCopyOption.REPLACE_EXISTING
            );
            
            // Mostrar mensaje de éxito
            mostrarMensajeExito(nombreRespaldo);
            
            // Preguntar si desea abrir la carpeta
            preguntarAbrirCarpeta();
            
        } catch (IOException e) {
            mostrarMensajeError("Error al crear respaldo", e);
        }
    }
    
    /**
     * Restaurar respaldo desde archivo seleccionado
     */
    public static void restaurarRespaldo() {
        try {
            JFileChooser fileChooser = new JFileChooser(getCarpetaRespaldos());
            fileChooser.setDialogTitle("Seleccionar Respaldo a Restaurar");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().endsWith(EXTENSION_DB) || f.getName().endsWith(EXTENSION_ZIP);
                }
                
                @Override
                public String getDescription() {
                    return "Archivos de Respaldo (*.db, *.zip)";
                }
            });
            
            int resultado = fileChooser.showOpenDialog(null);
            
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivoSeleccionado = fileChooser.getSelectedFile();
                
                // Confirmar restauración
                int confirmar = JOptionPane.showConfirmDialog(null,
                    "⚠️ ADVERTENCIA ⚠️\n\n" +
                    "Esta acción sobrescribirá la base de datos actual.\n" +
                    "Se creará un respaldo de seguridad automáticamente.\n\n" +
                    "La aplicación se cerrará y deberá reiniciarla manualmente.\n\n" +
                    "Archivo a restaurar:\n" + archivoSeleccionado.getName() + "\n\n" +
                    "¿Desea continuar con la restauración?",
                    "Confirmar Restauración",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (confirmar == JOptionPane.YES_OPTION) {
                    try {
                        // Paso 1: Crear respaldo de seguridad antes de restaurar
                        JOptionPane.showMessageDialog(null,
                            "📦 Creando respaldo de seguridad...",
                            "Preparando restauración",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        crearRespaldo();
                        
                        // Paso 2: Cerrar todas las conexiones
                        System.out.println("🔒 Cerrando conexiones...");
                        Conexion.cerrarTodasLasConexiones();
                        
                        // Paso 3: Esperar para asegurar cierre
                        Thread.sleep(1000);
                        
                        // Paso 4: Restaurar archivo
                        System.out.println("📂 Copiando archivo de respaldo...");
                        Files.copy(
                            archivoSeleccionado.toPath(),
                            Paths.get(getArchivoBD()),
                            StandardCopyOption.REPLACE_EXISTING
                        );
                        
                        // Paso 5: Confirmar éxito
                        JOptionPane.showMessageDialog(null,
                            "✅ ¡Respaldo restaurado exitosamente!\n\n" +
                            "La aplicación se cerrará ahora.\n\n" +
                            "Por favor, reinicie la aplicación para\n" +
                            "ver los cambios aplicados.",
                            "Restauración Completada",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Paso 6: Cerrar la aplicación
                        System.out.println("🚪 Cerrando aplicación...");
                        System.exit(0);
                        
                    } catch (Exception ex) {
                        // Si falla, intentar con script
                        System.err.println("⚠️ Método directo falló, usando script...");
                        crearScriptRestauracion(archivoSeleccionado);
                        
                        JOptionPane.showMessageDialog(null,
                            "⚠️ La restauración se completará al cerrar\n\n" +
                            "La aplicación se cerrará ahora.\n" +
                            "Espere 5 segundos y reinicie la aplicación.",
                            "Restauración Programada",
                            JOptionPane.WARNING_MESSAGE);
                        
                        System.exit(0);
                    }
                }
            }
            
        } catch (Exception e) {
            mostrarMensajeError("Error al restaurar respaldo", e);
        }
    }
    
    /**
     * Crear script para restaurar después de cerrar (método de respaldo)
     */
    private static void crearScriptRestauracion(File archivoRespaldo) {
        try {
            String archivoDB = getArchivoBD();
            String scriptPath = System.getProperty("java.io.tmpdir") + File.separator + "restaurar_bd.bat";
            
            // Crear script batch para Windows
            try (java.io.PrintWriter writer = new java.io.PrintWriter(scriptPath)) {
                writer.println("@echo off");
                writer.println("echo Esperando cierre de aplicacion...");
                writer.println("timeout /t 5 /nobreak >nul");
                writer.println("echo Restaurando base de datos...");
                writer.println("copy /Y \"" + archivoRespaldo.getAbsolutePath() + "\" \"" + archivoDB + "\"");
                writer.println("if %ERRORLEVEL% EQU 0 (");
                writer.println("    echo Restauracion completada exitosamente");
                writer.println(") else (");
                writer.println("    echo ERROR: No se pudo restaurar la base de datos");
                writer.println("    pause");
                writer.println(")");
                writer.println("timeout /t 3 >nul");
                writer.println("del \"%~f0\"");
            }
            
            // Ejecutar script en segundo plano
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "/min", scriptPath);
            pb.start();
            
            System.out.println("✅ Script de restauración creado: " + scriptPath);
            
        } catch (Exception e) {
            System.err.println("❌ Error al crear script: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crear respaldo comprimido (ZIP)
     */
    public static void crearRespaldoComprimido() {
        try {
            String archivoBD = getArchivoBD();
            String carpetaRespaldos = getCarpetaRespaldos();
            
            if (!validarArchivoBD(archivoBD)) {
                return;
            }
            
            crearCarpetaRespaldos(carpetaRespaldos);
            
            String fecha = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String nombreZip = carpetaRespaldos + File.separator + PREFIJO_RESPALDO + fecha + EXTENSION_ZIP;
            
            // Crear archivo ZIP
            try (FileOutputStream fos = new FileOutputStream(nombreZip);
                 ZipOutputStream zos = new ZipOutputStream(fos);
                 FileInputStream fis = new FileInputStream(archivoBD)) {
                
                ZipEntry zipEntry = new ZipEntry(new File(archivoBD).getName());
                zos.putNextEntry(zipEntry);
                
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                
                zos.closeEntry();
            }
            
            mostrarMensajeExito(nombreZip);
            preguntarAbrirCarpeta();
            
        } catch (Exception e) {
            mostrarMensajeError("Error al crear respaldo comprimido", e);
        }
    }
    
    /**
     * Mostrar menú de opciones de respaldo
     */
    public static void mostrarMenuRespaldo() {
        String[] opciones = {
            "💾 Crear Respaldo Simple",
            "📦 Crear Respaldo Comprimido (ZIP)",
            "♻️ Restaurar Respaldo",
            "📁 Abrir Carpeta de Respaldos",
            "🗑️ Limpiar Respaldos Antiguos",
            "❌ Cancelar"
        };
        
        int seleccion = JOptionPane.showOptionDialog(null,
            "Seleccione una opción de respaldo:",
            "Sistema de Respaldo",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]);
        
        switch (seleccion) {
            case 0:
                crearRespaldo();
                break;
            case 1:
                crearRespaldoComprimido();
                break;
            case 2:
                restaurarRespaldo();
                break;
            case 3:
                abrirCarpetaRespaldos();
                break;
            case 4:
                limpiarRespaldosAntiguos();
                break;
            default:
                break;
        }
    }
    
    /**
     * Limpiar respaldos antiguos (más de 30 días)
     */
    private static void limpiarRespaldosAntiguos() {
        try {
            File carpeta = new File(getCarpetaRespaldos());
            
            if (!carpeta.exists()) {
                JOptionPane.showMessageDialog(null,
                    "No se encontraron respaldos para limpiar.",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            File[] archivos = carpeta.listFiles((dir, name) ->
                name.startsWith(PREFIJO_RESPALDO) && 
                (name.endsWith(EXTENSION_DB) || name.endsWith(EXTENSION_ZIP)));
            
            if (archivos == null || archivos.length == 0) {
                JOptionPane.showMessageDialog(null,
                    "No se encontraron respaldos para limpiar.",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            long tiempoActual = System.currentTimeMillis();
            long treintaDias = 30L * 24 * 60 * 60 * 1000;
            int eliminados = 0;
            
            for (File archivo : archivos) {
                if (tiempoActual - archivo.lastModified() > treintaDias) {
                    if (archivo.delete()) {
                        eliminados++;
                    }
                }
            }
            
            JOptionPane.showMessageDialog(null,
                "🗑️ Limpieza completada\n\n" +
                "Respaldos eliminados: " + eliminados,
                "Limpieza de Respaldos",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            mostrarMensajeError("Error al limpiar respaldos antiguos", e);
        }
    }
    
    /**
     * Validar que existe el archivo de base de datos
     */
    private static boolean validarArchivoBD(String rutaBD) {
        File archivoBD = new File(rutaBD);
        if (!archivoBD.exists()) {
            JOptionPane.showMessageDialog(null,
                "❌ Error: No se encontró el archivo de base de datos\n\n" +
                "Ruta: " + rutaBD,
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    /**
     * Crear carpeta de respaldos si no existe
     */
    private static void crearCarpetaRespaldos(String ruta) {
        File carpeta = new File(ruta);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }
    
    /**
     * Mostrar mensaje de éxito con información del respaldo
     */
    private static void mostrarMensajeExito(String nombreArchivo) {
        long tamanoBytes = new File(nombreArchivo).length();
        String tamano = formatearTamano(tamanoBytes);
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        
        JOptionPane.showMessageDialog(null,
            "✅ Respaldo creado exitosamente\n\n" +
            "📁 Archivo: " + new File(nombreArchivo).getName() + "\n" +
            "📊 Tamaño: " + tamano + "\n" +
            "🕐 Fecha: " + fecha,
            "Respaldo Completado",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Preguntar si desea abrir la carpeta de respaldos
     */
    private static void preguntarAbrirCarpeta() {
        int abrirCarpeta = JOptionPane.showConfirmDialog(null,
            "¿Desea abrir la carpeta de respaldos?",
            "Abrir carpeta",
            JOptionPane.YES_NO_OPTION);
        
        if (abrirCarpeta == JOptionPane.YES_OPTION) {
            abrirCarpetaRespaldos();
        }
    }
    
    /**
     * Abrir carpeta de respaldos en el explorador
     */
    public static void abrirCarpetaRespaldos() {
        try {
            File carpeta = new File(getCarpetaRespaldos());
            
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }
            
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(carpeta);
            }
        } catch (Exception e) {
            mostrarMensajeError("No se pudo abrir la carpeta", e);
        }
    }
    
    /**
     * Verificar si es necesario crear respaldo periódico
     */
    public static void verificarRespaldoPeriodico() {
        try {
            File carpeta = new File(getCarpetaRespaldos());
            
            // Primera vez - no hay carpeta
            if (!carpeta.exists()) {
                mostrarDialogoPrimerRespaldo();
                return;
            }
            
            // Buscar archivos de respaldo
            File[] archivos = carpeta.listFiles((dir, name) ->
                name.startsWith(PREFIJO_RESPALDO) && 
                (name.endsWith(EXTENSION_DB) || name.endsWith(EXTENSION_ZIP)));
            
            // No hay respaldos
            if (archivos == null || archivos.length == 0) {
                mostrarDialogoSinRespaldos();
                return;
            }
            
            // Verificar antigüedad del último respaldo
            long ultimoRespaldo = obtenerFechaUltimoRespaldo(archivos);
            long diasDesdeUltimo = calcularDiasDesdeRespaldo(ultimoRespaldo);
            
            if (diasDesdeUltimo >= DIAS_RECORDATORIO) {
                mostrarRecordatorioRespaldo(diasDesdeUltimo);
            }
            
        } catch (Exception e) {
            // No mostrar error para no interrumpir el inicio
            System.err.println("Error al verificar respaldos: " + e.getMessage());
        }
    }
    
    /**
     * Mostrar diálogo para crear primer respaldo
     */
    private static void mostrarDialogoPrimerRespaldo() {
        int crear = JOptionPane.showConfirmDialog(null,
            "🔐 Sistema de Respaldo\n\n" +
            "No se han encontrado respaldos previos.\n" +
            "¿Desea crear su primer respaldo ahora?\n\n" +
            "Esto protegerá toda su información.",
            "Crear Primer Respaldo",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE);
        
        if (crear == JOptionPane.YES_OPTION) {
            crearRespaldo();
        }
    }
    
    /**
     * Mostrar diálogo cuando no hay respaldos
     */
    private static void mostrarDialogoSinRespaldos() {
        int crear = JOptionPane.showConfirmDialog(null,
            "⚠️ No se han encontrado respaldos\n\n" +
            "Es muy importante hacer respaldos periódicos\n" +
            "para proteger su información.\n\n" +
            "¿Desea crear uno ahora?",
            "Crear Respaldo",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (crear == JOptionPane.YES_OPTION) {
            crearRespaldo();
        }
    }
    
    /**
     * Mostrar recordatorio de respaldo
     */
    private static void mostrarRecordatorioRespaldo(long dias) {
        int crear = JOptionPane.showConfirmDialog(null,
            "⚠️ RECORDATORIO DE RESPALDO ⚠️\n\n" +
            "Han pasado " + dias + " días desde el último respaldo.\n\n" +
            "Se recomienda crear un respaldo periódicamente\n" +
            "para proteger su información.\n\n" +
            "¿Desea crear un respaldo ahora?",
            "Recordatorio de Respaldo",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (crear == JOptionPane.YES_OPTION) {
            crearRespaldo();
        }
    }
    
    /**
     * Obtener fecha del último respaldo
     */
    private static long obtenerFechaUltimoRespaldo(File[] archivos) {
        long ultimoRespaldo = 0;
        for (File archivo : archivos) {
            if (archivo.lastModified() > ultimoRespaldo) {
                ultimoRespaldo = archivo.lastModified();
            }
        }
        return ultimoRespaldo;
    }
    
    /**
     * Calcular días desde el último respaldo
     */
    private static long calcularDiasDesdeRespaldo(long ultimoRespaldo) {
        return (System.currentTimeMillis() - ultimoRespaldo) / (1000 * 60 * 60 * 24);
    }
    
    /**
     * Mostrar mensaje de error
     */
    private static void mostrarMensajeError(String mensaje, Exception e) {
        JOptionPane.showMessageDialog(null,
            "❌ " + mensaje + "\n\n" + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    
    /**
     * Formatear tamaño de archivo a formato legible
     */
    private static String formatearTamano(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}