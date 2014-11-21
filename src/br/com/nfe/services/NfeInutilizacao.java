/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 package br.com.nfe.services;

import br.com.nfe.bean.vnd.VND_NfpedidoBean;
import br.com.nfe.dao.adm.ADM_ParamDAO;
import br.com.nfe.dao.vnd.VND_pedvendaDAO;
import br.com.nfe.dao.vnd.VND_nfpedidoDAO;
import br.com.nfe.util.AssinarXMLsCertfificadoA1;
import br.com.nfe.util.KStore;
import br.inf.portalfiscal.nfe.inu.ObjectFactory;
import br.inf.portalfiscal.nfe.inu.TInutNFe;
import br.inf.portalfiscal.nfe.inu.TRetInutNFe;
import br.inf.portalfiscal.www.nfe.wsdl.nfeinutilizacao2.NfeInutilizacao2Stub;
import java.awt.Color;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
 
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.axis2.AxisFault;

 
/**
 *
 * @author supervisor
 */
public class NfeInutilizacao {
    
    private String ret[] = new String[3];
    String xml = "";
    TInutNFe inut = null;

    br.com.nfe.gui.Painel main = null;
    static Timer t = new Timer();
    static TimerTask tt;
    static int tempo = 1000 * 30;

    VND_nfpedidoDAO pedido_dao = null;
    
    public NfeInutilizacao(br.com.nfe.gui.Painel main) {
        this.main = main;
    }


