
package modeloDesign;

import Components.PanelGradient;
import Components.PanelRound;
import com.Dashboard.Dashboard;
import com.Productos.GestionProductos;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Animate {
    
   
    private static final Color COLOR_ACTIVO_BG = new Color(255, 250, 250);
    private static final Color COLOR_ACTIVO_FG = new Color(0, 191, 255);
    private static final Color COLOR_INACTIVO_BG = new Color(0, 191, 255);
    private static final Color COLOR_INACTIVO_FG = new Color(255, 250, 250);
    
   
    private static final String ICON_DASHBOARD = "/com/Imagenes/casa_1.png";
    private static final String ICON_NUEVA_VENTA = "/com/Imagenes/carrito-de-compras.png";
    private static final String ICON_VENTAS = "/com/Imagenes/carro.png";
    private static final String ICON_PRODUCTOS = "/com/Imagenes/bienes.png";
    private static final String ICON_INVENTARIO = "/com/Imagenes/lista-de-verificacion.png";
    
    
    private void setMenuActivo(PanelRound panel, JLabel label, String iconPath) {
        if (panel != null && label != null) {
            panel.setBackground(COLOR_ACTIVO_BG);
            label.setForeground(COLOR_ACTIVO_FG);
            label.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        }
    }
    
   private void setMenuInactivo(PanelRound panel, JLabel label, String iconPath) {
        if (panel != null && label != null) {
            panel.setBackground(COLOR_INACTIVO_BG);
            label.setForeground(COLOR_INACTIVO_FG);
            label.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        }
    }
    
  
    public void actualizarMenus(
            PanelRound menuDashboard, JLabel lblDashboard,
            PanelRound menuNuevaVenta, JLabel lblNuevaVenta,
            PanelRound menuVentas, JLabel lblVentas,
            PanelRound menuProductos, JLabel lblProductos,
            PanelRound menuInventario, JLabel lblInventario,
            SeccionActiva seccionActiva) {
        
       
        setMenuInactivo(menuDashboard, lblDashboard, ICON_DASHBOARD);
        setMenuInactivo(menuNuevaVenta, lblNuevaVenta, ICON_NUEVA_VENTA);
        setMenuInactivo(menuVentas, lblVentas, ICON_VENTAS);
        setMenuInactivo(menuProductos, lblProductos, ICON_PRODUCTOS);
        setMenuInactivo(menuInventario, lblInventario, ICON_INVENTARIO);
        
       
        switch (seccionActiva) {
            case DASHBOARD:
                setMenuActivo(menuDashboard, lblDashboard, ICON_DASHBOARD);
                break;
            case NUEVA_VENTA:
                setMenuActivo(menuNuevaVenta, lblNuevaVenta, ICON_NUEVA_VENTA);
                break;
            case VENTAS:
                setMenuActivo(menuVentas, lblVentas, ICON_VENTAS);
                break;
            case PRODUCTOS:
                setMenuActivo(menuProductos, lblProductos, ICON_PRODUCTOS);
                break;
            case INVENTARIO:
                setMenuActivo(menuInventario, lblInventario, ICON_INVENTARIO);
                break;
                
           
                
        }
    }
    
   
    public enum SeccionActiva {
        DASHBOARD,
        NUEVA_VENTA,
        VENTAS,
        PRODUCTOS,
        INVENTARIO,
        VALOR
    }
    
    
    public void activarDashboard(PanelRound[] menus, JLabel[] labels) {
        actualizarMenus(menus[0], labels[0], menus[1], labels[1], 
                       menus[2], labels[2], menus[3], labels[3], 
                       menus[4], labels[4], SeccionActiva.DASHBOARD);
    }
    
    public void activarNuevaVenta(PanelRound[] menus, JLabel[] labels) {
        actualizarMenus(menus[0], labels[0], menus[1], labels[1], 
                       menus[2], labels[2], menus[3], labels[3], 
                       menus[4], labels[4], SeccionActiva.NUEVA_VENTA);
    }
    
    public void activarVentas(PanelRound[] menus, JLabel[] labels) {
        actualizarMenus(menus[0], labels[0], menus[1], labels[1], 
                       menus[2], labels[2], menus[3], labels[3], 
                       menus[4], labels[4], SeccionActiva.VENTAS);
    }
    
    public void activarProductos(PanelRound[] menus, JLabel[] labels) {
        actualizarMenus(menus[0], labels[0], menus[1], labels[1], 
                       menus[2], labels[2], menus[3], labels[3], 
                       menus[4], labels[4], SeccionActiva.PRODUCTOS);
    }
    
    public void activarInventario(PanelRound[] menus, JLabel[] labels) {
        actualizarMenus(menus[0], labels[0], menus[1], labels[1], 
                       menus[2], labels[2], menus[3], labels[3], 
                       menus[4], labels[4], SeccionActiva.INVENTARIO);   
    
    
    }
    
    
  
    
    public void activarValor(PanelRound[] menus, JLabel[] labels) {
        actualizarMenus(menus[0], labels[0], menus[1], labels[1], 
                       menus[2], labels[2], menus[3], labels[3], 
                       menus[4], labels[4], SeccionActiva.VALOR); 
    
    }
    
}
    

