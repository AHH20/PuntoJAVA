/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.Ventas;

import Components.PanelRound;
import com.Controlador.Navegation;
import com.Dashboard.Ajustes;
import com.Productos.GestionProductos;
import static com.Productos.GestionProductos.Menu;
import static com.Productos.GestionProductos.Menu1;
import static com.Productos.GestionProductos.Menu2;
import static com.Productos.GestionProductos.Menu3;
import static com.Productos.GestionProductos.Menu4;
import static com.Productos.GestionProductos.lbl_ini;
import static com.Productos.GestionProductos.lbl_ini1;
import static com.Productos.GestionProductos.lbl_ini2;
import static com.Productos.GestionProductos.lbl_ini3;
import static com.Productos.GestionProductos.lbl_ini4;
import com.login.Login;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import com.bd.Conexion;
import java.math.BigDecimal;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import modeloDesign.Animate;



/**
 *
 * @author Aleci
 */
public class nuevaVenta extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(nuevaVenta.class.getName());
   
    Animate ColorOF =  new Animate();
    
    private DefaultTableModel modeloTabla;
    private BigDecimal totalPagar = BigDecimal.ZERO;

    
    
    private class ProductoSeleccionado {
        int id;
        String nombre;
        BigDecimal precio;
        int stockDisponible;
        
        ProductoSeleccionado(int id, String nombre, BigDecimal precio, int stock) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
            this.stockDisponible = stock;
        }
    }
    
    public nuevaVenta() {
       initComponents();
       setTitle("Ventas");
       setSize(1300,830);
       setLocationRelativeTo(null);
       setResizable(false);
       setDefaultCloseOperation(EXIT_ON_CLOSE);
       setVisible(true);
       Saludo.setText(Login.SesionUsuario.NombreUsuario);
       iniciarMenu();
       TamanoImagen();
       actualizarEstiloMenu();
        configurarTabla();
        configurarEventos();
        
    }
    
     private void actualizarEstiloMenu() {
        PanelRound[] menus = {Menu, Menu1, Menu2, Menu3, Menu4};
        JLabel[] labels = {lbl_ini, lbl_ini1, lbl_ini2, lbl_ini3, lbl_ini4};
        ColorOF.activarNuevaVenta(menus, labels);
    }
     
     
    public JTable getTablaCompra() {
    return TablaCompra;
}
    
    
    private void configurarTabla() {
        String[] columnas = {"Producto", "Cantidad", "Precio", "Operaciones"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 3; 
            }
        };
        
        TablaCompra.setModel(modeloTabla);
        
        // ⭐ CORREGIDO - Índices correctos (0, 1, 2, 3)
        TablaCompra.getColumnModel().getColumn(1).setCellRenderer(new CantidadRenderer());
        TablaCompra.getColumnModel().getColumn(1).setCellEditor(new CantidadEditor(this));
        
        TablaCompra.getColumnModel().getColumn(3).setCellRenderer(
            new EliminarRenderer("/com/Imagenes/eliminar.png"));
        TablaCompra.getColumnModel().getColumn(3).setCellEditor(
            new EliminarEditor(new javax.swing.JCheckBox(), this));
        
        // ⭐ CORREGIDO - Solo 4 columnas (0, 1, 2, 3)
        TablaCompra.getColumnModel().getColumn(0).setPreferredWidth(200); // Producto
        TablaCompra.getColumnModel().getColumn(1).setPreferredWidth(120); // Cantidad
        TablaCompra.getColumnModel().getColumn(2).setPreferredWidth(80);  // Precio
        TablaCompra.getColumnModel().getColumn(3).setPreferredWidth(80);  // Operaciones
        
        TablaCompra.setRowHeight(35);
        
        txtTotalPagar.setText("$0.00");
        txtCambio.setText("$0.00");
    }
    
    
    private void configurarEventos() {
        // Búsqueda en tiempo real
        jTextField2.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { buscarProducto(); }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { buscarProducto(); }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { buscarProducto(); }
        });
        
        // Calcular cambio en tiempo real
        JTextEfectivo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calcularCambio();
            }
        });
    }
     
   private void buscarProducto() {
      String busqueda = jTextField2.getText().trim().toLowerCase();
    
    Conexion conexion = new Conexion();
    
    try {
        // Si la búsqueda está vacía, limpiar productos sin cantidad
        if (busqueda.isEmpty()) {
            limpiarProductosSinCantidad();
            return;
        }
        
        // Buscar todos los productos que coincidan
        String sql = "SELECT id, nombreProducto, precioVenta, cantidad " +
                    "FROM Productos WHERE LOWER(nombreProducto) LIKE ? OR codigoBarras = ?";
        PreparedStatement ps = conexion.conectar().prepareStatement(sql);
        ps.setString(1, "%" + busqueda + "%");
        ps.setString(2, busqueda);
        
        ResultSet rs = ps.executeQuery();
        
        // Lista de productos encontrados
        java.util.List<String> productosEncontrados = new java.util.ArrayList<>();
        
        while (rs.next()) {
            String nombre = rs.getString("nombreProducto");
            BigDecimal precioVenta = rs.getBigDecimal("precioVenta");
            int stock = rs.getInt("cantidad");
            
            productosEncontrados.add(nombre);
            
            if (stock > 0) {
                // Verificar si ya está en la tabla
                boolean encontrado = false;
                int cantidadActual = 0;
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    if (modeloTabla.getValueAt(i, 0).equals(nombre)) {
                        encontrado = true;
                        cantidadActual = Integer.parseInt(modeloTabla.getValueAt(i,1).toString());
                        break;
                    }
                }
                
                // Si NO está, agregarlo con cantidad 0
                if (!encontrado) {
                    Object[] fila = {
                        nombre,
                        0,
                        precioVenta.setScale(2, BigDecimal.ROUND_HALF_UP),
                        "Eliminar"
                    };
                    modeloTabla.addRow(fila);
                }
            }
        }
        
        // Eliminar productos que NO coinciden con la búsqueda y tienen cantidad 0
        for (int i = modeloTabla.getRowCount() - 1; i >= 0; i--) {
            String nombreEnTabla = modeloTabla.getValueAt(i, 0).toString();
            int cantidad = Integer.parseInt(modeloTabla.getValueAt(i, 1).toString());
            
            // Si el producto no está en los encontrados Y tiene cantidad 0, eliminarlo
            if (!productosEncontrados.contains(nombreEnTabla) && cantidad == 0) {
                modeloTabla.removeRow(i);
            }
        }
        
        rs.close();
        ps.close();
        
    } catch (SQLException e) {
        logger.log(java.util.logging.Level.SEVERE, "Error al buscar producto", e);
    }
    }
   
   
   
   private void limpiarProductosSinCantidad() {
    for (int i = modeloTabla.getRowCount() - 1; i >= 0; i--) {
        int cantidad = Integer.parseInt(modeloTabla.getValueAt(i, 1).toString());
        if (cantidad == 0) {
            modeloTabla.removeRow(i);
        }
    }
}
     
     public void calcularTotal() {
        totalPagar = BigDecimal.ZERO;
        
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            int cantidad = Integer.parseInt(modeloTabla.getValueAt(i, 1).toString());
            
            Object precioObj = modeloTabla.getValueAt(i, 2);
            BigDecimal precio = (precioObj instanceof BigDecimal) ? 
                (BigDecimal) precioObj : new BigDecimal(precioObj.toString());
            
            BigDecimal subtotal = precio.multiply(new BigDecimal(cantidad));
            totalPagar = totalPagar.add(subtotal);
        }
        
        txtTotalPagar.setText("$" + totalPagar.setScale(2, BigDecimal.ROUND_HALF_UP));
        calcularCambio();
    }
      
    private void calcularCambio() {
        try {
            String efectivoStr = JTextEfectivo.getText().trim();
            
            if (efectivoStr.isEmpty()) {
                txtCambio.setText("$0.00");
                return;
            }
            
            BigDecimal efectivo = new BigDecimal(efectivoStr);
            BigDecimal cambio = efectivo.subtract(totalPagar);
            
            if (cambio.compareTo(BigDecimal.ZERO) >= 0) {
                txtCambio.setText("$" + cambio.setScale(2, BigDecimal.ROUND_HALF_UP));
            } else {
                txtCambio.setText("Insuficiente");
            }
        } catch (NumberFormatException e) {
            txtCambio.setText("$0.00");
        }
    }
       
    private void limpiarVenta() {
    if (TablaCompra.isEditing()) {
        TablaCompra.getCellEditor().stopCellEditing();
    }
    
    // ⭐ Limpiar el campo de búsqueda PRIMERO
    jTextField2.setText("");
    
    // ⭐ Luego limpiar la tabla
    modeloTabla.setRowCount(0);
    TablaCompra.clearSelection();
    TablaCompra.revalidate();
    TablaCompra.repaint();
    
    totalPagar = BigDecimal.ZERO;
    txtTotalPagar.setText("$0.00");
    txtCambio.setText("$0.00");
    JTextEfectivo.setText("");
    
    // ⭐ Dar foco al campo de búsqueda
    jTextField2.requestFocus();
    }
      
     
      
  
     
    private void actualizarInventarioEnOtrasVentanas() {
    try {
        
        if(Navegation.GestionProductos != null && Navegation.GestionProductos.isDisplayable()) {
            Navegation.GestionProductos.MostrarProductos();
        }
        
          if (Navegation.Inventario != null && Navegation.Inventario.isDisplayable() && Navegation.Inventario.isVisible()) {
              
          }
     
    } catch (Exception e) {
        // Si falla, no importa, solo era para actualizar la vista
        logger.log(java.util.logging.Level.WARNING, "No se pudo actualizar GestionProductos", e);
    }
}
     
     
     
    
   private void mostrarTicket(int idVenta, java.util.List<itemVentas> productos,
                          BigDecimal total, BigDecimal efectivo, BigDecimal cambio) {
        
        StringBuilder ticket = new StringBuilder();
        ticket.append("═══════════════════════════════════\n");
        ticket.append("     ARCÁNGEL MIGUEL\n");
        ticket.append("     Papelería y Regalos\n");
        ticket.append("═══════════════════════════════════\n");
        ticket.append(String.format("Ticket #%d\n", idVenta));
        ticket.append(String.format("Fecha: %s\n", 
            new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date())));
        
        String nombreCajero = (Login.SesionUsuario.NombreUsuario != null) ? 
            Login.SesionUsuario.NombreUsuario : "Cajero";
        ticket.append(String.format("Cajero: %s\n", nombreCajero));
        
        ticket.append("───────────────────────────────────\n");
        ticket.append("PRODUCTO         CANT  P.UNIT TOTAL\n");
        ticket.append("───────────────────────────────────\n");
        
        for (itemVentas item : productos) {
            String nombreCorto = item.getNombreProducto().length() > 15 ? 
                item.getNombreProducto().substring(0, 12) + "..." : item.getNombreProducto();
            
            ticket.append(String.format("%-15s %4d %6.2f %6.2f\n", 
                nombreCorto, 
                item.getCantidad(), 
                item.getPrecioUnitario(),
                item.getSubtotal()));
        }
        
        ticket.append("───────────────────────────────────\n");
        ticket.append(String.format("TOTAL:                    $%7.2f\n", total));
        ticket.append(String.format("EFECTIVO:                 $%7.2f\n", efectivo));
        ticket.append(String.format("CAMBIO:                   $%7.2f\n", cambio));
        ticket.append("═══════════════════════════════════\n");
        ticket.append("    ¡Gracias por su compra!\n");
        ticket.append("═══════════════════════════════════\n");
        
        javax.swing.JTextArea textArea = new javax.swing.JTextArea(ticket.toString());
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBackground(java.awt.Color.WHITE);
        
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 400));
        
        JOptionPane.showMessageDialog(this,
            scrollPane,
            "Ticket de Venta",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void confirmarVenta() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No hay productos en la venta",
                "Venta vacía",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar que haya al menos un producto con cantidad > 0
        boolean hayProductosConCantidad = false;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            int cantidad = Integer.parseInt(modeloTabla.getValueAt(i, 1).toString());
            if (cantidad > 0) {
                hayProductosConCantidad = true;
                break;
            }
        }
        
        if (!hayProductosConCantidad) {
            JOptionPane.showMessageDialog(this,
                "Debe agregar cantidad a los productos usando los botones +",
                "Sin cantidad",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        BigDecimal efectivo;
        try {
            efectivo = new BigDecimal(JTextEfectivo.getText().trim());
            
            if (efectivo.compareTo(totalPagar) < 0) {
                JOptionPane.showMessageDialog(this,
                    String.format("El efectivo es insuficiente\nTotal: $%.2f\nEfectivo: $%.2f", 
                        totalPagar, efectivo),
                    "Efectivo insuficiente",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Ingrese un monto válido de efectivo",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        BigDecimal cambio = efectivo.subtract(totalPagar);
        
        String mensaje = String.format(
            "═══════════════════════════════\n" +
            "       CONFIRMAR VENTA\n" +
            "═══════════════════════════════\n" +
            "Total:    $%.2f\n" +
            "Efectivo: $%.2f\n" +
            "Cambio:   $%.2f\n" +
            "═══════════════════════════════\n" +
            "¿Proceder con la venta?",
            totalPagar, efectivo, cambio
        );
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            mensaje,
            "Confirmar Venta",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
        // ⭐ PASO 2: PREGUNTAR SI QUIERE TICKET IMPRESO
        int quiereTicket = JOptionPane.showConfirmDialog(this,
            "¿Desea imprimir ticket?\n\n" +
            "(Presione NO si el cliente no requiere ticket)",
            "Imprimir Ticket",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        guardarVentaEnBD(efectivo, cambio, quiereTicket == JOptionPane.YES_OPTION);
    }
        
        
    }
        
    
    
    public void TamanoImagen(){
        ImageIcon Imagen = new ImageIcon(getClass().getResource("/com/Imagenes/miguel.jpg"));
        
        Image TamaImage = Imagen.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
        
        ImageIcon icono = new ImageIcon(TamaImage);
        
        AngelMiguel.setIcon(icono);
        
    };
    
    
    public void iniciarMenu(){
        
        JMenuItem Ajustes = new JMenuItem("Ajustes", getIcon("/com/Imagenes/ajuste.png",25,25));
        JMenuItem Cerrar = new JMenuItem("Cerrar Sesion", getIcon("/com/Imagenes/cerrar-sesion.png",25,25));
        
         
        MenuUsuario.add(Ajustes);
        MenuUsuario.addSeparator();
        MenuUsuario.add(Cerrar);
        
        Saludo.setComponentPopupMenu(MenuUsuario);
        
        Ajustes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR) {
        });
        
        Cerrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR){
        });
        
        
        Ajustes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              
                Ajustes configuracion = new Ajustes();
                
                configuracion.setVisible(true);
                
                nuevaVenta.this.dispose();
                
              
                
     
            }
        });
        
        Cerrar.addActionListener(new ActionListener(){
               @Override
              public void actionPerformed(ActionEvent e){
              
                  Login inicio = new Login();
                  
                  inicio.setVisible(true);
                  
                   nuevaVenta.this.dispose();
              }
        
        });
        
    }
    
    private void guardarVentaEnBD(BigDecimal efectivo, BigDecimal cambio, boolean imprimirTicket) {
    try {
         if (TablaCompra.isEditing()) {
            TablaCompra.getCellEditor().stopCellEditing();
        }
        java.util.List<itemVentas> productos = new java.util.ArrayList<>();
        Conexion conexion = new Conexion();
        
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String nombreProducto = modeloTabla.getValueAt(i, 0).toString();
            int cantidad = Integer.parseInt(modeloTabla.getValueAt(i, 1).toString());
            
            if (cantidad <= 0) {
                continue;
            }
            
            Object precioObj = modeloTabla.getValueAt(i, 2);
            BigDecimal precio = (precioObj instanceof BigDecimal) ? 
                (BigDecimal) precioObj : new BigDecimal(precioObj.toString());
            
            BigDecimal subtotal = precio.multiply(new BigDecimal(cantidad));
            
            String sql = "SELECT id FROM Productos WHERE nombreProducto = ?";
            PreparedStatement ps = conexion.conectar().prepareStatement(sql);
            ps.setString(1, nombreProducto);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int idProducto = rs.getInt("id");
                
                itemVentas item = new itemVentas(
                    idProducto, 
                    nombreProducto, 
                    cantidad, 
                    precio,
                    subtotal
                );
                productos.add(item);
            }
            
            rs.close();
            ps.close();
        }
        
        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe agregar cantidad a los productos",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        addVentas ventaController = new addVentas();
        int idVenta = ventaController.guardarVenta(
            Login.SesionUsuario.id, 
            productos,
            totalPagar,
            efectivo,
            cambio
        );
        
        if (idVenta > 0) {
            // ⭐ GUARDAR COPIAS DE LOS VALORES ANTES DE LIMPIAR
            BigDecimal totalFinal = totalPagar;
            BigDecimal efectivoFinal = efectivo;
            BigDecimal cambioFinal = cambio;
            java.util.List<itemVentas> productosVendidos = new java.util.ArrayList<>(productos);
            
            String nombreCajero = (Login.SesionUsuario.NombreUsuario != null) ? 
                Login.SesionUsuario.NombreUsuario : "Cajero";
            
            // ⭐ AHORA SÍ LIMPIAR LA INTERFAZ
            limpiarVenta();
            actualizarInventarioEnOtrasVentanas();
            Navegation.actualizarInventario();
            
            if (imprimirTicket) {
                boolean impreso = impresoraTermica.imprimirTicket(
                    idVenta, 
                    productosVendidos,  // ⭐ Usar la copia guardada
                    totalFinal,         // ⭐ Usar el valor guardado
                    efectivoFinal,      // ⭐ Usar el valor guardado
                    cambioFinal,        // ⭐ Usar el valor guardado
                    nombreCajero
                );
                
                if (impreso) {
                    JOptionPane.showMessageDialog(this,
                        String.format(
                            "✓ Venta #%d realizada correctamente\n" +
                            "✓ Ticket impreso en impresora térmica",
                            idVenta
                        ),
                        "Venta Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    int verEnPantalla = JOptionPane.showConfirmDialog(this,
                        String.format(
                            "✓ Venta #%d guardada exitosamente\n\n" +
                            "⚠ NO SE PUDO IMPRIMIR el ticket\n" +
                            "La impresora no está conectada o no responde\n\n" +
                            "¿Desea ver el ticket en pantalla?",
                            idVenta
                        ),
                        "Impresora No Disponible",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    
                    if (verEnPantalla == JOptionPane.YES_OPTION) {
                        mostrarTicket(idVenta, productosVendidos, totalFinal, efectivoFinal, cambioFinal);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    String.format(
                        "✓ Venta #%d realizada correctamente\n" +
                        "(Sin ticket impreso)",
                        idVenta
                    ),
                    "Venta Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    } catch (Exception e) {
        logger.log(java.util.logging.Level.SEVERE, "Error al guardar venta", e);
        JOptionPane.showMessageDialog(this,
            "Error al procesar la venta:\n" + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
    
    
    public Icon getIcon( String ruta, int width, int height){

        Icon miIcono = new ImageIcon(new ImageIcon(getClass().getResource(ruta)).getImage().getScaledInstance(width, height,0));
        return miIcono;
        }
    
    
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MenuUsuario = new javax.swing.JPopupMenu();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        AngelMiguel = new javax.swing.JLabel();
        Menu = new Components.PanelRound();
        lbl_ini = new javax.swing.JLabel();
        Menu1 = new Components.PanelRound();
        lbl_ini1 = new javax.swing.JLabel();
        Menu2 = new Components.PanelRound();
        lbl_ini2 = new javax.swing.JLabel();
        Menu3 = new Components.PanelRound();
        lbl_ini3 = new javax.swing.JLabel();
        Menu4 = new Components.PanelRound();
        lbl_ini4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaCompra = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        JTextEfectivo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtTotalPagar = new javax.swing.JLabel();
        txtCambio = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        Saludo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 191, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Arcangel Miguel");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));
        jPanel2.add(AngelMiguel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, -1, -1));

        Menu.setBackground(new java.awt.Color(0, 51, 255));
        Menu.setForeground(new java.awt.Color(0, 153, 153));
        Menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                MenuMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                MenuMousePressed(evt);
            }
        });
        Menu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_ini.setBackground(new java.awt.Color(0, 191, 255));
        lbl_ini.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        lbl_ini.setForeground(new java.awt.Color(255, 255, 255));
        lbl_ini.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/Imagenes/casa_1.png"))); // NOI18N
        lbl_ini.setText("  Dashboard");
        lbl_ini.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_ini.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_iniMousePressed(evt);
            }
        });
        Menu.add(lbl_ini, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 140, 32));

        jPanel2.add(Menu, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, 160, 40));

        Menu1.setBackground(new java.awt.Color(0, 51, 255));
        Menu1.setForeground(new java.awt.Color(0, 153, 153));
        Menu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Menu1MouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Menu1MousePressed(evt);
            }
        });
        Menu1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_ini1.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        lbl_ini1.setForeground(new java.awt.Color(255, 255, 255));
        lbl_ini1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/Imagenes/carrito-de-compras.png"))); // NOI18N
        lbl_ini1.setText(" Nueva venta");
        lbl_ini1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_ini1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_ini1MousePressed(evt);
            }
        });
        Menu1.add(lbl_ini1, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 140, 32));

        jPanel2.add(Menu1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 320, 160, 40));

        Menu2.setBackground(new java.awt.Color(0, 51, 255));
        Menu2.setForeground(new java.awt.Color(0, 153, 153));
        Menu2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Menu2MouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Menu2MousePressed(evt);
            }
        });
        Menu2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_ini2.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        lbl_ini2.setForeground(new java.awt.Color(255, 255, 255));
        lbl_ini2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/Imagenes/carro.png"))); // NOI18N
        lbl_ini2.setText("  Ventas");
        lbl_ini2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_ini2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_ini2MousePressed(evt);
            }
        });
        Menu2.add(lbl_ini2, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 120, 32));

        jPanel2.add(Menu2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 390, 160, 40));

        Menu3.setBackground(new java.awt.Color(0, 51, 255));
        Menu3.setForeground(new java.awt.Color(0, 153, 153));
        Menu3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Menu3MouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Menu3MousePressed(evt);
            }
        });
        Menu3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_ini3.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        lbl_ini3.setForeground(new java.awt.Color(255, 255, 255));
        lbl_ini3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/Imagenes/bienes.png"))); // NOI18N
        lbl_ini3.setText(" Productos");
        lbl_ini3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_ini3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_ini3MousePressed(evt);
            }
        });
        Menu3.add(lbl_ini3, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 120, 32));

        jPanel2.add(Menu3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 460, 160, 40));

        Menu4.setBackground(new java.awt.Color(0, 51, 255));
        Menu4.setForeground(new java.awt.Color(0, 153, 153));
        Menu4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Menu4MouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Menu4MousePressed(evt);
            }
        });
        Menu4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_ini4.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        lbl_ini4.setForeground(new java.awt.Color(255, 255, 255));
        lbl_ini4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/Imagenes/lista-de-verificacion.png"))); // NOI18N
        lbl_ini4.setText(" Inventario");
        lbl_ini4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_ini4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_ini4MousePressed(evt);
            }
        });
        Menu4.add(lbl_ini4, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 120, 32));

        jPanel2.add(Menu4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 530, 160, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 830));

        TablaCompra.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Producto", "Cantidad", "Precio", "Operaciones"
            }
        ));
        jScrollPane1.setViewportView(TablaCompra);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 160, 690, 580));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setBackground(new java.awt.Color(255, 0, 0));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Cancelar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 530, 90, 40));

        jButton2.setBackground(new java.awt.Color(0, 191, 255));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Confirmar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 530, 90, 40));

        JTextEfectivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JTextEfectivoActionPerformed(evt);
            }
        });
        jPanel4.add(JTextEfectivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 210, 130, 40));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setText("Resumen de la venta");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
        jPanel4.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 33, 300, 10));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("San Miguel Arcangel");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 200, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel6.setText("Total a pagar:");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, 40));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel7.setText("Efectivo:");
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, -1, 40));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel8.setText("Cambio:");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, -1, -1));

        txtTotalPagar.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        txtTotalPagar.setText("jLabel9");
        jPanel4.add(txtTotalPagar, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 120, -1, 40));

        txtCambio.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtCambio.setText("jLabel10");
        jPanel4.add(txtCambio, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 330, -1, 30));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 160, 270, 580));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel9.setText("Buscar Producto");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, -1, -1));

        jTextField2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 100, 190, 40));

        jPanel3.setBackground(new java.awt.Color(135, 206, 250));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Nueva Venta");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 230, 30));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/Imagenes/avatar (1).png"))); // NOI18N
        jLabel1.setText("jLabel1");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 10, 30, -1));

        Saludo.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Saludo.setForeground(new java.awt.Color(255, 255, 255));
        Saludo.setText("Dashboard");
        Saludo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.add(Saludo, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 10, 150, 30));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 0, 1060, 50));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lbl_iniMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_iniMousePressed
      
        Navegation.mostrarDashboard();
        this.setVisible(false);

    }//GEN-LAST:event_lbl_iniMousePressed

    private void MenuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_MenuMouseEntered

    private void MenuMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuMousePressed

         Navegation.mostrarDashboard(); 
    this.setVisible(false);
        
    }//GEN-LAST:event_MenuMousePressed

    private void lbl_ini1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_ini1MousePressed
        if(this.isVisible()) return;
      
         Navegation.mostrarNuevaVenta();
   this.setVisible(false);

    }//GEN-LAST:event_lbl_ini1MousePressed

    private void Menu1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu1MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_Menu1MouseEntered

    private void Menu1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu1MousePressed
        if(this.isVisible()) return;
       
        Navegation.mostrarNuevaVenta();
       this.setVisible(false);
    }//GEN-LAST:event_Menu1MousePressed

    private void lbl_ini2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_ini2MousePressed

        Navegation.mostrarVentas();
           this.setVisible(false);
    }//GEN-LAST:event_lbl_ini2MousePressed

    private void Menu2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu2MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_Menu2MouseEntered

    private void Menu2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu2MousePressed
   
        Navegation.mostrarVentas();
          this.setVisible(false);
    }//GEN-LAST:event_Menu2MousePressed

    private void lbl_ini3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_ini3MousePressed
       
        Navegation.mostrarProductos();
      this.setVisible(false);
    }//GEN-LAST:event_lbl_ini3MousePressed

    private void Menu3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu3MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_Menu3MouseEntered

    private void Menu3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu3MousePressed
   
        Navegation.mostrarProductos();
      this.setVisible(false);
    }//GEN-LAST:event_Menu3MousePressed

    private void lbl_ini4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_ini4MousePressed

         Navegation.mostrarInventario();
        this.setVisible(false);
    }//GEN-LAST:event_lbl_ini4MousePressed

    private void Menu4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu4MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_Menu4MouseEntered

    private void Menu4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu4MousePressed
 
         Navegation.mostrarInventario();
        this.setVisible(false);
    }//GEN-LAST:event_Menu4MousePressed

    private void JTextEfectivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JTextEfectivoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_JTextEfectivoActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        confirmarVenta();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if (modeloTabla.getRowCount() == 0) {
        limpiarVenta();
        return;
    }
    
    int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Desea cancelar la venta actual?\nSe perderán todos los productos agregados",
        "Confirmar cancelación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);
    
    if (confirmacion == JOptionPane.YES_OPTION) {
        limpiarVenta();
    }
        
        
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new nuevaVenta().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AngelMiguel;
    private javax.swing.JTextField JTextEfectivo;
    public static Components.PanelRound Menu;
    public static Components.PanelRound Menu1;
    public static Components.PanelRound Menu2;
    public static Components.PanelRound Menu3;
    public static Components.PanelRound Menu4;
    private javax.swing.JPopupMenu MenuUsuario;
    private javax.swing.JLabel Saludo;
    public javax.swing.JTable TablaCompra;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField2;
    public static javax.swing.JLabel lbl_ini;
    public static javax.swing.JLabel lbl_ini1;
    public static javax.swing.JLabel lbl_ini2;
    public static javax.swing.JLabel lbl_ini3;
    public static javax.swing.JLabel lbl_ini4;
    private javax.swing.JLabel txtCambio;
    private javax.swing.JLabel txtTotalPagar;
    // End of variables declaration//GEN-END:variables

   
}
