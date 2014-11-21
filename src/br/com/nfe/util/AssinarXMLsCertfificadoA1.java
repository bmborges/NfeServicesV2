/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.nfe.util;

import java.io.BufferedReader;  
import java.io.ByteArrayInputStream;  
import java.io.ByteArrayOutputStream;  
import java.io.FileInputStream;  
import java.io.FileReader;
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;  
import java.security.KeyStore;  
import java.security.NoSuchAlgorithmException;  
import java.security.PrivateKey;  
import java.security.cert.X509Certificate;  
import java.util.ArrayList;  
import java.util.Collections;  
import java.util.Enumeration;  
import java.util.List;  
import javax.swing.JOptionPane;
  
import javax.xml.crypto.dsig.CanonicalizationMethod;  
import javax.xml.crypto.dsig.DigestMethod;  
import javax.xml.crypto.dsig.Reference;  
import javax.xml.crypto.dsig.SignatureMethod;  
import javax.xml.crypto.dsig.SignedInfo;  
import javax.xml.crypto.dsig.Transform;  
import javax.xml.crypto.dsig.XMLSignature;  
import javax.xml.crypto.dsig.XMLSignatureFactory;  
import javax.xml.crypto.dsig.dom.DOMSignContext;  
import javax.xml.crypto.dsig.keyinfo.KeyInfo;  
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;  
import javax.xml.crypto.dsig.keyinfo.X509Data;  
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;  
import javax.xml.crypto.dsig.spec.TransformParameterSpec;  
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.transform.Transformer;  
import javax.xml.transform.TransformerException;  
import javax.xml.transform.TransformerFactory;  
import javax.xml.transform.dom.DOMSource;  
import javax.xml.transform.stream.StreamResult; 
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author supervisor
 */
public class AssinarXMLsCertfificadoA1 {
    
    private static final String INFINUT = "infInut";  
    private static final String INFCANC = "infCanc";  
    private static final String NFE = "NFe";  
    private static final String INFEVENT = "infEvento";
    private static final String INFDPEC = "infDPEC";
    private static final String EVENTO = "evento";
    
//    private String caminhoDoCertificadoDoCliente = "/home/supervisor/certificado/certificado.pfx";
    private String caminhoDoCertificadoDoCliente = "";
    private String senhaDoCertificadoDoCliente = "";  
    
    
    private PrivateKey privateKey;  
    private KeyInfo keyInfo;

    public AssinarXMLsCertfificadoA1() {

        String curDir = System.getProperty("user.home");
        String curSep = System.getProperty("file.separator");
        String caminho = curDir + curSep + "NfeServices"+ curSep +"certificado"+ curSep;

        try {
            BufferedReader in = new BufferedReader(new FileReader(caminho+"senha.txt"));
            while (in.ready()) {
                senhaDoCertificadoDoCliente += in.readLine();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "AssinarXMLsCertfificadoA1: Erro ao Ler Arquivo Senha");
            System.exit(0);
        }

        caminhoDoCertificadoDoCliente = caminho+"certificado.pfx";

    }


  
    public static void main(String[] args) {  
        try {  

            AssinarXMLsCertfificadoA1 assinarXMLsCertfificadoA1 = new AssinarXMLsCertfificadoA1();  
  
            /** 
             * Assinando o XML de Lote da NF-e 
             * fileEnviNFe = Caminho do Arquivo XML (EnviNFe) gerado; 
             */  
//            info("");  
//            String fileEnviNFe = "C:/JavaC/NF-e/xmlEnviNFe.xml";  
//            String xmlEnviNFe = lerXML(fileEnviNFe);  
//            String xmlEnviNFeAssinado = assinarXMLsCertfificadoA1.assinaEnviNFe(  
//                    xmlEnviNFe, caminhoDoCertificadoDoCliente, senhaDoCertificadoDoCliente);  
//            info("XML EnviNFe Assinado: " + xmlEnviNFeAssinado);  
  
            /** 
             * Assinando o XML de Cancelamento da NF-e 
             * fileCancNFe = Caminho do Arquivo XML (CancNFe) gerado; 
             */  
//            info("");  
//            String fileCancNFe = "C:/JavaC/NF-e/xmlCancNFe.xml";  
//            String xmlCancNFe = lerXML(fileCancNFe);  
//            String xmlCancNFeAssinado = assinarXMLsCertfificadoA1.assinaCancNFe(  
//                    xmlCancNFe, caminhoDoCertificadoDoCliente, senhaDoCertificadoDoCliente);  
//            info("XML CancNFe Assinado: " + xmlCancNFeAssinado);  
  
            /** 
             * Assinando o XML de Inutilizacao da NF-e 
             * fileInutNFe = Caminho do Arquivo XML (InutNFe) gerado; 
             */  
//            info("");  
//            String fileInutNFe = "C:/JavaC/NF-e/xmlInutNFe.xml";  
//            String xmlInutNFe = lerXML(fileInutNFe);  
//            String xmlInutNFeAssinado = assinarXMLsCertfificadoA1.assinaInutNFe(  
//                    xmlInutNFe, caminhoDoCertificadoDoCliente, senhaDoCertificadoDoCliente);  
//            info("XML InutNFe Assinado: " + xmlInutNFeAssinado);
            
            
           
  
        } catch (Exception e) {  
            error("| " + e.toString());  
        }  
    }  
  
