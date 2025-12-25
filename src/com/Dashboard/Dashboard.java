/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.Dashboard;


import Components.PanelRound;
import com.Productos.GestionProductos;
import com.login.Login;
import com.login.Login.SesionUsuario;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import modeloDesign.Animate;
import com.Controlador.Navegation;
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
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.math.BigDecimal;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;



/**
 *
 * @author Aleci
 */
public class Dashboard extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Dashboard.class.getName());
       Animate ColorOF =  new Animate();
       
       Navegation Actualizar = new Navegation();
       

    public Dashboard() {
       initComponents();
       setTitle("Panel inicial");
       setSize(1300,830);
       setLocationRelativeTo(null);
       setResizable(false);
       setDefaultCloseOperation(EXIT_ON_CLOSE);
       setVisible(true);
       Saludo.setText(SesionUsuario.NombreUsuario);
       actualizarEstiloMenu();
       cargarGraficaVD();
       cargarGraficasVM();
       iniciarMenu();
       TamanoImagen();
       graficaPastel();
       actualizarVentasDiarias();
       actualizarStockBajo();
       actualizarNuevosProductos();
       actualizarGananciasDelDia();
       
      
      
    }
    
    public void ActualizarGraficas(){
        Navegation.actualizarInventario();
        cargarGraficasVM();
        cargarGraficaVD();
        graficaPastel();
       
    }
    
    
    public void ActualizarTarjetas(){
        actualizarVentasDiarias();
        actualizarStockBajo();
        actualizarNuevosProductos();
         actualizarGananciasDelDia();
    }
    
    
    private void cargarGraficaVD(){
    
        try{
            
          DefaultCategoryDataset dataset = obtenerVentasPorDia();
          
          JFreeChart chart = ChartFactory.createBarChart("Ventas Totales Por Dia", 
                  "Dias", 
                  "Total", 
                  dataset);
          
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.BLACK);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        
        Color [] colores = {
            new Color(255, 99, 132),   
            new Color(54, 162, 235),   
            new Color(255, 206, 86),   
            new Color(75, 192, 192),   
            new Color(153, 102, 255),  
            new Color(255, 159, 64),   
            new Color(199, 199, 199)         
        };
          
        renderer.setSeriesPaint(0, colores[0]);
        
        for(int i= 0; i<7; i++){
            renderer.setSeriesPaint(i, colores[i]);
            
        }
        
         ChartPanel chartPanel = new ChartPanel(chart);
  
        GraficasVD.removeAll();
        GraficasVD.setLayout(new BorderLayout());
        GraficasVD.add(chartPanel, BorderLayout.CENTER);
        GraficasVD.revalidate();
        GraficasVD.repaint();
            
        }catch(Exception e){
            
            
        }
}
    
    
    private DefaultCategoryDataset obtenerVentasPorDia() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
    
    
    String sql = "SELECT " +
                 "CASE CAST(strftime('%w', fechaVenta) AS INTEGER) " +
                 "  WHEN 0 THEN 7 " + 
                 "  ELSE CAST(strftime('%w', fechaVenta) AS INTEGER) " +
                 "END as diaSemana, " +
                 "COUNT(*) as cantidadVentas " +
                 "FROM Ventas " +
                 "WHERE estado = 'completada' " +
                 "AND date(fechaVenta) >= date('now', 'weekday 0', '-6 days') " + 
                 "AND date(fechaVenta) <= date('now', 'weekday 0') " + 
                 "GROUP BY diaSemana " +
                 "ORDER BY diaSemana";
    
    try (Connection db = Conexion.conectar();
         Statement sms = db.createStatement();
         ResultSet rs = sms.executeQuery(sql)) {
        
        int[] ventasPorDia = new int[7];
        
        while (rs.next()) {
            int diaSemana = rs.getInt("diaSemana") - 1; 
            int cantidad = rs.getInt("cantidadVentas");
            ventasPorDia[diaSemana] = cantidad;
        }
        
     
        for (int i = 0; i < 7; i++) {
            dataset.addValue(ventasPorDia[i], dias[i], "");
        }
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al obtener ventas por día");
        e.printStackTrace();
    }
    
    return dataset;
}
    
    
    
    
    
    
    
    
    private void cargarGraficasVM(){
    try{
        
        DefaultCategoryDataset dataset = obtenerDatosVentasMensuales();
        
        JFreeChart chart = ChartFactory.createBarChart(
                "Ventas Totales Por Mes",
                "Meses",
                "Total",
                dataset
        );
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.BLACK);
        
         BarRenderer renderer = (BarRenderer) plot.getRenderer();
        Color[] colores = {
            new Color(255, 99, 132),   
            new Color(54, 162, 235),   
            new Color(255, 206, 86),   
            new Color(75, 192, 192),   
            new Color(153, 102, 255),  
            new Color(255, 159, 64),   
            new Color(199, 199, 199),  
            new Color(83, 102, 255),  
            new Color(255, 99, 255),  
            new Color(99, 255, 132),  
            new Color(255, 159, 132), 
            new Color(132, 99, 255)    
        };
        
        renderer.setSeriesPaint(0, colores[0]);
        for (int i = 0; i < 12; i++) {
            renderer.setSeriesPaint(i, colores[i]);
        }
        
        ChartPanel chartPanel = new ChartPanel(chart);
        
        GraficasVM.removeAll();
        GraficasVM.setLayout(new BorderLayout());
        GraficasVM.add(chartPanel, BorderLayout.CENTER);
        GraficasVM.revalidate();
        GraficasVM.repaint();
        
        
    }catch(Exception e){
        
        JOptionPane.showMessageDialog(null, "Error al cargar datos");
        
    }
       
        
    }
    
    
    
    private DefaultCategoryDataset obtenerDatosVentasMensuales(){
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                      "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        
         String sql = "SELECT strftime('%m', fechaVenta) as mes, " +
                 "SUM(totalVenta) as total " +
                 "FROM Ventas " +
                 "WHERE strftime('%Y', fechaVenta) = strftime('%Y', 'now') " +
                 "AND estado = 'completada' " +
                 "GROUP BY mes " +
                 "ORDER BY mes";
         
         try(Connection db = Conexion.conectar();
                 Statement sms = db.createStatement();
                 ResultSet rs = sms.executeQuery(sql)){
             
             double[] ventasPorMes = new double[12];
             
            while (rs.next()) {
            int mes = Integer.parseInt(rs.getString("mes")) - 1; 
            double total = rs.getDouble("total");
            ventasPorMes[mes] = total;
        }
            
             for (int i = 0; i < 12; i++) {
            dataset.addValue(ventasPorMes[i], meses[i],"");
        }
             
         }catch(Exception e){
             JOptionPane.showMessageDialog(null, "Error al obtener los datos");
         }
         
        return dataset;
    }
    
    
   private void graficaPastel(){
       
   
    try {
        DefaultPieDataset dataset = obtenerTop3Productos();
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Top 3 Productos Más Vendidos",
            dataset,
            true,   
            true,  
            false
        );
        
        chart.setBackgroundPaint(Color.WHITE);
        
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        
      
        Color[] colores = {
            new Color(255, 193, 7),    
            new Color(192, 192, 192),  
            new Color(205, 127, 50)    
        };
        
    
        int colorIndex = 0;
        for (Object key : dataset.getKeys()) {
            if (colorIndex < colores.length) {
                plot.setSectionPaint((Comparable) key, colores[colorIndex]);
                colorIndex++;
            }
        }
        
        plot.setLabelBackgroundPaint(Color.WHITE);
        plot.setLabelOutlinePaint(Color.WHITE);
        plot.setLabelShadowPaint(null);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        
        GraficaBarras.removeAll();
        GraficaBarras.setLayout(new BorderLayout());
        GraficaBarras.add(chartPanel, BorderLayout.CENTER);
        GraficaBarras.revalidate();
        GraficaBarras.repaint();

 
    }catch(Exception e){
        JOptionPane.showMessageDialog(null, "Error al cargar la grafica");
        e.printStackTrace();
    }
    
   }
   
   private DefaultPieDataset obtenerTop3Productos() {
    DefaultPieDataset dataset = new DefaultPieDataset();
    
    
    String sql = "SELECT p.nombreProducto, " +
                 "SUM(dv.cantidad) as totalVendido " +
                 "FROM DetalleVentas dv " +
                 "INNER JOIN Productos p ON dv.idProducto = p.id " +
                 "INNER JOIN Ventas v ON dv.idVenta = v.id " +
                 "WHERE v.estado = 'completada' " +
                 "GROUP BY p.id, p.nombreProducto " +
                 "ORDER BY totalVendido DESC " +
                 "LIMIT 3";
    
    try (Connection db = Conexion.conectar();
         Statement sms = db.createStatement();
         ResultSet rs = sms.executeQuery(sql)) {
        
        boolean hayDatos = false;
        
        while (rs.next()) {
            hayDatos = true;
            String producto = rs.getString("nombreProducto");
            int totalVendido = rs.getInt("totalVendido");
            
         
            if (producto.length() > 20) {
                producto = producto.substring(0, 17) + "...";
            }
            
            dataset.setValue(producto + " (" + totalVendido + ")", totalVendido);
        }
        
     
        if (!hayDatos) {
            dataset.setValue("Sin ventas", 1);
        }
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al obtener productos más vendidos");
        e.printStackTrace();
        dataset.setValue("Sin datos", 1);
    }
    
    return dataset;
}
   
   
   
   private void actualizarVentasDiarias(){
       String sql = "SELECT COALESCE(SUM(totalVenta), 0) as totalHoy " +
                 "FROM Ventas " +
                 "WHERE date(fechaVenta) = date('now') " +
                 "AND estado = 'completada'";
       
       try(Connection db = Conexion.conectar();
               Statement sms = db.createStatement();
               ResultSet rs = sms.executeQuery(sql)){
           
           if(rs.next()){
                BigDecimal totalHoy = rs.getBigDecimal("totalHoy");
               if(totalHoy == null){
                   totalHoy = BigDecimal.ZERO;
               }
               JVentasDia.setText("$"+ totalHoy.setScale(2,BigDecimal.ROUND_HALF_UP));
              
               JVentasDia.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);


           }else{
               JVentasDia.setText("$0.00");
           }
           
       }catch(Exception e){
        JVentasDia.setText("Error");
        System.out.println("Error al obtener ventas del día: " + e.getMessage());
           
       }
   }
   
   
   private void actualizarStockBajo(){
        String sql = "SELECT COUNT(*) as cantidad " +
                 "FROM Productos " +
                 "WHERE cantidad < 10 AND cantidad > 0";
        
        
        try(Connection db = Conexion.conectar();
                Statement sms = db.createStatement();
                ResultSet rs = sms.executeQuery(sql)){
            
            if (rs.next()) {
            int cantidad = rs.getInt("cantidad");
            if (cantidad > 0) {
                JStockBajo.setText(cantidad + " Productos");
                JStockBajo.setForeground(new Color(231, 76, 60)); 
            } else {
                JStockBajo.setText("Todo  Bien");
                JStockBajo.setForeground(new Color(46, 204, 113)); 
            }
        }
            
        }catch(Exception e){
             JStockBajo.setText("Error");
            
        }
   }
   
   
  
   private void actualizarNuevosProductos() {
    String sql = "SELECT COUNT(*) as cantidad FROM Productos";
    
    try (Connection db = Conexion.conectar();
         Statement sms = db.createStatement();
         ResultSet rs = sms.executeQuery(sql)) {
        
        if (rs.next()) {
            int cantidad = rs.getInt("cantidad");
            JNuevoProductos.setText(cantidad + " Productos");
        } else {
            JNuevoProductos.setText("0 Productos");
        }
        
    } catch (Exception e) {
        JNuevoProductos.setText("Error");
    }
}
   
   
  private void actualizarGananciasDelDia() {
    String sql = "SELECT " +
                 "COALESCE(SUM(dv.subtotal), 0) as totalVentas, " +
                 "COALESCE(SUM(dv.cantidad * p.precioDeCompra), 0) as totalCostos " +
                 "FROM DetalleVentas dv " +
                 "INNER JOIN Productos p ON dv.idProducto = p.id " +
                 "INNER JOIN Ventas v ON dv.idVenta = v.id " +
                 "WHERE date(v.fechaVenta) = date('now') " +
                 "AND v.estado = 'completada'";
    
    try (Connection db = Conexion.conectar();
         Statement sms = db.createStatement();
         ResultSet rs = sms.executeQuery(sql)) {
        
        if (rs.next()) {
            BigDecimal totalVentas = rs.getBigDecimal("totalVentas");
            BigDecimal totalCostos = rs.getBigDecimal("totalCostos");
            
            if (totalVentas == null) totalVentas = BigDecimal.ZERO;
            if (totalCostos == null) totalCostos = BigDecimal.ZERO;
            
            BigDecimal ganancia = totalVentas.subtract(totalCostos);
            
            JGanacias.setText("$" + ganancia.setScale(2, BigDecimal.ROUND_HALF_UP));
            
            // Cambiar color según ganancia
            if (ganancia.compareTo(BigDecimal.ZERO) > 0) {
                JGanacias.setForeground(new Color(46, 204, 113)); // Verde
            } else {
                JGanacias.setForeground(new Color(149, 165, 166)); // Gris
            }
        } else {
            JGanacias.setText("$0.00");
        }
        
    } catch (Exception e) {
        JGanacias.setText("Error");
        System.out.println("Error al calcular ganancias: " + e.getMessage());
    }
}
   
   
    
    
    
    
    
    
    
     private void actualizarEstiloMenu() {
        PanelRound[] menus = {Menu, Menu1, Menu2, Menu3, Menu4};
        JLabel[] labels = {lbl_ini, lbl_ini1, lbl_ini2, lbl_ini3, lbl_ini4};
        ColorOF.activarDashboard(menus, labels);
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
                Dashboard.this.dispose();
                
     
            }
        });
        
        Cerrar.addActionListener(new ActionListener(){
               @Override
              public void actionPerformed(ActionEvent e){
              
                  Login inicio = new Login();
                  
                  inicio.setVisible(true);
                  
                  Dashboard.this.dispose();
                  
              }
        
        });
        
    }
    
    
    
    //
        public Icon getIcon( String ruta, int width, int height){

        Icon miIcono = new ImageIcon(new ImageIcon(getClass().getResource(ruta)).getImage().getScaledInstance(width, height,0));
        return miIcono;
        }
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
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
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        Saludo = new javax.swing.JLabel();
        GraficasVD = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        JVentasDia = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        JStockBajo = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        JNuevoProductos = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        JGanacias = new javax.swing.JLabel();
        GraficasVM = new javax.swing.JPanel();
        GraficaBarras = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(248, 248, 255));
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

        jPanel3.setBackground(new java.awt.Color(135, 206, 250));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Inicio");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 110, 30));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/Imagenes/avatar (1).png"))); // NOI18N
        jLabel1.setText("jLabel1");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 10, 30, -1));

        Saludo.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Saludo.setForeground(new java.awt.Color(255, 255, 255));
        Saludo.setText("Dashboard");
        Saludo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.add(Saludo, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 10, 150, 30));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 0, 1060, 50));

        GraficasVD.setBackground(new java.awt.Color(255, 255, 255));
        GraficasVD.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout GraficasVDLayout = new javax.swing.GroupLayout(GraficasVD);
        GraficasVD.setLayout(GraficasVDLayout);
        GraficasVDLayout.setHorizontalGroup(
            GraficasVDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 618, Short.MAX_VALUE)
        );
        GraficasVDLayout.setVerticalGroup(
            GraficasVDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 258, Short.MAX_VALUE)
        );

        jPanel1.add(GraficasVD, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 520, 620, 260));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Ventas Del Dia");
        jPanel6.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 130, -1));

        JVentasDia.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        JVentasDia.setText("0");
        jPanel6.add(JVentasDia, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 60, -1, -1));

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 70, 240, 160));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Stock Bajo");
        jPanel7.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 100, -1));

        JStockBajo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        JStockBajo.setText("0 Productos");
        jPanel7.add(JStockBajo, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, -1, -1));

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 70, 240, 160));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Nuevos Productos");
        jPanel8.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 160, -1));

        JNuevoProductos.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        JNuevoProductos.setText("0 Productos");
        jPanel8.add(JNuevoProductos, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, -1, -1));

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 70, 240, 160));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Ganancias Del Dia");
        jPanel9.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 160, -1));

        JGanacias.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        JGanacias.setText("0");
        jPanel9.add(JGanacias, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, -1, -1));

        jPanel1.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 70, 240, 160));

        GraficasVM.setBackground(new java.awt.Color(255, 255, 255));
        GraficasVM.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout GraficasVMLayout = new javax.swing.GroupLayout(GraficasVM);
        GraficasVM.setLayout(GraficasVMLayout);
        GraficasVMLayout.setHorizontalGroup(
            GraficasVMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1028, Short.MAX_VALUE)
        );
        GraficasVMLayout.setVerticalGroup(
            GraficasVMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 268, Short.MAX_VALUE)
        );

        jPanel1.add(GraficasVM, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 240, 1030, 270));

        GraficaBarras.setBackground(new java.awt.Color(255, 255, 255));
        GraficaBarras.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout GraficaBarrasLayout = new javax.swing.GroupLayout(GraficaBarras);
        GraficaBarras.setLayout(GraficaBarrasLayout);
        GraficaBarrasLayout.setHorizontalGroup(
            GraficaBarrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );
        GraficaBarrasLayout.setVerticalGroup(
            GraficaBarrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 258, Short.MAX_VALUE)
        );

        jPanel1.add(GraficaBarras, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 520, 400, 260));

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

    private void MenuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuMouseEntered
        // TODO add your handling code here:
       
    }//GEN-LAST:event_MenuMouseEntered

    private void MenuMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuMousePressed
         if(this.isVisible()) return;
      
         Navegation.mostrarDashboard();
         this.setVisible(false);
         
    }//GEN-LAST:event_MenuMousePressed

    private void lbl_iniMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_iniMousePressed
       if(this.isVisible()) return;
  
      Navegation.mostrarDashboard(); 
       this.setVisible(false);
    }//GEN-LAST:event_lbl_iniMousePressed

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
        java.awt.EventQueue.invokeLater(() -> new Dashboard().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AngelMiguel;
    private javax.swing.JPanel GraficaBarras;
    private javax.swing.JPanel GraficasVD;
    private javax.swing.JPanel GraficasVM;
    private javax.swing.JLabel JGanacias;
    private javax.swing.JLabel JNuevoProductos;
    private javax.swing.JLabel JStockBajo;
    private javax.swing.JLabel JVentasDia;
    public static Components.PanelRound Menu;
    public static Components.PanelRound Menu1;
    public static Components.PanelRound Menu2;
    public static Components.PanelRound Menu3;
    public static Components.PanelRound Menu4;
    private javax.swing.JPopupMenu MenuUsuario;
    private javax.swing.JLabel Saludo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    public static javax.swing.JLabel lbl_ini;
    public static javax.swing.JLabel lbl_ini1;
    public static javax.swing.JLabel lbl_ini2;
    public static javax.swing.JLabel lbl_ini3;
    public static javax.swing.JLabel lbl_ini4;
    // End of variables declaration//GEN-END:variables
}
