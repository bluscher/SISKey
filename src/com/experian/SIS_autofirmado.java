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
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;

/**
 *
 * @author e10934a
 */


public class SIS_autofirmado {
    
   //ubicacion "sensible data" en system.properties
    private static final String KEYSTOREPATH = "jetty.keyStore.file";
    private static final String KEYSTOREPASS = "jetty.keyStore.password";
    //ubicacion archivo de configuracion -> despues debe ir el classpath donde va ir el jar
   
    /*-ubicacion del archivo para local test y desde carpeta del instalable*/
    private static final String PATHSYSTEM = System.getProperty("user.dir");
    private static final String NOMAPP = File.separator +"SISKey";
    private static final String SISCONFIG = "sis" + File.separator + "conf" + File.separator + "system" + File.separator + "system.properties";
    private static final Logger log = Logger.getLogger(SIS_autofirmado.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
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
       
        
        String rutaK ="";
        FileSystem sistemaFicheros = FileSystems.getDefault();
        String sisFolder = PATHSYSTEM.replaceAll(NOMAPP, "");
        try {
            //---Manipulacion archivo config del SIS
            Archivo arch_conf = new Archivo(sisFolder + SISCONFIG);
            Path ruta_keystore = arch_conf.getPath(arch_conf.getParamExt(KEYSTOREPATH));
            rutaK = ruta_keystore.toString();
            log.info("ruta JKS: "+sisFolder+rutaK);
            String pwd_keystore = arch_conf.getParam(KEYSTOREPASS);
            estaEncriptado(pwd_keystore);
            //log.debug(pwd_keystore);
            
            StrongBox certORI = new StrongBox(pwd_keystore,sisFolder+rutaK); 
            certORI.borrarCert(certORI.getNomFirstAlias());
            
            //Recupero Informacion del archivo de propiedad
            String alias = prop.getProperty("alias");
            String bodykeystore = "CN="+prop.getProperty("CN")+",O="
                                  +prop.getProperty("O")+",OU="
                                  +prop.getProperty("OU")+",L="
                                  +prop.getProperty("L")+",ST="
                                  +prop.getProperty("ST")+",C="
                                  +prop.getProperty("C");
            
            StrongBox cautsign = new StrongBox();
            cautsign.cargarKeystoreNEW(alias,pwd_keystore, bodykeystore);
            certORI.setKey(cautsign.getKey(alias, pwd_keystore),alias,pwd_keystore);
        
            pressAnyKeyToContinue();
            }//end main
 catch (IOException ex) {
            log.error("error Keystore", ex);
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
}