    /** 
     * Assinatura do XML de Envio de Lote da NF-e utilizando Certificado 
     * Digital A1. 
     * @param xml 
     * @param certificado 
     * @param senha 
     * @return 
     * @throws Exception 
     */  
    
  
    public String assinaXml(String xml, String tipo) throws Exception {
        
        String retorno = "";
        if (tipo.equals("Evento")) {
            retorno = assinaEnvEvento(xml, caminhoDoCertificadoDoCliente, senhaDoCertificadoDoCliente, EVENTO);
        }
        if (tipo.equals("assinaInfEvento")) {
            retorno = assinaManifestoDestinatario(xml, caminhoDoCertificadoDoCliente, senhaDoCertificadoDoCliente, INFEVENT);
        }
        if (tipo.equals("assinaInutNFe")){
            retorno = assinaCancelametoInutilizacao(xml, caminhoDoCertificadoDoCliente, senhaDoCertificadoDoCliente, INFINUT); 
        }
        if (tipo.equals("assinaCancNFe")){
            retorno = assinaCancelametoInutilizacao(xml, caminhoDoCertificadoDoCliente, senhaDoCertificadoDoCliente, INFCANC); 
        }
        if (tipo.equals("assinaEnvNFe")){
            retorno = assinaEnviNFe(xml, caminhoDoCertificadoDoCliente, senhaDoCertificadoDoCliente,NFE);
        }
        if (tipo.equals("assinaEnvDPEC")){
            retorno = assinaCancInutDpec(xml, caminhoDoCertificadoDoCliente, senhaDoCertificadoDoCliente,INFDPEC);
        }
        return retorno;
    }  
    public String assinaEnviNFe(String xml, String certificado, String senha, String tag)  
            throws Exception {  
        Document document = documentFactory(xml);  
        XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");  
        ArrayList<Transform> transformList = signatureFactory(signatureFactory);  
        loadCertificates(certificado, senha, signatureFactory);  
  
        for (int i = 0; i < document.getDocumentElement().getElementsByTagName(tag).getLength(); i++) {  
            assinarNFe(signatureFactory, transformList, privateKey, keyInfo, document, i);  
        }  
  
        return outputXML(document);  
    }
    private void assinarNFe(XMLSignatureFactory fac,  
            ArrayList<Transform> transformList, PrivateKey privateKey,  
            KeyInfo ki, Document document, int indexNFe) throws Exception {  
  
        NodeList elements = document.getElementsByTagName("infNFe");  
        org.w3c.dom.Element el = (org.w3c.dom.Element) elements.item(indexNFe);  
        String id = el.getAttribute("Id");  
  
        Reference ref = (Reference) fac.newReference("#" + id,  
                fac.newDigestMethod(DigestMethod.SHA1, null), transformList,  
                null, null);  
  
        SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(  
                CanonicalizationMethod.INCLUSIVE,  
                (C14NMethodParameterSpec) null), fac  
                .newSignatureMethod(SignatureMethod.RSA_SHA1, null),  
                Collections.singletonList(ref));  
  
        XMLSignature signature = fac.newXMLSignature(si, ki);  
  
