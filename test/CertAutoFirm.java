/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JKSinjector;

/**
 *
 * @author e10934a
 */
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate; //error checkear esta clase
import java.util.Date;
import sun.security.x509.*;

public class CertAutoFirm {

//Medida de las claves
public static final int KEY_LEN = 2048;

//Fecha de expiración
private static final int EXPIRATION = 365;

//Algoritmo de firma a usar
private static final String ALGORITHM = "SHA1withRSA";

/** 
 * Create a self-signed X.509 Certificate
 * @param dn the X.509 Distinguished Name, eg "CN=EXPERIAN_Java, L=CABA, C=AR"
 * @param pair the KeyPair
 * @param days how many days from now the Certificate is valid for
 * @param algorithm the signing algorithm, eg "SHA1withRSA"
 */ 
X509Certificate generateCertificate(String dn, KeyPair pair, int days, String algorithm)
  throws GeneralSecurityException, IOException
{
  PrivateKey privkey = pair.getPrivate();
  X509CertInfo info = new X509CertInfo();
  Date from = new Date();
  Date to = new Date(from.getTime() + days * 86400000l);
  CertificateValidity interval = new CertificateValidity(from, to);
  BigInteger sn = new BigInteger(64, new SecureRandom());
  X500Name owner = new X500Name(dn);
 
  info.set(X509CertInfo.VALIDITY, interval);
  info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
  info.set(X509CertInfo.SUBJECT, owner);
  info.set(X509CertInfo.ISSUER, owner);
  info.set(X509CertInfo.KEY, new CertificateX509Key(pair.getPublic()));
  info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
  AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
  info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));
 
  // Sign the cert to identify the algorithm that's used.
  X509CertImpl cert = new X509CertImpl(info);
  cert.sign(privkey, algorithm);
 
  // Update the algorith, and resign.
  algo = (AlgorithmId)cert.get(X509CertImpl.SIG_ALG);
  info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
  cert = new X509CertImpl(info);
  cert.sign(privkey, algorithm);
  return cert;
}   



public static void main (String[] args) throws Exception {

KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
keyGen.initialize(KEY_LEN);
KeyPair kPair = keyGen.genKeyPair();

String subject = "CN= EXPERIAN_Java,O=Experian,OU=Experian,L=CABA,ST=CABA,C=AR";
CertAutoFirm c = new CertAutoFirm();
X509Certificate cert = c.generateCertificate(subject, kPair, KEY_LEN, ALGORITHM);

FileOutputStream certFile = new FileOutputStream("C://test/certificadoTEST.crt");
certFile.write(cert.getEncoded());
certFile.close();
}
}