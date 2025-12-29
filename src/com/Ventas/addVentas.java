package com.Ventas;

import com.bd.Conexion;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import javax.swing.JOptionPane;

public class addVentas {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(addVentas.class.getName());
    
    public int guardarVenta(int idUsuario, List<itemVentas> productos,
                           BigDecimal total, BigDecimal efectivo, BigDecimal cambio) {
        
        Conexion conexion = new Conexion();
        Connection conn = null;
        int idVenta = -1;
        
        try {
            conn = conexion.conectar();
            conn.setAutoCommit(false);
            
            // ✅ CORREGIDO: totalVenta y fechaVenta (SQLite)
            String sqlVenta = "INSERT INTO Ventas (idUsuario, totalVenta, efectivo, cambio, fechaVenta) " +
                            "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
            PreparedStatement psVenta = conn.prepareStatement(sqlVenta, 
                                        Statement.RETURN_GENERATED_KEYS);
            
            psVenta.setInt(1, idUsuario);
            psVenta.setBigDecimal(2, total);
            psVenta.setBigDecimal(3, efectivo);
            psVenta.setBigDecimal(4, cambio);
            
            int filasAfectadas = psVenta.executeUpdate();
            
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo crear la venta");
            }
            
            ResultSet generatedKeys = psVenta.getGeneratedKeys();
            if (generatedKeys.next()) {
                idVenta = generatedKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el ID de la venta");
            }
            
            psVenta.close();
            generatedKeys.close();
            
            // ✅ CORREGIDO: Agregar nombreProducto
            String sqlDetalle = "INSERT INTO DetalleVentas " +
                              "(idVenta, idProducto, nombreProducto, cantidad, precioUnitario, subtotal) " +
                              "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
            
            String sqlActualizarStock = "UPDATE Productos SET cantidad = cantidad - ? " +
                                       "WHERE id = ?";
            PreparedStatement psStock = conn.prepareStatement(sqlActualizarStock);
            
            for (itemVentas item : productos) {
                psDetalle.setInt(1, idVenta);
                psDetalle.setInt(2, item.getIdProducto());
                psDetalle.setString(3, item.getNombreProducto());
                psDetalle.setDouble(4, item.getCantidad());
                psDetalle.setBigDecimal(5, item.getPrecioUnitario());
                psDetalle.setBigDecimal(6, item.getSubtotal());
                psDetalle.executeUpdate();
                
                psStock.setDouble(1, item.getCantidad());
                psStock.setInt(2, item.getIdProducto());
                int stockActualizado = psStock.executeUpdate();
                
                if (stockActualizado == 0) {
                    throw new SQLException("No se pudo actualizar el stock del producto ID: " 
                                         + item.getIdProducto());
                }
                
                String sqlVerificar = "SELECT cantidad FROM Productos WHERE id = ?";
                PreparedStatement psVerificar = conn.prepareStatement(sqlVerificar);
                psVerificar.setInt(1, item.getIdProducto());
                ResultSet rsVerificar = psVerificar.executeQuery();
                
                if (rsVerificar.next()) {
                    double stockRestante = rsVerificar.getDouble("cantidad");
                    if (stockRestante < 0) {
                        throw new SQLException(
                            String.format("Stock insuficiente para %s. Stock actual: %.2f",
                                        item.getNombreProducto(), stockRestante + item.getCantidad())
                        );
                    }
                }
                
                rsVerificar.close();
                psVerificar.close();
            }
            
            psDetalle.close();
            psStock.close();
            
            conn.commit();
            
            logger.info(String.format("Venta #%d guardada exitosamente", idVenta));
            return idVenta;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.severe("Transacción revertida debido a error: " + e.getMessage());
                } catch (SQLException ex) {
                    logger.severe("Error al hacer rollback: " + ex.getMessage());
                }
            }
            
            JOptionPane.showMessageDialog(null,
                "Error al guardar la venta:\n" + e.getMessage(),
                "Error de Base de Datos",
                JOptionPane.ERROR_MESSAGE);
            
            e.printStackTrace();
            return -1;
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.severe("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }
    
    public boolean verificarStockDisponible(List<itemVentas> productos) {
        Conexion conexion = new Conexion();
        
        try {
            String sql = "SELECT cantidad FROM Productos WHERE id = ?";
            PreparedStatement ps = conexion.conectar().prepareStatement(sql);
            
            for (itemVentas item : productos) {
                ps.setInt(1, item.getIdProducto());
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    double stockDisponible = rs.getDouble("cantidad");
                    
                    if (item.getCantidad() > stockDisponible) {
                        JOptionPane.showMessageDialog(null,
                            String.format("Stock insuficiente para: %s\n" +
                                        "Solicitado: %.2f\n" +
                                        "Disponible: %.2f",
                                        item.getNombreProducto(),
                                        item.getCantidad(),
                                        stockDisponible),
                            "Stock Insuficiente",
                            JOptionPane.WARNING_MESSAGE);
                        rs.close();
                        ps.close();
                        return false;
                    }
                }
                rs.close();
            }
            
            ps.close();
            return true;
            
        } catch (SQLException e) {
            logger.severe("Error al verificar stock: " + e.getMessage());
            return false;
        }
    }
}