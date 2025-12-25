
package com.Productos;

import com.bd.Conexion;
import java.math.BigDecimal;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.sql.Connection;



/**
 *
 * @author Aleci
 */
public class addProducto {
    
    // Agregar productos
    public void agregarProductos(JTextField nombreProducto, JTextField codigoBarras, JComboBox idCategoria, JTextField precioDeCompra, JTextField precioVenta,JTextField cantidad){
        
        Conexion ConexionProducto = new Conexion();
        EntidadProductos entityProducto = new EntidadProductos();
        

        
        String Consulta = "INSERT INTO Productos(nombreProducto,codigoBarras,idCategoria,precioDeCompra,precioVenta,cantidad)VALUES(?,?,?,?,?,?)";
        
        
        try{
            entityProducto.setNombreProducto(nombreProducto.getText());
             entityProducto.setCodigoBarras(codigoBarras.getText());
               entityProducto.setPrecioDeCompra(new BigDecimal(precioDeCompra.getText()));
                entityProducto.setPrecioVenta(new BigDecimal(precioVenta.getText()));
                 entityProducto.setCantidad(Integer.parseInt(cantidad.getText()));
                 
         itemCategoria categoria = (itemCategoria) idCategoria.getSelectedItem();
        if (categoria != null) {
            entityProducto.setIdCategoria(categoria.getId());
            
         PreparedStatement cs = ConexionProducto.conectar().prepareStatement(Consulta);
            
            cs.setString(1, entityProducto.getNombreProducto());
            cs.setString(2, entityProducto.getCodigoBarras());
            cs.setInt(3, entityProducto.getIdCategoria());
            cs.setBigDecimal(4, entityProducto.getPrecioDeCompra());
            cs.setBigDecimal(5, entityProducto.getPrecioVenta());
            cs.setInt(6, entityProducto.getCantidad());
             
           cs.execute();
           
            JOptionPane.showMessageDialog(null, "Se agrego Producto correctamente");
            
         
            
        }
            
        }catch(Exception e){
            
            System.out.println("error" + e);
            
            
        }finally{
            
            
        }
        
        

    }
    
       // Mostrar
        
