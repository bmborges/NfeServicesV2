/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.nfe.services;

import br.com.nfe.bean.adm.ADM_ParamBean;
import br.com.nfe.bean.cdc.CDC_EstabelecimBean;
import br.com.nfe.bean.lfs.LFS_NfentradBean;
import br.com.nfe.bean.lfs.LFS_Nfentrad_ManifestoBean;
import br.com.nfe.dao.adm.ADM_ParamDAO;
import br.com.nfe.dao.cdc.CDC_EstabelecimDAO;
import br.com.nfe.dao.lfs.LFS_NfentradDAO;
import br.com.nfe.dao.lfs.LFS_Nfentrad_ManifestoDAO;
import br.com.nfe.util.AssinarXMLsCertfificadoA1;
import br.com.nfe.util.DtSystem;
import br.com.nfe.util.FormataValores;
import br.com.nfe.util.KStore;
import br.com.nfe.util.NFeValidacaoXML;
import br.inf.portalfiscal.nfe.envManifesto.ObjectFactory;
import br.inf.portalfiscal.nfe.envManifesto.TEnvEvento;
import br.inf.portalfiscal.nfe.envManifesto.TEvento;
import br.inf.portalfiscal.nfe.envManifesto.TRetEnvEvento;
import br.inf.portalfiscal.www.nfe.wsdl.recepcaoevento.RecepcaoEventoStub;
import java.awt.Color;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
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
import org.w3c.dom.Node;



/**
 *
 * @author supervisor
 */
public class NfeManifesto {
    
    String xml = "";
    TEnvEvento env = null; 
    private Unmarshaller unmarshaller;
    br.com.nfe.gui.Painel main = null;
    static Timer t = new Timer();
    static TimerTask tt;
    static int tempo = 1000 * 30;
    LFS_Nfentrad_ManifestoDAO nfmanifesto = null;


    public NfeManifesto(br.com.nfe.gui.Painel main) {
        this.main = main;
    }
    public static void main(String[] args) throws Exception {
        LFS_Nfentrad_ManifestoDAO nfmanifesto = new LFS_Nfentrad_ManifestoDAO();
        NfeManifesto m = new NfeManifesto(null);
        LFS_NfentradBean bean = new LFS_NfentradBean();
        bean.setCdpedidonfe(nfmanifesto.Pesquisa_Nfentrad_Manifesto());
        if (bean.getCdpedidonfe() > 0){
            m.EventoManifesto(bean);
        }
    }

