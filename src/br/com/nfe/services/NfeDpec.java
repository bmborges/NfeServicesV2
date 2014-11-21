/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.nfe.services;

import br.com.nfe.bean.adm.ADM_ParamBean;
import br.com.nfe.bean.vnd.VND_NfpedidoBean;
import br.com.nfe.dao.adm.ADM_ParamDAO;
import br.com.nfe.dao.vnd.VND_nfpedidoDAO;
import br.com.nfe.util.AssinarXMLsCertfificadoA1;
import br.com.nfe.util.DtSystem;
import br.com.nfe.util.KStore;
import br.com.nfe.util.NFeValidacaoXML;
import br.inf.portalfiscal.nfe.dpec.env.*;
import br.inf.portalfiscal.nfe.dpec.env.TDPEC.InfDPEC;
import br.inf.portalfiscal.nfe.dpec.env.TDPEC.InfDPEC.IdeDec;
import br.inf.portalfiscal.nfe.dpec.env.TDPEC.InfDPEC.ResNFe;
import br.inf.portalfiscal.www.nfe.wsdl.scerecepcaorfb.SCERecepcaoRFBStub;
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
import org.apache.axis2.AxisFault;

/**
 *
 * @author supervisor
 */
public class NfeDpec {
    
    String xml = "";
    TDPEC dpec = null;
    private Unmarshaller unmarshaller;

    static Timer t = new Timer();
    static TimerTask tt;
    static int tempo = 1000 * 30;

    br.com.nfe.gui.Painel main = null;

