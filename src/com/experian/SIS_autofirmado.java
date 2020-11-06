/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.experian;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Scanner;
import jdk.nashorn.internal.ir.BreakNode;

import org.apache.log4j.Logger;

/**
 *
 * @author e10934a
 * @version 1.0
 */


public class SIS_autofirmado {
    
   //ubicacion "sensible data" en system.properties
    private static final String KEYSTOREPATH = "jetty.keyStore.file";
    private static final String KEYSTOREPASS = "jetty.keyStore.password";
    //ubicacion archivo de configuracion -> despues debe ir el classpath donde va ir el jar
    
    /*-ubicacion del archivo para local test y desde carpeta del instalable*/
    private static final String PATHSYSTEM = System.getProperty("user.dir");
    private static final String NOMAPP = File.separator +"SISKey"; 
    //##MEJORA###  linea 35 :: que tome el nombre de la carpeta dinamicamente y no dejarlo hardcodeado
    private static final String SISCONFIG = "sis" + File.separator + "conf" + File.separator + "system" + File.separator + "system.properties";
    private static final Logger log = Logger.getLogger(SIS_autofirmado.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        int userEleccion;
        userEleccion = menu();
        if (userEleccion == 3){System.exit(0);}
         //Gestion archivo de propiedad
        Properties prop = new Properties(System.getProperties());
        try {
           FileInputStream propFile = new FileInputStream(PATHSYSTEM + File.separator + "conf" + File.separator + "system.properties");
            try {
              prop.load(propFile);
              log.info("Carga system.properties [OK]");
            } catch (IOException ex) {
                log.error("No se puede arbir el archivo de propiedades.", ex);
            }
                
        } catch (FileNotFoundException ex) {
            log.error("No se encuentra el archivo de propiedades {system.properites}.", ex);
        }
       
        String pathCA ="";
        String rutaK ="";
        String sisFolder = PATHSYSTEM.replaceAll(NOMAPP, "");
        
        
        try {
            //---Manipulacion archivo config del SIS
            //Recupero el path del keystore desde el archivo de config
            Archivo arch_conf = new Archivo(sisFolder + File.separator + SISCONFIG); 
            Path ruta_keystore = arch_conf.getPath(arch_conf.getParamExt(KEYSTOREPATH));
            rutaK = ruta_keystore.toString();
            log.info("ruta JKS: "+sisFolder+rutaK);
            //Recupero la clave desde el archivo de config
            String pwd_keystore = arch_conf.getParam(KEYSTOREPASS);
            estaEncriptado(pwd_keystore);
            log.info("clave JKS: "+pwd_keystore);
            
            if (userEleccion == 1){
            StrongBox keystoreORI = new StrongBox(pwd_keystore,sisFolder+rutaK);
            String nomAliasORI = keystoreORI.getNomFirstAlias();
            keystoreORI.borrarCert(nomAliasORI);
            
            //Recupero Informacion del archivo de propiedad
            String alias = prop.getProperty("alias");
            String bodykeystore = "CN="+prop.getProperty("CN")+",O="
                                  +prop.getProperty("O")+",OU="
                                  +prop.getProperty("OU")+",L="
                                  +prop.getProperty("L")+",ST="
                                  +prop.getProperty("ST")+",C="
                                  +prop.getProperty("C");
            keystoreORI.newKeystore(alias, pwd_keystore, bodykeystore);
            log.info("Nuevo certificado autofirmado: \n"+"alias: "+alias +"\n"+"source: "+bodykeystore);
            }
            else 
              if (userEleccion == 2){
                 Carpeta input = new Carpeta();
                 File ca = input.getCertFile();
                 StrongBox keystoreCA = new StrongBox(pwd_keystore,sisFolder+rutaK);
                 keystoreCA.importCA(ca);
              }
                   
     
            pressAnyKeyToContinue();
            }//end main
        catch (IOException ex) {
            log.error("error No se ecuentra el archivo", ex);
        }
}
   
    //metodo para pausar ventana esperando interaccion con usuario
    static public void pressAnyKeyToContinue()
      { 
          String seguir;
         
          System.out.println("Pulsar una tecla para continuar...");
          Scanner teclado = new Scanner(System.in);
          seguir = teclado.nextLine();
             
     }  
    static public void estaEncriptado(String clave){
        if (clave.contains("ENC(")) {
            log.info("La clave: " + clave + " esta encriptada, no se puede seguir con el proceso");
            System.exit(0);
        }
    }
    
     public static int menu() {

        int selection;
        Scanner input = new Scanner(System.in);

        /***************************************************/
        System.out.println("Experian :: automatizacion certificado JKS para SIS");
        System.out.println("---------------------------------");
        System.out.println("1 - Generar requerimiento a CA");
        System.out.println("2 - Importar certificado firmado");
        System.out.println("3 - Salir");
        System.out.println("---------------------------------");
        
        System.out.print("# ");selection = input.nextInt();
        return selection;    
    }
}