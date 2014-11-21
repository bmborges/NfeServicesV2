/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 package br.com.nfe.services;

import br.com.nfe.bean.vnd.VND_NfpedidoBean;
import br.com.nfe.dao.adm.ADM_ParamDAO;
import br.com.nfe.dao.vnd.VND_nfpedidoDAO;

import br.com.nfe.util.DtSystem;
import br.com.nfe.util.KStore;
import br.com.nfe.util.NFeValidacaoXML;

import br.inf.portalfiscal.nfe.consreci.ObjectFactory;
import br.inf.portalfiscal.nfe.consreci.TConsReciNFe;
import br.inf.portalfiscal.nfe.consreci.TProtNFe;
import br.inf.portalfiscal.nfe.consreci.TRetConsReciNFe;
import br.inf.portalfiscal.www.nfe.wsdl.nferetrecepcao2.NfeRetRecepcao2Stub;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

 
 
/**
 *
 * @author supervisor
 */
public class NfeRetRecepcao {
    
    private String ret[] = new String[3];
    String xml = "";
    String cStat = "";
    public VND_nfpedidoDAO nfpedido = null;

    br.com.nfe.gui.Painel main = null;
    static Timer t = new Timer();
    static TimerTask tt;
    static int tempo = 1000 * 30;
    
    
    
