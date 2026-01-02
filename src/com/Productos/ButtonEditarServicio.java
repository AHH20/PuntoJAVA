/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Productos;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

/**
 *
 * @author Aleci
 */
public class ButtonEditarServicio extends AbstractCellEditor implements TableCellEditor, ActionListener  {
    
        private JCheckBox button;
    private String accion;
    private JTable table;
    private GestionServicios parent;
    
    public ButtonEditarServicio(JCheckBox checkBox, String accion, GestionServicios parent) {
        this.button = checkBox;
        this.accion = accion;
        this.parent = parent;
        this.button.addActionListener(this);
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        return button;
    }
    
    @Override
    public Object getCellEditorValue() {
        return null;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        fireEditingStopped();
        
        int filaVista = table.getSelectedRow();
        if (filaVista == -1) return;
        
        int filaModelo = table.convertRowIndexToModel(filaVista);
        
        if (accion.equals("editar")) {
            // Obtener datos de la fila
            int id = Integer.parseInt(table.getModel().getValueAt(filaModelo, 6).toString());
            String nombreServicio = table.getModel().getValueAt(filaModelo, 0).toString();
            BigDecimal costoServicio = new BigDecimal(table.getModel().getValueAt(filaModelo, 7).toString());
            
            String precioVentaStr = table.getModel().getValueAt(filaModelo, 1).toString().replace("$", "").trim();
            BigDecimal precioVenta = new BigDecimal(precioVentaStr);
            
            Object idProductoConsumoObj = table.getModel().getValueAt(filaModelo, 8);
            Integer idProductoConsumo = (idProductoConsumoObj != null && !idProductoConsumoObj.toString().isEmpty()) 
                                       ? Integer.parseInt(idProductoConsumoObj.toString()) 
                                       : null;
            
            double cantidadConsumo = Double.parseDouble(table.getModel().getValueAt(filaModelo, 9).toString());
            
            // Abrir diálogo de edición
            java.awt.Frame frame = (java.awt.Frame) SwingUtilities.getWindowAncestor(table);
            
            NewServicioDialog dialogo = new NewServicioDialog(
                frame,
                true,
                id,
                nombreServicio,
                costoServicio,
                precioVenta,
                idProductoConsumo,
                cantidadConsumo
            );
            dialogo.setVisible(true);
            
        } else if (accion.equals("eliminar")) {
            int id = Integer.parseInt(table.getModel().getValueAt(filaModelo, 6).toString());
            String nombreServicio = table.getModel().getValueAt(filaModelo, 0).toString();
            
            int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Estás seguro de eliminar el servicio: " + nombreServicio + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                addServicios servicioController = new addServicios();
                servicioController.eliminarServicio(id);
                
                if (parent != null) {
                    parent.mostrarServicios();
                }
            }
        }
    }
    
}
