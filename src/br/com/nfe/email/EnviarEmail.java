package br.com.nfe.email;


import br.com.nfe.dao.par.PAR_ParceiroDAO;
import java.awt.Color;
import java.util.HashMap;

public class EnviarEmail {
    
	String msg = "";
        br.com.nfe.gui.Painel main = null;


    public static void main(String[] args) throws Exception {
//        EnviarEmail e = new EnviarEmail();
//        e.Enviar();
    }

    public EnviarEmail(br.com.nfe.gui.Painel main) {
        this.main = main;
    }



  public void Enviar(String xml_nfe, byte[] pdf, String idnfe, String tipo) throws Exception{

      PAR_ParceiroDAO parceiro_dao = new PAR_ParceiroDAO();
      SendMail mail = new SendMail();
      String p[] = null;
      String titulo = "Nota Fiscal Eletronica - Cooperativa Agrovale";
      
        
      p = parceiro_dao.pesquisa_email(idnfe).toString().split(";");
      
      SetMsg(tipo);
      
      for (int i = 0; i < p.length; i++) {
        if(p[i].trim().length() > 0){  
            main.CarregaJtxa("Email para:" + p[i].trim() + " Idnfe: " + idnfe,new Color(0, 100, 0));
            mail.sendMail("agrovale@agrovale.com.br", p[i].trim(), titulo, msg, xml_nfe, pdf, idnfe);
        }
      }


  }
  private void SetMsg(String tipo){
      msg = "*** Esse é um e-mail automático. Não é necessário respondê-lo ***\n";
      if (tipo.contains("env")){
           msg +=" A Nota Fiscal Eletrônica (NF-e) foi gerada com sucesso. Neste e-mail você esta recebendo a representação simplificada da Nota Fiscal Eletrônica, chamada DANFE (Documento Auxiliar da Nota Fiscal Eletrônica).\n";
      }
      if (tipo.contains("canc")){
           msg +=" A Nota Fiscal Eletrônica (NF-e) foi Cancelada. Neste e-mail você esta recebendo o XML do protocolo de Cancelamento.\n";          
      }
      msg +=" Para consultar mais informações sobre essa nota fiscal. Acesse o Portal da Nota Fiscal Eletrônica do Ministério da Fazenda do Estado de Goiás em http://nfe.sefaz.go.gov.br/\n";      
      msg +=" Atenciosamente,\n Cooperativa Agrovale";
      msg +=" (64) 3615-9000 \n www.agrovale.com.br";
  }
}
