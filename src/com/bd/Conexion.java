package com.bd;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class Conexion {
    
    private static final String NOMBRE_BD = "punto_venta_dev_final.db";
    private static String rutaBD = null;
    private static Connection conexionActiva = null; 
    

   
    private static String obtenerRutaBD() {
        if (rutaBD != null) {
            return rutaBD;
        }
        
    
        String directorioJar = System.getProperty("user.dir");
        File bd1 = new File(directorioJar, NOMBRE_BD);
        
       
        String home = System.getProperty("user.home");
        File carpetaApp = new File(home, "PuntoVentaArcangelMiguel");
        File bd2 = new File(carpetaApp, NOMBRE_BD);
        
        
        if (bd1.exists()) {
            rutaBD = bd1.getAbsolutePath();
            System.out.println("BD encontrada en: " + rutaBD);
            return rutaBD;
        }
        
       
        if (bd2.exists()) {
            rutaBD = bd2.getAbsolutePath();
            System.out.println("BD encontrada en: " + rutaBD);
            return rutaBD;
        }
        
        
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
    
 
    public static Connection conectar() {
        try {
            
            Class.forName("org.sqlite.JDBC");
            
            String url = "jdbc:sqlite:" + obtenerRutaBD();
            Connection db = DriverManager.getConnection(url);
            
          
            conexionActiva = db;
            
            System.out.println("Conexión establecida exitosamente");
            System.out.println("Ubicación BD: " + obtenerRutaBD());
            
            return db;
            
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: Driver SQLite no encontrado");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("ERROR SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    public static void cerrarTodasLasConexiones() {
        try {
 
            if (conexionActiva != null && !conexionActiva.isClosed()) {
                conexionActiva.close();
                System.out.println(" Conexión activa cerrada");
            }
            
    
            DriverManager.getConnection("jdbc:sqlite:" + obtenerRutaBD()).close();
            
           
            System.gc();
            
          
            Thread.sleep(500);
            
            System.out.println("Todas las conexiones cerradas");
            
        } catch (Exception e) {
            System.out.println("Advertencia al cerrar conexiones: " + e.getMessage());
        }
    }
    
    
    public static String getRutaBD() {
        return obtenerRutaBD();
    }
    
    
    public static String getDirectorioBD() {
        File archivo = new File(obtenerRutaBD());
        return archivo.getParent();
    }
    
    
    public static boolean verificarConexion() {
        try (Connection conn = conectar()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
    
   
    
    
}