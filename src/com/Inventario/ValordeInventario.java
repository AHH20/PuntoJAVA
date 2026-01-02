/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.Inventario;

import Components.PanelRound;
import com.Controlador.Navegation;
import com.login.Login;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
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
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Aleci
 */
public class ValordeInventario extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ValordeInventario.class.getName());

   Animate ColorOF = new Animate();
   private Timer actualizacionTimer;
   private DecimalFormat df = new DecimalFormat("#,##0.00");
    public ValordeInventario() {
        initComponents();
        setTitle("Inventario");
       setSize(1350,700);
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
        cargarValorInventario();
        iniciarActualizacionAutomatica();
        configurarBusqueda();
        configurarBotonDescargar();
        configurarOrdenamiento();
       
    }
    
    
private void configurarNavegacionLabels() {
    JreporteGeneral.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    JReporteServicios.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    JValorInventario1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    
    // Navegar a Inventario (Reporte General)
    JreporteGeneral.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mousePressed(java.awt.event.MouseEvent evt) {
            Navegation.mostrarInventario();
            ValordeInventario.this.setVisible(false);
        }
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            JreporteGeneral.setForeground(new java.awt.Color(0, 191, 255));
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            JreporteGeneral.setForeground(new java.awt.Color(0, 0, 0));
        }
    });
    
    // ⭐ NAVEGAR A REPORTE SERVICIOS
    JReporteServicios.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mousePressed(java.awt.event.MouseEvent evt) {
            Navegation.mostrarReporteServicios();
            ValordeInventario.this.setVisible(false);
        }
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            JReporteServicios.setForeground(new java.awt.Color(0, 191, 255));
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            JReporteServicios.setForeground(new java.awt.Color(0, 0, 0));
        }
    });
    
    // YA ESTAMOS EN VALOR DE INVENTARIO
    JValorInventario1.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mousePressed(java.awt.event.MouseEvent evt) {
            System.out.println("Ya estás en Valor de Inventario");
        }
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            JValorInventario1.setForeground(new java.awt.Color(0, 191, 255));
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            JValorInventario1.setForeground(new java.awt.Color(0, 0, 0));
        }
    });
}
    
    
    private void configurarTabla() {
        String[] columnas = {
            "Producto", 
            "Stock Actual", 
            "Valor Total", 
            "Costo Total", 
            "Ganancia Total"
        };
        
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTableValor.setModel(modelo);
          
        JTableValor.getColumnModel().getColumn(0).setPreferredWidth(250);
        JTableValor.getColumnModel().getColumn(1).setPreferredWidth(150);
        JTableValor.getColumnModel().getColumn(2).setPreferredWidth(180);
        JTableValor.getColumnModel().getColumn(3).setPreferredWidth(180);
        JTableValor.getColumnModel().getColumn(4).setPreferredWidth(180);
        
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        
        for (int i = 1; i < JTableValor.getColumnCount(); i++) {
            JTableValor.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JTableValor.setRowHeight(30);
    }
    
    public void cargarValorInventario() {
        cargarValorInventario("");
    }
    
    private void cargarValorInventario(String busqueda) {
        DefaultTableModel modelo = (DefaultTableModel) JTableValor.getModel();
        modelo.setRowCount(0);
        
        String sql = "SELECT " +
                    "p.nombreProducto, " +
                    "p.cantidad AS stockActual, " +
                    "p.precioVenta, " +
                    "p.precioDeCompra, " +
                    "p.unidadMedida,"+
                    "(p.cantidad * p.precioVenta) AS valorTotal, " +
                    "(p.cantidad * p.precioDeCompra) AS costoTotal, " +
                    "((p.cantidad * p.precioVenta) - (p.cantidad * p.precioDeCompra)) AS gananciaTotal " +
                    "FROM Productos p " +
                    "WHERE p.nombreProducto LIKE ? OR p.codigoBarras LIKE ? " +
                    "ORDER BY valorTotal DESC";
        
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            String parametroBusqueda = "%" + busqueda + "%";
            pst.setString(1, parametroBusqueda);
            pst.setString(2, parametroBusqueda);
            
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String nombre = rs.getString("nombreProducto");
                int stockActual = rs.getInt("stockActual");
                String unidadMedida = rs.getString("unidadMedida");
                double valorTotal = rs.getDouble("valorTotal");
                double costoTotal = rs.getDouble("costoTotal");
                double gananciaTotal = rs.getDouble("gananciaTotal");
                
                if(unidadMedida == null || unidadMedida.trim().isEmpty()){
                    unidadMedida = "unidadMedida";
                }
                
                Object[] fila = {
                    nombre,
                   df.format(stockActual) + " " + unidadMedida,
                    "$" + df.format(valorTotal),
                    "$" + df.format(costoTotal),
                    "$" + df.format(gananciaTotal)
                };
                
                modelo.addRow(fila);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar valor de inventario: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, "Error al cargar valor de inventario", e);
        }
    }
    
    private void iniciarActualizacionAutomatica() {
        actualizacionTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String textoBusqueda = jTextField1.getText().trim();
                cargarValorInventario(textoBusqueda);
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
                cargarValorInventario(texto);
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
        fileChooser.setDialogTitle("Guardar Reporte de Valor de Inventario");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
        String nombreSugerido = "ValorInventario_" + sdf.format(new Date()) + ".pdf";
        fileChooser.setSelectedFile(new File(nombreSugerido));
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf");
        fileChooser.setFileFilter(filter);
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }
            
            try {
                exportarAPDF(fileToSave);
                JOptionPane.showMessageDialog(this,
                    "¡Reporte descargado exitosamente!\n" + fileToSave.getAbsolutePath(),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                    
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
    Document document = new Document(PageSize.A4.rotate());
    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(archivo));
    
    document.open();
    
    Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
    Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
    Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
    Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);
    
    Paragraph title = new Paragraph("REPORTE DE VALOR DE INVENTARIO", titleFont);
    title.setAlignment(Element.ALIGN_CENTER);
    title.setSpacingAfter(5);
    document.add(title);
    
    Paragraph subtitle = new Paragraph("ARCANGEL MIGUEL", titleFont);
    subtitle.setAlignment(Element.ALIGN_CENTER);
    subtitle.setSpacingAfter(10);
    document.add(subtitle);
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Paragraph date = new Paragraph("Fecha de generación: " + sdf.format(new Date()), subtitleFont);
    date.setAlignment(Element.ALIGN_CENTER);
    date.setSpacingAfter(20);
    document.add(date);
    
    DefaultTableModel model = (DefaultTableModel) JTableValor.getModel();
    PdfPTable table = new PdfPTable(model.getColumnCount());
    table.setWidthPercentage(100);
    
    float[] columnWidths = {3f, 2f, 2f, 2f, 2f};
    table.setWidths(columnWidths);
    
    for (int i = 0; i < model.getColumnCount(); i++) {
        PdfPCell headerCell = new PdfPCell(new Phrase(model.getColumnName(i), headerFont));
        headerCell.setBackgroundColor(new BaseColor(0, 51, 153));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setPadding(8);
        table.addCell(headerCell);
    }
    
    double totalValor = 0;
    double totalCosto = 0;
    double totalGanancia = 0;
    
    for (int i = 0; i < model.getRowCount(); i++) {
        for (int j = 0; j < model.getColumnCount(); j++) {
            Object value = model.getValueAt(i, j);
            String strValue = value != null ? value.toString() : "";
            
            // ✅ Sumar correctamente los valores
            if (j == 2) {
                totalValor += parseMoney(strValue);
            } else if (j == 3) {
                totalCosto += parseMoney(strValue);
            } else if (j == 4) {
                totalGanancia += parseMoney(strValue);
            }
            
            PdfPCell dataCell = new PdfPCell(new Phrase(strValue, dataFont));
            
            if (i % 2 == 0) {
                dataCell.setBackgroundColor(new BaseColor(240, 248, 255));
            } else {
                dataCell.setBackgroundColor(BaseColor.WHITE);
            }
            
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
    
    Paragraph summary = new Paragraph("\n\nRESUMEN FINANCIERO", summaryFont);
    summary.setSpacingBefore(15);
    summary.setSpacingAfter(10);
    document.add(summary);
    
    Paragraph summaryDetails = new Paragraph(
        "Total de productos: " + model.getRowCount() + "\n" +
        "Valor total del inventario: $" + df.format(totalValor) + "\n" +
        "Costo total del inventario: $" + df.format(totalCosto) + "\n" +
        "Ganancia potencial total: $" + df.format(totalGanancia),
        FontFactory.getFont(FontFactory.HELVETICA, 10)
    );
    document.add(summaryDetails);
    
    Paragraph footer = new Paragraph("\n\nGenerado por Sistema de Inventario Arcangel Miguel", 
                                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY));
    footer.setAlignment(Element.ALIGN_CENTER);
    document.add(footer);
    
    document.close();
    writer.close();
}
    
   
  private double parseMoney(String moneyString) {
    try {
        // Remover el símbolo $, comas y espacios
        String cleaned = moneyString.replace("$", "")
                                    .replace(",", "")
                                    .replace(" ", "")
                                    .trim();
        return Double.parseDouble(cleaned);
    } catch (Exception e) {
        logger.log(java.util.logging.Level.WARNING, "Error parseando: " + moneyString, e);
        return 0.0;
    }
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
                  
                   ValordeInventario.this.dispose();
              }
        
        });
        
    }
    
    
    public Icon getIcon( String ruta, int width, int height){

        Icon miIcono = new ImageIcon(new ImageIcon(getClass().getResource(ruta)).getImage().getScaledInstance(width, height,0));
        return miIcono;
        }
    
    
    private void configurarOrdenamiento() {
    javax.swing.table.TableRowSorter<DefaultTableModel> sorter = 
        new javax.swing.table.TableRowSorter<>((DefaultTableModel) JTableValor.getModel());
    
    JTableValor.setRowSorter(sorter);
    
    // Comparador para Stock Actual (columna 1)
    sorter.setComparator(1, new java.util.Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            try {
                int num1 = Integer.parseInt(o1.replaceAll("[^0-9]", ""));
                int num2 = Integer.parseInt(o2.replaceAll("[^0-9]", ""));
                return Integer.compare(num1, num2);
            } catch (NumberFormatException e) {
                return o1.compareTo(o2);
            }
        }
    });
    
    // Comparador para Valor Total (columna 2)
    sorter.setComparator(2, new java.util.Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            try {
                String clean1 = o1.replaceAll("[$,\\s]", "");
                String clean2 = o2.replaceAll("[$,\\s]", "");
                double num1 = Double.parseDouble(clean1);
                double num2 = Double.parseDouble(clean2);
                return Double.compare(num1, num2);
            } catch (NumberFormatException e) {
                return o1.compareTo(o2);
            }
        }
    });
    
    // Comparador para Costo Total (columna 3)
    sorter.setComparator(3, new java.util.Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            try {
                String clean1 = o1.replaceAll("[$,\\s]", "");
                String clean2 = o2.replaceAll("[$,\\s]", "");
                double num1 = Double.parseDouble(clean1);
                double num2 = Double.parseDouble(clean2);
                return Double.compare(num1, num2);
            } catch (NumberFormatException e) {
                return o1.compareTo(o2);
            }
        }
    });
    
    // Comparador para Ganancia Total (columna 4)
    sorter.setComparator(4, new java.util.Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            try {
                String clean1 = o1.replaceAll("[$,\\s]", "");
                String clean2 = o2.replaceAll("[$,\\s]", "");
                double num1 = Double.parseDouble(clean1);
                double num2 = Double.parseDouble(clean2);
                return Double.compare(num1, num2);
            } catch (NumberFormatException e) {
                return o1.compareTo(o2);
            }
        }
    });
}
    
    
 
    
    
    
    private void actualizarEstiloMenu() {
        PanelRound[] menus = {Menu, Menu1, Menu2, Menu3, Menu4};
        JLabel[] labels = {lbl_ini, lbl_ini1, lbl_ini2, lbl_ini3, lbl_ini4};
        ColorOF.activarValor(menus, labels);
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
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        Saludo = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        ButtonDescargar = new javax.swing.JButton();
        JReporteServicios = new javax.swing.JLabel();
        JreporteGeneral = new javax.swing.JLabel();
        JValorInventario1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        JTableValor = new javax.swing.JTable();

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
        Menu.add(lbl_ini, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 160, 32));

        jPanel2.add(Menu, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, 170, 40));

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

        jPanel2.add(Menu1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 320, 170, 40));

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
        Menu2.add(lbl_ini2, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 160, 32));

        jPanel2.add(Menu2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 390, 170, 40));

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

        jPanel2.add(Menu3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 460, 170, 40));

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

        jPanel2.add(Menu4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 530, 170, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 830));

        jPanel3.setBackground(new java.awt.Color(135, 206, 250));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Reporte de Inventario");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 470, 30));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/Imagenes/avatar (1).png"))); // NOI18N
        jLabel1.setText("jLabel1");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 10, 30, -1));

        Saludo.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Saludo.setForeground(new java.awt.Color(255, 255, 255));
        Saludo.setText("Dashboard");
        Saludo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.add(Saludo, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 10, 150, 30));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 0, 1110, 50));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ButtonDescargar.setBackground(new java.awt.Color(0, 191, 255));
        ButtonDescargar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        ButtonDescargar.setForeground(new java.awt.Color(255, 255, 255));
        ButtonDescargar.setText("Descargar");
        ButtonDescargar.setBorder(null);
        jPanel4.add(ButtonDescargar, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 10, 110, 50));

        JReporteServicios.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        JReporteServicios.setText("Reporte Servicios");
        jPanel4.add(JReporteServicios, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 20, 220, -1));

        JreporteGeneral.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        JreporteGeneral.setText("Reporte General");
        jPanel4.add(JreporteGeneral, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 180, -1));

        JValorInventario1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        JValorInventario1.setText("Valor de Inventario");
        jPanel4.add(JValorInventario1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, 220, -1));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 130, 1050, 70));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel4.setText("Buscar:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 60, 90, 50));

        jTextField1.setBackground(new java.awt.Color(255, 255, 255));
        jTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 70, 180, 40));

        JTableValor.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(JTableValor);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 210, 1050, 450));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
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
        java.awt.EventQueue.invokeLater(() -> new ValordeInventario().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AngelMiguel;
    private javax.swing.JButton ButtonDescargar;
    private javax.swing.JLabel JReporteServicios;
    private javax.swing.JTable JTableValor;
    private javax.swing.JLabel JValorInventario1;
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


