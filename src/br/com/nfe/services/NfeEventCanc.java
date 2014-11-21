/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 package br.com.nfe.services;

import br.com.nfe.bean.adm.ADM_ParamBean;
import br.com.nfe.bean.vnd.VND_NfpedidoBean;
import br.com.nfe.dao.adm.ADM_ParamDAO;
import br.com.nfe.dao.vnd.VND_pedvendaDAO;
import br.com.nfe.dao.vnd.VND_nfpedidoDAO;
import br.com.nfe.email.EnviarEmail;
import br.com.nfe.util.AssinarXMLsCertfificadoA1;
import br.com.nfe.util.DtSystem;
import br.com.nfe.util.KStore;
import br.com.nfe.util.NFeValidacaoXML;
import br.inf.portalfiscal.nfe.envCanc.*;
import br.inf.portalfiscal.www.nfe.wsdl.recepcaoevento.RecepcaoEventoStub;
import java.awt.Color;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
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
import org.apache.axis2.AxisFault;

 
/**
 *
 * @author supervisor
 */
public class NfeEventCanc {
    
    String xml = "";
    TEnvEvento env = null;
    private Unmarshaller unmarshaller;
    public VND_nfpedidoDAO nfpedido = null;

    static Timer t = new Timer();
    static TimerTask tt;
    static int tempo = 1000 * 30;

    br.com.nfe.gui.Painel main = null;

    
    public NfeEventCanc(br.com.nfe.gui.Painel main) throws Exception{
        this.main = main;
        nfpedido = new VND_nfpedidoDAO();
    }

    public static void main(String[] args) throws Exception {
        NfeEventCanc c = new NfeEventCanc(null);
        HashMap pedido_hm = new HashMap();
        VND_nfpedidoDAO pedido_dao = new VND_nfpedidoDAO();
        pedido_hm = pedido_dao.pesquisa_nfpedido_canc_cdpedido(6165370);
        c.Cancelamento(pedido_hm);
    }

