
package com.Productos;

import javax.swing.JComboBox;


public class itemCategoria {
    
        int id;
    String nombreCategoria;

    public itemCategoria(int id, String nombreCategoria){
        
        this.id = id;
        this.nombreCategoria = nombreCategoria;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    
    public void setNombreCategoria(String nombreCategoria){
        this.nombreCategoria = nombreCategoria;
    }
    
    
    public int getId() {
        return id;
    }

   

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    
    @Override
    public String toString(){
        return nombreCategoria;
    }

    
    
    
}
