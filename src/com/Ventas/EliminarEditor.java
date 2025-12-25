package com.Ventas;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

public class EliminarEditor extends AbstractCellEditor implements TableCellEditor {
    
    private JButton button;
    private int filaActual;
    private nuevaVenta ventanaVenta;
    
    public EliminarEditor(javax.swing.JCheckBox checkBox, nuevaVenta ventana) {
        this.ventanaVenta = ventana;
        this.button = new JButton("🗑️");
        this.button.setToolTipText("Eliminar");
        
        // ⭐ AGREGAR EL ACTION LISTENER AL BOTÓN
        this.button.addActionListener(e -> eliminarFila());
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.filaActual = row;
        return button;
    }
    
    @Override
    public Object getCellEditorValue() {
        return null;
    }
    
    private void eliminarFila() {
       
        fireEditingStopped();
        
      
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                JTable table = ventanaVenta.TablaCompra;
                DefaultTableModel modelo = (DefaultTableModel) table.getModel();
                
                // ⭐ VALIDAR QUE LA FILA EXISTE
                if (filaActual >= 0 && filaActual < modelo.getRowCount()) {
                    modelo.removeRow(filaActual);
                    ventanaVenta.calcularTotal();
                }
            } catch (Exception ex) {
                System.err.println("Error al eliminar fila: " + ex.getMessage());
            }
        });
    }
} 