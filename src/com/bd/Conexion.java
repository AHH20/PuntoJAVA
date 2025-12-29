package com.bd;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class Conexion {
    
    private static final String NOMBRE_BD = "punto_venta_dev.db";
    private static String rutaBD = null;
    private static Connection conexionActiva = null; // ✅ Mantener referencia
    

    /**
     * Obtener ruta de la base de datos
     */
    private static String obtenerRutaBD() {
        if (rutaBD != null) {
            return rutaBD;
        }
        
        // Opción 1: Mismo directorio que el JAR
        String directorioJar = System.getProperty("user.dir");
        File bd1 = new File(directorioJar, NOMBRE_BD);
        
        // Opción 2: Carpeta home del usuario
        String home = System.getProperty("user.home");
        File carpetaApp = new File(home, "PuntoVentaArcangelMiguel");
        File bd2 = new File(carpetaApp, NOMBRE_BD);
        
        // Verificar si existe en el directorio del JAR
        if (bd1.exists()) {
            rutaBD = bd1.getAbsolutePath();
            System.out.println("BD encontrada en: " + rutaBD);
            return rutaBD;
        }
        
        // Verificar si existe en la carpeta del usuario
        if (bd2.exists()) {
            rutaBD = bd2.getAbsolutePath();
            System.out.println("BD encontrada en: " + rutaBD);
            return rutaBD;
        }
        
        // Si no existe en ningún lado, crear en carpeta del usuario
        try {
            if (!carpetaApp.exists()) {
                carpetaApp.mkdirs();
            }
            rutaBD = bd2.getAbsolutePath();
            System.out.println("Nueva BD será creada en: " + rutaBD);
            return rutaBD;
        } catch (Exception e) {
            // Fallback: usar directorio actual
            rutaBD = bd1.getAbsolutePath();
            System.out.println("Usando directorio actual: " + rutaBD);
            return rutaBD;
        }
    }
    
    /**
     * Conectar a la base de datos
     */
    public static Connection conectar() {
        try {
            // Cargar el driver de SQLite explícitamente
            Class.forName("org.sqlite.JDBC");
            
            String url = "jdbc:sqlite:" + obtenerRutaBD();
            Connection db = DriverManager.getConnection(url);
            
            // Guardar referencia a la conexión activa
            conexionActiva = db;
            
            System.out.println("✅ Conexión establecida exitosamente");
            System.out.println("📁 Ubicación BD: " + obtenerRutaBD());
            
            return db;
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ ERROR: Driver SQLite no encontrado");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ ERROR SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Cerrar todas las conexiones activas (para respaldos)
     */
    public static void cerrarTodasLasConexiones() {
        try {
            // Cerrar conexión activa si existe
            if (conexionActiva != null && !conexionActiva.isClosed()) {
                conexionActiva.close();
                System.out.println("✅ Conexión activa cerrada");
            }
            
            // Forzar cierre de conexiones de SQLite
            DriverManager.getConnection("jdbc:sqlite:" + obtenerRutaBD()).close();
            
            // Forzar recolección de basura
            System.gc();
            
            // Esperar un momento
            Thread.sleep(500);
            
            System.out.println("✅ Todas las conexiones cerradas");
            
        } catch (Exception e) {
            System.out.println("⚠️ Advertencia al cerrar conexiones: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene la ruta donde está la base de datos
     */
    public static String getRutaBD() {
        return obtenerRutaBD();
    }
    
    /**
     * Obtiene el directorio donde está la base de datos
     */
    public static String getDirectorioBD() {
        File archivo = new File(obtenerRutaBD());
        return archivo.getParent();
    }
    
    /**
     * Verificar si la conexión está disponible
     */
    public static boolean verificarConexion() {
        try (Connection conn = conectar()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
    
   
    
    
}