    public void StartTimer() throws Exception{
        pedido_dao = new VND_nfpedidoDAO();
        main.CarregaJtxa(">>> TimerNfeInutilizacao...: " + tempo,Color.BLACK);
        tt = new TimerTask(){
          public void run() {
                try {
                    HashMap pedido_hm = new HashMap();
                    VND_nfpedidoDAO pedido_dao = new VND_nfpedidoDAO();
                    pedido_hm = pedido_dao.pesquisa_nfpedido_inut();
                    if (Integer.parseInt(pedido_hm.get("cdpedido").toString()) > 0 ){
                        Inutilizacao(pedido_hm);
                    }
                } catch (Exception ex) {
                    try {
                        main.CarregaJtxa(">>>TimerNfeInutilizacao não foi executado...", Color.RED);
                    } catch (BadLocationException ex1) {
                        Logger.getLogger(NfeInutilizacao.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
          }
       };
       t.schedule(tt, 0, tempo);
    }
    public void StopTimer() throws Exception{
        main.CarregaJtxa(">>>Stop TimerNfeInutilizacao...",Color.BLACK);
        tt.cancel();
    }
    
 public String Envio(String Xml, URL url, String CUF, String VersaoDados) throws XMLStreamException, AxisFault, RemoteException, BadLocationException{
    String Line = "";
     
     
    NfeInutilizacao2Stub.NfeInutilizacaoNF2Result result = null;
        
    OMElement ome = AXIOMUtil.stringToOM(Xml);  

    NfeInutilizacao2Stub.NfeDadosMsg dadosMsg = new NfeInutilizacao2Stub.NfeDadosMsg();
        
    dadosMsg.setExtraElement(ome);  
    NfeInutilizacao2Stub.NfeCabecMsg nfeCabecMsg = new NfeInutilizacao2Stub.NfeCabecMsg();
        
    nfeCabecMsg.setCUF(CUF);  
    nfeCabecMsg.setVersaoDados(VersaoDados);  
        
    NfeInutilizacao2Stub.NfeCabecMsgE nfeCabecMsgE = new NfeInutilizacao2Stub.NfeCabecMsgE();

    nfeCabecMsgE.setNfeCabecMsg(nfeCabecMsg);  
        
    NfeInutilizacao2Stub stub = new NfeInutilizacao2Stub(url.toString());
                
    try {            
        result = stub.nfeInutilizacaoNF2(dadosMsg, nfeCabecMsgE);  
        Line = result.getExtraElement().toString(); 
        main.CarregaJtxa(Line,Color.BLACK);
    } catch (Exception e) {
         main.CarregaJtxa("Erro NfeInutilizacao : " + e,Color.RED);
    }  
    
    return Line;    
}   
    
 public void MontaXML(HashMap pedido_hm) throws Exception {
     
     String CNPJ = pedido_hm.get("cnpj").toString();
     CNPJ = CNPJ.replace(".", "");
     CNPJ = CNPJ.replace("/", "");
     CNPJ = CNPJ.replace("-", "");             
    
     String iduf = pedido_hm.get("iduf").toString();
     String ano = pedido_hm.get("ano").toString();
     String nrnota = pedido_hm.get("cdnf").toString();
             
     String nrnotaf = nrnota;
             
     for (int i = 0; i < 9 - nrnota.length() ; i++) {
         nrnotaf = "0" + nrnotaf;
     }
     String id = "ID" + iduf + ano + CNPJ + "55000" + nrnotaf + nrnotaf;
     
     TInutNFe.InfInut inf = new TInutNFe.InfInut();
             
             
     inf.setId(id);
     inf.setTpAmb("1");        
     inf.setXServ("INUTILIZAR");
     inf.setCUF(iduf);
     inf.setAno(ano);
     inf.setCNPJ(CNPJ);
     inf.setMod("55");
     inf.setSerie("0");
     inf.setNNFIni(nrnota);
     inf.setNNFFin(nrnota);
     inf.setXJust("NUMERACAO NAO UTILIZADA");
             
     inut = new TInutNFe();

     inut.setVersao("2.00");
     inut.setInfInut(inf);

     JAXBContext context = null;

        try {
            StringWriter out = new StringWriter();

            context = JAXBContext.newInstance("br.inf.portalfiscal.nfe.inu");
            Marshaller marshaller = context.createMarshaller();
            JAXBElement<TInutNFe> element = new ObjectFactory().createInutNFe(inut);
            marshaller.setProperty(
                javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
                Boolean.TRUE
            );

            marshaller.marshal(element,new StreamResult(out));

            xml = out.toString();

            xml = xml.replace("xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");

            AssinarXMLsCertfificadoA1 AssinarXml = new AssinarXMLsCertfificadoA1();    
            xml = AssinarXml.assinaXml(xml, "assinaInutNFe") ;

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        
//        System.out.println(xml);
     
     
 }   
 public void Inutilizacao(HashMap pedido_hm) throws Exception {
         
             KStore k = new KStore();
             k.KeyStore(); 
             
//             String CNPJ = pedido_hm.get("cnpj").toString();
//             CNPJ = CNPJ.replace(".", "");
//             CNPJ = CNPJ.replace("/", "");
//             CNPJ = CNPJ.replace("-", "");             
//    
             String iduf = pedido_hm.get("iduf").toString();
//             String ano = pedido_hm.get("ano").toString();
//             String nrnota = pedido_hm.get("cdnf").toString();
             
//             String nrnotaf = nrnota;
//             
//             for (int i = 0; i < 9 - nrnota.length() ; i++) {
//                 nrnotaf = "0" + nrnotaf;
//             }
//             String id = "ID" + iduf + ano + CNPJ + "55000" + nrnotaf + nrnotaf;

            /**
             * Endereco WebService
            */

             ADM_ParamDAO dao = new ADM_ParamDAO();
             
             String[] endereco = null;
//             if (StatusServico.cStat == 107){
                 endereco = dao.pesquisa_webservice("WebserverNfeInutilizacao",iduf);
//             } else {
//                 endereco = dao.pesquisa_webservice("WebserverNfeInutilizacaoCont","91");
//             }
             
             URL url = new URL(endereco[0]);             
             
//             TInutNFe.InfInut inf = new TInutNFe.InfInut();
//             
//             inf.setId(id);
//             inf.setTpAmb("1");        
//             inf.setXServ("INUTILIZAR");
//             inf.setCUF(iduf);
//             inf.setAno(ano);
//             inf.setCNPJ(CNPJ);
//             inf.setMod("55");
//             inf.setSerie("0");
//             inf.setNNFIni(nrnota);
//             inf.setNNFFin(nrnota);
//             inf.setXJust("NUMERACAO NAO UTILIZADA");
//             
//             TInutNFe inut = new TInutNFe();
//
//             inut.setVersao("2.00");
//             inut.setInfInut(inf);
             
             try {
                 MontaXML(pedido_hm);
             } catch (Exception e) {
                 main.CarregaJtxa(e.toString(),Color.RED);
             }
             
//             JAXBContext context = null;
//
//            try {
//                StringWriter out = new StringWriter();
//
//                context = JAXBContext.newInstance("br.inf.portalfiscal.nfe.inu");
//                Marshaller marshaller = context.createMarshaller();
//                JAXBElement<TInutNFe> element = new ObjectFactory().createInutNFe(inut);
//                marshaller.setProperty(
//                    javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
//                    Boolean.TRUE
//                );
//                
//                marshaller.marshal(element,new StreamResult(out));
//
//                xml = out.toString();
//                
//                xml = xml.replace("xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
//                       
//                AssinarXMLsCertfificadoA1 AssinarXml = new AssinarXMLsCertfificadoA1();    
//                xml = AssinarXml.assinaXml(xml, "assinaInutNFe") ;
//               
//            } catch (JAXBException e) {
//                e.printStackTrace();
//            }
        
            // System.out.println(xml);
             /* Declaração variaveis de retorno */

             String retorno = Envio(xml, url, iduf, endereco[2]);

             TRetInutNFe rinut = null;
             JAXBContext context = null;

             if (retorno.length() > 0){
                 context = JAXBContext.newInstance(TRetInutNFe.class);
                 Unmarshaller unmarshaller = context.createUnmarshaller();
                 rinut = unmarshaller.unmarshal(new StreamSource(new StringReader(retorno)), TRetInutNFe.class).getValue();
                 int cstat = Integer.parseInt(rinut.getInfInut().getCStat());

                 VND_nfpedidoDAO pedido_dao = new VND_nfpedidoDAO();
                 VND_NfpedidoBean pedido_bean = new VND_NfpedidoBean();

                 pedido_bean.setXml_nfe(xml);
                 pedido_bean.setCdpedido(Integer.parseInt(pedido_hm.get("cdpedido").toString()));
                 pedido_bean.setCdnf(pedido_hm.get("cdnf").toString());
                 pedido_dao.Update_nfpedido_env(pedido_bean);



                 VND_pedvendaDAO atu_pedvenda = new VND_pedvendaDAO();
                 if (cstat == 102 || cstat == 563 || cstat == 206){

                    pedido_bean = new VND_NfpedidoBean();
                    pedido_bean.setStatus_nfe(cstat);
                    pedido_bean.setObs_canc_nfe(retorno);
                    pedido_bean.setProtocolo(rinut.getInfInut().getNProt());
                    pedido_bean.setCdpedido(Integer.parseInt(pedido_hm.get("cdpedido").toString()));
                    pedido_bean.setCdnf(pedido_hm.get("cdnf").toString());

                    pedido_dao.Update_nfpedido_inut(pedido_bean);

                    atu_pedvenda.atualiza_pedvenda(Integer.parseInt(pedido_hm.get("cdpedido").toString()), 11);

                 } else {
                     pedido_bean = new VND_NfpedidoBean();
                     if (cstat == 241) {
                         pedido_bean.setFlag("1");
                     }
                     pedido_bean.setCdpedido(Integer.parseInt(pedido_hm.get("cdpedido").toString()));
                     pedido_bean.setCdnf(pedido_hm.get("cdnf").toString());
                     pedido_bean.setStatus_nfe(cstat);
                     pedido_dao.Update_nfpedido_inut(pedido_bean);
                     pedido_dao.insert_nfpedido_rejeicao(Integer.parseInt(pedido_hm.get("cdpedido").toString()), null, cstat);
                 }
             
             }
         }
 
}

