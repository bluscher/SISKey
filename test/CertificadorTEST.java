/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JKSinjector;

import com.experian.StrongBox;


/**
 *
 * @author e10934a
 */
public class CertificadorTEST {

    private static final String path = "c:/test";
    private static final String KEYSTORE = "keystore.jks";
    private static final String STOREPASS = "changeit";
    private static final String KEYPASS = "changeit";
    private static final String PASSWORD = "Miclave.1";
    private static final String ARCHIVO = "c:/test/ib.jks";
    
    
    
    public static void main(String[] args) throws Throwable {
    
     
        
     StrongBox cr = new StrongBox(PASSWORD,ARCHIVO);
     cr.mostrarAliases();
    // cr.borrarAlias("larbshwzpxf2");
    
     System.out.println("---------------------------------");
     cr.mostrarAliases();
     //cr.generarCertAutoFirma();
     
    }//main
     
}
