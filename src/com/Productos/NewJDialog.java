/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.Productos;

import com.bd.Conexion;
import java.sql.Connection;
import javax.swing.JButton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.Productos.itemCategoria;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;


/**
 *
 * @author Aleci
 */
public class NewJDialog extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(NewJDialog.class.getName());
    
  
    private GestionProductos parent;
    private boolean modoEditar = false;
    private int idProductoEditar = -1;

   
    public NewJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = (GestionProductos) parent;
        setUndecorated(true);
        initComponents();
        setSize(480,560);
        setLocationRelativeTo(parent);
        setLocation(getX() + 60, getY() +30);
        setResizable(false);
        
        GuardarCombox();
      
        
        jLabel1.setText("Agregar Producto");
        jButton1.setText("Agregar");
    }
    
    public NewJDialog(java.awt.Frame parent, boolean modal, 
                      int idProducto, String codigoBarras, String nombre,
                      int idCategoria, String precioCompra, String precioVenta, 
                      int cantidad, String unidadMedida) {
        super(parent, modal);
        this.parent = (GestionProductos) parent;
        this.modoEditar = true;
        this.idProductoEditar = idProducto;
        
        setUndecorated(true);
        initComponents();
        setSize(480,560);
        setLocationRelativeTo(parent);
        setLocation(getX() + 60, getY() + 30);
        setResizable(false);
        GuardarCombox();
        
     
        jLabel1.setText("Editar Producto");
        jButton1.setText("Actualizar");
        btnCerrar.setText("Cancelar");
        

        txtNombreP.setText(nombre);
        txtCodeB.setText(codigoBarras);
        txtPrecioC.setText(precioCompra);
        txtPrecioV.setText(precioVenta);
        txtCantidad.setText(String.valueOf(cantidad));
        jComboUnidad.setModel(new javax.swing.DefaultComboBoxModel<>(
       new String[] { "unidad", "metro", "kilogramo", "litro" }
   ));

       if (unidadMedida != null && !unidadMedida.isEmpty()) {
            jComboUnidad.setSelectedItem(unidadMedida);
    
        }

        seleccionarCategoria(idCategoria);
     
 
    }
    
    private void seleccionarCategoria(int idCategoria) {
        for (int i = 0; i < jComboBox1.getItemCount(); i++) {
            itemCategoria item = jComboBox1.getItemAt(i);
            if (item != null && item.getId() == idCategoria) {
                jComboBox1.setSelectedIndex(i);
                break;
            }
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtPrecioV = new javax.swing.JTextField();
        btnCerrar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtNombreP = new javax.swing.JTextField();
        txtCodeB = new javax.swing.JTextField();
        txtPrecioC = new javax.swing.JTextField();
        txtCantidad = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jComboUnidad = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtPrecioV.setEditable(true);
        txtPrecioV.setBackground(new java.awt.Color(248, 248, 255));
        txtPrecioV.setBorder(null);
        txtPrecioV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioVActionPerformed(evt);
            }
        });
        jPanel1.add(txtPrecioV, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 270, 180, 40));

        btnCerrar.setBackground(new java.awt.Color(255, 0, 0));
        btnCerrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCerrar.setForeground(new java.awt.Color(255, 255, 255));
        btnCerrar.setText("Cancelar");
        btnCerrar.setBorder(null);
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        jPanel1.add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 490, 90, 40));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Codigo de barras:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 100, 180, 20));

        jPanel2.setBackground(new java.awt.Color(0, 191, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Agregar Productos");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 220, 30));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 60));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Precio de compra:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 180, 20));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Nombre de producto:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 190, 20));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Categoria del producto");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 200, 20));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Cantidad:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 340, 180, 20));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Precio de venta:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 230, 180, 20));

        txtNombreP.setEditable(true);
        txtNombreP.setBackground(new java.awt.Color(245, 245, 245));
        txtNombreP.setBorder(null);
        txtNombreP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombrePActionPerformed(evt);
            }
        });
        jPanel1.add(txtNombreP, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 180, 40));

        txtCodeB.setEditable(true);
        txtCodeB.setBackground(new java.awt.Color(248, 248, 255));
        txtCodeB.setBorder(null);
        txtCodeB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodeBActionPerformed(evt);
            }
        });
        jPanel1.add(txtCodeB, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 140, 180, 40));

        txtPrecioC.setEditable(true);
        txtPrecioC.setBackground(new java.awt.Color(248, 248, 255));
        txtPrecioC.setBorder(null);
        txtPrecioC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioCActionPerformed(evt);
            }
        });
        jPanel1.add(txtPrecioC, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 380, 180, 40));

        txtCantidad.setEditable(true);
        txtCantidad.setBackground(new java.awt.Color(248, 248, 255));
        txtCantidad.setBorder(null);
        txtCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantidadActionPerformed(evt);
            }
        });
        jPanel1.add(txtCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 380, 180, 40));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new itemCategoria[] { }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, 160, 50));

        jButton1.setBackground(new java.awt.Color(0, 191, 255));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Agregar");
        jButton1.setBorder(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 490, 90, 40));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 500, 10));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 420, 180, 20));
        jPanel1.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 180, 20));
        jPanel1.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 180, 180, 20));
        jPanel1.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 310, 180, 20));
        jPanel1.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 420, 180, 20));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setText("Unidad de medida");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 450, 170, 30));

        jComboUnidad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Unidad", "Metro", "Kilogramo", "Litros" }));
        jPanel1.add(jComboUnidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 490, 160, 50));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtPrecioVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioVActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioVActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        // TODO add your handling code her
       dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void txtNombrePActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombrePActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombrePActionPerformed

    private void txtCodeBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodeBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodeBActionPerformed

    private void txtPrecioCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioCActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioCActionPerformed

    private void txtCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       
     
  
      
        //Validaciones
       
        if(!ValidacionesF()){
            return ;
            
        }
        
        if(!ValidarNumeros()){
            return;
        }
        
        if(!ValidarCodigoB()){
            return;
        }
        
        if(!ValidarPrecios()){
            return;
        }
        
        if(!PrecioCompra()){
            return;
        }
        
        
    
        
        
        addProducto add = new addProducto();
        
    
    if (modoEditar) {

        add.actualizarProducto(
            idProductoEditar, 
            txtNombreP, 
            txtCodeB, 
            jComboBox1, 
            txtPrecioC, 
            txtPrecioV, 
            txtCantidad,
            jComboUnidad
        );
        
        java.awt.Window window = SwingUtilities.getWindowAncestor(this);
        if(window instanceof GestionProductos){
            ((GestionProductos) window).MostrarProductos();
        }
        
    } else {
      
        add.agregarProductos(
            txtNombreP, 
            txtCodeB, 
            jComboBox1, 
            txtPrecioC, 
            txtPrecioV, 
            txtCantidad,
            jComboUnidad
        );
    }
    
  
    if (parent != null) {
        parent.MostrarProductos();
    }
    
    dispose();
       
    }//GEN-LAST:event_jButton1ActionPerformed

    
    
    
        public boolean ValidacionesF(){
        if(txtNombreP.getText().trim().isEmpty() || txtCodeB.getText().trim().isEmpty() ||txtPrecioV.getText().trim().isEmpty() || txtPrecioC.getText().trim().isEmpty() || txtCantidad.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this,"Completa todo los campos");
            return false;
        }
        
        return true;
        }
        
        
        
        public boolean ValidarNumeros(){
            try{
            
              double cantidad =Double.parseDouble(txtCantidad.getText());
          
            
            if(cantidad <=0){
            
                JOptionPane.showMessageDialog(this, "La cantidad debe ser positivo");
                 return false; 
            
            }
            
            
            return true;
            
            }catch(NumberFormatException e){
               JOptionPane.showMessageDialog(this, "La cantidad solo permite numeros enteros(no letras,simbolos y decimales)");
              
            }
            
            return false;
      
         
        }
        
        
        
        
        public boolean ValidarCodigoB(){
            
            String codigo = txtCodeB.getText().trim();
           
            
            if(!codigo.matches("\\d+")){
                
                JOptionPane.showMessageDialog(this, "El codigo de barras solo permite digitos");
                return false;
            }
            
            if(codigo.length() >8 && codigo.length() >16){
                JOptionPane.showMessageDialog(this,"El cdigo de barra solo permite de 8 a 15 digitos");
                return false;
            }
            
            return true;
        };
        
        
        
        
        public boolean ValidarPrecios(){
            
            try{
                
              String precioV = txtPrecioV.getText();
              
              BigDecimal precio = new BigDecimal(precioV);
         
              
              if(precio.compareTo(BigDecimal.ZERO)<=0){
                  
                  JOptionPane.showMessageDialog(this, "El precio de venta debe ser mayor a 0");
                  
                  
                  return false;
              }
              
              
             if(precio.scale()>2){
                 JOptionPane.showMessageDialog(this, "el precio solo permite 2 decimales");
                 return false;
             }
                 
             return true;
           
                
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(this,"El precio de venta solo acepta numero");
            }
           
            return false;
        }
        
        
        
        public boolean PrecioCompra (){
            
            
            try{
            
            String PrecioC = txtPrecioC.getText();
            
            BigDecimal PrecioCompra = new BigDecimal(PrecioC);
            
            if(PrecioCompra.compareTo(BigDecimal.ZERO)<=0){
                JOptionPane.showMessageDialog(this,"El precio de compra tiene que ser mayor que 0");
                return false;
            }
            
            if(PrecioCompra.scale()>2){
                
                JOptionPane.showMessageDialog(this, "El precio de compra solo permite 2 decimales");
                return false;
            }
            
            return true;
            }catch(NumberFormatException e){
                
                JOptionPane.showMessageDialog(this,"El precio de venta solo acepta decimales");
            }
          
            
            return false;
        }
        
       
    
        public void GuardarCombox(){
        
        try(Connection conexion = Conexion.conectar();
                
                PreparedStatement pst = conexion.prepareStatement("SELECT id, nombreCategoria FROM Categorias");
                ResultSet rs = pst.executeQuery()){
            
            while(rs.next()){
            int id = rs.getInt("id");
            String nombre = rs.getString("nombreCategoria");
            
            jComboBox1.addItem(new itemCategoria(id,nombre));
                 
            }            
            
        }catch(Exception e){
             System.out.println("Error cargando categorías: " + e.getMessage());
        }
        
        }
        
        
     
        
   

    
    
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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                NewJDialog dialog = new NewJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
        
         
        
    }
    
      public void cerrarModal(){
          
          JButton btnCerrar =  new JButton("btnCerrar");
          
          btnCerrar.addActionListener(e ->{
        
          add(btnCerrar);
      
      });
            
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton jButton1;
    public javax.swing.JComboBox<itemCategoria> jComboBox1;
    private javax.swing.JComboBox<String> jComboUnidad;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtCodeB;
    private javax.swing.JTextField txtNombreP;
    private javax.swing.JTextField txtPrecioC;
    private javax.swing.JTextField txtPrecioV;
    // End of variables declaration//GEN-END:variables
}
