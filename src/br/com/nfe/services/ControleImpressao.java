package br.com.nfe.services;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import br.com.nfe.dao.adm.ADM_ParamDAO;
import br.com.nfe.dao.vnd.VND_nfpedidoDAO;
import br.com.nfe.dao.vnd.VND_pedvendaDAO;
import br.com.nfe.email.EnviarEmail;
import br.com.nfe.util.Database;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.xml.sax.SAXException;

/**
 *
 * @author supervisor
 */
public class ControleImpressao {
    
    private static PrintService impressora;
    java.sql.Connection conn = null;
    br.com.nfe.gui.Painel main = null;
    String curDir = System.getProperty("user.home");
    String curSep = System.getProperty("file.separator");
    String caminho = curDir + curSep + "NfeServices"+ curSep +"report"+ curSep;
   
    public static void main(String[] args) throws SQLException, Exception {

        VND_nfpedidoDAO nfpedido = new VND_nfpedidoDAO();
        ControleImpressao c = new ControleImpressao(null);
//        c.ViewPdf(null, 6074593);
        nfpedido.pesquisa_nfpedido_reimpressao(4, 6069120, 6069356, c);
               
    }

    public ControleImpressao(br.com.nfe.gui.Painel main) throws Exception{
      this.main = main;
      conn = Database.getConnection();

      if (conn == null) {
         throw new Exception(getClass().getName() + ": null connection passed.");
      }
         this.conn = conn;
    }

