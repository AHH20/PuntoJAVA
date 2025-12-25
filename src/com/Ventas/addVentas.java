/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Ventas;

import com.bd.Conexion;
import java.util.List;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class addVentas {
    
    public int guardarVenta(int idUsuario, List<itemVentas> productos, 
                           BigDecimal totalVenta, BigDecimal efectivo, BigDecimal cambio) {
        
        Conexion conexion = new Conexion();
        Connection conn = null;
        int idVentaGenerada = -1;
        
        try {
            conn = conexion.conectar();
            conn.setAutoCommit(false);
            
            // PASO 1: Insertar cabecera de venta
            String sqlVenta = "INSERT INTO Ventas(idUsuario, totalVenta, efectivo, cambio) VALUES(?, ?, ?, ?)";
            
            PreparedStatement psVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setInt(1, idUsuario);
            psVenta.setBigDecimal(2, totalVenta);
            psVenta.setBigDecimal(3, efectivo);
            psVenta.setBigDecimal(4, cambio);
            
            psVenta.executeUpdate();
            
            ResultSet rsKeys = psVenta.getGeneratedKeys();
            if (rsKeys.next()) {
                idVentaGenerada = rsKeys.getInt(1);
            }
            rsKeys.close();
            psVenta.close();
            
            if (idVentaGenerada == -1) {
                throw new SQLException("No se pudo obtener el ID de la venta");
            }
            
            // PASO 2: Insertar detalles y actualizar stock
            String sqlDetalle = "INSERT INTO DetalleVentas(idVenta, idProducto, nombreProducto, cantidad, precioUnitario, subtotal) VALUES(?, ?, ?, ?, ?, ?)";
            String sqlActualizarStock = "UPDATE Productos SET cantidad = cantidad - ? WHERE id = ?";
            String sqlMovimiento = "INSERT INTO MovimientosInventario(idProducto, idUsuario, tipoMovimiento, cantidadAnterior, cantidad, cantidadNueva, motivo, idVenta) VALUES(?, ?, 'salida', ?, ?, ?, 'venta', ?)";
            
            PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
            PreparedStatement psStock = conn.prepareStatement(sqlActualizarStock);
            PreparedStatement psMovimiento = conn.prepareStatement(sqlMovimiento);
            
            for (itemVentas item : productos) {
                // Obtener cantidad actual
                String sqlCantidadActual = "SELECT cantidad FROM Productos WHERE id = ?";
                PreparedStatement psConsulta = conn.prepareStatement(sqlCantidadActual);
                psConsulta.setInt(1, item.getIdProducto());
                ResultSet rs = psConsulta.executeQuery();
                
                int cantidadActual = 0;
                if (rs.next()) {
                    cantidadActual = rs.getInt("cantidad");
                }
                rs.close();
                psConsulta.close();
                
                // Validar stock
                if (cantidadActual < item.getCantidad()) {
                    throw new SQLException("Stock insuficiente para: " + item.getNombreProducto());
                }
                
                // Insertar detalle
                psDetalle.setInt(1, idVentaGenerada);
                psDetalle.setInt(2, item.getIdProducto());
                psDetalle.setString(3, item.getNombreProducto());
                psDetalle.setInt(4, item.getCantidad());
                psDetalle.setBigDecimal(5, item.getPrecioUnitario());
                psDetalle.setBigDecimal(6, item.getSubtotal());
                psDetalle.executeUpdate();
                
                // Actualizar stock
                psStock.setInt(1, item.getCantidad());
                psStock.setInt(2, item.getIdProducto());
                psStock.executeUpdate();
                
              
                int cantidadNueva = cantidadActual - item.getCantidad();
                psMovimiento.setInt(1, item.getIdProducto());
                psMovimiento.setInt(2, idUsuario);
                psMovimiento.setInt(3, cantidadActual);
                psMovimiento.setInt(4, -item.getCantidad());
                psMovimiento.setInt(5, cantidadNueva);
                psMovimiento.setInt(6, idVentaGenerada);
                psMovimiento.executeUpdate();
            }
            
            psDetalle.close();
            psStock.close();
            psMovimiento.close();
            
            // PASO 3: Commit
            conn.commit();
            
            System.out.println("✓ Venta guardada exitosamente. ID: " + idVentaGenerada);
            return idVentaGenerada;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    System.err.println("✗ Transacción revertida");
                }
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            
            System.err.println("Error al guardar venta: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al guardar la venta: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return -1;
            
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}
