
package com.Productos;


public class itemProducto {
    
    private int id;
    private String nombre;
    
    public itemProducto(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    public int getId() {
        return id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    @Override
    public String toString() {
        return nombre;
    }
    
}
