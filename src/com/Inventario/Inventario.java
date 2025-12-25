/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.Inventario;
import Components.PanelRound;
import com.login.Login;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.table.DefaultTableModel;
import modeloDesign.Animate;
import com.bd.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.FileOutputStream;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.Desktop;
/**
 *
 * @author Aleci
 */
public class Inventario extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Inventario.class.getName());

    
    Animate ColorOF = new Animate();
    private Timer actualizacionTimer;
    private DecimalFormat df = new DecimalFormat("#,##0.00");
    
    
    public Inventario() {
       initComponents();
       setTitle("Inventario");
       setSize(1300,830);
       setLocationRelativeTo(null);
       setResizable(false);
       setDefaultCloseOperation(EXIT_ON_CLOSE);
       setVisible(true);
       Saludo.setText(Login.SesionUsuario.NombreUsuario);
       iniciarMenu();
       TamanoImagen();
       actualizarEstiloMenu();
       configurarNavegacionLabels();
       configurarTabla();
       cargarInventario();
       iniciarActualizacionAutomatica();
       configurarBusqueda();
       configurarBotonDescargar();
       
  
    }
    
    private void configurarTabla() {
        String[] columnas = {
            "Producto", 
            "Stock Actual", 
            "Unidades Vendidas", 
            "Valor Total", 
            "Precio Venta", 
            "Nivel de Rotación"
        };
        
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTableReporte.setModel(modelo);
          
        JTableReporte.getColumnModel().getColumn(0).setPreferredWidth(200);  
        JTableReporte.getColumnModel().getColumn(1).setPreferredWidth(100);  
        JTableReporte.getColumnModel().getColumn(2).setPreferredWidth(120);  
        JTableReporte.getColumnModel().getColumn(3).setPreferredWidth(120);  
        JTableReporte.getColumnModel().getColumn(4).setPreferredWidth(100);  
        JTableReporte.getColumnModel().getColumn(5).setPreferredWidth(150);  
        
       
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        
        for (int i = 0; i < JTableReporte.getColumnCount(); i++) {
            if (i != 1) {
                JTableReporte.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        
  
        JTableReporte.setRowHeight(30);
    }
    
    public void cargarInventario() {
        cargarInventario("");
    }
    
    private void cargarInventario(String busqueda) {
        DefaultTableModel modelo = (DefaultTableModel) JTableReporte.getModel();
        modelo.setRowCount(0);
        
        String sql = "SELECT " +
                    "p.nombreProducto, " +
                    "p.cantidad AS stockActual, " +
                    "COALESCE(SUM(dv.cantidad), 0) AS unidadesVendidas, " +
                    "p.precioVenta, " +
                    "(p.cantidad * p.precioVenta) AS valorTotal " +
                    "FROM Productos p " +
                    "LEFT JOIN DetalleVentas dv ON p.id = dv.idProducto " +
                    "WHERE p.nombreProducto LIKE ? OR p.codigoBarras LIKE ? " +
                    "GROUP BY p.id, p.nombreProducto, p.cantidad, p.precioVenta " +
                    "ORDER BY p.nombreProducto";
        
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            String parametroBusqueda = "%" + busqueda + "%";
            pst.setString(1, parametroBusqueda);
            pst.setString(2, parametroBusqueda);
            
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String nombre = rs.getString("nombreProducto");
                int stockActual = rs.getInt("stockActual");
                int unidadesVendidas = rs.getInt("unidadesVendidas");
                double precioVenta = rs.getDouble("precioVenta");
                double valorTotal = rs.getDouble("valorTotal");
                
                String nivelRotacion = calcularNivelRotacion(unidadesVendidas, stockActual);
                
                Object[] fila = {
                    nombre,
                    stockActual + " unidades",
                    unidadesVendidas + " unidades",
                    "$" + df.format(valorTotal),
                    "$" + df.format(precioVenta),
                    nivelRotacion
                };
                
                modelo.addRow(fila);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar inventario: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, "Error al cargar inventario", e);
        }
    }
    
    private String calcularNivelRotacion(int unidadesVendidas, int stockActual) {
        if (stockActual == 0) {
            return "⚠️ Sin Stock";
        }
        
        double rotacion = (double) unidadesVendidas / (stockActual + unidadesVendidas);
        
        if (rotacion >= 0.7) {
            return "Rápida ";
        } else if (rotacion >= 0.4) {
            return "Media";
        } else if (rotacion >= 0.1) {
            return "Lenta";
        } else {
            return "️Muy Lenta";
        }
    }
    
    private void iniciarActualizacionAutomatica() {
        // Actualizar cada 5 segundos
        actualizacionTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String textoBusqueda = jTextField1.getText().trim();
                cargarInventario(textoBusqueda);
            }
        });
        actualizacionTimer.start();
    }
    
    private void configurarBusqueda() {
        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscar();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                buscar();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                buscar();
            }
            
            private void buscar() {
                String texto = jTextField1.getText().trim();
                cargarInventario(texto);
            }
        });
    }
    
    
     private void configurarBotonDescargar() {
        ButtonDescargar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                descargarPDF();
            }
        });
    }
     
     
     
     private void descargarPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Inventario");
        
        // Nombre sugerido con fecha y hora
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
        String nombreSugerido = "Inventario_" + sdf.format(new Date()) + ".pdf";
        fileChooser.setSelectedFile(new File(nombreSugerido));
        
        // Filtro para solo archivos PDF
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf");
        fileChooser.setFileFilter(filter);
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Asegurar que tenga extensión .pdf
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }
            
            try {
                exportarAPDF(fileToSave);
                JOptionPane.showMessageDialog(this,
                    "¡Reporte descargado exitosamente!\n" + fileToSave.getAbsolutePath(),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                    
                // Preguntar si desea abrir el archivo
                int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Desea abrir el archivo ahora?",
                    "Abrir archivo",
                    JOptionPane.YES_NO_OPTION);
                    
                if (respuesta == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(fileToSave);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al generar el archivo: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                logger.log(java.util.logging.Level.SEVERE, "Error al exportar PDF", ex);
            }
        }
    }
    
    private void exportarAPDF(File archivo) throws Exception {
        Document document = new Document(PageSize.A4.rotate()); // Horizontal para más espacio
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(archivo));
        
        document.open();
        
        // Fuentes
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
        
        // Título
        Paragraph title = new Paragraph("REPORTE DE INVENTARIO", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        document.add(title);
        
        // Subtítulo
        Paragraph subtitle = new Paragraph("ARCANGEL MIGUEL", titleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(10);
        document.add(subtitle);
        
        // Fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Paragraph date = new Paragraph("Fecha de generación: " + sdf.format(new Date()), subtitleFont);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(20);
        document.add(date);
        
        // Crear tabla
        DefaultTableModel model = (DefaultTableModel) JTableReporte.getModel();
        PdfPTable table = new PdfPTable(model.getColumnCount());
        table.setWidthPercentage(100);
        
        // Anchos de columnas proporcionales
        float[] columnWidths = {3f, 2f, 2.5f, 2f, 2f, 2.5f};
        table.setWidths(columnWidths);
        
        // Encabezados
        for (int i = 0; i < model.getColumnCount(); i++) {
            PdfPCell headerCell = new PdfPCell(new Phrase(model.getColumnName(i), headerFont));
            headerCell.setBackgroundColor(new BaseColor(0, 51, 153)); // Azul oscuro
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setPadding(8);
            table.addCell(headerCell);
        }
        
        // Datos con colores alternados
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                Object value = model.getValueAt(i, j);
                PdfPCell dataCell = new PdfPCell(new Phrase(value != null ? value.toString() : "", dataFont));
                
                // Alternar colores de fila
                if (i % 2 == 0) {
                    dataCell.setBackgroundColor(new BaseColor(240, 248, 255)); // Azul muy claro
                } else {
                    dataCell.setBackgroundColor(BaseColor.WHITE);
                }
                
                // Alineación
                if (j == 0) {
                    dataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                } else {
                    dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                
                dataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                dataCell.setPadding(6);
                table.addCell(dataCell);
            }
        }
        
        document.add(table);
        
        // Resumen
        Paragraph summary = new Paragraph("\n\nTotal de productos en inventario: " + model.getRowCount(), 
                                         FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11));
        summary.setSpacingBefore(15);
        document.add(summary);
        
        // Pie de página
        Paragraph footer = new Paragraph("\n\nGenerado por Sistema de Inventario Arcangel Miguel", 
                                        FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
        
        document.close();
        writer.close();
    }
    
    
    
    
    
    
    
    
    
    
    private void configurarNavegacionLabels() {
    
    JreporteGeneral.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    JValorInventario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
 
    

    JreporteGeneral.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mousePressed(java.awt.event.MouseEvent evt) {
    
            System.out.println("Ya estás en Reporte General");
        }
        
        public void mouseEntered(java.awt.event.MouseEvent evt) {
       
            JreporteGeneral.setForeground(new java.awt.Color(0, 191, 255));
        }
        
        public void mouseExited(java.awt.event.MouseEvent evt) {
          
            JreporteGeneral.setForeground(new java.awt.Color(0, 0, 0));
        }
    });
    
 
    JValorInventario.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mousePressed(java.awt.event.MouseEvent evt) {
            navegarAValorInventario();
        }
        
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            JValorInventario.setForeground(new java.awt.Color(0, 191, 255));
        }
        
        public void mouseExited(java.awt.event.MouseEvent evt) {
            JValorInventario.setForeground(new java.awt.Color(0, 0, 0));
        }
    });
    
   
    
}

