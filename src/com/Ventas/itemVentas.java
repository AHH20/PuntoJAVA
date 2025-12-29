package com.Ventas;

import java.math.BigDecimal;

/**
 * Clase que representa un item/producto en una venta
 * Soporta cantidades decimales para unidades como metros, kilogramos, litros
 */
public class itemVentas {
    
    private int idProducto;
    private String nombreProducto;
    private double cantidad;  // ✅ double para soportar 0.5, 1.5, etc.
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    
    /**
     * Constructor completo
     */
    public itemVentas(int idProducto, String nombreProducto, double cantidad,
                     BigDecimal precioUnitario, BigDecimal subtotal) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }
    
    /**
     * Constructor alternativo que calcula el subtotal automáticamente
     */
    public itemVentas(int idProducto, String nombreProducto, double cantidad,
                     BigDecimal precioUnitario) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
    }
    
    // ✅ GETTERS
    public int getIdProducto() {
        return idProducto;
    }
    
    public String getNombreProducto() {
        return nombreProducto;
    }
    
    public double getCantidad() {
        return cantidad;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    // ✅ SETTERS
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
    
    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
        // Recalcular subtotal cuando cambia la cantidad
        if (this.precioUnitario != null) {
            this.subtotal = this.precioUnitario.multiply(new BigDecimal(cantidad));
        }
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        // Recalcular subtotal cuando cambia el precio
        if (this.cantidad > 0) {
            this.subtotal = precioUnitario.multiply(new BigDecimal(this.cantidad));
        }
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    /**
     * Recalcula el subtotal basado en cantidad y precio actual
     */
    public void recalcularSubtotal() {
        if (this.precioUnitario != null && this.cantidad > 0) {
            this.subtotal = this.precioUnitario.multiply(new BigDecimal(this.cantidad));
        }
    }
    
    @Override
    public String toString() {
        return String.format("itemVentas{id=%d, nombre='%s', cantidad=%.2f, precio=%.2f, subtotal=%.2f}",
                           idProducto, nombreProducto, cantidad, 
                           precioUnitario, subtotal);
    }
}