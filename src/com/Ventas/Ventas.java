
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
import com.bd.Conexion;
import com.login.Login;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.table.DefaultTableModel;
import modeloDesign.Animate;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import static java.time.InstantSource.offset;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 *
 * @author Aleci
 */
public class Ventas extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Ventas.class.getName());
    
    Animate ColorOF = new Animate();
      private DefaultTableModel modeloTabla;
      
      private int paginaActual = 1;
    private int registrosPorPagina = 50; // Cargar solo 50 registros a la vez
    private int totalPaginas = 0;
    
    // Variables para filtrado por fecha
    private String filtroFecha = "ultimos_30_dias";
    
     private JPanel panelControles;
    private javax.swing.JComboBox<String> comboFiltroFecha;
    
    private JButton btnPrimera;
    private JButton btnAnterior;
    private JButton btnSiguiente;
    private JButton btnUltima;
    private JLabel lblPaginaInfo;

    /**
     * Creates new form Ventas
     */
    public Ventas() {
        initComponents();
        setTitle("Ventas");
       setSize(1350,700);
       setLocationRelativeTo(null);
       setResizable(false);
       setDefaultCloseOperation(EXIT_ON_CLOSE);
       setVisible(true);
       Saludo.setText(Login.SesionUsuario.NombreUsuario);
       iniciarMenu();
       TamanoImagen();
       actualizarEstiloMenu();
       configurarTabla();
       crearControlesPaginacion();
       calcularTotalPaginas();

       cargarHistorialVentas();
       
    }
    
    
     private void actualizarEstiloMenu() {
    PanelRound[] menus = {Menu, Menu1, Menu2, Menu3, Menu4};
    JLabel[] labels = {lbl_ini, lbl_ini1, lbl_ini2, lbl_ini3, lbl_ini4};
    ColorOF.activarVentas(menus, labels);
}
     
     
     private void crearControlesPaginacion() {
        // Panel de controles superior con filtro de fecha
        panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelControles.setBackground(new java.awt.Color(255, 255, 255));
        
        // ComboBox para filtrar por fecha
       
     
        comboFiltroFecha = new javax.swing.JComboBox<>(new String[]{
            "Últimos 7 días",
            "Últimos 30 días",
            "Últimos 3 meses",
            "Últimos 6 meses",
            "Último año",
            "Todo el historial"
        });
        comboFiltroFecha.setSelectedIndex(1); // Últimos 30 días por defecto
        comboFiltroFecha.addActionListener(e -> {
            cambiarFiltroFecha();
        });
        
     
        panelControles.add(comboFiltroFecha);
        
        // Agregar panel de controles arriba de la tabla
        jPanel1.add(panelControles, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 90, 710, 40));
        
        // Panel de paginación
        JPanel panelPaginacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelPaginacion.setBackground(new java.awt.Color(255, 255, 255));
        
        btnPrimera = new JButton("<<");
        btnAnterior = new JButton("<");
        lblPaginaInfo = new JLabel("Página 1 de 1");
        btnSiguiente = new JButton(">");
        btnUltima = new JButton(">>");
        
        // Estilo de botones
        estilizarBotonPaginacion(btnPrimera);
        estilizarBotonPaginacion(btnAnterior);
        estilizarBotonPaginacion(btnSiguiente);
        estilizarBotonPaginacion(btnUltima);
        
        lblPaginaInfo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
        
        // Eventos
        btnPrimera.addActionListener(e -> irAPagina(1));
        btnAnterior.addActionListener(e -> irAPagina(paginaActual - 1));
        btnSiguiente.addActionListener(e -> irAPagina(paginaActual + 1));
        btnUltima.addActionListener(e -> irAPagina(totalPaginas));
        
        panelPaginacion.add(btnPrimera);
        panelPaginacion.add(btnAnterior);
        panelPaginacion.add(lblPaginaInfo);
        panelPaginacion.add(btnSiguiente);
        panelPaginacion.add(btnUltima);
        
        jPanel1.add(panelPaginacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 600, 710, 40));
    }
     
     
     
     private void estilizarBotonPaginacion(JButton btn) {
        btn.setBackground(new java.awt.Color(0, 191, 255));
        btn.setForeground(java.awt.Color.WHITE);
        btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }
    
    private void cambiarFiltroFecha() {
        String seleccion = (String) comboFiltroFecha.getSelectedItem();
        
        switch(seleccion) {
            case "Últimos 7 días":
                filtroFecha = "ultimos_7_dias";
                break;
            case "Últimos 30 días":
                filtroFecha = "ultimos_30_dias";
                break;
            case "Últimos 3 meses":
                filtroFecha = "ultimos_3_meses";
                break;
            case "Últimos 6 meses":
                filtroFecha = "ultimos_6_meses";
                break;
            case "Último año":
                filtroFecha = "ultimo_ano";
                break;
            case "Todo el historial":
                filtroFecha = "todo";
                break;
        }
        
        paginaActual = 1;
        calcularTotalPaginas();
        cargarHistorialVentas();
    }
    
    private String obtenerCondicionFecha() {
        switch(filtroFecha) {
            case "ultimos_7_dias":
                return "WHERE v.fechaVenta >= datetime('now', '-7 days')";
            case "ultimos_30_dias":
                return "WHERE v.fechaVenta >= datetime('now', '-30 days')";
            case "ultimos_3_meses":
                return "WHERE v.fechaVenta >= datetime('now', '-3 months')";
            case "ultimos_6_meses":
                return "WHERE v.fechaVenta >= datetime('now', '-6 months')";
            case "ultimo_ano":
                return "WHERE v.fechaVenta >= datetime('now', '-1 year')";
            default:
                return "";
        }
    }
    
    private void calcularTotalPaginas() {
        try {
            Conexion conexion = new Conexion();
            String condicionFecha = obtenerCondicionFecha();
            
            String sql = "SELECT COUNT(*) as total FROM Ventas v " + condicionFecha;
            
            PreparedStatement ps = conexion.conectar().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int totalRegistros = rs.getInt("total");
                totalPaginas = (int) Math.ceil((double) totalRegistros / registrosPorPagina);
                if (totalPaginas == 0) totalPaginas = 1;
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al calcular páginas", e);
        }
    }
    
    private void irAPagina(int nuevaPagina) {
        if (nuevaPagina < 1 || nuevaPagina > totalPaginas) return;
        
        paginaActual = nuevaPagina;
        cargarHistorialVentas();
    }
    
    private void actualizarControlesPaginacion() {
        lblPaginaInfo.setText("Página " + paginaActual + " de " + totalPaginas);
        
        btnPrimera.setEnabled(paginaActual > 1);
        btnAnterior.setEnabled(paginaActual > 1);
        btnSiguiente.setEnabled(paginaActual < totalPaginas);
        btnUltima.setEnabled(paginaActual < totalPaginas);
    }
    
     
     
     
     
     
     
 private void configurarTabla() {
        String[] columnas = {"N° Venta", "Fecha", "Total", "Cajero", "Operaciones"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Solo la columna de operaciones es editable
            }
        };
        
        TablaHistorial.setModel(modeloTabla);
        
        // Configurar renderizador y editor para el botón
        TablaHistorial.getColumnModel().getColumn(4).setCellRenderer(
            new VerTicketRenderer());
        TablaHistorial.getColumnModel().getColumn(4).setCellEditor(
            new VerTicketEditor (new javax.swing.JCheckBox(), this));
        
        // Ajustar anchos de columnas
        TablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(80);  // N° Venta
        TablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(150); // Fecha
        TablaHistorial.getColumnModel().getColumn(2).setPreferredWidth(100); // Total
        TablaHistorial.getColumnModel().getColumn(3).setPreferredWidth(120); // Cajero
        TablaHistorial.getColumnModel().getColumn(4).setPreferredWidth(140); // Operaciones
        
        TablaHistorial.setRowHeight(35);
    }
    
   private void cargarHistorialVentas() {
    modeloTabla.setRowCount(0);
    
    try {
        Conexion conexion = new Conexion();
        
        // Calcular el offset según la página actual
        int offset = (paginaActual - 1) * registrosPorPagina;
        String condicionFecha = obtenerCondicionFecha();
        
        // SQL con LIMIT y OFFSET - NOTA EL ESPACIO ANTES DE LIMIT
        String sql = "SELECT v.id, v.fechaVenta, v.totalVenta, u.Usuario " +
                    "FROM Ventas v " +
                    "INNER JOIN Usuarios u ON v.idUsuario = u.id " +
                    condicionFecha + " " +
                    "ORDER BY v.fechaVenta DESC " +  // ✅ Espacio al final
                    "LIMIT ? OFFSET ?";
        
        PreparedStatement ps = conexion.conectar().prepareStatement(sql);
        ps.setInt(1, registrosPorPagina);  // 50 registros por página
        ps.setInt(2, offset);               // Desde qué registro empezar
        
        ResultSet rs = ps.executeQuery();
        
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        while (rs.next()) {
            int idVenta = rs.getInt("id");
            String fecha = formato.format(rs.getTimestamp("fechaVenta"));
            BigDecimal total = rs.getBigDecimal("totalVenta");
            String cajero = rs.getString("Usuario");
            
            Object[] fila = {
                idVenta,
                fecha,
                String.format("$%.2f", total),
                cajero,
                "Ver"
            };
            
            modeloTabla.addRow(fila);
        }
        
        rs.close();
        ps.close();
        
        // Actualizar los controles de paginación
        actualizarControlesPaginacion();
        
    } catch (SQLException e) {
        logger.log(java.util.logging.Level.SEVERE, "Error al cargar historial", e);
        JOptionPane.showMessageDialog(this,
            "Error al cargar el historial de ventas:\n" + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
    
   public void mostrarDetalleVenta(int idVenta) {
    try {
        Conexion conexion = new Conexion();
        
        String sqlVenta = "SELECT v.*, u.Usuario FROM Ventas v " +
                         "INNER JOIN Usuarios u ON v.idUsuario = u.id " +
                         "WHERE v.id = ?";
        PreparedStatement psVenta = conexion.conectar().prepareStatement(sqlVenta);
        psVenta.setInt(1, idVenta);
        ResultSet rsVenta = psVenta.executeQuery();
        
        if (!rsVenta.next()) {
            JOptionPane.showMessageDialog(this, 
                "No se encontró la venta", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        BigDecimal total = rsVenta.getBigDecimal("totalVenta");
        BigDecimal efectivo = rsVenta.getBigDecimal("efectivo");
        BigDecimal cambio = rsVenta.getBigDecimal("cambio");
        String cajero = rsVenta.getString("Usuario");
        Timestamp fechaVenta = rsVenta.getTimestamp("fechaVenta");
        
        String sqlDetalle = "SELECT nombreProducto, cantidad, precioUnitario, subtotal " +
                           "FROM DetalleVentas WHERE idVenta = ?";
        PreparedStatement psDetalle = conexion.conectar().prepareStatement(sqlDetalle);
        psDetalle.setInt(1, idVenta);
        ResultSet rsDetalle = psDetalle.executeQuery();
        
        java.util.List<itemVentas> productos = new java.util.ArrayList<>();
        while (rsDetalle.next()) {
            productos.add(new itemVentas(
                0,
                rsDetalle.getString("nombreProducto"),
                rsDetalle.getDouble("cantidad"), // ✅ getDouble
                rsDetalle.getBigDecimal("precioUnitario"),
                rsDetalle.getBigDecimal("subtotal")
            ));
        }
        
        StringBuilder ticket = new StringBuilder();
        ticket.append("═══════════════════════════════════\n");
        ticket.append("     ARCÁNGEL MIGUEL\n");
        ticket.append("     Papelería y Regalos\n");
        ticket.append("═══════════════════════════════════\n");
        ticket.append(String.format("Ticket #%d\n", idVenta));
        ticket.append(String.format("Fecha: %s\n", 
            new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fechaVenta)));
        ticket.append(String.format("Cajero: %s\n", cajero));
        ticket.append("───────────────────────────────────\n");
        ticket.append("PRODUCTO         CANT  P.UNIT TOTAL\n");
        ticket.append("───────────────────────────────────\n");
        
        for (itemVentas item : productos) {
            String nombreCorto = item.getNombreProducto().length() > 15 ? 
                item.getNombreProducto().substring(0, 12) + "..." : 
                item.getNombreProducto();
            
            // ✅ Formatear cantidad correctamente
            String cantidadStr;
            if (item.getCantidad() == Math.floor(item.getCantidad())) {
                cantidadStr = String.format("%.0f", item.getCantidad());
            } else {
                cantidadStr = String.format("%.1f", item.getCantidad());
            }
            
            ticket.append(String.format("%-15s %5s %6.2f %6.2f\n", 
                nombreCorto, 
                cantidadStr,
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
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 450));
        
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setLayout(new java.awt.BorderLayout(10, 10));
        panel.add(scrollPane, java.awt.BorderLayout.CENTER);
        
        javax.swing.JPanel botonesPanel = new javax.swing.JPanel();
        javax.swing.JButton btnImprimir = new javax.swing.JButton("Imprimir Ticket");
        javax.swing.JButton btnCerrar = new javax.swing.JButton("Cerrar");
        
        btnImprimir.setBackground(new java.awt.Color(0, 191, 255));
        btnImprimir.setForeground(java.awt.Color.WHITE);
        btnCerrar.setBackground(new java.awt.Color(150, 150, 150));
        btnCerrar.setForeground(java.awt.Color.WHITE);
        
        botonesPanel.add(btnImprimir);
        botonesPanel.add(btnCerrar);
        panel.add(botonesPanel, java.awt.BorderLayout.SOUTH);
     
        javax.swing.JDialog dialog = new javax.swing.JDialog(this, "Ticket #" + idVenta, true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        
        btnImprimir.addActionListener(e -> {
            boolean impreso = impresoraTermica.imprimirTicket(
                idVenta, productos, total, efectivo, cambio, cajero);
            
            if (impreso) {
                JOptionPane.showMessageDialog(dialog,
                    "Ticket impreso correctamente",
                    "Impresión Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "No se pudo imprimir el ticket.\n" +
                    "Verifique que la impresora esté conectada.",
                    "Error de Impresión",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnCerrar.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
        
        rsDetalle.close();
        psDetalle.close();
        rsVenta.close();
        psVenta.close();
        
    } catch (SQLException e) {
        logger.log(java.util.logging.Level.SEVERE, "Error al mostrar detalle", e);
        JOptionPane.showMessageDialog(this,
            "Error al cargar el detalle de la venta:\n" + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
     
     
     
     
     
     

    public void TamanoImagen(){
        ImageIcon Imagen = new ImageIcon(getClass().getResource("/com/Imagenes/miguel.jpg"));
        
        Image TamaImage = Imagen.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
        
        ImageIcon icono = new ImageIcon(TamaImage);
        
        AngelMiguel3.setIcon(icono);
        
    };
    
    
    public void iniciarMenu(){
        
      
        JMenuItem Cerrar = new JMenuItem("Cerrar Sesion", getIcon("/com/Imagenes/cerrar-sesion.png",25,25));
        
         
        MenuUsuario.add(Cerrar);
        
        Saludo.setComponentPopupMenu(MenuUsuario);
        
      
        
        Cerrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR){
        });
        
        
        Cerrar.addActionListener(new ActionListener(){
               @Override
              public void actionPerformed(ActionEvent e){
              
                  Login inicio = new Login();
                  
                  inicio.setVisible(true);
                  
                   Ventas.this.dispose();
              }
        
        });
        
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
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaHistorial = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        Saludo = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        AngelMiguel3 = new javax.swing.JLabel();
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
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        TablaHistorial.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(TablaHistorial);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 150, 1050, 410));

        jPanel3.setBackground(new java.awt.Color(135, 206, 250));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Ventas");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 130, 30));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/Imagenes/avatar (1).png"))); // NOI18N
        jLabel1.setText("jLabel1");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 10, 30, -1));

        Saludo.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Saludo.setForeground(new java.awt.Color(255, 255, 255));
        Saludo.setText("Dashboard");
        Saludo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.add(Saludo, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 10, 150, 30));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 0, 1110, 50));

        jPanel6.setBackground(new java.awt.Color(0, 191, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Arcangel Miguel");
        jPanel6.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));
        jPanel6.add(AngelMiguel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, -1, -1));

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
        Menu.add(lbl_ini, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 160, 32));

        jPanel6.add(Menu, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, 170, 40));

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
        Menu1.add(lbl_ini1, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 160, 32));

        jPanel6.add(Menu1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 320, 170, 40));

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
        Menu2.add(lbl_ini2, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 150, 32));

        jPanel6.add(Menu2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 390, 170, 40));

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
        Menu3.add(lbl_ini3, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 160, 32));

        jPanel6.add(Menu3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 460, 170, 40));

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
        Menu4.add(lbl_ini4, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 160, 32));

        jPanel6.add(Menu4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 530, 170, 40));

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 830));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setText("Mostrar Ventas:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 80, 210, 50));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1356, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lbl_iniMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_iniMousePressed
    
        Navegation.mostrarDashboard();
        this.dispose();
       
    }//GEN-LAST:event_lbl_iniMousePressed

    private void MenuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuMouseEntered
        // TODO add your handling code here:

    }//GEN-LAST:event_MenuMouseEntered

    private void MenuMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuMousePressed
        if(this.isVisible()) return;

        Navegation.mostrarDashboard();
        this.setVisible(false);

    }//GEN-LAST:event_MenuMousePressed

    private void lbl_ini1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_ini1MousePressed

        Navegation.mostrarNuevaVenta();
        this.setVisible(false);

    }//GEN-LAST:event_lbl_ini1MousePressed

    private void Menu1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu1MouseEntered
        // TODO add your handling code here:

    }//GEN-LAST:event_Menu1MouseEntered

    private void Menu1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu1MousePressed

        Navegation.mostrarNuevaVenta();
        this.setVisible(false);
    }//GEN-LAST:event_Menu1MousePressed

    private void lbl_ini2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_ini2MousePressed
         if(this.isVisible()) return;
      
         Navegation.mostrarVentas();
         this.setVisible(false);
      
    }//GEN-LAST:event_lbl_ini2MousePressed

    private void Menu2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu2MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_Menu2MouseEntered

    private void Menu2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu2MousePressed
          if(this.isVisible()) return;
      
         Navegation.mostrarVentas();
         this.setVisible(false);
        
    }//GEN-LAST:event_Menu2MousePressed

    private void lbl_ini3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_ini3MousePressed
        ;
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
        java.awt.EventQueue.invokeLater(() -> new Ventas().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AngelMiguel3;
    public static Components.PanelRound Menu;
    public static Components.PanelRound Menu1;
    public static Components.PanelRound Menu2;
    public static Components.PanelRound Menu3;
    public static Components.PanelRound Menu4;
    private javax.swing.JPopupMenu MenuUsuario;
    private javax.swing.JLabel Saludo;
    private javax.swing.JTable TablaHistorial;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JLabel lbl_ini;
    public static javax.swing.JLabel lbl_ini1;
    public static javax.swing.JLabel lbl_ini2;
    public static javax.swing.JLabel lbl_ini3;
    public static javax.swing.JLabel lbl_ini4;
    // End of variables declaration//GEN-END:variables
}