    public NfeRetRecepcao(br.com.nfe.gui.Painel main) throws Exception{
        this.main = main;
         nfpedido = new VND_nfpedidoDAO();
    }
    public static void main(String[] args) throws Exception {
        NfeRetRecepcao r = new NfeRetRecepcao(null);
        HashMap pedido_hm = new HashMap();
        VND_nfpedidoDAO pedido_dao = new VND_nfpedidoDAO();
        pedido_hm = pedido_dao.pesquisa_nfpedido_ret_cdpedido(5992099);
        if (Integer.parseInt(pedido_hm.get("cdpedido").toString()) > 0 ){
            r.RetRecepcao(pedido_hm);
        }
    }
public void StartTimer() throws Exception{
        main.CarregaJtxa(">>> TimerNfeRetRecepcao...: " + tempo,Color.BLACK);
        tt = new TimerTask(){
          public void run() {
                try {
                    HashMap pedido_hm = new HashMap();
                    VND_nfpedidoDAO pedido_dao = new VND_nfpedidoDAO();
                    pedido_hm = pedido_dao.pesquisa_nfpedido_ret();
                    if (Integer.parseInt(pedido_hm.get("cdpedido").toString()) > 0 ){
                        RetRecepcao(pedido_hm);
                    }
                } catch (Exception ex) {
                    try {
                        main.CarregaJtxa(">>>TimerNfeRetRecepcao não foi executado...", Color.RED);
                    } catch (BadLocationException ex1) {
                        Logger.getLogger(NfeConsulta.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
          }
       };
       t.schedule(tt, 0, tempo);
    }
    public void StopTimer() throws Exception{
        main.CarregaJtxa(">>>Stop TimerNfeRetRecepcao...",Color.BLACK);
        tt.cancel();
    }
   
 public void teste() throws JAXBException{
     String retorno = "<retConsReciNFe xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"2.00\"><tpAmb>1</tpAmb><verAplic>GO2.0</verAplic><nRec>521000146261425</nRec><cStat>104</cStat><xMotivo>Lote processado</xMotivo><cUF>52</cUF><protNFe versao=\"2.00\"><infProt Id=\"NFe00\"><tpAmb>1</tpAmb><verAplic>GO2.0</verAplic><chNFe>52121102233732000408550000000459741053912663</chNFe><dhRecbto>2012-11-28T15:16:28</dhRecbto><cStat>612</cStat><xMotivo>Rejeição: cEANTrib inválido</xMotivo></infProt></protNFe></retConsReciNFe>";
     
     JAXBContext context = JAXBContext.newInstance(TRetConsReciNFe.class);  
     Unmarshaller unmarshaller = context.createUnmarshaller();  
     TRetConsReciNFe retConsReciNFe  = unmarshaller.unmarshal(new StreamSource(new StringReader(retorno)), TRetConsReciNFe.class).getValue();  

     TProtNFe prot = new TProtNFe();
     for (int i = 0; i < retConsReciNFe.getProtNFe().size(); i++) {
         prot.setInfProt(retConsReciNFe.getProtNFe().get(i).getInfProt());
         System.out.println(prot.getInfProt().getCStat());
     }
 
//     prot = retConsReciNFe.getProtNFe().size();
     
 }   
 public String Envio(String Xml, URL url, String CUF, String VersaoDados) throws XMLStreamException, RemoteException, BadLocationException{
    String Line = "" ;
    NfeRetRecepcao2Stub.NfeRetRecepcao2Result result = null;
        
    OMElement ome = AXIOMUtil.stringToOM(Xml);  

    NfeRetRecepcao2Stub.NfeDadosMsg dadosMsg = new NfeRetRecepcao2Stub.NfeDadosMsg();
        
    dadosMsg.setExtraElement(ome);  
    NfeRetRecepcao2Stub.NfeCabecMsg nfeCabecMsg = new NfeRetRecepcao2Stub.NfeCabecMsg();
        
    nfeCabecMsg.setCUF(CUF);  
    nfeCabecMsg.setVersaoDados(VersaoDados);  
        
    NfeRetRecepcao2Stub.NfeCabecMsgE nfeCabecMsgE = new NfeRetRecepcao2Stub.NfeCabecMsgE();

    nfeCabecMsgE.setNfeCabecMsg(nfeCabecMsg);  
        
    NfeRetRecepcao2Stub stub = new NfeRetRecepcao2Stub(url.toString());
                
    try {            
        result = stub.nfeRetRecepcao2(dadosMsg, nfeCabecMsgE);  
        Line = result.getExtraElement().toString();
    } catch (Exception e) {
        main.CarregaJtxa("Erro Envio NfeRetRecepcao : " + e.toString(),Color.RED);
    }


    return Line;
}   
    
    
 public void RetRecepcao(HashMap pedido_hm) throws Exception {
     
             String nRec  = pedido_hm.get("obs_nfe").toString();
             String iduf = pedido_hm.get("iduf").toString();
             int cdpedido = Integer.parseInt(pedido_hm.get("cdpedido").toString());
             try {
                main.CarregaJtxa("RetRecepcao Pedido: " + cdpedido,Color.DARK_GRAY);
             } catch (Exception e) {
             }
             String xml_nfe = pedido_hm.get("xml_nfe").toString();
             //int flag = Integer.parseInt(pedido_hm.get("flag").toString());
             
             KStore k = new KStore();
             k.KeyStore(); 
     
             ADM_ParamDAO dao = new ADM_ParamDAO();
             
             String[] endereco = null;
             endereco = dao.pesquisa_webservice("WebserverNfeRetRecepcao",iduf);
             
             URL url = new URL(endereco[0]);
             
             TConsReciNFe consnfe = new TConsReciNFe();
             consnfe.setVersao("2.00");
             consnfe.setTpAmb("1");
             consnfe.setNRec(nRec);
             
             JAXBContext context = null;

            try {
                StringWriter out = new StringWriter();

                context = JAXBContext.newInstance("br.inf.portalfiscal.nfe.consreci");
                Marshaller marshaller = context.createMarshaller();
                JAXBElement<TConsReciNFe> element = new ObjectFactory().createConsReciNFe(consnfe);
                marshaller.setProperty(
                    javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE
                );
                
                marshaller.marshal(element,new StreamResult(out));

                xml = out.toString();
                
                xml = xml.replace("xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
                
               
            } catch (JAXBException e) {
                main.CarregaJtxa(e.toString(),Color.RED);
            }
        
           // System.out.println(xml);
                               
            VND_nfpedidoDAO pedido_dao = new VND_nfpedidoDAO();
            VND_NfpedidoBean pedido_bean = new VND_NfpedidoBean();  
            pedido_bean.setCdpedido(cdpedido);
            
            String validation = NFeValidacaoXML.validaRetRecepcaoNfe(xml);  
            if(!validation.isEmpty()) {  
                main.CarregaJtxa(validation.replace("cvc-pattern-valid: ", ""),Color.RED);
                nfpedido.insert_nfpedido_rejeicao(cdpedido, validation.replace("cvc-pattern-valid: ", ""), null);
                
                pedido_bean.setStatus_nfe(0);
                pedido_bean.setSituacaonf("N");
                pedido_dao.Update_nfpedido_env(pedido_bean);
                
            }  else {
                 /* Declaração variaveis de retorno */

                 String retorno = Envio(xml, url, iduf, endereco[2]);
                 
                 if (retorno.length() > 0 ){

                     context = JAXBContext.newInstance(TRetConsReciNFe.class);
                     Unmarshaller unmarshaller = context.createUnmarshaller();
                     TRetConsReciNFe retConsReciNFe  = unmarshaller.unmarshal(new StreamSource(new StringReader(retorno)), TRetConsReciNFe.class).getValue();

                     TProtNFe protnfe = new TProtNFe();

                     int cstat = Integer.parseInt(retConsReciNFe.getCStat());
                     pedido_bean.setObs_nfe(nRec);
                     pedido_bean.setStatus_nfe(cstat);
                     pedido_dao.Update_nfpedido_env(pedido_bean);

                     for (int i = 0; i < retConsReciNFe.getProtNFe().size(); i++) {

                        //System.out.println(protnfe.getInfProt().getCStat() +" "+ protnfe.getInfProt().getXMotivo() );

                        protnfe.setInfProt(retConsReciNFe.getProtNFe().get(i).getInfProt());

                        cstat = Integer.parseInt(protnfe.getInfProt().getCStat());
                        String protocolo = protnfe.getInfProt().getNProt();
                        if (cstat == 100 && retConsReciNFe.getCStat().equals("104")){

                            Document document = documentFactory(xml_nfe);
                            NodeList nodeListNfe = document.getDocumentElement().getElementsByTagName("NFe");
                            NodeList nodeListInfNfe = document.getElementsByTagName("infNFe");

                            Element el = (Element) nodeListInfNfe.item(i);
                            String chaveNFe = el.getAttribute("Id");

                            String xmlNFe = outputXML(nodeListNfe.item(i));
                            String xmlProtNFe = getProtNFe(retorno, chaveNFe);

                            pedido_bean.setXml_nfe(buildNFeProc(xmlNFe, xmlProtNFe));
                            pedido_bean.setProtocolo(protocolo);
                            pedido_bean.setStatus_nfe(cstat);
                            pedido_bean.setSituacaonf("N");
                            try {
                                main.CarregaJtxa(">>> Update RetRecepcaoNfe....:" + cdpedido,Color.BLACK);
                            } catch (Exception e) {
                            }
                            pedido_dao.Update_nfpedido_env(pedido_bean);


                            ControleImpressao c = new ControleImpressao(main);
                            c.PesquisaXML(cdpedido, pedido_bean.getXml_nfe());


                          } else {
                            pedido_bean.setSituacaonf("N");
                            //pedido_bean.setFlag(String.valueOf(flag+1));
                            pedido_bean.setStatus_nfe(cstat);
                            pedido_bean.setObs_nfe(null);
                            pedido_dao.Update_nfpedido_env(pedido_bean);
                            main.CarregaJtxa(cstat + " - " + retConsReciNFe.getProtNFe().get(i).getInfProt().getXMotivo(),Color.RED);
                          }
                    }
                }
            } 
     }
     private static Document documentFactory(String xml) throws SAXException,  
            IOException, ParserConfigurationException {  
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        factory.setNamespaceAware(true);  
        Document document = factory.newDocumentBuilder().parse(  
                new ByteArrayInputStream(xml.getBytes()));  
        return document;  
    }
     
    private static String outputXML(Node node) throws TransformerException {  
        ByteArrayOutputStream os = new ByteArrayOutputStream();  
        TransformerFactory tf = TransformerFactory.newInstance();  
        Transformer trans = tf.newTransformer();  
        trans.transform(new DOMSource(node), new StreamResult(os));  
        String xml = os.toString();  
        if ((xml != null) && (!"".equals(xml))) {  
            xml = xml.replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>", "");  
        }  
        return xml;  
    }
    private static String buildNFeProc(String xmlNFe, String xmlProtNFe) {  
        StringBuilder nfeProc = new StringBuilder();  
        nfeProc.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")  
            .append("<nfeProc versao=\"2.00\" xmlns=\"http://www.portalfiscal.inf.br/nfe\">")  
            .append(xmlNFe)  
            .append(xmlProtNFe)  
            .append("</nfeProc>");  
        return nfeProc.toString();  
    }
    private static String getProtNFe(String xml, String chaveNFe) throws SAXException,   
            IOException, ParserConfigurationException, TransformerException {  
        Document document = documentFactory(xml);  
        NodeList nodeListProtNFe = document.getDocumentElement().getElementsByTagName("protNFe");  
        NodeList nodeListChNFe = document.getElementsByTagName("chNFe");  
        for (int i = 0; i < nodeListProtNFe.getLength(); i++) {  
            Element el = (Element) nodeListChNFe.item(i);  
            String chaveProtNFe = el.getTextContent();  
            if (chaveNFe.contains(chaveProtNFe)) {  
                return outputXML(nodeListProtNFe.item(i));  
            }  
        }         
        return "";  
    }     
}

