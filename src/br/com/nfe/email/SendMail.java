
package br.com.nfe.email;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;  
import javax.mail.Session;  
import javax.mail.Transport;  
import javax.mail.internet.InternetAddress;  
import javax.mail.internet.MimeMessage;   
import javax.mail.Authenticator;  
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;   
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.om.ds.InputStreamDataSource;
  
public class SendMail {  
      
    private String mailSMTPServer;  
    private String mailSMTPServerPort;  
      
    /* 
     * quando instanciar um Objeto ja sera atribuido o servidor SMTP do GMAIL  
     * e a porta usada por ele 
     */  
    SendMail() { //Para o GMAIL   
//        mailSMTPServer = "smtp.gmail.com";  
//        mailSMTPServerPort = "465";
        mailSMTPServer = "mail.agrovale.com.br";  
        mailSMTPServerPort = "25";
        
    }  
    /* 
     * caso queira mudar o servidor e a porta, so enviar para o contrutor 
     * os valor como string 
     */  
    SendMail(String mailSMTPServer, String mailSMTPServerPort) { //Para outro Servidor  
        this.mailSMTPServer = mailSMTPServer;  
        this.mailSMTPServerPort = mailSMTPServerPort;  
    }  
      
    public void sendMail(String from, String to, String subject, String message, String xml_nfe, byte[] pdf, String idnfe) {  
          
        Properties props = new Properties();  
  
        // quem estiver utilizando um SERVIDOR PROXY descomente essa parte e atribua as propriedades do SERVIDOR PROXY utilizado  
        props.setProperty("proxySet","true"); 
        props.setProperty("socksProxyHost","192.168.1.5"); // IP do Servidor Proxy 
        props.setProperty("socksProxyPort","3128");  // Porta do servidor Proxy 
                  
  
        props.put("mail.transport.protocol", "smtp"); //define protocolo de envio como SMTP  
//        props.put("mail.smtp.starttls.enable","true");   
        props.put("mail.smtp.host", mailSMTPServer); //server SMTP do GMAIL  
        props.put("mail.smtp.auth", "true"); //ativa autenticacao  
        props.put("mail.smtp.user", from); //usuario ou seja, a conta que esta enviando o email (tem que ser do GMAIL)  
//        props.put("mail.debug", "true");  
        props.put("mail.smtp.port", mailSMTPServerPort); //porta  
//        props.put("mail.smtp.socketFactory.port", mailSMTPServerPort); //mesma porta para o socket  
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");  
//        props.put("mail.smtp.socketFactory.fallback", "false");  
          
        //Cria um autenticador que sera usado a seguir  
        SimpleAuth auth = null;  
        auth = new SimpleAuth (from,"agro9090");  
          
        //Session - objeto que ira realizar a conexão com o servidor  
        /*Como há necessidade de autenticação é criada uma autenticacao que 
         * é responsavel por solicitar e retornar o usuário e senha para  
         * autenticação */  
        Session session = Session.getDefaultInstance(props, auth);  
//        session.setDebug(true); //Habilita o LOG das ações executadas durante o envio do email  
  

            // cria a mensagem
            MimeMessage msg = new MimeMessage(session);
        
        try {  
            

            //Setando o destinatário  
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));  
            //Setando a origem do email  
            msg.setFrom(new InternetAddress(from));  
            //Setando o assunto  
            msg.setSubject(subject);  
            //Setando o conteúdo/corpo do email  
//            msg.setContent(message,"multipart/form-data");
            msg.setSentDate(new Date());

            // cria a primeira parte da mensagem
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(message);

          


            // cria a Multipart
            MimeMultipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            
            // cria a segunda parte da mensage
            MimeBodyPart xmlBodyPart = null;
            MimeBodyPart pdfBodyPart = null; 
            
            if(xml_nfe != null){       
                xmlBodyPart = new MimeBodyPart();
                xmlBodyPart.setDataHandler(new DataHandler(xml_nfe.getBytes(),"application/octet-stream"));
                xmlBodyPart.setFileName("Nfe"+idnfe+".xml");
                
                mp.addBodyPart(xmlBodyPart);
            }

            if (pdf != null){
                DataSource dataSource = new ByteArrayDataSource(pdf, "application/pdf");
                pdfBodyPart = new MimeBodyPart();
                pdfBodyPart.setDataHandler(new DataHandler(dataSource));
                pdfBodyPart.setFileName("Nfe"+idnfe+".pdf");
                
                mp.addBodyPart(pdfBodyPart);
            }
            
            msg.setContent(mp);

  
        } catch (Exception e) {  
            System.out.println(">> Erro: Completar Mensagem");  
            e.printStackTrace();  
        }  
          
        //Objeto encarregado de enviar os dados para o email  
        Transport tr;  
        try {  
            tr = session.getTransport("smtp"); //define smtp para transporte  
            /* 
             *  1 - define o servidor smtp 
             *  2 - seu nome de usuario do gmail 
             *  3 - sua senha do gmail 
             */  
            tr.connect(mailSMTPServer, from, "agro9090");  
            msg.saveChanges(); // don't forget this  
            //envio da mensagem  
            tr.sendMessage(msg, msg.getAllRecipients());  
            tr.close();  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            System.out.println(">> Erro: Envio Mensagem");  
            e.printStackTrace();  
        }  
  
    }  
}  
  
//clase que retorna uma autenticacao para ser enviada e verificada pelo servidor smtp  
class SimpleAuth extends Authenticator {  
    public String username = null;  
    public String password = null;  
  
  
    public SimpleAuth(String user, String pwd) {  
        username = user;  
        password = pwd;  
    }  
  
    protected PasswordAuthentication getPasswordAuthentication() {  
        return new PasswordAuthentication (username,password);  
    }  
}