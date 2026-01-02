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
                    + "nombreProducto VARCHAR(50) NOT NULL,"
                    + "codigoBarras VARCHAR(50) NOT NULL UNIQUE,"
                    + "idCategoria INTEGER NOT NULL,"
                    + "precioDeCompra REAL NOT NULL,"
                    + "precioVenta REAL NOT NULL,"
                    + "cantidad REAL NOT NULL,"
                    + "unidadMedida VARCHAR(20) DEFAULT 'unidad',"
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
           
           // Tabla Servicios
           String sqlServicios = 
                "CREATE TABLE IF NOT EXISTS Servicios("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombreServicio VARCHAR(100) NOT NULL,"
                + "costoServicio REAL DEFAULT 0,"  
                + "precioVenta REAL NOT NULL,"
                + "idProductoConsumo INTEGER,"     
                + "cantidadConsumo REAL DEFAULT 0,"
                + "FOREIGN KEY(idProductoConsumo) REFERENCES Productos(id))";
           sms.execute(sqlServicios);
           System.out.println("Tabla Servicios creada");
           
           // Crear índice para mejorar rendimiento de consultas por fecha
           String sqlIndiceVentasFecha = 
                   "CREATE INDEX IF NOT EXISTS idx_ventas_fecha " +
                   "ON Ventas(fechaVenta)";
           sms.execute(sqlIndiceVentasFecha);
           System.out.println("Índice de fecha en Ventas creado");
           
           // ✅ VERIFICAR SI HAY QUE MODIFICAR DetalleVentas
           verificarYModificarDetalleVentas(db, sms);
           
           // Tabla MovimientosInventario
           String sqlMovimientos = 
                   "CREATE TABLE IF NOT EXISTS MovimientosInventario("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "idProducto INTEGER NOT NULL,"
                   + "idUsuario INTEGER NOT NULL,"
                   + "tipoMovimiento TEXT NOT NULL,"
                   + "cantidadAnterior REAL NOT NULL,"
                   + "cantidad REAL NOT NULL,"
                   + "cantidadNueva REAL NOT NULL,"
                   + "motivo TEXT,"
                   + "idVenta INTEGER,"
                   + "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                   + "FOREIGN KEY(idProducto) REFERENCES Productos(id),"
                   + "FOREIGN KEY(idUsuario) REFERENCES Usuarios(id),"
                   + "FOREIGN KEY(idVenta) REFERENCES Ventas(id))";
           sms.execute(sqlMovimientos);
           System.out.println("Tabla MovimientosInventario creada");
           
           // Tabla VentasArchivadas
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
           
           // Tabla DetalleVentasArchivadas
           String sqlDetalleArchivadas = 
                   "CREATE TABLE IF NOT EXISTS DetalleVentasArchivadas("
                   + "id INTEGER PRIMARY KEY,"
                   + "idVenta INTEGER,"
                   + "idProducto INTEGER,"
                   + "nombreProducto VARCHAR(100),"
                   + "cantidad REAL,"
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
           
           // Insertar categorías por defecto
           String contar = "SELECT COUNT(*) FROM Categorias";
           ResultSet rs = sms.executeQuery(contar);
           
           if(rs.next() && rs.getInt(1)==0){
               String insertCategorias =
                    "INSERT INTO Categorias(nombreCategoria) VALUES"
                    + "('Papelería General'),"
                    + "('Útiles Escolares'),"
                    + "('Oficina'),"
                    + "('Regalos'),"
                    + "('Decoraciones'),"
                    + "('Fiesta y Globos'),"
                    + "('Juguetes'),"
                    + "('Dulcería'),"
                    + "('Ropa Infantil'),"
                    + "('Calcetas y Accesorios'),"
                    + "('Zapatería'),"
                    + "('Medicamentos'),"
                    + "('Bebidas')";
               sms.execute(insertCategorias);
               System.out.println("Categorias insertadas");
           }
           
           // Optimizar base de datos
           sms.execute("ANALYZE");
           System.out.println("Base de datos optimizada");
        
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ✅ MÉTODO MEJORADO PARA VERIFICAR Y MODIFICAR DetalleVentas
    private static void verificarYModificarDetalleVentas(Connection db, Statement sms) {
        try {
            // Verificar si la tabla existe y su estructura
            ResultSet rs = sms.executeQuery(
                "SELECT sql FROM sqlite_master WHERE type='table' AND name='DetalleVentas'"
            );
            
            boolean necesitaModificacion = false;
            
            if (rs.next()) {
                String sqlActual = rs.getString("sql");
                
                // Si idProducto es NOT NULL o no tiene los campos nuevos, modificar
                if (sqlActual.contains("idProducto INTEGER NOT NULL") || 
                    !sqlActual.contains("idServicio") ||
                    !sqlActual.contains("tipoItem") ||
                    !sqlActual.contains("costoInsumo")) {  // ✅ VERIFICAR costoInsumo
                    necesitaModificacion = true;
                }
            } else {
                // La tabla no existe, crearla con la estructura correcta
                necesitaModificacion = true;
            }
            rs.close();
            
            if (necesitaModificacion) {
                System.out.println("🔧 Creando/Modificando tabla DetalleVentas para soportar servicios y costos históricos...");
                
                // Verificar si existe tabla antigua para migrar datos
                ResultSet rsCheck = sms.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='DetalleVentas'"
                );
                boolean existeTablaAntigua = rsCheck.next();
                rsCheck.close();
                
                if (existeTablaAntigua) {
                    // Renombrar tabla actual
                    sms.execute("ALTER TABLE DetalleVentas RENAME TO DetalleVentas_OLD");
                    System.out.println("  → Tabla antigua respaldada como DetalleVentas_OLD");
                }
                
                // ✅ CREAR NUEVA TABLA CON ESTRUCTURA COMPLETA
                String sqlDetalleVentas = 
                    "CREATE TABLE DetalleVentas("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "idVenta INTEGER NOT NULL,"
                    + "idProducto INTEGER,"  // ✅ NULLABLE para soportar servicios
                    + "idServicio INTEGER,"  // ✅ NUEVO campo para servicios
                    + "tipoItem TEXT NOT NULL DEFAULT 'producto'," // ✅ 'producto' o 'servicio'
                    + "nombreProducto VARCHAR(100) NOT NULL,"
                    + "cantidad REAL NOT NULL,"
                    + "precioUnitario REAL NOT NULL,"
                    + "subtotal REAL NOT NULL,"
                    + "costoInsumo REAL DEFAULT 0," // ✅ NUEVO: Costo histórico de insumos
                    + "precioCompraHistorico REAL DEFAULT 0," // ✅ NUEVO: Precio compra del producto en ese momento
                    + "FOREIGN KEY(idVenta) REFERENCES Ventas(id) ON DELETE CASCADE,"
                    + "FOREIGN KEY(idProducto) REFERENCES Productos(id),"
                    + "FOREIGN KEY(idServicio) REFERENCES Servicios(id))";
                sms.execute(sqlDetalleVentas);
                System.out.println("  ✓ Nueva estructura de DetalleVentas creada");
                
                // Migrar datos si había tabla antigua
                if (existeTablaAntigua) {
                    try {
                        // Verificar columnas de la tabla antigua
                        ResultSet rsColumns = sms.executeQuery(
                            "PRAGMA table_info(DetalleVentas_OLD)"
                        );
                        
                        boolean tieneIdServicio = false;
                        boolean tieneTipoItem = false;
                        
                        while (rsColumns.next()) {
                            String columnName = rsColumns.getString("name");
                            if (columnName.equals("idServicio")) tieneIdServicio = true;
                            if (columnName.equals("tipoItem")) tieneTipoItem = true;
                        }
                        rsColumns.close();
                        
                        // Migrar según columnas disponibles
                        String sqlMigrar;
                        if (tieneIdServicio && tieneTipoItem) {
                            // Tabla ya tenía servicios
                            sqlMigrar = 
                                "INSERT INTO DetalleVentas (id, idVenta, idProducto, idServicio, tipoItem, nombreProducto, cantidad, precioUnitario, subtotal, costoInsumo, precioCompraHistorico) " +
                                "SELECT id, idVenta, idProducto, idServicio, tipoItem, nombreProducto, cantidad, precioUnitario, subtotal, 0, 0 " +
                                "FROM DetalleVentas_OLD";
                        } else {
                            // Tabla antigua solo tenía productos
                            sqlMigrar = 
                                "INSERT INTO DetalleVentas (id, idVenta, idProducto, tipoItem, nombreProducto, cantidad, precioUnitario, subtotal, costoInsumo, precioCompraHistorico) " +
                                "SELECT id, idVenta, idProducto, 'producto', nombreProducto, cantidad, precioUnitario, subtotal, 0, 0 " +
                                "FROM DetalleVentas_OLD";
                        }
                        
                        sms.execute(sqlMigrar);
                        System.out.println("  ✓ Datos migrados correctamente (" + 
                            (tieneIdServicio ? "con servicios" : "solo productos") + ")");
                        
                        // Eliminar tabla antigua
                        sms.execute("DROP TABLE DetalleVentas_OLD");
                        System.out.println("  ✓ Tabla antigua eliminada");
                        
                    } catch (Exception e) {
                        System.out.println("  ⚠ Advertencia: No se pudieron migrar datos: " + e.getMessage());
                        System.out.println("  → Puedes intentar migrar manualmente o eliminar DetalleVentas_OLD");
                    }
                }
                
                // Crear índices
                String sqlIndiceDetalleVentas = 
                    "CREATE INDEX IF NOT EXISTS idx_detalle_idventa " +
                    "ON DetalleVentas(idVenta)";
                sms.execute(sqlIndiceDetalleVentas);
                
                String sqlIndiceTipoItem = 
                    "CREATE INDEX IF NOT EXISTS idx_detalle_tipoitem " +
                    "ON DetalleVentas(tipoItem)";
                sms.execute(sqlIndiceTipoItem);
                
                System.out.println("  ✓ Índices creados");
                System.out.println("✅ Tabla DetalleVentas actualizada exitosamente");
                
            } else {
                System.out.println("✓ Tabla DetalleVentas ya tiene la estructura correcta");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al verificar/modificar DetalleVentas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
