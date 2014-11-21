/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.nfe.services;

import br.com.nfe.bean.adm.ADM_ParamBean;
import br.com.nfe.bean.cdc.CDC_EstabelecimBean;
import br.com.nfe.bean.lfs.LFS_NfentradBean;
import br.com.nfe.bean.lfs.LFS_Nfentrad_ManifestoBean;
import br.com.nfe.bean.vnd.VND_CartaBean;
import br.com.nfe.dao.adm.ADM_ParamDAO;
import br.com.nfe.dao.cdc.CDC_EstabelecimDAO;
import br.com.nfe.dao.lfs.LFS_NfentradDAO;
import br.com.nfe.dao.lfs.LFS_Nfentrad_ManifestoDAO;
import br.com.nfe.dao.vnd.VND_CartaDAO;
import br.com.nfe.dao.vnd.VND_nfpedidoDAO;
import br.com.nfe.util.AssinarXMLsCertfificadoA1;
import br.com.nfe.util.DtSystem;
import br.com.nfe.util.FormataValores;
import br.com.nfe.util.KStore;
import br.com.nfe.util.NFeValidacaoXML;
import br.inf.portalfiscal.nfe.cce.ObjectFactory;
import br.inf.portalfiscal.nfe.cce.TEnvEvento;
import br.inf.portalfiscal.nfe.cce.TEvento;
import br.inf.portalfiscal.nfe.cce.TRetEnvEvento;
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
public class NfeCCe {
    
    String xml = "";
    TEnvEvento env = null; 
    private Unmarshaller unmarshaller;
    public VND_nfpedidoDAO nfpedido = null;

    static Timer t = new Timer();
    static TimerTask tt;
    static int tempo = 1000 * 30;