       public void mostrarProductos(JTable tablaMuestraProducto){
    
    Conexion conexionProducto = new Conexion();
   
    DefaultTableModel modelo = new DefaultTableModel(){
        @Override
        public boolean isCellEditable (int row, int column){
            return column == 4 || column == 5;
        }
    };
   

    modelo.addColumn("Nombre");        
    modelo.addColumn("Categoria");     
    modelo.addColumn("Precio");       
    modelo.addColumn("Cantidad");     
    modelo.addColumn("Editar");       
    modelo.addColumn("Eliminar");
    
 
    modelo.addColumn("id");            
    modelo.addColumn("CodigoBarras");  
    modelo.addColumn("IDCategoria");   
    modelo.addColumn("PrecioCompra");  
    
    tablaMuestraProducto.setModel(modelo);
    tablaMuestraProducto.setRowHeight(30);
    tablaMuestraProducto.getColumn("Precio").setCellRenderer(new PrecioRenderer());
    

    String Consulta = 
    "SELECT p.id, p.nombreProducto, p.codigoBarras, p.idCategoria, c.nombreCategoria, " +
    "p.precioDeCompra, p.precioVenta, p.cantidad " +
    "FROM Productos p INNER JOIN Categorias c ON p.idCategoria = c.id";
    
    try{
        Statement st = conexionProducto.conectar().createStatement();
        ResultSet rs = st.executeQuery(Consulta);
        
        while(rs.next()){

            modelo.addRow(new Object[]{
         
                rs.getString("nombreProducto"),      
                rs.getString("nombreCategoria"),    
                rs.getBigDecimal("precioVenta"),     
                rs.getInt("cantidad"),               
                "Editar",                            
                "Eliminar",                          
             
                rs.getInt("id"),                    
                rs.getString("codigoBarras"),        
                rs.getInt("idCategoria"),           
                rs.getBigDecimal("precioDeCompra")   
            });
        }
        
        tablaMuestraProducto.setModel(modelo);
        
     
        OcultarC(tablaMuestraProducto, 6);
        OcultarC(tablaMuestraProducto, 7);
        OcultarC(tablaMuestraProducto, 8);
        OcultarC(tablaMuestraProducto, 9);
        

        tablaMuestraProducto.getColumn("Editar")
            .setCellRenderer(new ButtonRenderer("/com/Imagenes/editar.png"));
        tablaMuestraProducto.getColumn("Editar")
            .setCellEditor(new ButtonEditar(new JCheckBox(), "editar", this)); 
        
        tablaMuestraProducto.getColumn("Eliminar")
            .setCellRenderer(new ButtonRenderer("/com/Imagenes/eliminar.png"));
        tablaMuestraProducto.getColumn("Eliminar")
            .setCellEditor(new ButtonEditar(new JCheckBox(), "eliminar", this));
        
        

    }catch(Exception e){
        JOptionPane.showMessageDialog(null, "No se mostraron los productos: " + e);
        e.printStackTrace();
    }
       }
       
       
       private void OcultarC (JTable tablaMuestraProducto, int columna){
           tablaMuestraProducto.getColumnModel().getColumn(columna).setMinWidth(0);
           tablaMuestraProducto.getColumnModel().getColumn(columna).setMaxWidth(0);
           tablaMuestraProducto.getColumnModel().getColumn(columna).setWidth(0);
           tablaMuestraProducto.getColumnModel().getColumn(columna).setPreferredWidth(0);
           
           
       }
       
       
       //Actualizar
     public void actualizarProducto(int idProducto, JTextField nombreProducto, 
                               JTextField codigoBarras, JComboBox idCategoria, 
                               JTextField precioDeCompra, JTextField precioVenta, 
                               JTextField cantidad){
    
    Conexion ConexionProducto = new Conexion();
    EntidadProductos entityProductos = new EntidadProductos();
    
    String Consulta = "UPDATE Productos SET nombreProducto=?, codigoBarras=?, " +
                      "idCategoria=?, precioDeCompra=?, precioVenta=?, cantidad=? " +
                      "WHERE id=?";
    
    try{
        entityProductos.setNombreProducto(nombreProducto.getText().trim());
        entityProductos.setCodigoBarras(codigoBarras.getText().trim());
        entityProductos.setPrecioDeCompra(new BigDecimal(precioDeCompra.getText().trim()));
        entityProductos.setPrecioVenta(new BigDecimal(precioVenta.getText().trim()));
        entityProductos.setCantidad(Integer.parseInt(cantidad.getText().trim()));
        
        itemCategoria categoria = (itemCategoria) idCategoria.getSelectedItem();
        if (categoria != null) {
            entityProductos.setIdCategoria(categoria.getId());
            
            PreparedStatement cs = ConexionProducto.conectar().prepareStatement(Consulta);
            
            cs.setString(1, entityProductos.getNombreProducto());
            cs.setString(2, entityProductos.getCodigoBarras());
            cs.setInt(3, entityProductos.getIdCategoria());
            cs.setBigDecimal(4, entityProductos.getPrecioDeCompra());
            cs.setBigDecimal(5, entityProductos.getPrecioVenta());
            cs.setInt(6, entityProductos.getCantidad());
            cs.setInt(7, idProducto); // ✅ Usar el int directamente
            
            int filasActualizadas = cs.executeUpdate();
            
            if (filasActualizadas > 0) {
                JOptionPane.showMessageDialog(null, "Producto actualizado correctamente");
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo actualizar el producto");
            }
            
            cs.close();
        }
    } catch(Exception e){
        JOptionPane.showMessageDialog(null, "Error al actualizar: " + e.getMessage());
        e.printStackTrace();
    }
       
       
       
    
     }
     
     
     public boolean CodigoExiste(String codigoBarras){
            
            String sql = "SELECT COUNT(*) FROM Productos WHERE codigoBarras = ?";
            
         
            
            
            try( Connection conn = new Conexion().conectar();
                 PreparedStatement ps = conn.prepareStatement(sql)){
                
                ps.setString(1, codigoBarras);
                
                try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
                }
                
            }catch(SQLException e){
                  JOptionPane.showMessageDialog(null,"Error al validar el código de barras: " + e.getMessage());
            }
            return false;
        }
     
     

     
     public void eliminarProducto(int idProducto) {
    Conexion conexion = new Conexion();
    String query = "DELETE FROM Productos WHERE id = ?";
    
    try {
        PreparedStatement ps = conexion.conectar().prepareStatement(query);
        ps.setInt(1, idProducto);
        
        int filasAfectadas = ps.executeUpdate();
        
        if (filasAfectadas > 0) {
            JOptionPane.showMessageDialog(null, "Producto eliminado correctamente");
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró el producto");
        }
        
        ps.close();
        
    } catch (Exception e) {
        System.out.println("Error eliminando producto: " + e);
        JOptionPane.showMessageDialog(null, "Error al eliminar producto: " + e.getMessage());
        e.printStackTrace();
    }
}
}
