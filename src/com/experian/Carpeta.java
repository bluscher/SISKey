/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.experian;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Scanner;
import org.apache.log4j.Logger;

/**
 *
 * @author e10934a
 */
public class Carpeta {
    private static final Logger log = Logger.getLogger(Carpeta.class.getName());
    //static final String pwd_keystore = "Miclave.1";
    private String rutaProyecto;
    private Path path;

  
    public Carpeta(Path ruta){
        this.path = ruta;
        if(!ruta.toFile().isDirectory()) {
            System.out.println("No existe la carpeta");
            System.exit(1);
        }
        else
           {
            this.rutaProyecto = ruta.toString();
            // System.out.println(rutaProyecto);
           }
    }
    
    public Carpeta(){
        rutaProyecto = System.getProperty("user.dir");
    }
    
    //Verifica si en la carpeta Input tiene un certificado y lo obtiene el primero.
    public File getCertFile(){
        File carpeta = new File(rutaProyecto + File.separator +"Input"+File.separator);
        File[] listaf = carpeta.listFiles();
        
        if (carpeta.exists() && listaf.length <= 1) {
            File aux = listaf[0];
            log.info("ruta archivo y nombre: "+aux.getPath() + " | " + aux.getName());
            return aux;
        }else
            log.info("No hay certificado en la carpeta.");
            return null;       
    }
    
    public void listarArchivos(){    
        String[] lista = path.toFile().list();
        for(int i=0; i<lista.length; i++){
            System.out.println(lista[i]);
        }
    }
    
    public String getNombreFile(){
        String[] lista = path.toFile().list();
        return lista[0];
    }
    
    public Path getPath(){
        return this.path;
    }
    
    public String getServiceConfig(String nom){
        File f = new File(rutaProyecto);
        String[] listarArchivos = f.list(new FiltroContenido(nom));
        if (listarArchivos.length == 0) {
            log.info("No se encontro el Archivo de configuracion");
            return "";
        }else
        return rutaProyecto + "/"+ listarArchivos[0] + File.separator + "conf" + File.separator + "com.eda.crypto.cfg";
    }
    
}