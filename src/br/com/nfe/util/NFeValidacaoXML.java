/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.nfe.util;

import java.io.StringReader;  
import java.net.URL;  
  
import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.transform.stream.StreamSource;  
import javax.xml.validation.Schema;  
import javax.xml.validation.SchemaFactory;  
import javax.xml.validation.Validator;  
  
import org.xml.sax.InputSource;  
import org.xml.sax.SAXParseException;

/**
 *
 * @author supervisor
 */
public class NFeValidacaoXML {
    public static void main(String[] args) {    
         
    }    
 private static String ValidaDoc(String stringXml, String xsdFileName)  
    {  
        //Define o tipo de  - we use W3C  
        String schemaLang = "http://www.w3.org/2001/XMLSchema";  
        //valida driver  
        SchemaFactory factory = SchemaFactory.newInstance(schemaLang);  
        //  
        try   
        {
            URL xsdPath = NFeValidacaoXML.class.getResource("/schemas/" + xsdFileName);  
  
            Schema schema = factory.newSchema(new StreamSource(xsdPath.toURI().toString()));  
            Validator validator = schema.newValidator();  
            //Perform the validation:  
            validator.validate(new StreamSource(new StringReader(stringXml)));  
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();  
            DocumentBuilder builder = fact.newDocumentBuilder();  
            builder.parse(new InputSource(new StringReader(stringXml)));  
        }  
        catch (Exception e)  
        {  
            if(e instanceof SAXParseException)  
                return "Erro no Schema XML na Col: "+ ((SAXParseException) e).getColumnNumber() + " | Lin: " + ((SAXParseException) e).getLineNumber() + " \n" + ((SAXParseException) e).getLocalizedMessage();  
            else  
                return "Unknow error attemping to validate XML.";  
        }  
        return "";  
    }  
//    public static String validaPedCartaCorrecao(String stringXml) {  
//        return ValidaDoc(stringXml, "envCCe_v1.00.xsd");  
//    }  
      
    public static String validaCartaCorrecao(String stringXml) {  
        return ValidaDoc(stringXml, "envCCe_v1.00.xsd");  
    }
    public static String validaEnviNfe(String stringXml) {  
        return ValidaDoc(stringXml, "enviNFe_v2.00.xsd");  
    }
    public static String validaCancNfe(String stringXml){
        return ValidaDoc(stringXml, "cancNFe_v2.00.xsd");
    }
    public static String validaEnvCanc(String stringXml){
        return ValidaDoc(stringXml, "EnvEventoCancNFe_v1.00.xsd");
    }
    public static String validaDownloadNfe(String stringXml){
        return ValidaDoc(stringXml, "downloadNFe_v1.00.xsd");
    }
    public static String validaRetRecepcaoNfe(String stringXml){
        return ValidaDoc(stringXml, "consReciNFe_v2.00.xsd");
    }
    public static String validaDpecNfe(String stringXml){
        return ValidaDoc(stringXml, "envDPEC_v1.01.xsd");
    }   
    public static String validaManifesto(String stringXml){
        return ValidaDoc(stringXml, "envConfRecebto_v1.00.xsd");
    } 
}