    public NfeDpec(br.com.nfe.gui.Painel main) {
        this.main = main;
    }
    public static void main(String[] args) throws Exception {
        NfeDpec d = new NfeDpec(null);
        HashMap pedido_hm = new HashMap();
        VND_nfpedidoDAO pedido_dao = new VND_nfpedidoDAO();
        pedido_hm = pedido_dao.pesquisa_nfpedido_dpec();
        if (Integer.parseInt(pedido_hm.get("cdpedido").toString()) > 0 ){
            d.Dpec(pedido_hm);
        }
    }

public void StartTimer() throws Exception{
        try {
            main.CarregaJtxa(">>> TimerNfeDpec...: " + tempo,Color.BLACK);
        } catch (Exception e) {
        }
        tt = new TimerTask(){
          public void run() {
                try {
                    HashMap pedido_hm = new HashMap();
                    VND_nfpedidoDAO pedido_dao = new VND_nfpedidoDAO();
                    pedido_hm = pedido_dao.pesquisa_nfpedido_dpec();
                    if (Integer.parseInt(pedido_hm.get("cdpedido").toString()) > 0 ){
                        Dpec(pedido_hm);
                    }
                } catch (Exception ex) {
                    try {
                        main.CarregaJtxa(">>>TimerNfeDpec não foi executado...", Color.BLACK);
                    } catch (BadLocationException ex1) {
                        Logger.getLogger(NfeStatusServico.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
          }
       };
       t.schedule(tt, 0, tempo);
    }
    public void StopTimer() throws Exception{
        main.CarregaJtxa(">>>Stop TimerNfeDpec...",Color.BLACK);
        tt.cancel();
    }
public String Envio(String Xml, URL url, String CUF, String VersaoDados) throws XMLStreamException, AxisFault, RemoteException, BadLocationException{
    String Line = "";
    SCERecepcaoRFBStub.SceRecepcaoDPECResult result = null;     
        
    OMElement ome = AXIOMUtil.stringToOM(Xml);  

    SCERecepcaoRFBStub.SceDadosMsg dadosMsg = new SCERecepcaoRFBStub.SceDadosMsg();
        
    dadosMsg.setExtraElement(ome);
    
    
    SCERecepcaoRFBStub.SceCabecMsg nfeCabecMsg = new SCERecepcaoRFBStub.SceCabecMsg();
        
    nfeCabecMsg.setVersaoDados(VersaoDados);  

    SCERecepcaoRFBStub.SceCabecMsgE nfeCabecMsgE = new SCERecepcaoRFBStub.SceCabecMsgE();

    
    nfeCabecMsgE.setSceCabecMsg(nfeCabecMsg);  
        
    SCERecepcaoRFBStub stub = new SCERecepcaoRFBStub(url.toString());
    try {            
        result = stub.sceRecepcaoDPEC(dadosMsg, nfeCabecMsgE);  
        Line = result.getExtraElement().toString();
        try {
            main.CarregaJtxa(Line,Color.RED);
        } catch (Exception exc) {
        }
    } catch (Exception e) {
        try {
            main.CarregaJtxa("Erro Envio NfeDpec : " + e.toString(),Color.RED);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    } 
    return Line;
} 
     public void Dpec(HashMap pedido_hm) throws Exception {
         
             KStore k = new KStore();
             k.KeyStore(); 
             
            /**
             * Endereco WebService
            */

             ADM_ParamDAO dao = new ADM_ParamDAO();
             ADM_ParamBean bean = new ADM_ParamBean();
             VND_nfpedidoDAO pedido_dao = new VND_nfpedidoDAO();
             VND_NfpedidoBean pedido_bean = new VND_NfpedidoBean();

             bean = dao.getDados_webservice("WebserverNfeDpec",null);
             
             URL url = new URL(bean.getValorparam());
             try {
                 MontaXML(pedido_hm);
             } catch (Exception e) {
                 main.CarregaJtxa(e.toString(),Color.RED);
             }
             
             JAXBContext context = null;

            try {
                StringWriter out = new StringWriter();

                context = JAXBContext.newInstance("br.inf.portalfiscal.nfe.dpec.env");
                Marshaller marshaller = context.createMarshaller();  
                JAXBElement<TDPEC> element = new ObjectFactory().createEnvDPEC(dpec);  
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);  
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                
                marshaller.marshal(element,new StreamResult(out));

                xml = out.toString();
                
                xml = pedido_dao.remove_acento(xml);
                
                xml = xml.replace("xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
                
                       
                AssinarXMLsCertfificadoA1 AssinarXml = new AssinarXMLsCertfificadoA1();    
                xml = AssinarXml.assinaXml(xml, "assinaEnvDPEC") ;
                
               
            } catch (JAXBException e) {
                e.printStackTrace();
            }
            
            String validation = NFeValidacaoXML.validaDpecNfe(xml);
            if(!validation.isEmpty()) {  
                main.CarregaJtxa(validation.replace("cvc-pattern-valid: ", ""),Color.RED);
            }  else {
                try {
                main.CarregaJtxa(url.toString(),Color.BLUE);
                } catch (Exception e) {
                }
                String retorno = Envio(xml, url, null, bean.getValor().toString());
                if (retorno.length() > 0){
                    context = JAXBContext.newInstance(TRetDPEC.class);  
                    unmarshaller = context.createUnmarshaller();
                    TRetDPEC retdpec  = unmarshaller.unmarshal(new StreamSource(new StringReader(retorno)), TRetDPEC.class).getValue();
                    if (Integer.parseInt(retdpec.getInfDPECReg().getCStat()) == 124){

                        pedido_bean.setCdpedido(Integer.parseInt(pedido_hm.get("cdpedido").toString()));
                        pedido_bean.setNregdpec(retdpec.getInfDPECReg().getNRegDPEC());
                        pedido_bean.setStatus_nfe(Integer.parseInt(retdpec.getInfDPECReg().getCStat()));

                        String dhreg = retdpec.getInfDPECReg().getDhRegDPEC().toString();
                        dhreg = dhreg.replace("T", " ");
                        pedido_bean.setDhregdpec(dhreg);
                       
                        main.CarregaJtxa(">>>DPEC....:",Color.BLUE);
                        pedido_dao.Update_nfpedido_env(pedido_bean);

                        ControleImpressao c = new ControleImpressao(main);
                        c.PesquisaXML(Integer.parseInt(pedido_hm.get("cdpedido").toString()), null);

                    } else {
                        pedido_bean.setCdpedido(Integer.parseInt(pedido_hm.get("cdpedido").toString()));
                        pedido_bean.setStatus_nfe(Integer.parseInt(retdpec.getInfDPECReg().getCStat()));
                        pedido_bean.setNregdpec("");
                        main.CarregaJtxa(">>>DPEC....:" + retdpec.getInfDPECReg().getNRegDPEC() ,Color.BLUE);
                        pedido_dao.Update_nfpedido_env(pedido_bean);
                    }
                } else {
                    main.CarregaJtxa(">>>Erro retorno DPEC....:",Color.RED);
                }
                
            }  

}
     private void MontaXML(HashMap pedido_hm) throws Exception{
    
            /** 
             * Estrutura XML com a Declaração Prévia Emissão em Contingência - DPEC. 
             */  
            dpec = new TDPEC();  
            /* 
             * Versão do leiaute. 
             */  
            dpec.setVersao("1.01");  
  
            /** 
             * Tag de grupo com Informações da Declaração  
             * Prévia de Emissão em Contingência 
             */  
            InfDPEC infDPEC = new InfDPEC();  
            /* 
             * Grupo de Identificação da TAG a ser assinada. 
             * Informar com a literal "DPEC" + CNPJ do emissor. 
             */  
            infDPEC.setId("DPEC" + pedido_hm.get("cnpj").toString());  
              
            /** 
             * Grupo de Identificação do Declarante, deve ser  
             * informado com os dados do emissor das NF-e  
             * emitidas em contingência eletrônica 
             */  
            IdeDec ideDec = new IdeDec();  
            /* 
             * Código da UF do emitente do Documento Fiscal. Utilizar a Tabela do IBGE. 
             */  
            ideDec.setCUF(pedido_hm.get("iduf").toString());  
            /* 
             * Identificação do Ambiente: 
             * 1 - Produção 
             * 2 - Homologação. 
             */  
            ideDec.setTpAmb("1");  
            /* 
             * Versão do aplicativo utilizado no processo de emissão da DPEC. 
             */  
            ideDec.setVerProc("Sistema Sief");  
            /* 
             * Número do CNPJ do emitente, vedada a formatação do campo. 
             */  
            ideDec.setCNPJ(pedido_hm.get("cnpj").toString());
            /* 
             * Número da Inscrição Estadual do emitente, vedada a formatação do campo. 
             */  
            ideDec.setIE(pedido_hm.get("inscricaoestad").toString());  
            infDPEC.setIdeDec(ideDec);  
  
            /** 
             * Resumo das NF-e emitidas no Sistema de 
             * Contingência Eletrônica (até 50 NF-e com tpEmis = "4") 
             * tpEmis "4" = Contingência DPEC - emissão em contingência com envio da Declaração 
             * Prévia de Emissão em Contingência – DPEC; 
             */  
            ResNFe resNFe = new ResNFe();  
            /* 
             * Chave de Acesso da NF-e emitida em contingência eletrônica. 
             */  
            resNFe.setChNFe(pedido_hm.get("idnfe").toString());  
            /* 
             * Informar o CNPJ ou o CPF do destinatário da NF-e, 
             * em caso de destinatário ou remetente estabelecido 
             * no exterior deverá ser informado a tag CNPJ sem 
             * conteúdo. 
             */  
            
            if (pedido_hm.get("cnpj_pedido").toString().toString().length() > 11){
                resNFe.setCNPJ(pedido_hm.get("cnpj_pedido").toString());  
            } else {
                resNFe.setCPF(pedido_hm.get("cnpj_pedido").toString());
            }
            /* 
             * Sigla da UF de destino da mercadoria. 
             */  
            resNFe.setUF(TUf.valueOf(pedido_hm.get("uf").toString()));  
            /* 
             * Valor total da NF-e. 
             */  
            resNFe.setVNF(pedido_hm.get("totalpedido").toString());  
            /* 
             * Valor Total do ICMS da operação própria. 
             */  
            resNFe.setVICMS(pedido_hm.get("totalicms").toString());  
            /* 
             * Valor Total do ICMS retido por Substituição Tributária 
             */  
            resNFe.setVST(pedido_hm.get("totalicmssubst").toString());  
              
            infDPEC.getResNFe().add(resNFe);  
              
            dpec.setInfDPEC(infDPEC);  
            dpec.setSignature(null); // Assinar o XML antes do envio;  
     
 }
     
     /** 
     * Log ERROR. 
     * @param error 
     */  
    private static void error(String error) {  
        System.out.println("| ERROR: " + error);  
    }  
  
    /** 
     * Log INFO. 
     * @param info 
     */  
    private static void info(String info) {  
        System.out.println("| INFO: " + info);  
    }  
    
}
