/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author e10934a
 */

import java.io.*;
import java.net.*;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
 
public class TestSSL_Keystore {
 
    private static final String HOST = "https://localhost:8093/DAServiceREST?initStrategy=POSOF";
 
    public static void main(String[] args) throws Exception {
 
        System.setProperty("javax.net.debug", "ssl,handshake");
 
        System.setProperty("javax.net.ssl.keyStoreType", "JKS");
        System.setProperty("javax.net.ssl.keyStore", "C:\\SIS\\SISv2.9\\sis\\key\\jetty\\ib.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "Miclave.1");
 
        final URL url = new URL(HOST);
        HttpsURLConnection conection = (HttpsURLConnection) url.openConnection();
        
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conection.getInputStream()));
        String inputLine;
        final StringBuffer response = new StringBuffer();
        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();
        System.out.println(response.toString());
    }

    private static void rintln(String toString) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

