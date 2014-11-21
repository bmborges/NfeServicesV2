/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Painel.java
 *
 * Created on 01/07/2013, 08:25:36
 */

package br.com.nfe.gui;

import br.com.nfe.services.*;
import br.com.nfe.util.AssinarXMLsCertfificadoA1;
import br.com.nfe.util.DtSystem;
import br.com.nfe.util.NFeBuildAllCacerts;
import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author supervisor
 */
public class Painel extends javax.swing.JFrame {
    NfeStatusServico NfeStatusServico =  null;
    NfeManifesto NfeManifesto = null;
    NfeInutilizacao NfeInutilizacao = null;
    NfeConsulta NfeConsulta = null;
    NfeRecepcao NfeRecepcao = null;
    NfeDpec NfeDpec = null;
    NfeEventCanc NfeEventCanc = null;
    NfeCCe NfeCCe = null;
    ImportXMLProprio impXML = null;
    NfeRetRecepcao NfeRetRecepcao = null;

    StyledDocument doc = null;
    Style style = null;

    Timer timer1 = new Timer();
    Timer timer2 = new Timer();


    /** Creates new form Painel */
    public Painel() {
        initComponents();
        jtxa.setEditable(false);
        mnStop.setEnabled(false);
        doc = jtxa.getStyledDocument();
        style = jtxa.addStyle("estilo", null);

        try {
            disparaProcesso();
            StartServicos();
        } catch (Exception ex) {
            Logger.getLogger(Painel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void TesteCertificado() throws Exception{

        String curDir = System.getProperty("user.home");
        String curSep = System.getProperty("file.separator");
        String caminho = curDir + curSep + "NfeServices"+ curSep +"certificado"+ curSep;

        CarregaJtxa("Teste Certificado Pasta: " + caminho , Color.MAGENTA);

        File diretorio = new File(caminho);
        if (!diretorio.exists()){
            diretorio.mkdir();
        }

        String certificado = caminho + "certificado.pfx";
        String senha = "";

        try {
            BufferedReader in = new BufferedReader(new FileReader(caminho + "senha.txt"));
            while (in.ready()) {
                senha += in.readLine();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "TesteCertificado: Erro ao Ler Arquivo Senha");
            System.exit(0);
        }


        AssinarXMLsCertfificadoA1 a = new AssinarXMLsCertfificadoA1();
        a.loadCertificates(certificado, senha);
    }

    public void CarregaJtxa(String str, Color cor) throws BadLocationException{
        if (cor == null){
            cor = Color.BLACK;
        }
        StyleConstants.setForeground(style, cor);
        doc.insertString(doc.getLength(), DtSystem.getDate()+" "+ str + "\n", style);
        jtxa.setCaretPosition(jtxa.getDocument().getLength());
    }
    
    private void StartServicos() throws Exception{
        TesteCertificado();
        mnStart.setEnabled(false);
        mnStop.setEnabled(true);
        NfeStatusServico = new NfeStatusServico(this);
        NfeManifesto = new NfeManifesto(this);
        NfeInutilizacao = new NfeInutilizacao(this);
        NfeConsulta = new NfeConsulta(this);
        NfeRecepcao = new NfeRecepcao(this);
        NfeDpec = new NfeDpec(this);
        NfeEventCanc = new NfeEventCanc(this);
        NfeCCe = new NfeCCe(this);
        impXML = new ImportXMLProprio(this);
        NfeRetRecepcao = new NfeRetRecepcao(this);

        NfeStatusServico.StartTimer();
        NfeManifesto.StartTimer();
        NfeInutilizacao.StartTimer();
        NfeConsulta.StartTimer();
        NfeRecepcao.StartTimer();
        NfeDpec.StartTimer();
        NfeEventCanc.StartTimer();
        NfeCCe.StartTimer();
        NfeRetRecepcao.StartTimer();
        impXML.StartTimer();
    }
    private void StopServicos() throws Exception{
        mnStart.setEnabled(true);
        mnStop.setEnabled(false);
        NfeStatusServico.StopTimer();
        NfeManifesto.StopTimer();
        NfeInutilizacao.StopTimer();
        NfeConsulta.StopTimer();
        NfeRecepcao.StopTimer();
        NfeDpec.StopTimer();
        NfeEventCanc.StopTimer();
        NfeCCe.StopTimer();
        impXML.StopTimer();
        NfeRetRecepcao.StopTimer();
    }
    public void exibirPrograma(JInternalFrame tela) {
    Point loc = desktopPane.getLocation();
    int locX = loc.x;
    int locY = loc.y - 40;
    tela.setLocation((locX + (this.getWidth()  / 2)) - (tela.getWidth()  / 2),     //-- Horizontal ----
                     (locY + (this.getHeight() / 2)) - (tela.getHeight() / 2));    //-- Vertical ------
    desktopPane.add(tela);
    tela.show();
  }

private void disparaProcesso() throws BadLocationException {

    timer1 = new Timer();
    timer2 = new Timer();
		
    java.util.Date agora = Calendar.getInstance().getTime();
    Calendar c = Calendar.getInstance();
    c.set(Calendar.HOUR,12);
    c.set(Calendar.MINUTE,00);
    c.set(Calendar.SECOND,0);
    c.set(Calendar.AM_PM,Calendar.AM);
    java.util.Date horaAgendada1 = c.getTime();
    CarregaJtxa("Hora Agendada1 ConsultaPedidos " + c.getTime(), Color.red);

    c = Calendar.getInstance();
    c.set(Calendar.HOUR,6);
    c.set(Calendar.MINUTE,0);
    c.set(Calendar.SECOND,0);
    c.set(Calendar.AM_PM,Calendar.PM);

    java.util.Date horaAgendada2 = c.getTime();
    CarregaJtxa("Hora Agendada2 ConsultaPedidos " + c.getTime(), Color.red);

    if (!agora.after(horaAgendada1)){
            long delay = horaAgendada1.getTime() - agora.getTime();
            long period = 86400000;
            timer1.scheduleAtFixedRate(new TimerTask(){
                    public void run(){
                        try {
                            ConsultaPedidos();
                        } catch (BadLocationException ex) {
                            Logger.getLogger(Painel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
            }, delay, period);
    }
    if (!agora.after(horaAgendada2)){
            long delay = horaAgendada2.getTime() - agora.getTime();
            long period = 86400000;
            timer2.scheduleAtFixedRate(new TimerTask(){
                    public void run(){
                        try {
                            ConsultaPedidos();
                        } catch (BadLocationException ex) {
                            Logger.getLogger(Painel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
            }, delay, period);
    }
 }
private void ConsultaPedidos() throws BadLocationException{
    CarregaJtxa("Executou Consulta Pedidos", Color.red);
}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desktopPane = new javax.swing.JDesktopPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtxa = new javax.swing.JTextPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mnStart = new javax.swing.JMenuItem();
        mnStop = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        nmcacerts = new javax.swing.JMenuItem();
        mnconfigImp = new javax.swing.JMenuItem();
        nmconfigwebservice = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jtxa.setEditable(false);
        jScrollPane2.setViewportView(jtxa);

        jScrollPane2.setBounds(0, 10, 790, 510);
        desktopPane.add(jScrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jMenuBar1.setMaximumSize(new java.awt.Dimension(800, 30));
        jMenuBar1.setPreferredSize(new java.awt.Dimension(800, 30));

        jMenu1.setMnemonic('s');
        jMenu1.setText("Sistema");

        mnStart.setText("Start Serviços");
        mnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnStartActionPerformed(evt);
            }
        });
        jMenu1.add(mnStart);

        mnStop.setText("Stop Serviços");
        mnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnStopActionPerformed(evt);
            }
        });
        jMenu1.add(mnStop);

        jMenuItem3.setText("Sair");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setMnemonic('f');
        jMenu2.setText("Funções");

        nmcacerts.setText("Gerar Cacerts");
        nmcacerts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nmcacertsActionPerformed(evt);
            }
        });
        jMenu2.add(nmcacerts);

        mnconfigImp.setText("Config. Impressora");
        mnconfigImp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnconfigImpActionPerformed(evt);
            }
        });
        jMenu2.add(mnconfigImp);

        nmconfigwebservice.setText("Config. WebService");
        nmconfigwebservice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nmconfigwebserviceActionPerformed(evt);
            }
        });
        jMenu2.add(nmconfigwebservice);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 790, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-800)/2, (screenSize.height-600)/2, 800, 600);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void mnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnStartActionPerformed
        try {
            StartServicos();
        } catch (Exception ex) {
            Logger.getLogger(Painel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_mnStartActionPerformed

    private void mnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnStopActionPerformed
        try {
            StopServicos();
        } catch (Exception ex) {
            Logger.getLogger(Painel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mnStopActionPerformed

    private void nmcacertsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nmcacertsActionPerformed
        // TODO add your handling code here:
        if(!mnStart.isEnabled()){
            JOptionPane.showMessageDialog(this, "Pare os Serviços antes de Gerar o Arquivo");
            return;
        }
        try {
            NFeBuildAllCacerts bc = new NFeBuildAllCacerts(this);
            bc.GeraCacerts();
            JOptionPane.showMessageDialog(this, "Arquivo Gerado com sucesso");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar Arquivo Cacerts: " + e.toString());
        }
    }//GEN-LAST:event_nmcacertsActionPerformed

    private void mnconfigImpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnconfigImpActionPerformed
        // TODO add your handling code here:
        exibirPrograma(new ConfigImp());
    }//GEN-LAST:event_mnconfigImpActionPerformed

    private void nmconfigwebserviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nmconfigwebserviceActionPerformed
        // TODO add your handling code here:
        exibirPrograma(new ConfigWebService());
    }//GEN-LAST:event_nmconfigwebserviceActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Painel().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jtxa;
    private javax.swing.JMenuItem mnStart;
    private javax.swing.JMenuItem mnStop;
    private javax.swing.JMenuItem mnconfigImp;
    private javax.swing.JMenuItem nmcacerts;
    private javax.swing.JMenuItem nmconfigwebservice;
    // End of variables declaration//GEN-END:variables

}
