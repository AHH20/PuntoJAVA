package com.Controlador;
import com.Dashboard.Dashboard;

import com.Inventario.ValordeInventario;
import com.Inventario.Inventario;
import com.Productos.GestionProductos;
import com.Ventas.Ventas;
import com.Ventas.nuevaVenta;



public class Navegation {
    
    public static Dashboard Dashboard;
    public static GestionProductos GestionProductos; // ⭐ ÚNICA variable
    public static nuevaVenta nuevaVenta;
    public static Ventas Ventas;
    public static Inventario Inventario;

   public static ValordeInventario ValordeInventario;
    
    public static void mostrarDashboard() {
        
        if (GestionProductos != null && GestionProductos.isVisible()) {
            GestionProductos.setVisible(false);
        }
        if (nuevaVenta != null && nuevaVenta.isVisible()) {
            nuevaVenta.setVisible(false);
        }
        if (Ventas != null && Ventas.isVisible()) {
            Ventas.setVisible(false);
        }
        if (Inventario != null && Inventario.isVisible()) {
            Inventario.setVisible(false);
        }
        
        // Crear o mostrar Dashboard
        if (Dashboard == null || !Dashboard.isDisplayable()) {
            Dashboard = new Dashboard();
        }else{
            Dashboard.ActualizarGraficas();
            Dashboard.ActualizarTarjetas();
        }
        Dashboard.setVisible(true);
        Dashboard.toFront();
    }
    
    public static void mostrarProductos() {
        // ⭐ OCULTAR TODAS LAS DEMÁS VENTANAS
        if (Dashboard != null && Dashboard.isVisible()) {
            Dashboard.setVisible(false);
        }
        if (nuevaVenta != null && nuevaVenta.isVisible()) {
            nuevaVenta.setVisible(false);
        }
        if (Ventas != null && Ventas.isVisible()) {
            Ventas.setVisible(false);
        }
        if (Inventario != null && Inventario.isVisible()) {
            Inventario.setVisible(false);
        }
        
       
        
        // Crear o mostrar GestionProductos
        if (GestionProductos == null || !GestionProductos.isDisplayable()) {
            GestionProductos = new GestionProductos();
        } else {
            // Si ya existe, actualizar los productos
            GestionProductos.MostrarProductos();
        }
        GestionProductos.setVisible(true);
        GestionProductos.toFront();
    }
    
    // ⭐ MÉTODO PARA ACTUALIZAR INVENTARIO
    public static void actualizarInventario() {
        if (GestionProductos != null && 
            GestionProductos.isDisplayable() && 
            GestionProductos.isVisible()) {
            GestionProductos.MostrarProductos();
        }
        
        if(Dashboard !=null && Dashboard.isDisplayable() && Dashboard.isVisible()){
            Dashboard.ActualizarGraficas();
            Dashboard.ActualizarTarjetas();
        }
    }
    
    public static void mostrarNuevaVenta() {
        // ⭐ OCULTAR TODAS LAS DEMÁS VENTANAS
        if (Dashboard != null && Dashboard.isVisible()) {
            Dashboard.setVisible(false);
        }
        if (GestionProductos != null && GestionProductos.isVisible()) {
            GestionProductos.setVisible(false);
        }
        if (Ventas != null && Ventas.isVisible()) {
            Ventas.setVisible(false);
        }
        if (Inventario != null && Inventario.isVisible()) {
            Inventario.setVisible(false);
        }
        
        if (nuevaVenta == null || !nuevaVenta.isDisplayable()) {
            nuevaVenta = new nuevaVenta();
        }
        
       
        nuevaVenta.setVisible(true);
        nuevaVenta.toFront();
    }
    
    public static void mostrarVentas() {
        // ⭐ OCULTAR TODAS LAS DEMÁS VENTANAS
        if (Dashboard != null && Dashboard.isVisible()) {
            Dashboard.setVisible(false);
        }
        if (GestionProductos != null && GestionProductos.isVisible()) {
            GestionProductos.setVisible(false);
        }
        if (nuevaVenta != null && nuevaVenta.isVisible()) {
            nuevaVenta.setVisible(false);
        }
        if (Inventario != null && Inventario.isVisible()) {
            Inventario.setVisible(false);
        }
        
        if (Ventas == null || !Ventas.isDisplayable()) {
            Ventas = new Ventas();
        }
        
        Ventas.setVisible(true);
        Ventas.toFront();
    }
    
    public static void mostrarInventario() {
        // ⭐ OCULTAR TODAS LAS DEMÁS VENTANAS
        if (Dashboard != null && Dashboard.isVisible()) {
            Dashboard.setVisible(false);
        }
        if (GestionProductos != null && GestionProductos.isVisible()) {
            GestionProductos.setVisible(false);
        }
        if (nuevaVenta != null && nuevaVenta.isVisible()) {
            nuevaVenta.setVisible(false);
        }
        if (Ventas != null && Ventas.isVisible()) {
            Ventas.setVisible(false);
        }
        

        
        if (Inventario == null || !Inventario.isDisplayable()) {
            Inventario = new Inventario();
        }
       
        Inventario.setVisible(true);
        Inventario.toFront();
    }
    
    
    public static void mostrarValorInventario() {
    // Ocultar todas las ventanas
    if (Dashboard != null && Dashboard.isVisible()) {
        Dashboard.setVisible(false);
    }
    if (GestionProductos != null && GestionProductos.isVisible()) {
        GestionProductos.setVisible(false);
    }
    if (nuevaVenta != null && nuevaVenta.isVisible()) {
        nuevaVenta.setVisible(false);
    }
    if (Ventas != null && Ventas.isVisible()) {
        Ventas.setVisible(false);
    }
    if (Inventario != null && Inventario.isVisible()) {
        Inventario.setVisible(false);
    }
    
    
    // Crear o mostrar ValordeInventario
    if (ValordeInventario == null || !ValordeInventario.isDisplayable()) {
        ValordeInventario = new ValordeInventario();
    }
    
    ValordeInventario.setVisible(true);
    ValordeInventario.toFront();
    
    }
    
    
    public static void mostrarEntradasSalidas() {
    // Ocultar todas las ventanas
    if (Dashboard != null && Dashboard.isVisible()) {
        Dashboard.setVisible(false);
    }
    if (GestionProductos != null && GestionProductos.isVisible()) {
        GestionProductos.setVisible(false);
    }
    if (nuevaVenta != null && nuevaVenta.isVisible()) {
        nuevaVenta.setVisible(false);
    }
    if (Ventas != null && Ventas.isVisible()) {
        Ventas.setVisible(false);
    }
    if (Inventario != null && Inventario.isVisible()) {
        Inventario.setVisible(false);
    }
    if (ValordeInventario != null && ValordeInventario.isVisible()) {
        ValordeInventario.setVisible(false);
    }
    
    
    

}
    
    
    
    
    
    public static void cerrarTodo() {
        if (Dashboard != null && Dashboard.isDisplayable()) {
            Dashboard.dispose();
        }
        if (GestionProductos != null && GestionProductos.isDisplayable()) {
            GestionProductos.dispose();
        }
        if (nuevaVenta != null && nuevaVenta.isDisplayable()) {
            nuevaVenta.dispose();
        }
        if (Ventas != null && Ventas.isDisplayable()) {
            Ventas.dispose();
        }
        if (Inventario != null && Inventario.isDisplayable()) {
            Inventario.dispose();
        }
     
        
        Dashboard = null;
        GestionProductos = null;
        nuevaVenta = null;
        Ventas = null;
        Inventario = null;
  
    }
}