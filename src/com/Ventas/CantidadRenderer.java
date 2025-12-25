
package com.Ventas;

import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class CantidadRenderer extends JPanel implements TableCellRenderer {
    
    
    private JButton btnMenos;
    private JLabel lblCantidad;
    private JButton btnMas;
    
    public CantidadRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 3, 2));
        
        btnMenos = new JButton("-");
        btnMenos.setPreferredSize(new java.awt.Dimension(35, 25));
        btnMenos.setFocusPainted(false);
        
        lblCantidad = new JLabel("0");
        lblCantidad.setPreferredSize(new java.awt.Dimension(30, 25));
        lblCantidad.setHorizontalAlignment(JLabel.CENTER);
        lblCantidad.setFont(new java.awt.Font("Segoe UI", 1, 14));
        
        btnMas = new JButton("+");
        btnMas.setPreferredSize(new java.awt.Dimension(35, 25));
        btnMas.setFocusPainted(false);
        
        add(btnMenos);
        add(lblCantidad);
        add(btnMas);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        lblCantidad.setText(value != null ? value.toString() : "0");
        return this;
    }
}
    
 

