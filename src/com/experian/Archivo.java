/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.experian;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Scanner;
import org.apache.log4j.Logger;


/**
 *
 * @author e10934a
 */
public class Archivo {
   
    private String ruta;
    private File file;
    private Object cadenaVacia = (Object)"";
    
    static  Logger log = Logger.getLogger(Archivo.class.getName());
    
    public Archivo(String ruta) {
        this.ruta = ruta;
        try {
            if (ruta.equals(cadenaVacia)) {
                System.err.println("No existe la ruta al archivo");
                System.exit(0);
            } else {
               this.file = new File(ruta); 
            }
        } catch (Exception e) {
            log.error("No se puede abrir el archivo de la ruta: " + ruta, e);
        }
    }
    
    //metodo obsoleto se abre y cierra cuando se quiere obtener parametro
    public void abrir(){
     try {
            // Localizamos el archivo.
            File file = new File(this.ruta);
             
            // Cargamos el archivo con la clase Scanner
            Scanner scanner = new Scanner(file);
             
            // Imprimimos el contenido.
            while (scanner.hasNext()) {
                System.out.println(scanner.nextLine());
            }
            scanner.close();
            
         } catch (FileNotFoundException ex) {
            log.error("Archivo no encontrado",ex);
         } catch (Exception ex) {
            log.error("Excepcion desconocida",ex);
         }
    }
    
    
    //obtendo el parametro que corresponde a la variable String del archivo de config.
    public String getParam(String var){
            FileReader fr = null;
        try {
            fr = new FileReader(this.file);
        } catch (FileNotFoundException ex) {
            log.error("No se enuecntra el archivo: ", ex);
        }
            BufferedReader br = new BufferedReader(fr);           
            boolean flagFind = false;
            String resul = null;
            String linea = null;
            //int i= 0;
            while (flagFind==false){
                try {
                    linea = br.readLine();
                } catch (IOException ex) {
                    log.error("Error de IO: ", ex);
                }
               if(linea.contains("=")){
                 String [] arrOdStr = linea.split("=");
                 if(arrOdStr[0] == null ? var == null : arrOdStr[0].equals(var)){
                   resul = arrOdStr[1];
                   flagFind = true;
                 }
               }
            } //while     
        try {
            fr.close();
        } catch (IOException ex) {
            log.error("Error IO: ", ex);
        }
            return resul;
    }
    
     //metodo mejorado que eliminad el shortuct ${}
    public String getParamExt(String var) throws FileNotFoundException, IOException{
            FileReader fr = new FileReader(this.file);
            BufferedReader br = new BufferedReader(fr);           
            boolean flagFind = false;
            String resul = null;
            String linea = null;
            //int i= 0;
            while (flagFind==false){
               linea = br.readLine();
               if(linea.contains("=")){
                 String [] arrOdStr = linea.split("=");
                 if(arrOdStr[0] == null ? var == null : arrOdStr[0].equals(var)){
                   String [] finalPath = arrOdStr[1].split("}");
                   resul = File.separator+"sis"+finalPath[1];
                   flagFind = true;
                 }
               }
            } //while     
            fr.close();
            return resul;
    }
    
    // MODIFICAR LA CLAVE .. PASAR COMO PARAMETRO LA NUEVA  EN Posicion 2 DEL SPLIT
     public void modifParam(String var, String tomodif) throws FileNotFoundException, IOException{
            FileReader fr = new FileReader(this.file);
            BufferedReader br = new BufferedReader(fr);     
           
            String linea = "";
            String newLinea = ""; String oldLinea="";
            String oldText = "";
            
             
            while ((linea = br.readLine())!=null){
               oldText += linea +"\r\n";
               if(linea.contains("=")){
                 String [] arrOdStr = linea.split("=");
                 if(arrOdStr[0] == null ? var == null : arrOdStr[0].equals(var)){
                   oldLinea = arrOdStr[0]+"="+arrOdStr[1];
                   arrOdStr[1] = tomodif;
                   newLinea =arrOdStr[0]+"="+tomodif;                   
                 }
            //    System.out.println("test"); 
               }
            } //while 
            fr.close();
            //System.out.println(newLinea);
            //System.out.println(oldLinea);
           //replazar la linea
           String newText = oldText.replaceAll(oldLinea, newLinea);
           FileWriter fw = new FileWriter(this.file);
           BufferedWriter bw = new BufferedWriter(fw);
           System.out.println(newText);
           bw.write(newText);
           bw.close();
           
          // System.out.println(oldText);

    }
   
   //  
   public Path getPath(String paramRuta){
           FileSystem sisFicheros;
           sisFicheros = FileSystems.getDefault();
           Path rutaFichero = sisFicheros.getPath(paramRuta);
         //  System.out.println(rutaFichero.getFileName());
           return rutaFichero;
   }
     
}//end Archivo
