/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 package br.com.nfe.services;

import br.com.nfe.bean.vnd.VND_NfpedidoBean;
import br.com.nfe.dao.adm.ADM_ParamDAO;
import br.com.nfe.dao.vnd.VND_nfpedidoDAO;
import br.com.nfe.util.AssinarXMLsCertfificadoA1;
import br.com.nfe.util.DtSystem;
import br.com.nfe.util.KStore;
import br.com.nfe.util.NFeValidacaoXML;
import br.inf.portalfiscal.nfe.env.*;
import br.inf.portalfiscal.www.nfe.wsdl.nferecepcao2.NfeRecepcao2Stub;
import java.awt.Color;
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
import javax.xml.stream.XMLStreamException;


import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

 
 
/**
 *
 * @author supervisor
 */
public class NfeRecepcao {
    
    private String ret[] = new String[3];
    String xml = "";
    String cStat = "";
    public static TNFe.InfNFe infnfe = null;
    public VND_nfpedidoDAO nfpedido = null;

    static Timer t = new Timer();
    static TimerTask tt;
    static int tempo = 1000 * 30;

    br.com.nfe.gui.Painel main = null;
    
    
    public NfeRecepcao(br.com.nfe.gui.Painel main) throws Exception{
        this.main = main;
        nfpedido = new VND_nfpedidoDAO();
    }
    public static void main(String[] args) throws Exception {
        NfeRecepcao r = new NfeRecepcao(null);
        r.testeXML(6977483);

//        HashMap pedido_hm = new HashMap();
//        VND_nfpedidoDAO nfpedido = new VND_nfpedidoDAO();
//        pedido_hm = nfpedido.pesquisa_nfpedido_env();
//        if (Integer.parseInt(pedido_hm.get("cdpedido").toString()) > 0){
//            r.Recepcao(pedido_hm,false);
//        }


    }
   
public void StartTimer() throws Exception{
        main.CarregaJtxa(">>> TimerNfeRecepcao...: " + tempo,Color.BLACK);
        tt = new TimerTask(){
          public void run() {
                try {
                    HashMap pedido_hm = new HashMap();
                    nfpedido = new VND_nfpedidoDAO();
                    pedido_hm = nfpedido.pesquisa_nfpedido_env();
                    if (Integer.parseInt(pedido_hm.get("cdpedido").toString()) > 0){
                        Recepcao(pedido_hm,false);
                    }
                    pedido_hm = nfpedido.pesquisa_nfpedido_envDEPC();
                    if (Integer.parseInt(pedido_hm.get("cdpedido").toString()) > 0){
                        Recepcao(pedido_hm,true);
                    }
                } catch (Exception ex) {
                    try {
                        main.CarregaJtxa(">>>TimerNfeRecepcao não foi executado...", Color.BLACK);
                    } catch (BadLocationException ex1) {
                        ex1.printStackTrace();
                    }
                }
          }
       };
       t.schedule(tt, 0, tempo);
    }
    public void StopTimer() throws Exception{
        try {
            main.CarregaJtxa(">>>Stop TimerNfeRecepcao...",Color.BLACK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tt.cancel();
    }
    
 public String Envio(String Xml, URL url, String CUF, String VersaoDados) throws XMLStreamException, RemoteException, BadLocationException{
    String Line = ""; 
    NfeRecepcao2Stub.NfeRecepcaoLote2Result result = null;
        
    OMElement ome = AXIOMUtil.stringToOM(Xml);  

    NfeRecepcao2Stub.NfeDadosMsg dadosMsg = new NfeRecepcao2Stub.NfeDadosMsg();
        
    dadosMsg.setExtraElement(ome);  
    NfeRecepcao2Stub.NfeCabecMsg nfeCabecMsg = new NfeRecepcao2Stub.NfeCabecMsg();
        
    nfeCabecMsg.setCUF(CUF);  
    nfeCabecMsg.setVersaoDados(VersaoDados);  
        
    NfeRecepcao2Stub.NfeCabecMsgE nfeCabecMsgE = new NfeRecepcao2Stub.NfeCabecMsgE();

    nfeCabecMsgE.setNfeCabecMsg(nfeCabecMsg);  
        
    NfeRecepcao2Stub stub = new NfeRecepcao2Stub(url.toString());
    try {            
        result = stub.nfeRecepcaoLote2(dadosMsg, nfeCabecMsgE);  
        Line = result.getExtraElement().toString();
        try {
            main.CarregaJtxa(Line,Color.RED);
        } catch (Exception e) {
        }
    } catch (Exception e) {
        try {
            main.CarregaJtxa("Erro NfeRecepcao : " + e.toString(),Color.RED);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }  
    return Line;
}   

 private void testeXML(int cdpedido) throws Exception{
     
      HashMap pedido_hm = new HashMap();
      VND_nfpedidoDAO pedido_dao = new VND_nfpedidoDAO();
      pedido_hm = pedido_dao.pesquisa_nfpedido_cdpedido(cdpedido);
        
      try {
         MontaXML(pedido_hm);
      } catch (Exception e) {
         main.CarregaJtxa(e.toString(),Color.RED);
      }
      
         TEnviNFe envnfe = new TEnviNFe();
         envnfe.setIdLote("1");

         TNFe nfe = new TNFe();
         nfe.setInfNFe(infnfe);

         envnfe.getNFe().add(nfe);             
         envnfe.setVersao("2.00");

         JAXBContext context = null;

        try {
            StringWriter out = new StringWriter();

            context = JAXBContext.newInstance("br.inf.portalfiscal.nfe.env");
            Marshaller marshaller = context.createMarshaller();
            JAXBElement<TEnviNFe> element = new ObjectFactory().createEnviNFe(envnfe);
            marshaller.setProperty(
                javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
                Boolean.TRUE
            );

            marshaller.marshal(element,new StreamResult(out));

            xml = out.toString();

            xml = pedido_dao.remove_acento(xml);

            xml = xml.replace("xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");

            System.out.println(xml);

            AssinarXMLsCertfificadoA1 AssinarXml = new AssinarXMLsCertfificadoA1();    
            xml = AssinarXml.assinaXml(xml, "assinaEnvNFe") ;

        } catch (JAXBException e) {
            System.out.println(e);
        }

        String validation = NFeValidacaoXML.validaEnviNfe(xml);  
        if(!validation.isEmpty()) {  
            System.out.println(validation.replace("cvc-pattern-valid: ", ""));
            System.out.println(xml);
        } else {
            System.out.println(xml);
        }
 }
    
 public void Recepcao(HashMap pedido_hm, boolean dpec) throws Exception {

             KStore k = new KStore();
             k.KeyStore(); 
             
             int cdpedido = Integer.parseInt(pedido_hm.get("cdpedido").toString());
             try {
                main.CarregaJtxa("Recepcao Pedido: " + cdpedido,Color.DARK_GRAY);
             } catch (Exception e) {
                 System.out.println("Recepcao Pedido: " + cdpedido);
             }
             String iduf = pedido_hm.get("iduf").toString();

            /**
             * Endereco WebService
            */

             ADM_ParamDAO dao = new ADM_ParamDAO();
             VND_nfpedidoDAO pedido_dao = new VND_nfpedidoDAO();
             
             String[] endereco = null;
             endereco = dao.pesquisa_webservice("WebserverNfeRecepcao",iduf);
             
             URL url = new URL(endereco[0]);
             try {
                 MontaXML(pedido_hm);
             } catch (Exception e) {
                 main.CarregaJtxa(e.toString(),Color.RED);
             }
             
             
             TEnviNFe envnfe = new TEnviNFe();
             envnfe.setIdLote("1");
             
             TNFe nfe = new TNFe();
             nfe.setInfNFe(infnfe);
             
             envnfe.getNFe().add(nfe);             
             envnfe.setVersao("2.00");
             
             JAXBContext context = null;

            try {
                StringWriter out = new StringWriter();

                context = JAXBContext.newInstance("br.inf.portalfiscal.nfe.env");
                Marshaller marshaller = context.createMarshaller();
                JAXBElement<TEnviNFe> element = new ObjectFactory().createEnviNFe(envnfe);
                marshaller.setProperty(
                    javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE
                );
                
                marshaller.marshal(element,new StreamResult(out));

                xml = out.toString();
                
                xml = pedido_dao.remove_acento(xml);
                
                xml = xml.replace("xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
                       
                AssinarXMLsCertfificadoA1 AssinarXml = new AssinarXMLsCertfificadoA1();    
                xml = AssinarXml.assinaXml(xml, "assinaEnvNFe") ;
               
            } catch (JAXBException e) {
                main.CarregaJtxa(e.toString(),Color.RED);
            }
        
            main.CarregaJtxa(endereco[0],Color.BLUE);
           
            VND_NfpedidoBean pedido_bean = new VND_NfpedidoBean();
            
            pedido_bean.setCdpedido(cdpedido);
            
            String validation = NFeValidacaoXML.validaEnviNfe(xml);  
            if(!validation.isEmpty()) {  
                main.CarregaJtxa(validation.replace("cvc-pattern-valid: ", ""),Color.RED);
                nfpedido.insert_nfpedido_rejeicao(cdpedido, validation.replace("cvc-pattern-valid: ", ""), null);
                
                pedido_bean.setStatus_nfe(0);
                pedido_bean.setSituacaonf("N");
                pedido_bean.setXml_nfe(xml);
                pedido_dao.Update_nfpedido_env(pedido_bean);
            } else {
                 /* Declaração variaveis de retorno */
                //xml = RemoveCaracteresEdicao(xml);
//                System.out.println(xml);
                
                String retorno = Envio(xml, url, iduf, endereco[2]);
                
//                System.out.println(retorno);    

                TRetEnviNFe retEnviNFe = null;
                if (retorno.length() > 0){

                    context = JAXBContext.newInstance(TRetEnviNFe.class);  
                    Unmarshaller unmarshaller = context.createUnmarshaller();  
                    retEnviNFe = unmarshaller.unmarshal(new StreamSource(new StringReader(retorno)), TRetEnviNFe.class).getValue();  
                    int cstat = Integer.parseInt(retEnviNFe.getCStat());
                    pedido_bean.setStatus_nfe(cstat);
                    pedido_bean.setObs_nfe(retEnviNFe.getInfRec().getNRec());
                    pedido_bean.setXml_nfe(xml);
                    pedido_bean.setSituacaonf("E");
                    main.CarregaJtxa(">>> Update RecepcaoNfe....: Pedido: " + cdpedido,Color.BLACK);
                    pedido_dao.Update_nfpedido_env(pedido_bean);
//                    Thread.sleep(1000 * 15);
//                    if (cstat == 103 || cstat == 104 || cstat == 105){
//                        NfeRetRecepcao retrecp = new NfeRetRecepcao();
//                        retrecp.RetRecepcao(retEnviNFe.getInfRec().getNRec(), iduf, cdpedido, xml);
//                    }    
                    
                } else {
                    if (!dpec){
                        pedido_bean.setXml_nfe(xml);
                        pedido_bean.setTpemis(4);
                        main.CarregaJtxa(">>>DPEC....: Pedido: "+ cdpedido,Color.GREEN);
                        pedido_dao.Update_nfpedido_env(pedido_bean);
                    }
                }


            }
         }
 private void SetaDpec(int cdpedido){
     
     
 }
 private void MontaXML(HashMap pedido_hm) throws Exception{
    
    infnfe = new TNFe.InfNFe();
    
    int cdpedido = Integer.parseInt(pedido_hm.get("cdpedido").toString());
     
    //A - Dados da Nota Fiscal eletrônica

    infnfe.setVersao("2.00");      
    infnfe.setId("NFe"+ pedido_hm.get("idnfe")); // chave na nfe
    
    //B - Identificação da Nota Fiscal eletrônica
    nfpedido.pesquisa_nfpedido_env_ide(cdpedido);

    //C - Identificação do Emitente da Nota Fiscal eletrônica 
    nfpedido.pesquisa_nfpedido_env_emit(cdpedido);

    //D - Identificação do Fisco Emitente da NF-e
    
    //E - Identificação do Destinatário da Nota Fiscal eletrônica
    nfpedido.pesquisa_nfpedido_env_dest(cdpedido);    

    //F - Identificação do Local de Retirada
    //G - Identificação do Local de Entrega                 
    
    //H - Detalhamento de Produtos e Serviços da NF-e
    nfpedido.pesquisa_nfpedido_env_det(cdpedido);
    
    //W - Valores Totais da NF-e
    nfpedido.pesquisa_nfpedido_env_total(cdpedido);

    //X - Informações do Transporte da NF-e
    nfpedido.pesquisa_nfpedido_env_transp(cdpedido);

    //Y – Dados da Cobrança
    nfpedido.pesquisa_nfpedido_env_cob(cdpedido);
    
    //Z - Informações Adicionais da NF-e
    nfpedido.pesquisa_nfpedido_env_infadic(cdpedido);
    
     
 }
 private String RemoveCaracteresEdicao(String xml){
     String[] naopode = {"\n","\t","\r","> <",">  <"};
     for (int i = 0; i < naopode.length; i++) {
         while (xml.contains(naopode[i])) {
             if(naopode[i]  == "> <"){
                xml = xml.replace(naopode[i],"><");
             } else if (naopode[i]  == ">  <"){
                xml = xml.replace(naopode[i],"><");
             } else {
                 xml = xml.replace(naopode[i],"");
             }
         }
     }
  return xml;
 }
 
}

