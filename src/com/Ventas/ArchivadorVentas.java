
package com.Ventas;

import com.bd.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Clase para archivar ventas antiguas y mantener la base de datos optimizada
 * @author Aleci
 */
public class ArchivadorVentas {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(ArchivadorVentas.class.getName());
    
 
  
    public static void verificarYArchivar() {
        try {
            Conexion conexion = new Conexion();
            
            // Contar ventas totales
            String sqlCount = "SELECT COUNT(*) as total FROM Ventas";
            PreparedStatement ps = conexion.conectar().prepareStatement(sqlCount);
            ResultSet rs = ps.executeQuery();
            
            int totalVentas = 0;
            if (rs.next()) {
                totalVentas = rs.getInt("total");
            }
            
            rs.close();
            ps.close();
            
            // Si hay más de 5000 ventas, sugerir archivado
            if (totalVentas > 5000) {
                int opcion = JOptionPane.showConfirmDialog(null,
                    "Tienes " + totalVentas + " ventas registradas.\n" +
                    "¿Deseas archivar las ventas con más de 6 meses?\n" +
                    "Esto mejorará el rendimiento del sistema.",
                    "Archivar Ventas Antiguas",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (opcion == JOptionPane.YES_OPTION) {
                    archivarVentasAntiguas(6);
                }
            }
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al verificar ventas", e);
        }
    }
    
    /**
     * Archiva ventas con más de X meses a una tabla de archivo
     * @param mesesAntiguedad Meses de antigüedad para archivar
     */
    public static void archivarVentasAntiguas(int mesesAntiguedad) {
        try {
            Conexion conexion = new Conexion();
            Statement stmt = conexion.conectar().createStatement();
            
            // 1. Crear tabla de archivo si no existe
            String sqlCrearArchivo = 
                "CREATE TABLE IF NOT EXISTS VentasArchivadas(" +
                "id INTEGER PRIMARY KEY," +
                "idUsuario INTEGER," +
                "fechaVenta TIMESTAMP," +
                "totalVenta REAL," +
                "efectivo REAL," +
                "cambio REAL," +
                "metodoPago TEXT," +
                "estado TEXT," +
                "observaciones TEXT," +
                "fechaArchivado TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            
            stmt.execute(sqlCrearArchivo);
            
            String sqlCrearDetalleArchivo = 
                "CREATE TABLE IF NOT EXISTS DetalleVentasArchivadas(" +
                "id INTEGER PRIMARY KEY," +
                "idVenta INTEGER," +
                "idProducto INTEGER," +
                "nombreProducto VARCHAR(100)," +
                "cantidad INTEGER," +
                "precioUnitario REAL," +
                "subtotal REAL," +
                "fechaArchivado TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            
            stmt.execute(sqlCrearDetalleArchivo);
            
            // 2. Copiar ventas antiguas a tabla de archivo
            String sqlCopiarVentas = 
                "INSERT INTO VentasArchivadas " +
                "SELECT *, datetime('now') as fechaArchivado " +
                "FROM Ventas " +
                "WHERE fechaVenta < datetime('now', '-' || ? || ' months')";
            
            PreparedStatement psCopiar = conexion.conectar().prepareStatement(sqlCopiarVentas);
            psCopiar.setInt(1, mesesAntiguedad);
            int ventasArchivadas = psCopiar.executeUpdate();
            
            // 3. Copiar detalles de ventas archivadas
            String sqlCopiarDetalles = 
                "INSERT INTO DetalleVentasArchivadas " +
                "SELECT dv.*, datetime('now') as fechaArchivado " +
                "FROM DetalleVentas dv " +
                "INNER JOIN Ventas v ON dv.idVenta = v.id " +
                "WHERE v.fechaVenta < datetime('now', '-' || ? || ' months')";
            
            PreparedStatement psCopiarDetalles = conexion.conectar().prepareStatement(sqlCopiarDetalles);
            psCopiarDetalles.setInt(1, mesesAntiguedad);
            psCopiarDetalles.executeUpdate();
            
            // 4. Eliminar detalles de ventas antiguas
            String sqlEliminarDetalles = 
                "DELETE FROM DetalleVentas " +
                "WHERE idVenta IN (" +
                "  SELECT id FROM Ventas " +
                "  WHERE fechaVenta < datetime('now', '-' || ? || ' months'))";
            
            PreparedStatement psEliminarDetalles = conexion.conectar().prepareStatement(sqlEliminarDetalles);
            psEliminarDetalles.setInt(1, mesesAntiguedad);
            psEliminarDetalles.executeUpdate();
            
            // 5. Eliminar ventas antiguas
            String sqlEliminarVentas = 
                "DELETE FROM Ventas " +
                "WHERE fechaVenta < datetime('now', '-' || ? || ' months')";
            
            PreparedStatement psEliminar = conexion.conectar().prepareStatement(sqlEliminarVentas);
            psEliminar.setInt(1, mesesAntiguedad);
            psEliminar.executeUpdate();
            
            // 6. Optimizar base de datos
            stmt.execute("VACUUM");
            
            psCopiar.close();
            psCopiarDetalles.close();
            psEliminarDetalles.close();
            psEliminar.close();
            stmt.close();
            
            JOptionPane.showMessageDialog(null,
                "Se archivaron " + ventasArchivadas + " ventas exitosamente.\n" +
                "El sistema ahora funcionará más rápido.",
                "Archivado Exitoso",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al archivar ventas", e);
            JOptionPane.showMessageDialog(null,
                "Error al archivar ventas: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Exporta ventas archivadas a un archivo CSV para respaldo
     * @param rutaArchivo Ruta donde guardar el archivo CSV
     * @param mesesAntiguedad Meses de antigüedad a exportar
     */
    public static void exportarVentasArchivadas(String rutaArchivo, int mesesAntiguedad) {
        try {
            Conexion conexion = new Conexion();
            
            String sql = 
                "SELECT v.id, v.fechaVenta, v.totalVenta, v.efectivo, v.cambio, " +
                "       u.Usuario as cajero, " +
                "       dv.nombreProducto, dv.cantidad, dv.precioUnitario, dv.subtotal " +
                "FROM Ventas v " +
                "INNER JOIN Usuarios u ON v.idUsuario = u.id " +
                "INNER JOIN DetalleVentas dv ON v.id = dv.idVenta " +
                "WHERE v.fechaVenta < datetime('now', '-' || ? || ' months') " +
                "ORDER BY v.fechaVenta DESC";
            
            PreparedStatement ps = conexion.conectar().prepareStatement(sql);
            ps.setInt(1, mesesAntiguedad);
            ResultSet rs = ps.executeQuery();
            
            File archivo = new File(rutaArchivo);
            FileWriter writer = new FileWriter(archivo);
            
            
            writer.write("ID_Venta,Fecha,Total,Efectivo,Cambio,Cajero," +
                        "Producto,Cantidad,PrecioUnitario,Subtotal\n");
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            while (rs.next()) {
                writer.write(
                    rs.getInt("id") + "," +
                    sdf.format(rs.getTimestamp("fechaVenta")) + "," +
                    rs.getBigDecimal("totalVenta") + "," +
                    rs.getBigDecimal("efectivo") + "," +
                    rs.getBigDecimal("cambio") + "," +
                    rs.getString("cajero") + "," +
                    rs.getString("nombreProducto").replace(",", ";") + "," +
                    rs.getInt("cantidad") + "," +
                    rs.getBigDecimal("precioUnitario") + "," +
                    rs.getBigDecimal("subtotal") + "\n"
                );
            }
            
            writer.close();
            rs.close();
            ps.close();
            
            JOptionPane.showMessageDialog(null,
                "Ventas exportadas exitosamente a:\n" + rutaArchivo,
                "Exportación Exitosa",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException | IOException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al exportar ventas", e);
            JOptionPane.showMessageDialog(null,
                "Error al exportar ventas: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Obtiene estadísticas de ventas archivadas
     */
    public static String obtenerEstadisticasArchivo() {
        try {
            Conexion conexion = new Conexion();
            
            String sql = "SELECT COUNT(*) as total, " +
                        "MIN(fechaVenta) as primera, " +
                        "MAX(fechaVenta) as ultima " +
                        "FROM VentasArchivadas";
            
            PreparedStatement ps = conexion.conectar().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int total = rs.getInt("total");
                String primera = rs.getString("primera");
                String ultima = rs.getString("ultima");
                
                rs.close();
                ps.close();
                
                if (total > 0) {
                    return "Ventas archivadas: " + total + "\n" +
                           "Desde: " + primera + "\n" +
                           "Hasta: " + ultima;
                } else {
                    return "No hay ventas archivadas";
                }
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al obtener estadísticas", e);
        }
        
        return "Error al obtener estadísticas";
    }
    
    /**
     * Restaura ventas archivadas a la tabla principal
     * @param fechaInicio Fecha de inicio para restaurar
     * @param fechaFin Fecha de fin para restaurar
     */
    public static void restaurarVentasArchivadas(Date fechaInicio, Date fechaFin) {
        try {
            Conexion conexion = new Conexion();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            // Mover ventas de archivo a tabla principal
            String sqlRestaurar = 
                "INSERT INTO Ventas " +
                "SELECT id, idUsuario, fechaVenta, totalVenta, efectivo, cambio, " +
                "       metodoPago, estado, observaciones " +
                "FROM VentasArchivadas " +
                "WHERE fechaVenta BETWEEN ? AND ?";
            
            PreparedStatement ps = conexion.conectar().prepareStatement(sqlRestaurar);
            ps.setString(1, sdf.format(fechaInicio));
            ps.setString(2, sdf.format(fechaFin));
            int restauradas = ps.executeUpdate();
            
            // Restaurar detalles
            String sqlRestaurarDetalles = 
                "INSERT INTO DetalleVentas " +
                "SELECT id, idVenta, idProducto, nombreProducto, cantidad, " +
                "       precioUnitario, subtotal " +
                "FROM DetalleVentasArchivadas " +
                "WHERE idVenta IN (" +
                "  SELECT id FROM VentasArchivadas " +
                "  WHERE fechaVenta BETWEEN ? AND ?)";
            
            PreparedStatement psDetalles = conexion.conectar().prepareStatement(sqlRestaurarDetalles);
            psDetalles.setString(1, sdf.format(fechaInicio));
            psDetalles.setString(2, sdf.format(fechaFin));
            psDetalles.executeUpdate();
            
            // Eliminar de archivo
            String sqlEliminarArchivo = 
                "DELETE FROM VentasArchivadas " +
                "WHERE fechaVenta BETWEEN ? AND ?";
            
            PreparedStatement psEliminar = conexion.conectar().prepareStatement(sqlEliminarArchivo);
            psEliminar.setString(1, sdf.format(fechaInicio));
            psEliminar.setString(2, sdf.format(fechaFin));
            psEliminar.executeUpdate();
            
            String sqlEliminarDetallesArchivo = 
                "DELETE FROM DetalleVentasArchivadas " +
                "WHERE idVenta IN (" +
                "  SELECT id FROM VentasArchivadas " +
                "  WHERE fechaVenta BETWEEN ? AND ?)";
            
            PreparedStatement psEliminarDetalles = conexion.conectar().prepareStatement(sqlEliminarDetallesArchivo);
            psEliminarDetalles.setString(1, sdf.format(fechaInicio));
            psEliminarDetalles.setString(2, sdf.format(fechaFin));
            psEliminarDetalles.executeUpdate();
            
            ps.close();
            psDetalles.close();
            psEliminar.close();
            psEliminarDetalles.close();
            
            JOptionPane.showMessageDialog(null,
                "Se restauraron " + restauradas + " ventas exitosamente.",
                "Restauración Exitosa",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al restaurar ventas", e);
            JOptionPane.showMessageDialog(null,
                "Error al restaurar ventas: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
