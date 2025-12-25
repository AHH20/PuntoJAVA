
package com.Productos;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class ButtonRenderer extends JLabel implements TableCellRenderer {
    
    public ButtonRenderer(String path){
       setIcon(new ImageIcon(getClass().getResource(path)));
       setHorizontalAlignment(CENTER);
    }
    
      @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
    
    
}
