package com.Ventas;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class EliminarRenderer extends JLabel implements TableCellRenderer {
    
    public EliminarRenderer(String path) {
        try {
            setIcon(new ImageIcon(getClass().getResource(path)));
        } catch (Exception e) {
            setText("🗑️");
        }
        setHorizontalAlignment(CENTER);
        setToolTipText("Eliminar producto");
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}