/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Ventas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Aleci
 */
public class VerTicketRenderer extends JButton implements TableCellRenderer {
    
      public VerTicketRenderer() {
        setOpaque(true);
        setBackground(new Color(0, 191, 255));
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        setText((value == null) ? "Ver" : value.toString());
        
        if (isSelected) {
            setBackground(new Color(0, 150, 200));
        } else {
            setBackground(new Color(0, 191, 255));
        }
        
        return this;
    }
    
}
