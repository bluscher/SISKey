/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.experian;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import org.apache.log4j.Logger;
import sun.security.x509.X500Name;
import sun.security.tools.keytool.CertAndKeyGen;


/**
 *
 * @author e10934a
 */
public final class StrongBox {
  
     //Medida de las claves
    public static final int KEY_LEN = 2048;
    //Fecha de expiración
    private static final int EXPIRATION = 365;
    //Algoritmo de firma a usar
    private static final String ALGORITHM = "SHA1withRSA";
    private static final String OUTPUT_PATH = System.getProperty("user.dir") + File.separator + "output"+ File.separator + "certificado.crt";
    private static final String OUTPUTKEYSTORE_PATH = System.getProperty("user.dir") + File.separator + "output"+ File.separator + "newKeystore.jks";
  
    private static final Logger log = Logger.getLogger(StrongBox.class.getName());
    
    private String password;
    private String archivo;
    private KeyStore ks;
    private InputStream ksData;
    private char[] ksPass;
    private File Keystorefile; 
    
    
    public StrongBox(String pwd, String arch){
    //constructor
        password = pwd;
        archivo = arch;
        //  Keystorefile = new File(arch); borrar
        cargarKeystore();
    }
     public StrongBox(String pwd, File f){
    //constructor
        password = pwd;
        archivo = "";
        Keystorefile = f;
        cargarKeystore(pwd,f);
    }
    public KeyStore getKeystore(){
        return this.ks;
    }
    
    public StrongBox(){}
     
    public void mostrarAliases(){
     System.out.println("#Listar Alias " + this.archivo + ": [BEGIN]");
     Enumeration aliases = null ;
        try {
            aliases = ks.aliases();
        } catch (KeyStoreException ex) {
            log.error("Error conn el Keystore: ",ex);
        }
      while (aliases.hasMoreElements()){
         System.out.println(aliases.nextElement());
     }
     System.out.println("#Listar Alias: [END]");
    } 
    
    public String getNomFirstAlias(){
        String result = "Ningun Alias";
        Enumeration aliases = null;
        try {
          log.info("El numero de alias es : " + ks.size());
          aliases = ks.aliases();
        } catch (KeyStoreException ex) {
            log.error("No existe ningun Alias",ex);
        }
            if (aliases.hasMoreElements()){
            result = (String)aliases.nextElement();
            log.info("Nombre alias: " +result);
            }
            return result;
   
    }
    
    public int cantAlias(){
        try {
            return this.ks.size();
        } catch (KeyStoreException ex) {
            log.error("Error con cargar el Keystore", ex);
        }
        return 0;
    }
    
    public void borrarAlias(String aliasToDell){
        try {
            if(ks.containsAlias(aliasToDell)){
                try (FileOutputStream resul = new FileOutputStream(this.archivo)) {
                    ks.deleteEntry(aliasToDell);
                    ks.store(resul, ksPass);
                }
                log.info("[Borrado exitoso] "+aliasToDell);
            }else
                log.info("No hubo Borrado:" + aliasToDell);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException ex) {
           log.error(ex);
        }
    } 
    
