/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Productos;

import java.math.BigDecimal;

/**
 *
 * @author Aleci
 */
public class EntidadProductos {

   
    int id;
    String nombreProducto;
    String codigoBarras;
   private  int idCategoria ;
    BigDecimal precioDeCompra;
    BigDecimal precioVenta;
    int Cantidad;
    
    
    
    
    
   public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
  

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public BigDecimal getPrecioDeCompra() {
        return precioDeCompra;
    }

    public void setPrecioDeCompra(BigDecimal precioDeCompra) {
        this.precioDeCompra = precioDeCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getCantidad() {
        return Cantidad;
    }

    public void setCantidad(int Cantidad) {
        this.Cantidad = Cantidad;
    }
    
   
    
    
}
