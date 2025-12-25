
package com.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;   

public class Conexion {
    
    private static final String URL =  System.getProperty("user.dir")+ File.separator + "punto_venta.db";
    
    public static Connection conectar(){
    
        
        try{
        Connection  db = DriverManager.getConnection("jdbc:sqlite:"+ URL);
            System.out.println("Conexion buena");
            return db;
        }catch(SQLException e){
        System.out.println("Error" + e.getMessage());
        }
    
        return null;
    }

    
    
}

