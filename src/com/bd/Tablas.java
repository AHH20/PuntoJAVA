package com.bd;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class Tablas {
    
    public static void crear(){
    
        try(Connection db = Conexion.conectar()){
            Statement sms = db.createStatement();
            
            // Tabla Usuarios
            String sqlUsuarios = 
                    "CREATE TABLE IF NOT EXISTS Usuarios ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "Usuario TEXT NOT NULL UNIQUE,"
                    + "Correo TEXT NOT NULL UNIQUE,"
                    + "Contrasena TEXT NOT NULL)";
            
            sms.execute(sqlUsuarios);
            System.out.println("Tabla usuarios creada");
            
            // Tabla Categorías
            String sqlCategorias = 
                   "CREATE TABLE IF NOT EXISTS Categorias("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "nombreCategoria VARCHAR(50) NOT NULL)";
           sms.execute(sqlCategorias);
           System.out.println("Tabla Categoria Creada");
            
            // Tabla Productos
            String sqlProductos = 
                    "CREATE TABLE IF NOT EXISTS Productos("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nombreProducto VARCHAR(50)NOT NULL,"
                    + "codigoBarras VARCHAR(50) NOT NULL UNIQUE,"
                    + "idCategoria INTEGER NOT NULL,"
                    + "precioDeCompra REAL NOT NULL,"
                    + "precioVenta REAL NOT NULL,"
                    + "cantidad INTEGER NOT NULL,"
                    + "FOREIGN KEY(idCategoria) REFERENCES Categorias(id))";
           sms.execute(sqlProductos);
           System.out.println("Tabla Productos creada");
           
           // Tabla Ventas
           String sqlVentas = 
                   "CREATE TABLE IF NOT EXISTS Ventas("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "idUsuario INTEGER NOT NULL,"
                   + "fechaVenta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                   + "totalVenta REAL NOT NULL,"
                   + "efectivo REAL NOT NULL,"
                   + "cambio REAL NOT NULL,"
                   + "metodoPago TEXT DEFAULT 'efectivo',"
                   + "estado TEXT DEFAULT 'completada',"
                   + "observaciones TEXT,"
                   + "FOREIGN KEY(idUsuario) REFERENCES Usuarios(id))";
           sms.execute(sqlVentas);
           System.out.println("Tabla Ventas creada");
           
           // Crear índice para mejorar rendimiento de consultas por fecha
           String sqlIndiceVentasFecha = 
                   "CREATE INDEX IF NOT EXISTS idx_ventas_fecha " +
                   "ON Ventas(fechaVenta)";
           sms.execute(sqlIndiceVentasFecha);
           System.out.println("Índice de fecha en Ventas creado");
           
           // Tabla DetalleVentas
            String sqlDetalleVentas = 
                   "CREATE TABLE IF NOT EXISTS DetalleVentas("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "idVenta INTEGER NOT NULL,"
                   + "idProducto INTEGER NOT NULL,"
                   + "nombreProducto VARCHAR(100) NOT NULL,"
                   + "cantidad INTEGER NOT NULL,"
                   + "precioUnitario REAL NOT NULL,"
                   + "subtotal REAL NOT NULL,"
                   + "FOREIGN KEY(idVenta) REFERENCES Ventas(id) ON DELETE CASCADE,"
                   + "FOREIGN KEY(idProducto) REFERENCES Productos(id))";
           sms.execute(sqlDetalleVentas);
           System.out.println("Tabla DetalleVentas creada");
           
         
           String sqlIndiceDetalleVentas = 
                   "CREATE INDEX IF NOT EXISTS idx_detalle_idventa " +
                   "ON DetalleVentas(idVenta)";
           sms.execute(sqlIndiceDetalleVentas);
           System.out.println("Índice en DetalleVentas creado");
           
        
           String sqlMovimientos = 
                   "CREATE TABLE IF NOT EXISTS MovimientosInventario("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "idProducto INTEGER NOT NULL,"
                   + "idUsuario INTEGER NOT NULL,"
                   + "tipoMovimiento TEXT NOT NULL,"
                   + "cantidadAnterior INTEGER NOT NULL,"
                   + "cantidad INTEGER NOT NULL,"
                   + "cantidadNueva INTEGER NOT NULL,"
                   + "motivo TEXT,"
                   + "idVenta INTEGER,"
                   + "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                   + "FOREIGN KEY(idProducto) REFERENCES Productos(id),"
                   + "FOREIGN KEY(idUsuario) REFERENCES Usuarios(id),"
                   + "FOREIGN KEY(idVenta) REFERENCES Ventas(id))";
           sms.execute(sqlMovimientos);
           System.out.println("Tabla MovimientosInventario creada");
           
           
           
         
           String sqlVentasArchivadas = 
                   "CREATE TABLE IF NOT EXISTS VentasArchivadas("
                   + "id INTEGER PRIMARY KEY,"
                   + "idUsuario INTEGER,"
                   + "fechaVenta TIMESTAMP,"
                   + "totalVenta REAL,"
                   + "efectivo REAL,"
                   + "cambio REAL,"
                   + "metodoPago TEXT,"
                   + "estado TEXT,"
                   + "observaciones TEXT,"
                   + "fechaArchivado TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
           sms.execute(sqlVentasArchivadas);
           System.out.println("Tabla VentasArchivadas creada");
           
         
           String sqlIndiceArchivadas = 
                   "CREATE INDEX IF NOT EXISTS idx_ventas_archivadas_fecha " +
                   "ON VentasArchivadas(fechaVenta)";
           sms.execute(sqlIndiceArchivadas);
           System.out.println("Índice en VentasArchivadas creado");
           
          
           String sqlDetalleArchivadas = 
                   "CREATE TABLE IF NOT EXISTS DetalleVentasArchivadas("
                   + "id INTEGER PRIMARY KEY,"
                   + "idVenta INTEGER,"
                   + "idProducto INTEGER,"
                   + "nombreProducto VARCHAR(100),"
                   + "cantidad INTEGER,"
                   + "precioUnitario REAL,"
                   + "subtotal REAL,"
                   + "fechaArchivado TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
           sms.execute(sqlDetalleArchivadas);
           System.out.println("Tabla DetalleVentasArchivadas creada");
           
          
           String sqlIndiceDetalleArchivadas = 
                   "CREATE INDEX IF NOT EXISTS idx_detalle_archivadas_idventa " +
                   "ON DetalleVentasArchivadas(idVenta)";
           sms.execute(sqlIndiceDetalleArchivadas);
           System.out.println("Índice en DetalleVentasArchivadas creado");
           
          
           String contar = "SELECT COUNT(*) FROM Categorias";
           ResultSet rs = sms.executeQuery(contar);
           
           if(rs.next() && rs.getInt(1)==0){
               String insertCategorias =
                       "INSERT INTO Categorias(nombreCategoria) VALUES"
                        + "('Papeleria General'),"
                        + "('Regalos'),"
                        + "('Juguetes'),"
                        + "('Zapateria'),"
                        + "('Oficina'),"
                        + "('Escolar'),"
                        + "('Servicios'),"
                        + "('Dulceria')";
               sms.execute(insertCategorias);
               System.out.println("Categorias insertadas");
           }
           
           String insertNuevaCategoria = "INSERT INTO Categorias(nombreCategoria) "
                  + "SELECT 'Medicamentos' "
                  + "WHERE NOT EXISTS(SELECT 1 FROM Categorias WHERE nombreCategoria = 'Medicamentos')";
           
           sms.execute(insertNuevaCategoria);
           System.out.println("Nuevas Categorias insertadas");
           
           String insertNuevaCategorias = "INSERT INTO Categorias(nombreCategoria) "
                  + "SELECT 'Bedidas' "
                  + "WHERE NOT EXISTS(SELECT 1 FROM Categorias WHERE nombreCategoria = 'Bebidas')";
           
           sms.execute(insertNuevaCategorias);
           System.out.println("Nuevas Categorias insertadas");
           
           // Optimizar base de datos
           sms.execute("ANALYZE");
           System.out.println("Base de datos optimizada");
        
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}