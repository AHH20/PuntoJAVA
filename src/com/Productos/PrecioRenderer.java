/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Productos;

import java.awt.Component;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class PrecioRenderer extends DefaultTableCellRenderer{
    
    
    private final NumberFormat formato =
        NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        if (value instanceof BigDecimal) {
            value = formato.format(value);
        }

        return super.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, column);
    }
    
}
