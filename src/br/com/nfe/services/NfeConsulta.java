/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 package br.com.nfe.services;

import br.com.nfe.dao.vnd.VND_pedvendaDAO;
import br.com.nfe.dao.lfs.LFS_atualiza_status_nfeDAO;
import br.com.nfe.bean.lfs.LFS_atualiza_status_nfeBean;
import br.com.nfe.dao.adm.ADM_ParamDAO;
import br.com.nfe.dao.vnd.VND_nfpedidoDAO;
import br.com.nfe.util.KStore;
import br.inf.portalfiscal.nfe.conssitnfe.ObjectFactory;
import br.inf.portalfiscal.nfe.conssitnfe.TConsSitNFe;
import br.inf.portalfiscal.nfe.conssitnfe.TRetConsSitNFe;
import br.inf.portalfiscal.www.nfe.wsdl.nfeconsulta2.NfeConsulta2Stub;
import java.awt.Color;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
 
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import java.net.Authenticator;
import java.rmi.RemoteException;
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
public class NfeConsulta {
    
    private String ret[] = new String[3];
    String xml = "";
    String cStat = "";
    private Unmarshaller unmarshaller;

    br.com.nfe.gui.Painel main = null;
    static Timer t = new Timer();
    static TimerTask tt;
    static int tempo = 1000 * 30;