 public void StartTimer() throws Exception{
        main.CarregaJtxa(">>> TimerNfeEventCanc...: " + tempo,Color.BLACK);
        tt = new TimerTask(){
          public void run() {
                try {
                    HashMap pedido_hm = new HashMap();
                    VND_nfpedidoDAO pedido_dao = new VND_nfpedidoDAO();
                    pedido_hm = pedido_dao.pesquisa_nfpedido_canc();
                    if (Integer.parseInt(pedido_hm.get("cdpedido").toString()) > 0 ){
                        Cancelamento(pedido_hm);
                    }
                } catch (Exception ex) {
                    try {
                        main.CarregaJtxa(">>>TimerNfeEventCanc não foi executado...: Erro >>> " + ex, Color.BLACK);
                    } catch (BadLocationException ex1) {
                        Logger.getLogger(NfeStatusServico.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
          }
       };
       t.schedule(tt, 0, tempo);
    }
    public void StopTimer() throws Exception{
        main.CarregaJtxa(">>>Stop TimerNfeEventCanc...",Color.BLACK);
        tt.cancel();
    }
   
    
 public String Envio(String Xml, URL url, String CUF, String VersaoDados) throws XMLStreamException, AxisFault, BadLocationException {
    String Line = "";
    VersaoDados = "1.00";

    RecepcaoEventoStub.NfeRecepcaoEventoResult result = null; 
        
    OMElement ome = AXIOMUtil.stringToOM(Xml);  
            
    RecepcaoEventoStub.NfeDadosMsg dadosMsg = new RecepcaoEventoStub.NfeDadosMsg();  
    dadosMsg.setExtraElement(ome);  
    RecepcaoEventoStub.NfeCabecMsg nfeCabecMsg = new RecepcaoEventoStub.NfeCabecMsg();  
    nfeCabecMsg.setCUF(CUF);  
    nfeCabecMsg.setVersaoDados(VersaoDados);  
    RecepcaoEventoStub.NfeCabecMsgE nfeCabecMsgE = new RecepcaoEventoStub.NfeCabecMsgE();  
    nfeCabecMsgE.setNfeCabecMsg(nfeCabecMsg);  
    RecepcaoEventoStub stub = new RecepcaoEventoStub(url.toString());  
        
    try {            
        result = stub.nfeRecepcaoEvento(dadosMsg, nfeCabecMsgE);   
        Line = result.getExtraElement().toString();
         main.CarregaJtxa(Line,Color.RED);
     } catch (Exception e) {
         try {
          main.CarregaJtxa("Erro Envio NfeEventCanc : " + e.toString(),Color.RED);
        } catch (Exception e1) {
          e1.printStackTrace();
        }
     }
        
    return Line;
}   
    
    
 public void Cancelamento(HashMap pedido_hm) throws Exception {
             
             String iduf = pedido_hm.get("iduf").toString();
             int cdpedido = Integer.parseInt(pedido_hm.get("cdpedido").toString());
             String chavenfe = pedido_hm.get("idnfe").toString();
     
             KStore k = new KStore();
             k.KeyStore(); 
             
             ADM_ParamDAO dao = new ADM_ParamDAO();
             ADM_ParamBean bean = new ADM_ParamBean();
             VND_nfpedidoDAO pedido_dao = new VND_nfpedidoDAO();
             VND_NfpedidoBean pedido_bean = new VND_NfpedidoBean();

             bean = dao.getDados_webservice("WebserverNfeEvento",iduf);
               
             URL url = new URL(bean.getValorparam());
             try {
                 MontaXML(pedido_hm);
             } catch (Exception e) {
                  main.CarregaJtxa(e.toString(),Color.RED);
             }
             
             JAXBContext context = null;

            try {
                StringWriter out = new StringWriter();

                context = JAXBContext.newInstance("br.inf.portalfiscal.nfe.envCanc");
                Marshaller marshaller = context.createMarshaller();
                JAXBElement<TEnvEvento> element = new ObjectFactory().createEnvEvento(env);
                marshaller.setProperty(
                    javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE
                );
                
                marshaller.marshal(element,new StreamResult(out));

                xml = out.toString();
                
                xml = xml.replace("xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
                xml = xml.replace("ns3:", "");
                xml = xml.replace(":ns3", "");
                       
                AssinarXMLsCertfificadoA1 AssinarXml = new AssinarXMLsCertfificadoA1();    
                xml = AssinarXml.assinaXml(xml, "assinaInfEvento") ;
                
                
                xml = xml.replace("</evento>", "");
                xml = xml.replace("</envEvento>", "</evento></envEvento>");

                
             //System.out.println(xml);
                
            } catch (JAXBException e) {
                e.printStackTrace();
            }
            
            String validation = NFeValidacaoXML.validaEnvCanc(xml);  
            //if(!validation.isEmpty()) {  
                //System.err.println(validation.replace("cvc-pattern-valid: ", ""));
//                System.err.println(xml);
                nfpedido.insert_nfpedido_rejeicao(cdpedido, validation.replace("cvc-pattern-valid: ", ""), null);
            //} else {
            
                /* Declaração variaveis de retorno */

               
                String retorno = "";
                retorno = Envio(xml, url, iduf, bean.getValor().toString());
                
                if (retorno.length() > 0) {
                    context = JAXBContext.newInstance(TRetEnvEvento.class);  
                    unmarshaller = context.createUnmarshaller();
                    TRetEnvEvento retcanc  = unmarshaller.unmarshal(new StreamSource(new StringReader(retorno)), TRetEnvEvento.class).getValue();

                    pedido_dao = new VND_nfpedidoDAO();
                    pedido_bean = new VND_NfpedidoBean();

                     main.CarregaJtxa(retcanc.getRetEvento().get(0).getInfEvento().getCStat().toString(),Color.BLUE);
                    if (Integer.parseInt(retcanc.getRetEvento().get(0).getInfEvento().getCStat()) == 135){
                        
                        
                        pedido_bean.setStatus_nfe(Integer.parseInt(retcanc.getRetEvento().get(0).getInfEvento().getCStat()));
                        pedido_bean.setXml_canc(retorno);
                        pedido_bean.setCdpedido(cdpedido);
                        pedido_bean.setFlag("1");
                        pedido_dao.Update_nfpedido_canc(pedido_bean);  
//
//                        VND_pedvendaDAO atu_pedvenda = new VND_pedvendaDAO(); 
//                        atu_pedvenda.atualiza_pedvenda(cdpedido, 7);
//
//                        EnviarEmail e = new EnviarEmail();
//                        e.Enviar(retorno, null, chavenfe,"canc");
//
                    } else {
                        if (Integer.parseInt(retcanc.getRetEvento().get(0).getInfEvento().getCStat()) == 580){
                            
                            pedido_bean.setStatus_nfe(Integer.parseInt(retcanc.getRetEvento().get(0).getInfEvento().getCStat()));
                            pedido_bean.setSituacaonf("N");
                            pedido_bean.setCdpedido(cdpedido);
                            pedido_bean.setObs_canc_nfe(null);
                            pedido_bean.setFlag("1");
                            pedido_dao.Update_nfpedido_canc(pedido_bean);  
                            
                        } else {
                            pedido_bean.setSituacaonf("N");
                            pedido_bean.setCdpedido(cdpedido);
                            pedido_bean.setObs_canc_nfe(null);
                            pedido_dao.Update_nfpedido_canc(pedido_bean);  
                            pedido_dao.insert_nfpedido_rejeicao(cdpedido, null, Integer.parseInt(retcanc.getRetEvento().get(0).getInfEvento().getCStat()));
                        }
                    }
                }
            //}
         }
 
      private void MontaXML(HashMap pedido_hm) throws Exception{
          
        env = new TEnvEvento();
          
        env.setVersao("1.00");
    
        env.setIdLote("1");
          
        TEvento evento = new TEvento();
          
        evento.setVersao("1.00");
          
        TEvento.InfEvento infevento = new TEvento.InfEvento();
        
        infevento.setId("ID110111"+pedido_hm.get("idnfe").toString()+"01");
        infevento.setCOrgao(pedido_hm.get("iduf").toString());
        infevento.setTpAmb("1");
        infevento.setCNPJ(pedido_hm.get("cnpj").toString());
        infevento.setChNFe(pedido_hm.get("idnfe").toString());
       
        
        infevento.setDhEvento(DtSystem.getdhEvento());
        infevento.setTpEvento("110111");
        infevento.setNSeqEvento("1");
        infevento.setVerEvento("1.00");
        
        TEvento.InfEvento.DetEvento dtevento = new TEvento.InfEvento.DetEvento();

        dtevento.setVersao("1.00");
        
        dtevento.setDescEvento("Cancelamento");
          
        dtevento.setNProt(pedido_hm.get("protocolo").toString());
          
        dtevento.setXJust("CANCELAR NOTA FISCAL");
         

        infevento.setDetEvento(dtevento);
        evento.setInfEvento(infevento);
        env.getEvento().add(evento);
          
          
 }
 
}

