/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.experian;

import java.io.File;
import java.nio.file.Path;
import org.apache.log4j.Logger;

/**
 *
 * @author e10934a
 */
public class Carpeta {
    private static final Logger log = Logger.getLogger(Carpeta.class.getName());
    private String rutaProyecto;
    private Path path;
    private static final String NOMAPP = File.separator +"SISKey";
    private static final String PATHSYSTEM = System.getProperty("user.dir");

  
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
        rutaProyecto = PATHSYSTEM.replaceAll(NOMAPP, "");
        //log.info("Ruta de la carpeta: " +rutaProyecto);
    }
    
    //Verifica si en la carpeta Input tiene un certificado y lo obtiene el primero.
    public File getCertFile(){
        File carpeta = new File(PATHSYSTEM + File.separator +"input"+File.separator);
        File[] listaf = carpeta.listFiles();
      //  log.debug("existe la carpeta? "+carpeta.exists());
      //  log.debug("tamaño lista: "+listaf.length);
        
        if (carpeta.exists() && listaf.length >= 1) {
            File aux = listaf[0];
            log.info("ruta archivo y nombre: "+aux.getPath() + " | " + aux.getName());
            return aux;
        }else
            log.info("No hay certificado en la carpeta input");
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