    br.com.nfe.gui.Painel main = null;
    
    
    /**
     * @param args the command line arguments
     */
    public NfeCCe(br.com.nfe.gui.Painel main) throws Exception {
        this.main = main;
         nfpedido = new VND_nfpedidoDAO();
    }
    public static void main(String[] args) throws Exception {
        NfeCCe cce = new NfeCCe(null);
        VND_CartaDAO ccDao = new VND_CartaDAO();
        VND_CartaBean ccBean = new VND_CartaBean();
//        ccBean = ccDao.pesquisa_carta();
        ccBean = ccDao.pesquisa_cartaCdpedido(6748605);
        if (ccBean.getCdpedido() > 0){
            cce.EventoCartaCorrecao(ccBean);
        }
    }
public void StartTimer() throws Exception{
        main.CarregaJtxa(">>> TimerNfeCCe...: " + tempo,Color.BLACK);
        tt = new TimerTask(){
          public void run() {
                try {
                    VND_CartaDAO ccDao = new VND_CartaDAO();
                    VND_CartaBean ccBean = new VND_CartaBean();
                    ccBean = ccDao.pesquisa_carta();
                    if (ccBean.getCdpedido() > 0){
                        EventoCartaCorrecao(ccBean);
                    }
                } catch (Exception ex) {
                    try {
                        main.CarregaJtxa(">>>TimerNfeCCe não foi executado...", Color.BLACK);
                    } catch (BadLocationException ex1) {
                        Logger.getLogger(NfeStatusServico.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
          }
       };
       t.schedule(tt, 0, tempo);
    }
    public void StopTimer() throws Exception{
        main.CarregaJtxa(">>>Stop TimerNfeCCe...",Color.BLACK);
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
            main.CarregaJtxa("Erro Envio NfeCCe : " + e.toString(),Color.RED);
        } 
        
        return Line;
    }
    public void EventoCartaCorrecao(VND_CartaBean beanCC) throws URISyntaxException, Exception{
    // TODO code application logic here

            JAXBContext context = null;
            
            /**
             * Endereco WebService
            */

             ADM_ParamDAO dao = new ADM_ParamDAO();
             ADM_ParamBean bean = new ADM_ParamBean();
             VND_nfpedidoDAO pedido_dao = new VND_nfpedidoDAO();
//             VND_NfpedidoBean pedido_bean = new VND_NfpedidoBean();

             bean = dao.getDados_webservice("WebserverNfeEvento",beanCC.getIduf());
             
             URL url = new URL(bean.getValorparam());
             if (beanCC.getXml_cce() == null){
                 try {
                    KStore k = new KStore();
                    k.KeyStore();

                    MontaXMLCC(beanCC);



                    try {
                        StringWriter out = new StringWriter();

                        context = JAXBContext.newInstance("br.inf.portalfiscal.nfe.cce");
                        Marshaller marshaller = context.createMarshaller();
                        JAXBElement<TEnvEvento> element = new ObjectFactory().createEnvEvento(env);
                        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
                        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

                        marshaller.marshal(element,new StreamResult(out));

                        xml = out.toString();

                        xml = pedido_dao.remove_acento(xml);

                        xml = xml.replace("xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");

                        AssinarXMLsCertfificadoA1 AssinarXml = new AssinarXMLsCertfificadoA1();
                        xml = AssinarXml.assinaXml(xml, "Evento") ;

                    } catch (JAXBException e) {
                        e.printStackTrace();
                    }
                 } catch (Exception e) {
                     main.CarregaJtxa(e.toString(),Color.RED);
                 }
             }
            
            String validation = NFeValidacaoXML.validaCartaCorrecao(xml);
            if(!validation.isEmpty()) {  
                main.CarregaJtxa(validation.replace(beanCC.getCdpedido()+": cvc-pattern-valid: ", ""),Color.RED);
                nfpedido.insert_nfpedido_rejeicao(beanCC.getCdpedido(), validation.replace("cvc-pattern-valid: ", ""), null);
            } else {
                try {
                    VND_CartaDAO dao_carta = new VND_CartaDAO();
                    if (beanCC.getXml_cce() == null){
                        beanCC.setXml_cce(xml);
                        dao_carta.atualiza_carta(beanCC);
                    }
                    String retorno = Envio(xml, url, beanCC.getIduf(), FormataValores.formataVr_Float(bean.getValor(),"0.00"));
                    main.CarregaJtxa(retorno,Color.RED);
                    
                    if (retorno.length() > 0){
                        context = JAXBContext.newInstance(TRetEnvEvento.class);  
                        unmarshaller = context.createUnmarshaller();
                        TRetEnvEvento retevent  = unmarshaller.unmarshal(new StreamSource(new StringReader(retorno)), TRetEnvEvento.class).getValue();

                        beanCC.setXml_cce(null);
                        beanCC.setStatus_nfe(Integer.parseInt(retevent.getRetEvento().get(0).getInfEvento().getCStat()));
                        
                        if (beanCC.getStatus_nfe() == 135){
                            
                            beanCC.setProtocolo(retevent.getRetEvento().get(0).getInfEvento().getNProt());
                            dao_carta.atualiza_carta(beanCC);
                            pedido_dao.insert_nfpedido_rejeicao(beanCC.getCdpedido(), null, beanCC.getStatus_nfe());
                        } else {
                            pedido_dao.insert_nfpedido_rejeicao(beanCC.getCdpedido(), null, beanCC.getStatus_nfe());
                        }
                    
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }
    private void MontaXMLCC(VND_CartaBean beanCC) throws Exception{
        env = new TEnvEvento();
        
        env.setVersao("1.00");
        env.setIdLote("1");
        
        TEvento evento = new TEvento();
        
        evento.setVersao("1.00");
        
        TEvento.InfEvento infevento = new TEvento.InfEvento();
        
        String seqEvento = beanCC.getNSeqEvento();
        for (int p = 0; p < 3 - seqEvento.length() ; p++) {
            seqEvento = "0" + seqEvento;
        }

        infevento.setId("ID110110"+beanCC.getIdnfe()+seqEvento);
        infevento.setCOrgao(beanCC.getIduf());
        infevento.setTpAmb("1");
        infevento.setCNPJ(beanCC.getCnpj_cpf());
        infevento.setChNFe(beanCC.getIdnfe());
        infevento.setDhEvento(DtSystem.getdhEvento());
        infevento.setTpEvento("110110");
        infevento.setNSeqEvento(beanCC.getNSeqEvento());
        infevento.setVerEvento("1.00");
        
        TEvento.InfEvento.DetEvento dtevento = new TEvento.InfEvento.DetEvento();

        dtevento.setVersao("1.00");
        
        dtevento.setDescEvento("Carta de Correção");
        dtevento.setXCorrecao(beanCC.getDescricao());
        
        dtevento.setXCondUso("A Carta de Correcao e disciplinada pelo"
                + " paragrafo 1o-A do art. 7o do Convenio S/N, de"
                + " 15 de dezembro de 1970 e pode ser utilizada"
                + " para regularizacao de erro ocorrido na emissao"
                + " de documento fiscal, desde que o erro nao"
                + " esteja relacionado com: I - as variaveis que"
                + " determinam o valor do imposto tais como: base"
                + " de calculo, aliquota, diferenca de preco,"
                + " quantidade, valor da operacao ou da prestacao;"
                + " II - a correcao de dados cadastrais que implique"
                + " mudanca do remetente ou do destinatario; III - a"
                + " data de emissao ou de saida.");

        infevento.setDetEvento(dtevento);
        evento.setInfEvento(infevento);
        env.getEvento().add(evento);
        
        
    }
    
}
