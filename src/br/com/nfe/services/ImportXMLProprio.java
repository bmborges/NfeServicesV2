/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.nfe.services;

import br.com.nfe.gui.Painel;
import br.com.nfe.util.Database;
import br.inf.portalfiscal.nfe.canc.TCancNFe;
import br.inf.portalfiscal.nfe.canc.TProcCancNFe;
import br.inf.portalfiscal.nfe.env.TNfeProc;
import br.inf.portalfiscal.nfe.envCanc.TProcEvento;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author supervisor
 */
public class ImportXMLProprio{

    java.sql.Connection conn = null;
    static Timer t = new Timer();
    static TimerTask tt;
    static int tempo = 1000 * 30;
    br.com.nfe.gui.Painel main = null;

    String curDir = System.getProperty("user.home");
    String curSep = System.getProperty("file.separator");
    
    
    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) throws Exception {
        ImportXMLProprio i = new ImportXMLProprio(null);
        i.Importa();
    }
    public void StartTimer() throws Exception{
        main.CarregaJtxa(">>>TimerImportXML...", Color.red);
        tt = new TimerTask(){
          public void run() {
                try {
                    Importa();
                } catch (Exception ex) {
                    try {
                        main.CarregaJtxa(">>>ImportXMLProprio não foi executado..." + ex.toString(), Color.red);
                    } catch (BadLocationException ex1) {
                        Logger.getLogger(ImportXMLProprio.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
          }
       };
       t.schedule(tt, 18000, tempo);
    }
    public void StopTimer() throws Exception{
        main.CarregaJtxa(">>>Stop TimerImportXML...",Color.BLACK);
        tt.cancel();
    }
    public ImportXMLProprio(br.com.nfe.gui.Painel main) throws Exception {
        this.main = main;
        conn = Database.getConnection();

      if (conn == null) {
         throw new Exception(getClass().getName() + ": null connection passed.");
      }
         this.conn = conn;
    }
    
    protected void Importa() throws SQLException, IOException, JAXBException, BadLocationException{
        File diretorio = new File(curDir + curSep + "xml");
        String [] arquivos = diretorio.list();  
        for(int i=0; i<arquivos.length; i++) {
            File notaFile = new File(curDir + curSep + "xml" + curSep + arquivos[i]);
            System.err.println("Lendo Arquivo " + notaFile.getPath());
            if (notaFile.toString().endsWith(".xml")){
                String str ="";
                try {
                    String encoding = "ISO-8859-1"; 
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream (curDir + curSep + "xml" + curSep + notaFile.getName()), encoding));

                    while (reader.ready()) {
                        str += reader.readLine();
                    }
                    reader.close();
                    
                    str.replaceAll ("\uFFFD", "");
                    
                    JAXBContext context = null;
                    
                    Unmarshaller unmarshaller = null;
                    try {
                        context = JAXBContext.newInstance("br.inf.portalfiscal.nfe.canc");
                        unmarshaller = context.createUnmarshaller();
                        TCancNFe proc = unmarshaller.unmarshal( new StreamSource(notaFile), TCancNFe.class ).getValue();
                        GravaXml(str,proc);
                        notaFile.delete();
                    } catch (Exception TCancNFe) {
                        try{
                            context = JAXBContext.newInstance("br.inf.portalfiscal.nfe.env");
                            unmarshaller = context.createUnmarshaller();
                            TNfeProc proc = unmarshaller.unmarshal( new StreamSource(notaFile), TNfeProc.class ).getValue();
                            GravaXml(str,proc);
                            notaFile.delete();
                        } catch (Exception e) {
                            try {
                                context = JAXBContext.newInstance("br.inf.portalfiscal.nfe.canc");
                                unmarshaller = context.createUnmarshaller();
                                TProcCancNFe proc = unmarshaller.unmarshal( new StreamSource(notaFile), TProcCancNFe.class ).getValue();
                                GravaXml(str,proc);
                                notaFile.delete();
                            } catch (Exception ex) {
                                context = JAXBContext.newInstance("br.inf.portalfiscal.nfe.envCanc");
                                unmarshaller = context.createUnmarshaller();
                                TProcEvento proc = unmarshaller.unmarshal( new StreamSource(notaFile), TProcEvento.class ).getValue();

                                if (proc.getRetEvento().getInfEvento().getTpEvento().endsWith("110111")){
                                    GravaXml(str,proc);
                                    notaFile.delete();
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }
        
        //FaltaXml();
    }
    protected void GravaXml(String arquivo, TCancNFe proc) throws SQLException, BadLocationException{

        if (proc.getInfCanc().getNProt() == null || proc.getInfCanc().getNProt().length() <= 0){
            return;
        }

        String qry = "update vnd_nfpedido set xml_canc = ?, situacaonf = 'S', status_nfe = ?::integer,"
                + "  protocolo = ? where cdpedido = (select cdpedido from vnd_pedvenda where idnfe = ?)";

        PreparedStatement stmt = conn.prepareStatement(qry);

        stmt.setString(1, arquivo);
        stmt.setString(2, "101");
        stmt.setString(3, proc.getInfCanc().getNProt());
        stmt.setString(4, proc.getInfCanc().getChNFe());
        try {
            stmt.executeUpdate();
        } catch (Exception e) {
            main.CarregaJtxa(e.toString(),Color.RED);
        }

        qry = "select vnd_atualiza_pedvenda((select cdpedido from vnd_pedvenda where idnfe = ?), 7)";
        stmt = conn.prepareStatement(qry);
        stmt.setString(1, proc.getInfCanc().getChNFe());

        try {
            stmt.executeQuery();
            main.CarregaJtxa("GravaXml_Canc Idnfe: " + proc.getInfCanc().getChNFe(), Color.RED);
        } catch (Exception e) {
            main.CarregaJtxa(e.toString(),Color.RED);
        }

        stmt.close();

    }
    protected void GravaXml(String arquivo, TNfeProc proc) throws SQLException, BadLocationException{
        
        String qry = "update vnd_nfpedido set xml_nfe = ?, situacaonf = 'N', status_nfe = ?::integer,"
                + " protocolo = ?, tpemis = ? where cdpedido = ?::integer";
           
        PreparedStatement stmt = conn.prepareStatement(qry);

        stmt.setString(1, arquivo);
        stmt.setString(2, proc.getProtNFe().getInfProt().getCStat());
        stmt.setString(3, proc.getProtNFe().getInfProt().getNProt());
        stmt.setString(4, proc.getNFe().getInfNFe().getIde().getTpEmis());
        stmt.setString(5, proc.getNFe().getInfNFe().getIde().getCNF());
        try {
            stmt.executeUpdate();
        } catch (Exception e) {
            main.CarregaJtxa(e.toString(),Color.RED);
        }
        
        qry = "update vnd_pedvenda set idnfe = ? where cdpedido = ?::integer";
        stmt = conn.prepareStatement(qry);
        stmt.setString(1, proc.getProtNFe().getInfProt().getChNFe());
        stmt.setString(2, proc.getNFe().getInfNFe().getIde().getCNF());
        try {
            stmt.executeUpdate();
            main.CarregaJtxa("GravaXml_Nfe Pedido: " + proc.getNFe().getInfNFe().getIde().getCNF(),Color.RED);
        } catch (Exception e) {
            main.CarregaJtxa(e.toString(),Color.RED);
        }        
        
        stmt.close();

    }
    protected void GravaXml(String arquivo, TProcCancNFe proc) throws SQLException, BadLocationException{
        
        String qry = "update vnd_nfpedido set xml_canc = ?, situacaonf = 'S', status_nfe = ?::integer,"
                + "  protocolo = ? where cdpedido = (select cdpedido from vnd_pedvenda where idnfe = ?)";
           
        PreparedStatement stmt = conn.prepareStatement(qry);

        stmt.setString(1, arquivo);
        stmt.setString(2, proc.getRetCancNFe().getInfCanc().getCStat());
        stmt.setString(3, proc.getCancNFe().getInfCanc().getNProt());
        stmt.setString(4, proc.getCancNFe().getInfCanc().getChNFe());
        try {
            stmt.executeUpdate();
        } catch (Exception e) {
            main.CarregaJtxa(e.toString(),Color.RED);
        }
        
        qry = "select vnd_atualiza_pedvenda((select cdpedido from vnd_pedvenda where idnfe = ?), 7)";
        stmt = conn.prepareStatement(qry);
        stmt.setString(1, proc.getCancNFe().getInfCanc().getChNFe());

        try {
            stmt.executeQuery();
            main.CarregaJtxa("GravaXml_Canc Idnfe: " + proc.getCancNFe().getInfCanc().getChNFe(), Color.RED);
        } catch (Exception e) {
            main.CarregaJtxa(e.toString(),Color.RED);
        }        
        
        stmt.close();

    }
    protected void GravaXml(String arquivo, TProcEvento proc) throws SQLException, BadLocationException{
        
        String qry = "update vnd_nfpedido set xml_canc = ?, situacaonf = 'S', status_nfe = ?::integer"
                //+ "  protocolo = ?"
                + " where cdpedido = (select cdpedido from vnd_pedvenda where idnfe = ?)";
           
        PreparedStatement stmt = conn.prepareStatement(qry);

        stmt.setString(1, arquivo);
        stmt.setString(2, "101");
        stmt.setString(3, proc.getRetEvento().getInfEvento().getChNFe());
        try {
            stmt.executeUpdate();
        } catch (Exception e) {
            main.CarregaJtxa(e.toString(),Color.RED);
        }
        
        qry = "select vnd_atualiza_pedvenda((select cdpedido from vnd_pedvenda where idnfe = ?), 7)";
        stmt = conn.prepareStatement(qry);
        stmt.setString(1, proc.getRetEvento().getInfEvento().getChNFe());

        try {
            stmt.executeQuery();
            main.CarregaJtxa("GravaXml_Canc Idnfe: " + proc.getRetEvento().getInfEvento().getChNFe(), Color.RED);
        } catch (Exception e) {
            main.CarregaJtxa(e.toString(),Color.RED);
        }        
        
        stmt.close();

    }
    protected void FaltaXml() throws SQLException, FileNotFoundException, IOException{
//        System.err.println("Chamou FaltaXml");
         String qry = "select distinct idestabelecimen, to_char(dt_pedido,'dd/mm/yyyy') as data, dt_pedido from vnd_nfpedido"
                 + " inner join vnd_pedvenda using (cdpedido)"
                 + " where xml_nfe is null and modelo = '55'"
                 + " and dt_pedido >= '2009-01-01'"
                 + " order by dt_pedido desc, idestabelecimen"
                 + " limit 500";
         
          PreparedStatement stmt = conn.prepareStatement(qry);
          
          ResultSet rs = stmt.executeQuery();
          
          
          File diretorio = new File("/home/supervisor/Downloads/xml/Falta");
          if(!diretorio.exists()){
              diretorio.mkdir();
          }
          
          File arquivo = new File("/home/supervisor/Downloads/xml/Falta/FaltaXml.txt"); 
          FileOutputStream fos = new FileOutputStream(arquivo);  
          while(rs.next()){
              String linha = rs.getInt("idestabelecimen")+"  "+rs.getString("data") + "\n";
              fos.write(linha.getBytes());  
          }
          fos.close();
          rs.close();
          stmt.close();
         
    }
    
    
}
