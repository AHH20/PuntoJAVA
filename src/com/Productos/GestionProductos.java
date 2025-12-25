
package com.Productos;

import Components.PanelRound;
import com.Dashboard.Ajustes;
import com.Dashboard.Dashboard;
import com.login.Login;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import com.Controlador.Navegation;
import com.Productos.addProducto;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import modeloDesign.Animate;




public class GestionProductos extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GestionProductos.class.getName());

   Animate ColorOF =  new Animate();
   
   private TableRowSorter<DefaultTableModel> sorter;
   
   
    public GestionProductos() {
       initComponents();
       setTitle("Gestion Productos");
       setSize(1300,830);
       setLocationRelativeTo(null);
       setResizable(false);
       setDefaultCloseOperation(EXIT_ON_CLOSE);
       setVisible(true);
       Saludo.setText(Login.SesionUsuario.NombreUsuario);
       iniciarMenu();
       TamanoImagen();
       actualizarEstiloMenu();
       MostrarProductos();
       FiltrarBusquedas();
        
    }
    
    
    
    private void actualizarEstiloMenu() {
        PanelRound[] menus = {Menu, Menu1, Menu2, Menu3, Menu4};
        JLabel[] labels = {lbl_ini, lbl_ini1, lbl_ini2, lbl_ini3, lbl_ini4};
        ColorOF.activarProductos(menus, labels);
    }
    
    public void MostrarProductos(){
        
        
        tablaMuestraProducto.setRowSorter(null);
        
        addProducto MostrarP = new addProducto();
        MostrarP.mostrarProductos(tablaMuestraProducto);
        
        sorter = new TableRowSorter<>((DefaultTableModel)tablaMuestraProducto.getModel());
        tablaMuestraProducto.setRowSorter(sorter);
        
        txtBusquedaP.setText("");
        
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
                
                GestionProductos.this.dispose();
                
              
                
     
            }
        });
        
        Cerrar.addActionListener(new ActionListener(){
               @Override
              public void actionPerformed(ActionEvent e){
              
                  Login inicio = new Login();
                  
                  inicio.setVisible(true);
                  
                   GestionProductos.this.dispose();
              }
        
        });
        
    }
    
    
    public void FiltrarBusquedas(){
        sorter = new TableRowSorter<>((DefaultTableModel) tablaMuestraProducto.getModel());
        tablaMuestraProducto.setRowSorter(sorter);
        
        txtBusquedaP.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            
            @Override
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            
            @Override
            public void changedUpdate(DocumentEvent e) { filtrar(); }
            
            private void filtrar() {
                String texto = txtBusquedaP.getText().trim();
                
                if(texto.isEmpty()){
                    sorter.setRowFilter(null);
                    jLabel4.setVisible(false);
                    return;
                }
                
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0));
                
                jLabel4.setVisible(tablaMuestraProducto.getRowCount() == 0);
                jLabel4.setText("No existe el producto");
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
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaMuestraProducto = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        txtBusquedaP = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
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

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Productos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tablaMuestraProducto.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tablaMuestraProducto);

        jPanel5.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 950, 580));

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 150, 1010, 640));

        jButton1.setBackground(new java.awt.Color(0, 191, 255));
        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Agregar");
        jButton1.setBorder(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 60, 110, 50));

        txtBusquedaP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtBusquedaP.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtBusquedaP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBusquedaPActionPerformed(evt);
            }
        });
        jPanel1.add(txtBusquedaP, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 100, 200, 40));
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 100, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setText("Buscar Productos: ");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 60, -1, -1));

        jPanel3.setBackground(new java.awt.Color(135, 206, 250));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Gestion Productos");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 350, 30));

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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
       if(this.isVisible()) return;
     
        Navegation.mostrarProductos();
         this.setVisible(false);

    }//GEN-LAST:event_lbl_ini3MousePressed

    private void Menu3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu3MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_Menu3MouseEntered

    private void Menu3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Menu3MousePressed
       if(this.isVisible()) return;
       
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        
        NewJDialog producto =  new NewJDialog(this,true);
        producto.setVisible(true);
        MostrarProductos();
        
     
     
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtBusquedaPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBusquedaPActionPerformed
     
        
    }//GEN-LAST:event_txtBusquedaPActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new GestionProductos().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AngelMiguel;
    public static Components.PanelRound Menu;
    public static Components.PanelRound Menu1;
    public static Components.PanelRound Menu2;
    public static Components.PanelRound Menu3;
    public static Components.PanelRound Menu4;
    private javax.swing.JPopupMenu MenuUsuario;
    private javax.swing.JLabel Saludo;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JLabel lbl_ini;
    public static javax.swing.JLabel lbl_ini1;
    public static javax.swing.JLabel lbl_ini2;
    public static javax.swing.JLabel lbl_ini3;
    public static javax.swing.JLabel lbl_ini4;
    private javax.swing.JTable tablaMuestraProducto;
    private javax.swing.JTextField txtBusquedaP;
    // End of variables declaration//GEN-END:variables

    
}