    public NfeConsulta(br.com.nfe.gui.Painel main) {
        this.main = main;
    }
public void StartTimer() throws Exception{
        main.CarregaJtxa(">>> TimerNfeConsulta...: " + tempo,Color.BLACK);
        tt = new TimerTask(){
          public void run() {
                try {
                    VND_nfpedidoDAO idnfe = new VND_nfpedidoDAO();
                    String retorno[] = idnfe.pesquisa_nfpedido();
                     if (retorno[0].length() > 0){
                       consultanfe(retorno[0],retorno[1]);
                     }
                } catch (Exception ex) {
                    try {
                        main.CarregaJtxa(">>>TimerNfeConsulta não foi executado...", Color.RED);
                    } catch (BadLocationException ex1) {
                        Logger.getLogger(NfeConsulta.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
          }
       };
       t.schedule(tt, 0, tempo);
    }
    public void StopTimer() throws Exception{
        main.CarregaJtxa(">>>Stop TimerNfeConsulta...",Color.BLACK);
        tt.cancel();
    }
 public String Envio(String Xml, URL url, String CUF, String VersaoDados) throws XMLStreamException, AxisFault, RemoteException, BadLocationException{
    String Line = ""; 
    NfeConsulta2Stub.NfeConsultaNF2Result result = null;
        
    OMElement ome = AXIOMUtil.stringToOM(Xml);  

    NfeConsulta2Stub.NfeDadosMsg dadosMsg = new NfeConsulta2Stub.NfeDadosMsg();
        
        dadosMsg.setExtraElement(ome);  
        NfeConsulta2Stub.NfeCabecMsg nfeCabecMsg = new NfeConsulta2Stub.NfeCabecMsg();
        
        nfeCabecMsg.setCUF(CUF);  
        nfeCabecMsg.setVersaoDados(VersaoDados);  
        
        NfeConsulta2Stub.NfeCabecMsgE nfeCabecMsgE = new NfeConsulta2Stub.NfeCabecMsgE();

        nfeCabecMsgE.setNfeCabecMsg(nfeCabecMsg);  
        
        NfeConsulta2Stub stub = new NfeConsulta2Stub(url.toString());
                
        try {            
            result = stub.nfeConsultaNF2(dadosMsg, nfeCabecMsgE);  
            Line = result.getExtraElement().toString(); 
            main.CarregaJtxa(Line,Color.red);
        } catch (Exception e) {
             main.CarregaJtxa("Erro NfeConsulta : " + e,Color.RED);
        }          
        
        return Line;
}   
    
    
 public void consultanfe(String idnfe, String cdpedido) throws Exception {
         try {
             String codigoDoEstado = idnfe.substring(0, 2);;
             
             /**
              * Endereco WebService
              */

             ADM_ParamDAO dao = new ADM_ParamDAO();
             String[] endereco = dao.pesquisa_webservice("WebserverNfeConsulta",codigoDoEstado);
            
             
             URL url = new URL(endereco[0]);
             /**
              * Colocar a Chave de Acesso da NF-e Aqui.
             */

             String chaveDaNFe = idnfe;
              
             KStore k = new KStore();
             k.KeyStore(); 
             
             /**
              * Xml de Consulta.
              */

             
             TConsSitNFe cons = new TConsSitNFe();
             cons.setVersao("2.00");
             cons.setTpAmb("1");
             cons.setXServ("CONSULTAR");
             cons.setChNFe(chaveDaNFe);
             
             /* Declaração variaveis de retorno */

            JAXBContext context = null;

            try {
                StringWriter out = new StringWriter();

                context = JAXBContext.newInstance("br.inf.portalfiscal.nfe.conssitnfe");
                Marshaller marshaller = context.createMarshaller();
                JAXBElement<TConsSitNFe> element = new ObjectFactory().createConsSitNFe(cons);
                marshaller.setProperty(
                    javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE
                );
                
                marshaller.marshal(element,new StreamResult(out));

                xml = out.toString();
                
                xml = xml.replace("xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
                       
//                AssinarXMLsCertfificadoA1 AssinarXml = new AssinarXMLsCertfificadoA1();    
//                xml = AssinarXml.assinaXml(xml, "assinaInutNFe") ;
               
            } catch (JAXBException e) {
                main.CarregaJtxa(e.toString(),Color.RED);
            }             
             
             
             String retorno = "";
             String separador = ";";
             String obs_nfe = "";
             String nfe = "NFe00";
             String tpAmb = "";
             String verAplic = "";
             String chNFe = "";
             String dhRecbto = "";
             String nProt = "";
             String cStat = "";
             String xMotivo = "";
             String nmArq = "";

             retorno = Envio(xml.toString(), url, codigoDoEstado, endereco[2]);
             
             TRetConsSitNFe retsit = null;
             if (retorno.length() > 0) {
                    context = JAXBContext.newInstance(TRetConsSitNFe.class);  
                    unmarshaller = context.createUnmarshaller();
                    retsit = unmarshaller.unmarshal(new StreamSource(new StringReader(retorno)), TRetConsSitNFe.class).getValue();
                    
//                    main.CarregaJtxa(retsit.getCStat(),Color.red);



             cStat = retorno.substring(retorno.indexOf("<cStat>") + 7, retorno.indexOf("</cStat>"));
             
             if (Integer.parseInt(cStat) == 100){
                nmArq = "env_" + cdpedido;
             } else if (Integer.parseInt(cStat) == 101){
                nmArq = "can2_" + cdpedido;
             } else {
                nmArq = "inu_" + cdpedido;
             }

             if (Integer.parseInt(cStat) == 100 || Integer.parseInt(cStat) == 101){
                tpAmb = retorno.substring(retorno.indexOf("<tpAmb>") + 7, retorno.indexOf("</tpAmb>"));
                verAplic = retorno.substring(retorno.indexOf("<verAplic>") + 10, retorno.indexOf("</verAplic>"));
                chNFe = retorno.substring(retorno.indexOf("<chNFe>") + 7, retorno.indexOf("</chNFe>"));
                dhRecbto = retorno.substring(retorno.indexOf("<dhRecbto>") + 10, retorno.indexOf("</dhRecbto>"));
                nProt = retorno.substring(retorno.indexOf("<nProt>") + 7, retorno.indexOf("</nProt>"));
                xMotivo = retorno.substring(retorno.indexOf("<xMotivo>") + 9, retorno.indexOf("</xMotivo>"));

                obs_nfe = nfe + separador + tpAmb + separador + verAplic + separador + chNFe + separador + dhRecbto;
                obs_nfe +=  separador + nProt + separador + separador + cStat + separador + xMotivo + separador + "1" + separador + separador;
             } else {
                tpAmb = retorno.substring(retorno.indexOf("<tpAmb>") + 7, retorno.indexOf("</tpAmb>"));
                verAplic = retorno.substring(retorno.indexOf("<verAplic>") + 10, retorno.indexOf("</verAplic>"));
                chNFe = retorno.substring(retorno.indexOf("<chNFe>") + 7, retorno.indexOf("</chNFe>"));
                xMotivo = retorno.substring(retorno.indexOf("<xMotivo>") + 9, retorno.indexOf("</xMotivo>"));

                obs_nfe = nfe + separador + tpAmb + separador + verAplic + separador + chNFe + separador + dhRecbto;
                obs_nfe +=  separador + nProt + separador + separador + cStat + separador + xMotivo + separador + "1" + separador + separador;
             }

             //System.out.println(">>>Retorno Sefaz...: " + resultado);
             //System.out.println(">>>Obs_nfe Sefaz...: " + obs_nfe);

             if (obs_nfe.length() > 0) {
                  LFS_atualiza_status_nfeBean bean = new LFS_atualiza_status_nfeBean();
                  LFS_atualiza_status_nfeDAO bk = new LFS_atualiza_status_nfeDAO();

                  VND_pedvendaDAO atu_pedvenda = new VND_pedvendaDAO();
                  int numpedido = 0 ;
                  Boolean atupedido = false;
                  
                  if (nmArq.length() > 0){
                    numpedido = bk.set_atualiza_status_nfe(nmArq, obs_nfe);
                    if (numpedido > 0) {
                        //System.out.println("Gravou pedido: " + numpedido);
                        if (Integer.parseInt(retsit.getCStat()) == 101){
                            try {
                                atupedido = atu_pedvenda.atualiza_pedvenda(numpedido, 7);
                                main.CarregaJtxa("Atualizou cancelamento Pedido",Color.orange);
                            } catch (Exception e) {
                                   main.CarregaJtxa(e.toString(),Color.RED);
                            }
                        }
                    }
                 }
             }
          }
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
}

