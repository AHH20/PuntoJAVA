package com.Ventas;

import com.bd.Conexion;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

public class CantidadEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    
    private JPanel panel;
    private JButton btnMenos;
    private JLabel lblCantidad;
    private JButton btnMas;
    private double cantidad;
    private int filaActual;
    private JTable table;
    private nuevaVenta ventanaVenta;
    private String unidadMedida = "unidad";
    private double incremento = 1.0;
    
    public CantidadEditor(nuevaVenta ventana) {
        this.ventanaVenta = ventana;
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 2));
        
        btnMenos = new JButton("-");
        btnMenos.setPreferredSize(new java.awt.Dimension(35, 25));
        btnMenos.setFocusPainted(false);
        btnMenos.addActionListener(this);
        
        lblCantidad = new JLabel("0");
        lblCantidad.setPreferredSize(new java.awt.Dimension(40, 25));
        lblCantidad.setHorizontalAlignment(JLabel.CENTER);
        lblCantidad.setFont(new java.awt.Font("Segoe UI", 1, 14));
        
        btnMas = new JButton("+");
        btnMas.setPreferredSize(new java.awt.Dimension(35, 25));
        btnMas.setFocusPainted(false);
        btnMas.addActionListener(this);
        
        panel.add(btnMenos);
        panel.add(lblCantidad);
        panel.add(btnMas);
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.table = table;
        this.filaActual = row;
        
        // Obtener unidad de medida del producto
        String nombreProducto = table.getValueAt(row, 0).toString();
        obtenerUnidadMedida(nombreProducto);
        
        // Convertir valor a double
        cantidad = value != null ? Double.parseDouble(value.toString()) : 0.0;
        actualizarLabel();
        
        return panel;
    }
    
    private void obtenerUnidadMedida(String nombreProducto) {
        try {
            Conexion conexion = new Conexion();
            String sql = "SELECT unidadMedida FROM Productos WHERE nombreProducto = ?";
            PreparedStatement ps = conexion.conectar().prepareStatement(sql);
            ps.setString(1, nombreProducto);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                unidadMedida = rs.getString("unidadMedida");
                if (unidadMedida == null) unidadMedida = "unidad";
                
                // Definir incremento según unidad
                if (unidadMedida.equalsIgnoreCase("metro") || 
                    unidadMedida.equalsIgnoreCase("kilogramo") || 
                    unidadMedida.equalsIgnoreCase("litro")) {
                    incremento = 0.5;
                } else {
                    incremento = 1.0;
                }
            }
            
            rs.close();
            ps.close();
        } catch (Exception e) {
            unidadMedida = "unidad";
            incremento = 1.0;
        }
    }
    
    @Override
    public Object getCellEditorValue() {
        return cantidad;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultTableModel modelo = (DefaultTableModel) table.getModel();
        
        if (filaActual < 0 || filaActual >= modelo.getRowCount()) {
            stopCellEditing();
            return;
        }
        
        if (e.getSource() == btnMenos) {
            if (cantidad > 0) {
                cantidad -= incremento;
                if (cantidad < 0) cantidad = 0;
                actualizarCantidad();
            }
        } else if (e.getSource() == btnMas) {
            String nombreProducto = modelo.getValueAt(filaActual, 0).toString();
            if (verificarStock(nombreProducto, cantidad + incremento)) {
                cantidad += incremento;
                actualizarCantidad();
            }
        }
    }
    
    private boolean verificarStock(String nombreProducto, double cantidadSolicitada) {
        try {
            Conexion conexion = new Conexion();
            String sql = "SELECT cantidad FROM Productos WHERE nombreProducto = ?";
            PreparedStatement ps = conexion.conectar().prepareStatement(sql);
            ps.setString(1, nombreProducto);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                double stockDisponible = rs.getDouble("cantidad");
                
                if (cantidadSolicitada > stockDisponible) {
                    JOptionPane.showMessageDialog(panel,
                        String.format("Stock insuficiente\nDisponible: %.2f %s", 
                                     stockDisponible, unidadMedida),
                        "Sin stock",
                        JOptionPane.WARNING_MESSAGE);
                    rs.close();
                    ps.close();
                    return false;
                }
            }
            
            rs.close();
            ps.close();
            return true;
            
        } catch (Exception ex) {
            System.err.println("Error al verificar stock: " + ex.getMessage());
            return false;
        }
    }
    
    private void actualizarCantidad() {
        try {
            actualizarLabel();
            
            DefaultTableModel modelo = (DefaultTableModel) table.getModel();
            
            if (filaActual >= 0 && filaActual < modelo.getRowCount() &&
                1 < modelo.getColumnCount()) {
                
                modelo.setValueAt(cantidad, filaActual, 1);
                
                javax.swing.SwingUtilities.invokeLater(() -> {
                    ventanaVenta.calcularTotal();
                });
            }
        } catch (Exception ex) {
            System.err.println("Error al actualizar cantidad: " + ex.getMessage());
            stopCellEditing();
        }
    }
    
    private void actualizarLabel() {
        if (cantidad == Math.floor(cantidad)) {
            lblCantidad.setText(String.valueOf((int)cantidad));
        } else {
            lblCantidad.setText(String.format("%.1f", cantidad));
        }
    }
}