    public void printPDF(byte[] f, int cdpedido){
        int cdc = 0;
        PrintService printService1 = null;
        try {
            DocFlavor dflavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            
            ADM_ParamDAO dao = new ADM_ParamDAO();
            VND_nfpedidoDAO nfpedido = new VND_nfpedidoDAO();
            
            cdc = nfpedido.idcentrocusto_operador(cdpedido);
            
            HashMap map_imp = new HashMap();
            map_imp = dao.pesquisa_impressora("ImpressoraNfe",cdc);
            if(map_imp.get("valorparam") != null){

                    //System.err.println(map_imp.get("valorparam"));

                    PrintService[] printService = PrintServiceLookup.lookupPrintServices(dflavor, null);
                    for (int i = 0; i < printService.length; i++) {
                        printService1 = printService[i];
                        if (printService1.getName().contentEquals(map_imp.get("valorparam").toString())){
                            impressora = printService1;
                            break;
                        }
                    }
                    try {
                        DocPrintJob dpj = impressora.createPrintJob();


                        InputStream stream = new ByteArrayInputStream(f);

                        // Configura o conjunto de parametros para a impressora
                        PrintRequestAttributeSet printerAttributes = new HashPrintRequestAttributeSet();
                        // Adiciona uma propriedade de impressão: imprimir x cópias baseado no retorno do parametro
                        printerAttributes.add(new Copies(Integer.parseInt(map_imp.get("valor").toString())));

                        Doc doc = new SimpleDoc(stream, dflavor, null);
                        dpj.print(doc, printerAttributes);

                        main.CarregaJtxa("Imprimiu Pedido: " + cdpedido + " CDC: " + cdc + " Impressora: " + impressora.getName(),Color.MAGENTA);

                    } catch (Exception e) {
                        main.CarregaJtxa(e.getMessage(), Color.red);
                    }
                    
            } else {
                main.CarregaJtxa("Não foi localizado Impressora CDC " + cdc,Color.RED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    public void PesquisaXML(int cdpedido, String xml_nfe) throws SQLException, SAXException, JRException, FileNotFoundException, ParserConfigurationException, IOException, Exception{
        
        if (xml_nfe == null){
            String qry = "Select xml_nfe from vnd_nfpedido where cdpedido = ?";

            PreparedStatement stmt = conn.prepareStatement(qry);
            stmt.setInt(1, cdpedido);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
               xml_nfe = rs.getString("xml_nfe");
            }
            rs.close();
            stmt.close();
        }
        GeraPdf(xml_nfe,cdpedido);

    }
  
    public void GeraPdf(String xml_nfe, int cdpedido) throws JRException, FileNotFoundException, SAXException, ParserConfigurationException, IOException, Exception{
         byte[] pdf = null;  
        
        try {
          //Caminho do arquivo 
          String jasper_rel = caminho + "danfeR.jasper";
          
          InputStream source = new ByteArrayInputStream(xml_nfe.getBytes()); 
          
          VND_nfpedidoDAO nfpedidoDAO = new VND_nfpedidoDAO();
          String retorno = nfpedidoDAO.pesquisa_protocolo(cdpedido);
          
          String xpath = "/nfeProc/NFe/infNFe/det";
          if(retorno == null){
              xpath = "/enviNFe/NFe/infNFe/det";
          }
          
          /*JRXmlDataSource xml =  new JRXmlDataSource(source,"/nfeProc/NFe/infNFe/det");*/
          JRXmlDataSource xml =  new JRXmlDataSource(source,xpath);
          
          HashMap map = new HashMap();
          map.put("Logo", caminho + "Logotipo.jpg");
          map.put("SUBREPORT_DIR", caminho + "danfeRDuplic.jasper");
          map.put("SUBREPORT_DIR2", caminho + "danfeRDpec.jasper");
          map.put("REPORT_CONNECTION",conn);

          /** 
            * Gerando o relatorio 
            */ 
            JasperPrint print = null;
            try {
                print = JasperFillManager.fillReport(jasper_rel, map, xml);
                /* Exportando em pdf */  
                pdf = JasperExportManager.exportReportToPdf(print);
                JasperExportManager.exportReportToPdfFile(print,caminho+"nota.pdf");
            } catch (Exception e) {
                main.CarregaJtxa(e.toString(),Color.RED);
            }
         
//          JasperViewer.viewReport(print);
          
          printPDF(pdf,cdpedido);
          
          VND_pedvendaDAO pedido_dao = new VND_pedvendaDAO();
          String idnfe = pedido_dao.select_idnfe(cdpedido);
          
          EnviarEmail e = new EnviarEmail(main);
          e.Enviar(xml_nfe, pdf, idnfe,"env");
          
        } catch (JRException e) {
          e.printStackTrace();
          pdf = null; 
        }

    }
    
    public void ViewPdf(String xml_nfe, int cdpedido) throws JRException, FileNotFoundException, SAXException, ParserConfigurationException, IOException, Exception{
         byte[] pdf = null;
         
         
         
         if (xml_nfe == null){
            String qry = "Select xml_nfe from vnd_nfpedido where cdpedido = ?";

            PreparedStatement stmt = conn.prepareStatement(qry);
            stmt.setInt(1, cdpedido);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
               xml_nfe = rs.getString("xml_nfe");
            }
            rs.close();
            stmt.close();
        }
         
        
        try {
          //Caminho do arquivo 
          String jasper_rel = caminho + "danfeR.jasper";
          System.out.println(jasper_rel);
          
          InputStream source = new ByteArrayInputStream(xml_nfe.getBytes()); 
          
          VND_nfpedidoDAO nfpedidoDAO = new VND_nfpedidoDAO();
          String retorno = nfpedidoDAO.pesquisa_protocolo(cdpedido);
          
          String xpath = "/nfeProc/NFe/infNFe/det";
          if(retorno == null){
              xpath = "/enviNFe/NFe/infNFe/det";
          }
          
          /*JRXmlDataSource xml =  new JRXmlDataSource(source,"/nfeProc/NFe/infNFe/det");*/
          JRXmlDataSource xml =  new JRXmlDataSource(source,xpath);
          
          HashMap map = new HashMap();
          map.put("Logo", caminho + "Logotipo.jpg");
          map.put("SUBREPORT_DIR", caminho + "danfeRDuplic.jasper");
          map.put("SUBREPORT_DIR2", caminho + "danfeRDpec.jasper");
          map.put("REPORT_CONNECTION",conn);

          /** 
            * Gerando o relatorio 
            */ 
            JasperPrint print = null;
            try {
                print = JasperFillManager.fillReport(jasper_rel, map, xml);
                /* Exportando em pdf */  
                pdf = JasperExportManager.exportReportToPdf(print);
                JasperExportManager.exportReportToPdfFile(print,caminho+cdpedido+".pdf");
            } catch (Exception e) {
            }
         
//          JasperViewer.viewReport(print);
          
          
        } catch (JRException e) {
          e.printStackTrace();
          pdf = null; 
        }


        byte[] f = pdf;


        PrintService printService1 = null;
        try {
            DocFlavor dflavor = DocFlavor.INPUT_STREAM.AUTOSENSE;

            PrintService[] printService = PrintServiceLookup.lookupPrintServices(dflavor, null);
            for (int i = 0; i < printService.length; i++) {
                printService1 = printService[i];
                if (printService1.getName().contentEquals("print-galc")){
                    impressora = printService1;
                    break;
                }
            }
            try {
                DocPrintJob dpj = impressora.createPrintJob();


                InputStream stream = new ByteArrayInputStream(f);

                // Configura o conjunto de parametros para a impressora
                PrintRequestAttributeSet printerAttributes = new HashPrintRequestAttributeSet();
                // Adiciona uma propriedade de impressão: imprimir x cópias baseado no retorno do parametro
                printerAttributes.add(new Copies(1));
                printerAttributes.add(MediaSizeName.ISO_A4);

                Doc doc = new SimpleDoc(stream, dflavor, null);

                dpj.print(doc, printerAttributes);
                System.out.println("Imprimiu Pedido: "+cdpedido+" Impressora: " + impressora.getName());

                Thread.sleep(1000*10);


            } catch (Exception e) {
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    
    
}
