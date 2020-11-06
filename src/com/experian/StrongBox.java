/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.experian;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;
import sun.security.pkcs10.PKCS10;


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
    //Instancia de keystore
    private static final String INSTANCE ="JKS";
    //Rutas 
    private static final String OUTPUTCERT_PATH = System.getProperty("user.dir") + File.separator + "output"+ File.separator + "certCA.csr";
    private static final String OUTPUTKEYSTORE_PATH = System.getProperty("user.dir") + File.separator + "output"+ File.separator + "newKeystore.jks";
    //Log
    private static final Logger LOG = Logger.getLogger(StrongBox.class.getName());
    
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
        try {
            System.out.println("#Listar Alias " + this.archivo + ": [BEGIN]");
            Enumeration aliases = ks.aliases();
            while (aliases.hasMoreElements()){
                System.out.println(aliases.nextElement());
            }
            System.out.println("#Listar Alias: [END]");
        } catch (KeyStoreException ex) {
            LOG.error("El Keystore no tiene Aliases: ",ex);;
        }
    } 
    
    public String getNomFirstAlias(){
        String result = "Ningun Alias";
        Enumeration aliases = null;
        try {
          LOG.info("El numero de alias es : " + ks.size());
          aliases = ks.aliases();
        } catch (KeyStoreException ex) {
            LOG.error("No existe ningun Alias",ex);
        }
            if (aliases.hasMoreElements()){
            result = (String)aliases.nextElement();
            LOG.info("Nombre alias: " +result);
            }
            return result;
   
    }
    
    public int cantAlias(){
        try {
            return this.ks.size();
        } catch (KeyStoreException ex) {
            LOG.error("Error con cargar el Keystore", ex);
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
                LOG.info("[Borrado exitoso] "+aliasToDell);
            }else
                LOG.info("No hubo Borrado:" + aliasToDell);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException ex) {
           LOG.error(ex);
        }
    } 
    
    
    public KeyStore cargarKeystore(String pwd, File pathKS){
        try{
            KeyStore ks = KeyStore.getInstance(INSTANCE);
            char[] ksPass = pwd.toCharArray();
            try(InputStream ksData = new FileInputStream(pathKS)){
                try {
                    //antes que un keystore se pueda ser accedido debe ser cargado "load"
                    ks.load(ksData,ksPass);
                    ksData.close();
                } catch (NoSuchAlgorithmException ex) {
                } catch (CertificateException ex) {
                    LOG.error("error certificado", ex);
                }
            } catch (FileNotFoundException ex) {
                LOG.error("error archivo no encontrado", ex);
            } catch (IOException ex) {
                LOG.error("error IO", ex);
            }
            return ks;
            
        }//cargarKeystore sobrecargado//cargarKeystore sobrecargado
        catch (KeyStoreException ex) {
            LOG.error("error keystore", ex);
        }
        return null;
        
    }
    
    public void cargarKeystore() {
        try {
            this.ks = KeyStore.getInstance(INSTANCE);
            this.ksPass = this.password.toCharArray();
            try {
                this.ksData = new FileInputStream(this.archivo);
            } catch (FileNotFoundException ex) {
                LOG.error("error FileNotFound", ex);
            }
            try {
                ks.load(ksData,ksPass);
            } catch (IOException ex) {
                LOG.error("Error con el KeyStore: ", ex);
            } catch (NoSuchAlgorithmException ex) {
                LOG.error("error Algoritmo", ex);
            } catch (CertificateException ex) {
                LOG.error("error certificado", ex);
            }
            ksData.close();
        } //cargarKeystore //cargarKeystore
        catch (KeyStoreException ex) {
            LOG.error("error keystore", ex);
        } catch (IOException ex) {
           LOG.error("error IO", ex);
        }
    }

    public KeyStore.Entry getKey(String alias, String password){
        char[] keyPassowrd = password.toCharArray();
        KeyStore.ProtectionParameter claveEntrada = new KeyStore.PasswordProtection(keyPassowrd);
        try {
            KeyStore.Entry keyEntry = ks.getEntry(alias, claveEntrada);
            LOG.info("Recuperar entrada Keystore alias : "+alias+" [OK]");
            return keyEntry;
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException ex) {
            LOG.error(ex);
        }
        return null;
    }
   
    
    public void setKey(KeyStore.Entry keyEntry,String alias, String password){
        char[] keyPassowrd = password.toCharArray();
        KeyStore.ProtectionParameter claveEntrada = new KeyStore.PasswordProtection(keyPassowrd);
        try {
            FileOutputStream newkeystore = new FileOutputStream(archivo);
            ks.setEntry(alias, keyEntry, claveEntrada);
            LOG.info("Cargar clave del alias :"+alias +" [OK]");
            ks.store(newkeystore, keyPassowrd);
        } catch (KeyStoreException ex) {
            LOG.error(ex);
        } catch (IOException | NoSuchAlgorithmException | CertificateException ex) {
            LOG.error(ex);
        }
    
    }
    
    public void getDatosCertificado(String alias){    
        try {
            System.out.println( this.ks.getCertificate(alias).toString());
        } catch (KeyStoreException ex) {
            LOG.error("Error certificado", ex);
        }
    }
    
    
    void borrarCert(String nomAliasJOB) {
        try {
            ks.deleteEntry(nomAliasJOB);
        } catch (KeyStoreException ex) {
            LOG.error("Error KeyStore", ex);
        }
    }
   
    public boolean existeAlias(String alias){
        try {
            return ks.containsAlias(alias);
        } catch (KeyStoreException ex) {
            LOG.error("No existe el Alias en el Keystore", ex);
        }
        return false; 
    }
    
    public Certificate getCertificado(String alias){
        try {
            return ks.getCertificate(alias);
        } catch (KeyStoreException ex) {
            LOG.error(ex);
        }
        return null;
    }
    
    //Genero un nuevo keystore autofirmado con par de lleve publica, privada y genere requerimieto para CA
    public void newKeystore(String alias,String password, String bodyCert) {
        try {
            CertAndKeyGen keyGen = new CertAndKeyGen("RSA", ALGORITHM, null);
            try {
                keyGen.generate(KEY_LEN);
                PrivateKey pk = keyGen.getPrivateKey();
                X509Certificate cert;
                cert = keyGen.getSelfCertificate(new X500Name(bodyCert), (long) EXPIRATION * 24 * 60 * 60);
                X509Certificate[] chain = new X509Certificate[1];
                chain[0]= cert;
                //creo el request PKCS10
                PKCS10  certreq = new PKCS10 (keyGen.getPublicKey());
                Signature signature = Signature.getInstance(ALGORITHM);
                signature.initSign(pk);
                certreq.encodeAndSign(new X500Name(bodyCert), signature);
                //-creo el archivo CSR-
                //FileOutputStream filereq = new FileOutputStream("C:\\test\\certreq.csr");
                FileOutputStream filereq = new FileOutputStream(OUTPUTCERT_PATH);
                PrintStream ps = new PrintStream(filereq);
                certreq.print(ps);
                ps.close();
                filereq.close();
                
                this.ks = KeyStore.getInstance(INSTANCE);
                this.ksPass = password.toCharArray();
                try (FileOutputStream newkeystore = new FileOutputStream(archivo)) {
                    ks.load(null,null);
                    ks.setKeyEntry(alias, pk, ksPass, chain);
                    ks.store(newkeystore, ksPass);
                }
                 } catch (InvalidKeyException | IOException | CertificateException | SignatureException | KeyStoreException ex) {
                LOG.error("Error a manipular el archivo: "+archivo,ex);
                 }
                
            } 
        catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            LOG.error(ex);
        }
    }
    
    //importo el certificado CA entregado bajo la forma .CER
    public void importCA(File certfile){
        try {
            File keystoreFile = new File(archivo);
           
            //copia .cer -> formato manipulacion
            FileInputStream fis = new FileInputStream(certfile);
            DataInputStream dis = new DataInputStream(fis);
            byte[] bytes = new byte[dis.available()];
            dis.readFully(bytes);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            bais.close();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate certs = cf.generateCertificate(bais);
             //System.out.println(certs.toString());
             //inic keystore
            FileInputStream newib = new FileInputStream(keystoreFile);
            KeyStore keystore = KeyStore.getInstance(INSTANCE);
            keystore.load(newib,ksPass );
            String alias = (String)keystore.aliases().nextElement();
            newib.close();
            
            //recupero el array de certificado del keystore generado para CA
            Certificate[] certChain = keystore.getCertificateChain(alias);
            certChain[0]= certs; //inserto el certificado entregado por CA
            //LOG.info("Inserto certifica CA: "+certChain[0].toString());
            //recupero la clave privada para generar la nueva entrada
            Key pk = (PrivateKey)keystore.getKey(alias, ksPass);      
            keystore.setKeyEntry(alias, pk, ksPass, certChain);
            LOG.info("Keystore actualizado: OK");
                    
            FileOutputStream fos = new FileOutputStream(keystoreFile);
            keystore.store(fos,ksPass);
            fos.close(); 
            
        } catch (FileNotFoundException ex) {
            LOG.error("Error - no se encuentra el archivo",ex);
        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException ex) {
            LOG.error("Error con el certificado",ex);
        } catch (UnrecoverableKeyException ex) {
            LOG.error(ex);
        }
    }
    
}//end StrongBox