private void navegarAValorInventario() {
    Navegation.mostrarValorInventario();
    this.setVisible(false);
}

    

     private void actualizarEstiloMenu() {
        PanelRound[] menus = {Menu, Menu1, Menu2, Menu3, Menu4};
        JLabel[] labels = {lbl_ini, lbl_ini1, lbl_ini2, lbl_ini3, lbl_ini4};
        ColorOF.activarInventario(menus, labels);
    }
    
     public void TamanoImagen(){
        ImageIcon Imagen = new ImageIcon(getClass().getResource("/com/Imagenes/miguel.jpg"));
        
        Image TamaImage = Imagen.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
        
        ImageIcon icono = new ImageIcon(TamaImage);
        
        AngelMiguel.setIcon(icono);
        
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
                  
                   Inventario.this.dispose();
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
        JTableReporte = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        Saludo = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        ButtonDescargar = new javax.swing.JButton();
        JValorInventario = new javax.swing.JLabel();
        JreporteGeneral = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();

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

        JTableReporte.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(JTableReporte);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 210, 1000, 610));

        jPanel3.setBackground(new java.awt.Color(135, 206, 250));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Inventario");
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

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ButtonDescargar.setBackground(new java.awt.Color(0, 191, 255));
        ButtonDescargar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        ButtonDescargar.setForeground(new java.awt.Color(255, 255, 255));
        ButtonDescargar.setText("Descargar");
        ButtonDescargar.setBorder(null);
        jPanel4.add(ButtonDescargar, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 10, 110, 50));

        JValorInventario.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        JValorInventario.setText("Valor de Inventario");
        jPanel4.add(JValorInventario, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, 220, -1));

        JreporteGeneral.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        JreporteGeneral.setText("Reporte General");
        jPanel4.add(JreporteGeneral, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 180, -1));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 130, 1000, 70));

        jTextField1.setBackground(new java.awt.Color(255, 255, 255));
        jTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 70, 180, 40));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel4.setText("Buscar:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 60, 90, 50));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1303, Short.MAX_VALUE)
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
        if(this.isVisible()) return;
    
        Navegation.mostrarInventario();
        this.setVisible(false);
    }//GEN-LAST:event_lbl_ini4MousePressed

    private void Menu4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu4MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_Menu4MouseEntered

    private void Menu4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu4MousePressed
       if(this.isVisible()) return;
      
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
        java.awt.EventQueue.invokeLater(() -> new Inventario().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AngelMiguel;
    private javax.swing.JButton ButtonDescargar;
    private javax.swing.JTable JTableReporte;
    private javax.swing.JLabel JValorInventario;
    private javax.swing.JLabel JreporteGeneral;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    public static javax.swing.JLabel lbl_ini;
    public static javax.swing.JLabel lbl_ini1;
    public static javax.swing.JLabel lbl_ini2;
    public static javax.swing.JLabel lbl_ini3;
    public static javax.swing.JLabel lbl_ini4;
    // End of variables declaration//GEN-END:variables
}