    //----------prueba con array de certificado y otra libreria----------------
    public void cargarKeystoreNEW(String alias,String password, String bodyCert) {
        try {
            CertAndKeyGen keyGen = new CertAndKeyGen("RSA", ALGORITHM, null);
            try {
                keyGen.generate(KEY_LEN);
                PrivateKey pk = keyGen.getPrivateKey();
                X509Certificate cert;
                cert = keyGen.getSelfCertificate(new X500Name(bodyCert), (long) 365 * 24 * 60 * 60);
                X509Certificate[] chain = new X509Certificate[1];
                chain[0]= cert;
                
                this.ks = KeyStore.getInstance("JKS");
                this.ksPass = password.toCharArray();
                try (FileOutputStream newkeystore = new FileOutputStream(OUTPUTKEYSTORE_PATH)) {
                    ks.load(null,null);
                    ks.setKeyEntry(alias, pk, ksPass, chain);
                    ks.store(newkeystore, ksPass);
                    //#escribir certificado en archivo externo
                    //FileOutputStream certFile = new FileOutputStream(OUTPUTCERT_PATH);
                    //certFile.write(cert.getEncoded());
                    //log.info("nuevo certificado creado en path [OK]");
                }
                 } catch (InvalidKeyException | IOException | CertificateException | SignatureException | KeyStoreException ex) {
                log.error(ex);
                 }
            } 
        catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            log.error(ex);
        }
    }
    
    public KeyStore cargarKeystore(String pwd, File pathKS){
        try{
            KeyStore ks = KeyStore.getInstance("JKS");
            char[] ksPass = pwd.toCharArray();
            try(InputStream ksData = new FileInputStream(pathKS)){
                try {
                    //antes que un keystore se pueda ser accedido debe ser cargado "load"
                    ks.load(ksData,ksPass);
                    ksData.close();
                } catch (NoSuchAlgorithmException ex) {
                } catch (CertificateException ex) {
                    log.error("error certificado", ex);
                }
            } catch (FileNotFoundException ex) {
                log.error("error archivo no encontrado", ex);
            } catch (IOException ex) {
                log.error("error IO", ex);
            }
            return ks;
            
        }//cargarKeystore sobrecargado
        catch (KeyStoreException ex) {
            log.error("error keystore", ex);
        }
        return null;
        
    }
    
    public void cargarKeystore() {
        try {
            this.ks = KeyStore.getInstance("JKS");
            this.ksPass = this.password.toCharArray();
            try {
                this.ksData = new FileInputStream(this.archivo);
            } catch (FileNotFoundException ex) {
                log.error("error FileNotFound", ex);
            }
            try {
                ks.load(ksData,ksPass);
            } catch (IOException ex) {
                log.error("Error con el KeyStore: ", ex);
            } catch (NoSuchAlgorithmException ex) {
                log.error("error Algoritmo", ex);
            } catch (CertificateException ex) {
                log.error("error certificado", ex);
            }
            ksData.close();
        } //cargarKeystore
        catch (KeyStoreException ex) {
            log.error("error keystore", ex);
        } catch (IOException ex) {
           log.error("error IO", ex);
        }
    }

    public KeyStore.Entry getKey(String alias, String password){
        char[] keyPassowrd = password.toCharArray();
        KeyStore.ProtectionParameter claveEntrada = new KeyStore.PasswordProtection(keyPassowrd);
        try {
            KeyStore.Entry keyEntry = ks.getEntry(alias, claveEntrada);
            log.info("Recuperar entrada Keystore alias : "+alias+" [OK]");
            return keyEntry;
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException ex) {
            log.error(ex);
        }
        return null;
    }
   
    
    public void setKey(KeyStore.Entry keyEntry,String alias, String password){
        char[] keyPassowrd = password.toCharArray();
        KeyStore.ProtectionParameter claveEntrada = new KeyStore.PasswordProtection(keyPassowrd);
        try {
            FileOutputStream newkeystore = new FileOutputStream(archivo);
            ks.setEntry(alias, keyEntry, claveEntrada);
            log.info("Cargar clave del alias :"+alias +" [OK]");
            ks.store(newkeystore, keyPassowrd);
        } catch (KeyStoreException ex) {
            log.error(ex);
        } catch (IOException | NoSuchAlgorithmException | CertificateException ex) {
            log.error(ex);
        }
    
    }
    
    public void getDatosCertificado(String alias){    
        try {
            System.out.println( this.ks.getCertificate(alias).toString());
        } catch (KeyStoreException ex) {
            log.error("Error certificado", ex);
        }
    }
    
    
    void borrarCert(String nomAliasJOB) {
        try {
            ks.deleteEntry(nomAliasJOB);
        } catch (KeyStoreException ex) {
            log.error("Error KeyStore", ex);
        }
    }
   
    public boolean existeAlias(String alias){
        try {
            return ks.containsAlias(alias);
        } catch (KeyStoreException ex) {
            log.error("No existe el Alias en el Keystore", ex);
        }
        return false; 
    }
    
    public Certificate getCertificado(String alias){
        try {
            return ks.getCertificate(alias);
        } catch (KeyStoreException ex) {
            log.error(ex);
        }
        return null;
    }
    
}//end certificado