        DOMSignContext dsc = new DOMSignContext(privateKey,   
                document.getDocumentElement().getElementsByTagName(NFE).item(indexNFe));  
        signature.sign(dsc);  
    }  
  
    private String assinaCancelametoInutilizacao(String xml,  
            String certificado, String senha, String tagCancInut)  
            throws Exception {  
        Document document = documentFactory(xml);  
  
        XMLSignatureFactory signatureFactory = XMLSignatureFactory  
                .getInstance("DOM");  
        ArrayList<Transform> transformList = signatureFactory(signatureFactory);  
        loadCertificates(certificado, senha, signatureFactory);  
  
        NodeList elements = document.getElementsByTagName(tagCancInut);  
        org.w3c.dom.Element el = (org.w3c.dom.Element) elements.item(0);  
        String id = el.getAttribute("Id");  
  
        Reference ref = (Reference) signatureFactory.newReference("#" + id,  
                signatureFactory.newDigestMethod(DigestMethod.SHA1, null),  
                transformList, null, null);  
  
        SignedInfo si = signatureFactory.newSignedInfo(signatureFactory  
                .newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,  
                        (C14NMethodParameterSpec) null), signatureFactory  
                .newSignatureMethod(SignatureMethod.RSA_SHA1, null),  
                Collections.singletonList(ref));  
  
        XMLSignature signature = signatureFactory.newXMLSignature(si, keyInfo);  
  
        DOMSignContext dsc = new DOMSignContext(privateKey, document.getFirstChild());  
        signature.sign(dsc);  
  
        return outputXML(document);  
    }

     private String assinaEnvEvento(String xml, String certificado, String senha, String tag) throws Exception {  
        Document document = documentFactory(xml);  
        XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");  
        ArrayList<Transform> transformList = signatureFactory(signatureFactory);  
        loadCertificates(certificado, senha, signatureFactory);  
  
        for (int i = 0; i < document.getDocumentElement().getElementsByTagName(tag).getLength(); i++) {  
            assinarEvento(signatureFactory, transformList, privateKey, keyInfo, document, i);  
        }  
  
        return outputXML(document);  
    }
    private void assinarEvento(XMLSignatureFactory fac,  
            ArrayList<Transform> transformList, PrivateKey privateKey,  
            KeyInfo ki, Document document, int indexNFe) throws Exception {  
  
        NodeList elements = document.getElementsByTagName("infEvento");  
        org.w3c.dom.Element el = (org.w3c.dom.Element) elements.item(indexNFe);  
        String id = el.getAttribute("Id");  
  
        Reference ref = (Reference) fac.newReference("#" + id,  
                fac.newDigestMethod(DigestMethod.SHA1, null), transformList,  
                null, null);  
  
        SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(  
                CanonicalizationMethod.INCLUSIVE,  
                (C14NMethodParameterSpec) null), fac  
                .newSignatureMethod(SignatureMethod.RSA_SHA1, null),  
                Collections.singletonList(ref));  
  
        XMLSignature signature = fac.newXMLSignature(si, ki);  
  
        DOMSignContext dsc = new DOMSignContext(privateKey,   
                document.getDocumentElement().getElementsByTagName(EVENTO).item(indexNFe));  
        signature.sign(dsc);  
    } 
    
    private String assinaManifestoDestinatario(String xml,  
            String certificado, String senha, String tag)  
            throws Exception {  
        Document document = documentFactory(xml);  
  
        XMLSignatureFactory signatureFactory = XMLSignatureFactory  
                .getInstance("DOM");  
        ArrayList<Transform> transformList = signatureFactory(signatureFactory);  
        loadCertificates(certificado, senha, signatureFactory);  
  
        NodeList elements = document.getElementsByTagName(tag);  
        org.w3c.dom.Element el = (org.w3c.dom.Element) elements.item(0);  
        String id = el.getAttribute("Id");  
  
        Reference ref = (Reference) signatureFactory.newReference("#" + id,  
                signatureFactory.newDigestMethod(DigestMethod.SHA1, null),  
                transformList, null, null);  
  
        SignedInfo si = signatureFactory.newSignedInfo(signatureFactory  
                .newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,  
                        (C14NMethodParameterSpec) null), signatureFactory  
                .newSignatureMethod(SignatureMethod.RSA_SHA1, null),  
                Collections.singletonList(ref));  
  
        XMLSignature signature = signatureFactory.newXMLSignature(si, keyInfo);  
  
        DOMSignContext dsc = new DOMSignContext(privateKey, document.getFirstChild());  
        signature.sign(dsc);  
  
        return outputXML(document);  
    }
        private String assinaCancInutDpec(String xml, String certificado, String senha, String tag)  
            throws Exception {  
        Document document = documentFactory(xml);  
  
        XMLSignatureFactory signatureFactory = XMLSignatureFactory  
                .getInstance("DOM");  
        ArrayList<Transform> transformList = signatureFactory(signatureFactory);  
        loadCertificates(certificado, senha, signatureFactory);  
  
        NodeList elements = document.getElementsByTagName(tag);  
        org.w3c.dom.Element el = (org.w3c.dom.Element) elements.item(0);  
        String id = el.getAttribute("Id");  
  
        Reference ref = signatureFactory.newReference("#" + id,  
                signatureFactory.newDigestMethod(DigestMethod.SHA1, null),  
                transformList, null, null);  
  
        SignedInfo si = signatureFactory.newSignedInfo(signatureFactory  
                .newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,  
                        (C14NMethodParameterSpec) null), signatureFactory  
                .newSignatureMethod(SignatureMethod.RSA_SHA1, null),  
                Collections.singletonList(ref));  
  
        XMLSignature signature = signatureFactory.newXMLSignature(si, keyInfo);  
  
        DOMSignContext dsc = new DOMSignContext(privateKey, document.getFirstChild());  
        signature.sign(dsc);  
  
        return outputXML(document);  
    }
  
    private ArrayList<Transform> signatureFactory(  
            XMLSignatureFactory signatureFactory)  
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {  
        ArrayList<Transform> transformList = new ArrayList<Transform>();  
        TransformParameterSpec tps = null;  
        Transform envelopedTransform = signatureFactory.newTransform(  
                Transform.ENVELOPED, tps);  
        Transform c14NTransform = signatureFactory.newTransform(  
                "http://www.w3.org/TR/2001/REC-xml-c14n-20010315", tps);  
  
        transformList.add(envelopedTransform);  
        transformList.add(c14NTransform);  
        return transformList;  
    }  
  
    private Document documentFactory(String xml) throws SAXException,  
            IOException, ParserConfigurationException {  
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        factory.setNamespaceAware(true);  
        Document document = (Document) factory.newDocumentBuilder().parse(  
                new ByteArrayInputStream(xml.getBytes()));  
        return document;  
    }  
  
    private void loadCertificates(String certificado, String senha,
            XMLSignatureFactory signatureFactory) throws Exception {  
  
        InputStream entrada = new FileInputStream(certificado);  
        KeyStore ks = KeyStore.getInstance("pkcs12");  
        try {  
            ks.load(entrada, senha.toCharArray());  
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Senha do Certificado Digital incorreta ou Certificado inválido.");
            System.exit(0);
        }  
  
        KeyStore.PrivateKeyEntry pkEntry = null;  
        Enumeration<String> aliasesEnum = ks.aliases();  
        while (aliasesEnum.hasMoreElements()) {  
            String alias = (String) aliasesEnum.nextElement();  
            if (ks.isKeyEntry(alias)) {  
                pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(alias,  
                        new KeyStore.PasswordProtection(senha.toCharArray()));  
                privateKey = pkEntry.getPrivateKey();  
                break;  
            }  
        }  
  
        X509Certificate cert = (X509Certificate) pkEntry.getCertificate();  
        info("SubjectDN: " + cert.getSubjectDN().toString());  
  
        KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();  
        List<X509Certificate> x509Content = new ArrayList<X509Certificate>();  
  
        x509Content.add(cert);  
        X509Data x509Data = keyInfoFactory.newX509Data(x509Content);  
        keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));  
    }  
  public void loadCertificates(String certificado, String senha) throws Exception {

        InputStream entrada = new FileInputStream(certificado);
        KeyStore ks = KeyStore.getInstance("pkcs12");
        try {
            ks.load(entrada, senha.toCharArray());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Senha do Certificado Digital incorreta ou Certificado inválido.");
            System.exit(0);
        }

    }
    private String outputXML(Document doc) throws TransformerException {  
        ByteArrayOutputStream os = new ByteArrayOutputStream();  
        TransformerFactory tf = TransformerFactory.newInstance();  
        Transformer trans = tf.newTransformer();  
        trans.transform(new DOMSource(doc), new StreamResult(os));  
        String xml = os.toString();  
        if ((xml != null) && (!"".equals(xml))) {  
            xml = xml.replaceAll("\\r\\n", "");  
            xml = xml.replaceAll(" standalone=\"no\"", "");  
        }  
        return xml;  
    }  
  
    private static String lerXML(String fileXML) throws IOException {  
        String linha = "";  
        StringBuilder xml = new StringBuilder();  
  
        BufferedReader in = new BufferedReader(new InputStreamReader(  
                new FileInputStream(fileXML)));  
        while ((linha = in.readLine()) != null) {  
            xml.append(linha);  
        }  
        in.close();  
  
        return xml.toString();  
    }  
  
    /** 
     * Log ERROR. 
     *  
     * @param error 
     */  
    private static void error(String error) {  
        System.out.println("| ERROR: " + error);  
    }  
  
    /** 
     * Log INFO. 
     *  
     * @param info 
     */  
    private static void info(String info) {  
       // System.out.println("| INFO: " + info);  
    }
    
}
