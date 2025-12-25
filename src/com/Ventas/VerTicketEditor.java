/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Ventas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

/**
 *
 * @author Aleci
 */
public class VerTicketEditor extends DefaultCellEditor {
    
    private JButton button;
    private int idVenta;
    private boolean clicked;
    private Ventas ventanaVentas;
    
    public VerTicketEditor(JCheckBox checkBox, Ventas ventanaVentas) {
        super(checkBox);
        this.ventanaVentas = ventanaVentas;
        
        button = new JButton();
        button.setOpaque(true);
        button.setBackground(new Color(0, 191, 255));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        
        idVenta = Integer.parseInt(table.getValueAt(row, 0).toString());
        button.setText((value == null) ? "Ver" : value.toString());
        clicked = true;
        return button;
    }
    
    @Override
    public Object getCellEditorValue() {
        if (clicked) {
            // Mostrar el detalle de la venta
            ventanaVentas.mostrarDetalleVenta(idVenta);
        }
        clicked = false;
        return "Ver";
    }
    
    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }
    
}
