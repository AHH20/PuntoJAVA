/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Productos;


import com.bd.Conexion;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Aleci
 */
public class addServicios {
    
    public void agregarServicio(JTextField nombreServicio, JTextField costoServicio,
                                JTextField precioVenta, JComboBox comboProducto,
                                JTextField cantidadConsumo, boolean consumeProducto) {
        
        String consulta = "INSERT INTO Servicios(nombreServicio, costoServicio, precioVenta, " +
                         "idProductoConsumo, cantidadConsumo) VALUES(?,?,?,?,?)";
        
        try (Connection conn = Conexion.conectar();
             PreparedStatement ps = conn.prepareStatement(consulta)) {
            
            String nombre = nombreServicio.getText().trim();
            BigDecimal costo = costoServicio.getText().trim().isEmpty() ? 
                              BigDecimal.ZERO : new BigDecimal(costoServicio.getText().trim());
            BigDecimal precio = new BigDecimal(precioVenta.getText().trim());
            
            ps.setString(1, nombre);
            ps.setBigDecimal(2, costo);
            ps.setBigDecimal(3, precio);
            
            if (consumeProducto) {
                Object selectedItem = comboProducto.getSelectedItem();
                if (selectedItem instanceof itemProducto) {
                    itemProducto item = (itemProducto) selectedItem;
                    if (item.getId() > 0) {
                        ps.setInt(4, item.getId());
                        ps.setDouble(5, Double.parseDouble(cantidadConsumo.getText().trim()));
                    } else {
                        ps.setNull(4, java.sql.Types.INTEGER);
                        ps.setDouble(5, 0);
                    }
                } else {
                    ps.setNull(4, java.sql.Types.INTEGER);
                    ps.setDouble(5, 0);
                }
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
                ps.setDouble(5, 0);
            }
            
            ps.execute();
            JOptionPane.showMessageDialog(null, "Servicio agregado correctamente");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al agregar servicio: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Actualizar servicio
    public void actualizarServicio(int idServicio, JTextField nombreServicio, JTextField costoServicio,
                                   JTextField precioVenta, JComboBox comboProducto,
                                   JTextField cantidadConsumo, boolean consumeProducto) {
        
        String consulta = "UPDATE Servicios SET nombreServicio=?, costoServicio=?, precioVenta=?, " +
                         "idProductoConsumo=?, cantidadConsumo=? WHERE id=?";
        
        try (Connection conn = Conexion.conectar();
             PreparedStatement ps = conn.prepareStatement(consulta)) {
            
            String nombre = nombreServicio.getText().trim();
            BigDecimal costo = costoServicio.getText().trim().isEmpty() ? 
                              BigDecimal.ZERO : new BigDecimal(costoServicio.getText().trim());
            BigDecimal precio = new BigDecimal(precioVenta.getText().trim());
            
            ps.setString(1, nombre);
            ps.setBigDecimal(2, costo);
            ps.setBigDecimal(3, precio);
            
            if (consumeProducto) {
                Object selectedItem = comboProducto.getSelectedItem();
                if (selectedItem instanceof itemProducto) {
                    itemProducto item = (itemProducto) selectedItem;
                    if (item.getId() > 0) {
                        ps.setInt(4, item.getId());
                        ps.setDouble(5, Double.parseDouble(cantidadConsumo.getText().trim()));
                    } else {
                        ps.setNull(4, java.sql.Types.INTEGER);
                        ps.setDouble(5, 0);
                    }
                } else {
                    ps.setNull(4, java.sql.Types.INTEGER);
                    ps.setDouble(5, 0);
                }
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
                ps.setDouble(5, 0);
            }
            
            ps.setInt(6, idServicio);
            
            int filasActualizadas = ps.executeUpdate();
            
            if (filasActualizadas > 0) {
                JOptionPane.showMessageDialog(null, "Servicio actualizado correctamente");
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo actualizar el servicio");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Eliminar servicio
    public void eliminarServicio(int idServicio) {
        String consulta = "DELETE FROM Servicios WHERE id = ?";
        
        try (Connection conn = Conexion.conectar();
             PreparedStatement ps = conn.prepareStatement(consulta)) {
            
            ps.setInt(1, idServicio);
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Servicio eliminado correctamente");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el servicio");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar servicio: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Mostrar servicios en tabla
    public void mostrarServicios(JTable tablaServicios) {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5; // Editar y Eliminar
            }
        };
        
        modelo.addColumn("Servicio");
        modelo.addColumn("Precio");
        modelo.addColumn("Consume");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Editar");
        modelo.addColumn("Eliminar");
        
        // Columnas ocultas
        modelo.addColumn("id");
        modelo.addColumn("costoServicio");
        modelo.addColumn("idProductoConsumo");
        modelo.addColumn("cantidadConsumo");
        
        String consulta = 
            "SELECT s.*, p.nombreProducto " +
            "FROM Servicios s " +
            "LEFT JOIN Productos p ON s.idProductoConsumo = p.id";
        
        try (Connection conn = Conexion.conectar();
             PreparedStatement ps = conn.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String consumeProducto = rs.getObject("idProductoConsumo") != null ? 
                                        rs.getString("nombreProducto") : "No";
                
                double cantidadConsumo = rs.getDouble("cantidadConsumo");
                String cantidadStr = cantidadConsumo > 0 ? String.valueOf(cantidadConsumo) : "-";
                
                modelo.addRow(new Object[]{
                    rs.getString("nombreServicio"),
                    "$" + rs.getBigDecimal("precioVenta"),
                    consumeProducto,
                    cantidadStr,
                    "Editar",
                    "Eliminar",
                    // Ocultas
                    rs.getInt("id"),
                    rs.getBigDecimal("costoServicio"),
                    rs.getObject("idProductoConsumo"),
                    rs.getDouble("cantidadConsumo")
                });
            }
            
            tablaServicios.setModel(modelo);
            tablaServicios.setRowHeight(30);
            
            ocultarColumna(tablaServicios, 6);
            ocultarColumna(tablaServicios, 7);
            ocultarColumna(tablaServicios, 8);
            ocultarColumna(tablaServicios, 9);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar servicios: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void ocultarColumna(JTable tabla, int columna) {
        tabla.getColumnModel().getColumn(columna).setMinWidth(0);
        tabla.getColumnModel().getColumn(columna).setMaxWidth(0);
        tabla.getColumnModel().getColumn(columna).setWidth(0);
        tabla.getColumnModel().getColumn(columna).setPreferredWidth(0);
    }
    
}
