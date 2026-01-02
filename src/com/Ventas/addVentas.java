package com.Ventas;

import com.bd.Conexion;
import java.util.logging.Level;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import javax.swing.JOptionPane;

public class addVentas {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(addVentas.class.getName());
    
    public int guardarVenta(int idUsuario, java.util.List<itemVentas> productos,
                       BigDecimal totalVenta, BigDecimal efectivo, BigDecimal cambio) {
        Connection conn = null;
        int idVentaGenerado = -1;
        
        try {
            conn = Conexion.conectar();
            conn.setAutoCommit(false);
            
            // 1. Insertar venta
            String sqlVenta = "INSERT INTO Ventas (idUsuario, totalVenta, efectivo, cambio, fechaVenta) " +
                             "VALUES (?, ?, ?, ?, datetime('now', 'localtime'))";
            PreparedStatement psVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setInt(1, idUsuario);
            psVenta.setBigDecimal(2, totalVenta);
            psVenta.setBigDecimal(3, efectivo);
            psVenta.setBigDecimal(4, cambio);
            psVenta.executeUpdate();
            
            ResultSet rsKeys = psVenta.getGeneratedKeys();
            if (rsKeys.next()) {
                idVentaGenerado = rsKeys.getInt(1);
            }
            rsKeys.close();
            psVenta.close();
            
            // 2. Insertar detalles y actualizar inventario
            for (itemVentas item : productos) {
                
                // ✅ DETERMINAR SI ES PRODUCTO O SERVICIO
                boolean esServicio = item.getIdProducto() < 0;
                int idReal = Math.abs(item.getIdProducto());
                
                double costoInsumo = 0.0;
                double precioCompraProducto = 0.0;
                
                // ✅ CALCULAR COSTO HISTÓRICO
                if (!esServicio) {
                    // Es producto - obtener precio de compra ACTUAL
                    String sqlPrecioCompra = "SELECT precioDeCompra FROM Productos WHERE id = ?";
                    PreparedStatement psPrecio = conn.prepareStatement(sqlPrecioCompra);
                    psPrecio.setInt(1, idReal);
                    ResultSet rsPrecio = psPrecio.executeQuery();
                    
                    if (rsPrecio.next()) {
                        precioCompraProducto = rsPrecio.getDouble("precioDeCompra");
                        // Para productos, el costo es el precio de compra * cantidad
                        costoInsumo = precioCompraProducto * item.getCantidad();
                    }
                    rsPrecio.close();
                    psPrecio.close();
                    
                } else {
                    // Es servicio - verificar si consume producto
                    String sqlServicio = "SELECT idProductoConsumo, cantidadConsumo FROM Servicios WHERE id = ?";
                    PreparedStatement psServicio = conn.prepareStatement(sqlServicio);
                    psServicio.setInt(1, idReal);
                    ResultSet rsServicio = psServicio.executeQuery();
                    
                    if (rsServicio.next()) {
                        Integer idProductoConsumo = rsServicio.getObject("idProductoConsumo") != null ? 
                            rsServicio.getInt("idProductoConsumo") : null;
                        Double cantidadConsumo = rsServicio.getObject("cantidadConsumo") != null ? 
                            rsServicio.getDouble("cantidadConsumo") : null;
                        
                        // Si consume producto, calcular costo del insumo
                        if (idProductoConsumo != null && cantidadConsumo != null) {
                            String sqlPrecioInsumo = "SELECT precioDeCompra FROM Productos WHERE id = ?";
                            PreparedStatement psPrecioInsumo = conn.prepareStatement(sqlPrecioInsumo);
                            psPrecioInsumo.setInt(1, idProductoConsumo);
                            ResultSet rsPrecioInsumo = psPrecioInsumo.executeQuery();
                            
                            if (rsPrecioInsumo.next()) {
                                double precioCompraInsumo = rsPrecioInsumo.getDouble("precioDeCompra");
                                // Costo = precio del insumo * cantidad consumida por servicio * cantidad de servicios
                                costoInsumo = precioCompraInsumo * cantidadConsumo * item.getCantidad();
                            }
                            rsPrecioInsumo.close();
                            psPrecioInsumo.close();
                        }
                    }
                    rsServicio.close();
                    psServicio.close();
                }
                
                // ✅ INSERTAR EN DetalleVentas CON COSTOS HISTÓRICOS
                String sqlDetalle = "INSERT INTO DetalleVentas " +
                                   "(idVenta, idProducto, idServicio, tipoItem, nombreProducto, cantidad, precioUnitario, subtotal, costoInsumo, precioCompraHistorico) " +
                                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
                psDetalle.setInt(1, idVentaGenerado);
                
                if (esServicio) {
                    // Es servicio
                    psDetalle.setObject(2, null);           // idProducto = NULL
                    psDetalle.setInt(3, idReal);            // idServicio = ID del servicio
                    psDetalle.setString(4, "servicio");     // tipoItem = 'servicio'
                } else {
                    // Es producto
                    psDetalle.setInt(2, idReal);            // idProducto = ID del producto
                    psDetalle.setObject(3, null);           // idServicio = NULL
                    psDetalle.setString(4, "producto");     // tipoItem = 'producto'
                }
                
                psDetalle.setString(5, item.getNombreProducto());
                psDetalle.setDouble(6, item.getCantidad());
                psDetalle.setBigDecimal(7, item.getPrecioUnitario());
                psDetalle.setBigDecimal(8, item.getSubtotal());
                psDetalle.setDouble(9, costoInsumo);                      // ✅ Guardar costo histórico
                psDetalle.setDouble(10, precioCompraProducto);            // ✅ Guardar precio compra histórico
                psDetalle.executeUpdate();
                psDetalle.close();
                
                // ✅ ACTUALIZAR INVENTARIO
                if (!esServicio) {
                    // Es un producto - descontar stock directo
                    String sqlUpdate = "UPDATE Productos SET cantidad = cantidad - ? WHERE id = ?";
                    PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
                    psUpdate.setDouble(1, item.getCantidad());
                    psUpdate.setInt(2, idReal);
                    psUpdate.executeUpdate();
                    psUpdate.close();
                    
                } else {
                    // Es un servicio - verificar si consume producto
                    String sqlServicio = "SELECT idProductoConsumo, cantidadConsumo FROM Servicios WHERE id = ?";
                    PreparedStatement psServicio = conn.prepareStatement(sqlServicio);
                    psServicio.setInt(1, idReal);
                    ResultSet rsServicio = psServicio.executeQuery();
                    
                    if (rsServicio.next()) {
                        Integer idProductoConsumo = rsServicio.getObject("idProductoConsumo") != null ? 
                            rsServicio.getInt("idProductoConsumo") : null;
                        Double cantidadConsumo = rsServicio.getObject("cantidadConsumo") != null ? 
                            rsServicio.getDouble("cantidadConsumo") : null;
                        
                        // Si consume producto, descontarlo
                        if (idProductoConsumo != null && cantidadConsumo != null) {
                            double cantidadTotal = cantidadConsumo * item.getCantidad();
                            
                            String sqlUpdateProducto = "UPDATE Productos SET cantidad = cantidad - ? WHERE id = ?";
                            PreparedStatement psUpdateProd = conn.prepareStatement(sqlUpdateProducto);
                            psUpdateProd.setDouble(1, cantidadTotal);
                            psUpdateProd.setInt(2, idProductoConsumo);
                            psUpdateProd.executeUpdate();
                            psUpdateProd.close();
                        }
                    }
                    
                    rsServicio.close();
                    psServicio.close();
                }
            }
            
            conn.commit();
            return idVentaGenerado;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error en rollback", ex);
            }
            logger.log(Level.SEVERE, "Error guardando venta", e);
            return -1;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error cerrando conexión", e);
            }
        }
    }
    
    public boolean verificarStockDisponible(List<itemVentas> productos) {
        Conexion conexion = new Conexion();
        
        try {
            String sql = "SELECT cantidad FROM Productos WHERE id = ?";
            PreparedStatement ps = conexion.conectar().prepareStatement(sql);
            
            for (itemVentas item : productos) {
                // Solo validar productos (IDs positivos)
                if (item.getIdProducto() > 0) {
                    ps.setInt(1, item.getIdProducto());
                    ResultSet rs = ps.executeQuery();
                    
                    if (rs.next()) {
                        double stockDisponible = rs.getDouble("cantidad");
                        
                        if (item.getCantidad() > stockDisponible) {
                            JOptionPane.showMessageDialog(null,
                                String.format("Stock insuficiente para: %s\n" +
                                            "Solicitado: %.2f\n" +
                                            "Disponible: %.2f",
                                            item.getNombreProducto(),
                                            item.getCantidad(),
                                            stockDisponible),
                                "Stock Insuficiente",
                                JOptionPane.WARNING_MESSAGE);
                            rs.close();
                            ps.close();
                            return false;
                        }
                    }
                    rs.close();
                }
            }
            
            ps.close();
            return true;
            
        } catch (SQLException e) {
            logger.severe("Error al verificar stock: " + e.getMessage());
            return false;
        }
    }
}