    /**
     * @param args the command line arguments
     */
    public void StartTimer() throws Exception{
        nfmanifesto = new LFS_Nfentrad_ManifestoDAO();
        try {
            main.CarregaJtxa(">>> TimerNfeManifesto...: " + tempo,Color.BLACK);
        } catch (Exception e) {
        }
        tt = new TimerTask(){
          public void run() {
                try {
                    LFS_NfentradBean bean = new LFS_NfentradBean();
                    bean.setCdpedidonfe(nfmanifesto.Pesquisa_Nfentrad_Manifesto());
                    if (bean.getCdpedidonfe() > 0){
                        EventoManifesto(bean);
                    }
                } catch (Exception ex) {
                    try {
                        main.CarregaJtxa(">>>TimerNfeManifesto não foi executado...", Color.RED);
                    } catch (BadLocationException ex1) {
                        Logger.getLogger(NfeManifesto.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
          }
       };
       t.schedule(tt, 0, tempo);
    }
    public void StopTimer() throws Exception{
        main.CarregaJtxa(">>>Stop TimerNfeManifesto...",Color.BLACK);
        tt.cancel();
    }
    
    public String Envio(String Xml, URL url, String CUF, String VersaoDados) throws XMLStreamException, AxisFault, RemoteException, BadLocationException{
        String Line = "";
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
        } catch (Exception e) {
            try {
                main.CarregaJtxa("Erro Envio NfeManifesto : " + e,Color.RED);
            } catch (Exception ex) {
                System.out.println(e);
            }
        } 
        
        return Line;
    }
    public void EventoManifesto(LFS_NfentradBean beanE) throws URISyntaxException, Exception{
    // TODO code application logic here
       
        
            KStore k = new KStore();
            k.KeyStore(); 
             
            /**
             * Endereco WebService
            */

             ADM_ParamDAO dao = new ADM_ParamDAO();
             ADM_ParamBean bean = new ADM_ParamBean();

             bean = dao.getDados_webservice("WebserverNfeManifesto","52");
             
             URL url = new URL(bean.getValorparam());
             try {
                 MontaXML(beanE);
             } catch (Exception e) {
                main.CarregaJtxa(e.toString(),Color.RED);
             }
             
            JAXBContext context = null;

            try {
                StringWriter out = new StringWriter();

                context = JAXBContext.newInstance("br.inf.portalfiscal.nfe.envManifesto");
                Marshaller marshaller = context.createMarshaller();  
                JAXBElement<TEnvEvento> element = new ObjectFactory().createEnvEvento(env);  
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);  
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                
                marshaller.marshal(element,new StreamResult(out));

                xml = out.toString();
                
                xml = xml.replace("xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
                
                       
                AssinarXMLsCertfificadoA1 AssinarXml = new AssinarXMLsCertfificadoA1();    
                xml = AssinarXml.assinaXml(xml, "Evento") ;
//                System.out.println(xml);
                
               
            } catch (JAXBException e) {
                e.printStackTrace();
            }
            
            String validation = NFeValidacaoXML.validaManifesto(xml);
            if(!validation.isEmpty()) {  
                main.CarregaJtxa(validation.replace(": cvc-pattern-valid: ", ""),Color.RED);
            } else {
                try {

                    String retorno = Envio(xml, url, bean.getCodigo().toString(), FormataValores.formataVr_Float(bean.getValor(),"0.00"));
                    try {
                        main.CarregaJtxa(retorno,Color.BLACK);
                    } catch (Exception e) {
                    }
                    
                    
                    if (retorno.length() > 0){
                        context = JAXBContext.newInstance(TRetEnvEvento.class);  
                        unmarshaller = context.createUnmarshaller();
                        TRetEnvEvento retevent  = unmarshaller.unmarshal(new StreamSource(new StringReader(retorno)), TRetEnvEvento.class).getValue();
                        
                        
                        LFS_Nfentrad_ManifestoBean manifestoResult = new LFS_Nfentrad_ManifestoBean();
                        LFS_Nfentrad_ManifestoDAO nfentrad_manifesto = new LFS_Nfentrad_ManifestoDAO();
                        if (retevent.getCStat().equals("128"))
                            manifestoResult.setCdpedidonfe(beanE.getCdpedidonfe());    
                            manifestoResult.setCstat(Integer.parseInt(retevent.getRetEvento().get(0).getInfEvento().getCStat()));
                            manifestoResult.setTpevento(Integer.parseInt(retevent.getRetEvento().get(0).getInfEvento().getTpEvento()));
                            manifestoResult.setRetorno(retorno);

                            nfentrad_manifesto.Update_Nfentrad_Manifesto(manifestoResult);
                        
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }
    private void MontaXML(LFS_NfentradBean bean) throws Exception{
        
        LFS_NfentradBean nfentradResult = new LFS_NfentradBean();
        LFS_NfentradDAO nfentrad = new LFS_NfentradDAO();
        nfentradResult = nfentrad.select(bean);
           
        /* localiza valores estabelecimen */
        CDC_EstabelecimBean estabelecimResult = new CDC_EstabelecimBean();
        estabelecimResult.setIdestabelecimen(nfentradResult.getIdestabelecimen());
            
        CDC_EstabelecimDAO estabelecimen = new CDC_EstabelecimDAO();
        estabelecimResult = estabelecimen.select(estabelecimResult);
        
        
        /* localiza valores manifesto */
        LFS_Nfentrad_ManifestoBean nfentrad_manifestoResult = new LFS_Nfentrad_ManifestoBean();
        nfentrad_manifestoResult.setCdpedidonfe(bean.getCdpedidonfe());
        LFS_Nfentrad_ManifestoDAO nfentrad_manifesto = new LFS_Nfentrad_ManifestoDAO();
            
        int SeqEvento = nfentrad_manifesto.CountManifesto(nfentrad_manifestoResult);
        
        
        if (SeqEvento == 1) {
        
            String CNPJ = estabelecimResult.getCnpj();
            CNPJ = CNPJ.replace(".", "");
            CNPJ = CNPJ.replace("/", "");
            CNPJ = CNPJ.replace("-", "");

            String dhEvento = nfentradResult.getDtentrada();
            dhEvento = dhEvento.substring(0, 19);
            dhEvento = dhEvento.replace(" ", "T");
            dhEvento = dhEvento+"-03:00";

 
            env = new TEnvEvento();

            env.setVersao("1.00");
            env.setIdLote("1");

            TEvento evento = new TEvento();

            evento.setVersao("1.00");

            TEvento.InfEvento infevento = new TEvento.InfEvento();


            infevento.setId("ID210210"+nfentradResult.getIdnfe()+"01");
            infevento.setCOrgao("91");
            infevento.setTpAmb("1");
            infevento.setCNPJ(CNPJ);
            infevento.setChNFe(nfentradResult.getIdnfe());
            infevento.setDhEvento(dhEvento);
            infevento.setTpEvento("210210");
            infevento.setNSeqEvento("1");
            infevento.setVerEvento("1.00");
        
            TEvento.InfEvento.DetEvento dtevento = new TEvento.InfEvento.DetEvento();

            dtevento.setVersao("1.00");

            dtevento.setDescEvento("Ciencia da Operacao");

            infevento.setDetEvento(dtevento);
            evento.setInfEvento(infevento);
            env.getEvento().add(evento);
        
        
        }
        
    }
    
}
