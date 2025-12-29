package com.Productos;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import com.Productos.NewJDialog;
import com.Productos.addProducto;


public class ButtonEditar extends AbstractCellEditor implements TableCellEditor, ActionListener {
    
    private JCheckBox button;
    private String accion;
    private JTable table;
    private addProducto productoController; 
    
   
    public ButtonEditar(JCheckBox checkBox, String accion, addProducto productoController) {
        this.button = checkBox;
        this.accion = accion;
        this.productoController = productoController;
        this.button.addActionListener(this);
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,boolean isSelected, int row, int column) {
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
        
        int filaVisita = table.getSelectedRow();
        if(filaVisita == -1) return;
        
        int filaModelo = table.convertRowIndexToModel(filaVisita);
        
        String Producto = table.getModel().getValueAt(filaModelo, 0).toString();
        
        if (accion.equals("editar")) {
           
            String nombre = table.getModel().getValueAt(filaModelo, 0).toString();
            String categoriaNombre = table.getModel().getValueAt(filaModelo, 1).toString();     
            String precioVenta = table.getModel().getValueAt(filaModelo, 2).toString();         
            double cantidad = Double.parseDouble(table.getModel().getValueAt(filaModelo, 11).toString());
            
            int id = Integer.parseInt(table.getModel().getValueAt(filaModelo, 6).toString());           
            String codigoBarras = table.getModel().getValueAt(filaModelo, 7).toString();                
            int idCategoria = Integer.parseInt(table.getModel().getValueAt(filaModelo, 8).toString()); 
            String precioCompra = table.getModel().getValueAt(filaModelo, 9).toString();
            String unidadMedida = table.getModel().getValueAt(filaModelo, 10).toString();
            
            java.awt.Frame frame = (java.awt.Frame) SwingUtilities.getWindowAncestor(table);
            
            NewJDialog dialogo = new NewJDialog(
                frame,
                true,
                id, 
                codigoBarras,
                nombre, 
                idCategoria, 
                precioCompra, 
                precioVenta, 
                (int) cantidad,
                unidadMedida
            );
            dialogo.setVisible(true);
         
        } else if (accion.equals("eliminar")) {
            int id = Integer.parseInt(table.getModel().getValueAt(filaModelo, 6).toString());
            String nombre = table.getModel().getValueAt(filaModelo, 0).toString();
            
            int confirmacion = JOptionPane.showConfirmDialog(
                null, 
                "¿Estás seguro de eliminar el producto: " + nombre + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                productoController.eliminarProducto(id);
                
                java.awt.Frame frame = (java.awt.Frame) SwingUtilities.getWindowAncestor(table);
                if (frame instanceof com.Productos.GestionProductos) {
                    ((com.Productos.GestionProductos) frame).MostrarProductos();
                }
            }
        }
    }
}