/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.distri.servidor;

import java.net.ServerSocket;

/**
 *
 * @author shane
 */
public class ServidorMultiple {
    
    public static void main(String args[]) {
        System.out.println("Servidor de multiples clientes");
        
        try {
            ServerSocket sc = new ServerSocket(667);
            while (true) {
                AppSocketSession cliente = new AppSocketSession(sc.accept());
                cliente.start();
            }
        } catch (Exception e) {
            System.out.println("Un cliente que finalizo sesion a intentado conectarse"); 
        }
        
    }
    
}
