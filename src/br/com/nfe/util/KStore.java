/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.nfe.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Security;
import javax.swing.JOptionPane;

/**
 *
 * @author supervisor
 */
public class KStore {
    public void KeyStore() throws URISyntaxException{
        /**
        * Informações do Certificado Digital.
        */
        String CertificadoDoCliente = "";
        String arquivokeystore = "";
        String senhaDoCertificadoDoCliente = "";


        String curDir = System.getProperty("user.home");
        String curSep = System.getProperty("file.separator");
        String caminho = curDir + curSep + "NfeServices"+ curSep +"certificado"+ curSep;

        try {
            BufferedReader in = new BufferedReader(new FileReader(caminho + "senha.txt"));
            while (in.ready()) {
                senhaDoCertificadoDoCliente += in.readLine();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "KeyStore: Erro ao Ler Arquivo Senha");
            System.exit(0);
        }

        CertificadoDoCliente = caminho + "certificado.pfx";
        arquivokeystore = caminho + "NFeCacerts";

//        String senhaDoCertificadoDoCliente = "agro9090";
//        String CertificadoDoCliente = "/home/supervisor/certificado/certificado.pfx";
//        String arquivokeystore = "/home/supervisor/certificado/NFeCacerts";
  
        /**
        * Informações do Certificado Digital.
        */
        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation","true");
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");

        System.clearProperty("javax.net.ssl.keyStore");
        System.clearProperty("javax.net.ssl.keyStorePassword");
        System.clearProperty("javax.net.ssl.trustStore");
        System.clearProperty("javax.net.ssl.trustStorePassword");

        System.setProperty("javax.net.ssl.keyStore", CertificadoDoCliente);
        System.setProperty("javax.net.ssl.keyStorePassword", senhaDoCertificadoDoCliente);

        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        System.setProperty("javax.net.ssl.trustStore", arquivokeystore);
    }
}
