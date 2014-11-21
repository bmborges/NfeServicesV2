/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.nfe.dao.vnd;

import br.com.nfe.services.NfeRecepcao;
import br.com.nfe.bean.vnd.VND_NfpedidoBean;
import br.com.nfe.services.ControleImpressao;
import br.com.nfe.util.Database;
import br.com.nfe.util.DtSystem;
import br.inf.portalfiscal.nfe.env.TEnderEmi;
import br.inf.portalfiscal.nfe.env.TEndereco;
import br.inf.portalfiscal.nfe.env.TNFe;
import br.inf.portalfiscal.nfe.env.TUf;
import br.inf.portalfiscal.nfe.env.TUfEmi;
import br.inf.portalfiscal.nfe.env.TVeiculo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author supervisor
 */
public class VND_nfpedidoDAO {
    
      public String dadosadicionais = "";
      public String strColunas;
      java.sql.Connection conn = null;

      public VND_nfpedidoDAO() throws Exception{
         conn = Database.getConnection();

         if (conn == null) {
            throw new Exception(getClass().getName() + ": null connection passed.");
         }
            this.conn = conn;
         }
private void definirColunas() {
    strColunas = "cdpedido,"
            + "cdnf,"
            + "data";
  }
public String[] pesquisa_nfpedido() throws SQLException{
    String qry = "select idnfe, ped.cdpedido from vnd_pedvenda ped";
    qry += " inner join vnd_nfpedido nfp using (cdpedido)";
    qry += " where ped.idnfe is not null  and (nfp.flag::integer >= 1 and nfp.flag::integer < 9)"
            + " order by flag ,nfp.id_nfpedido limit 1 ";
  
    
    PreparedStatement stmt = conn.prepareStatement(qry);


    ResultSet rs = stmt.executeQuery();

    //System.out.println(">>>Select....: " + stmt.toString());

    String retorno[] = new String[2];
    retorno[0]= "";

    if (rs.next()) {
       retorno[0] = rs.getString("idnfe");
       retorno[1] = rs.getString("cdpedido");
    }
    rs.close();
    stmt.close();

    //System.out.println(">>>Select....: " + retorno[1]);
    return retorno;

}
public String pesquisa_protocolo(int cdpedido) throws SQLException{
    String qry = "select protocolo from vnd_nfpedido ";
    qry += " where cdpedido = ?";
    
    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setInt(1, cdpedido);
    
    ResultSet rs = stmt.executeQuery();

    //System.out.println(">>>Select....: " + stmt.toString());

    String retorno = "";
    
    if (rs.next()) {
       retorno = rs.getString("protocolo");
    }
    rs.close();
    stmt.close();

    return retorno;

}

public void atualiza_nfpedido() throws SQLException{
    String qry = "update vnd_nfpedido set flag = '1'";
    qry += " where nfp.flag::integer = 9 and data = current_date ";


    PreparedStatement stmt = conn.prepareStatement(qry);


   stmt.executeUpdate();
   stmt.close();
  
    
}
public int idcentrocusto_operador(int cdpedido) throws SQLException{
   int retorno = 0;
   
   String qry = "select idcentrocusto from vnd_nfpedido n"
           + "  inner join adm_operador o on (o.nickname = n.nmoperador)"
           + "  where cdpedido = ? ";

   PreparedStatement stmt = conn.prepareStatement(qry);
   stmt.setInt(1, cdpedido);
   
   ResultSet rs = stmt.executeQuery();
   if(rs.next()){
       retorno = rs.getInt("idcentrocusto");
   }
   
   stmt.close();
   rs.close();
  
   return retorno; 
}
public HashMap pesquisa_nfpedido_inut() throws SQLException, Exception{
    
   
    String qry = "select cdpedido, cdnf, iduf, e.cnpj,"
            + " substring(extract('year' from coalesce(nf.data,current_date))::text,3,2) as ano  from vnd_nfpedido nf"
            + "	inner join vnd_pedvenda using (cdpedido)"
            + "	inner join cdc_estabelecim e using (idestabelecimen)"
            + "	inner join adm_estado es on (e.uf = es.uf )"
            + " where (status_nfe is null or status_nfe in (0)) and"
            + " situacaonf = 'I' order by id_nfpedido limit 1";


    PreparedStatement stmt = conn.prepareStatement(qry);


    ResultSet rs = stmt.executeQuery();
    HashMap map = new HashMap();

    if (rs.next()) {
        map.put("cdpedido", rs.getInt(1));
        map.put("cdnf", rs.getString(2));
        map.put("iduf", rs.getInt(3));
        map.put("cnpj", rs.getString(4));
        map.put("ano", rs.getString(5));
    } else {
        map.put("cdpedido", 0);
    }
    rs.close();
    stmt.close();

    //System.out.println(">>>Select....: " + retorno[1]);
    return map;

}
public HashMap pesquisa_nfpedido_inut(int cdpedido) throws SQLException, Exception{
    
   
    String qry = "select cdpedido, cdnf, iduf, e.cnpj,"
            + " substring(extract('year' from coalesce(nf.data,current_date))::text,3,2) as ano  from vnd_nfpedido nf"
            + "	inner join vnd_pedvenda using (cdpedido)"
            + "	inner join cdc_estabelecim e using (idestabelecimen)"
            + "	inner join adm_estado es on (e.uf = es.uf )"
            + " where cdpedido = ?";


    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setInt(1, cdpedido);


    ResultSet rs = stmt.executeQuery();
    
    
    HashMap map = new HashMap();

    if (rs.next()) {
        map.put("cdpedido", rs.getInt(1));
        map.put("cdnf", rs.getString(2));
        map.put("iduf", rs.getInt(3));
        map.put("cnpj", rs.getString(4));
        map.put("ano", rs.getString(5));
    } else {
        map.put("cdpedido", 0);
    }
    rs.close();
    stmt.close();

    //System.out.println(">>>Select....: " + retorno[1]);
    return map;

}
public HashMap pesquisa_nfpedido_dpec() throws SQLException, Exception{
     String replace = "";
   
    String qry = "select cdpedido, iduf, e.cnpj, e.inscricaoestad, idnfe, cnpj_pedido, pv.uf, "
            + " trim(to_char(totalpedido,'999999999999990.00')) as totalpedido,"
            + " trim(to_char(totalicms,'999999999999990.00')) as totalicms,"
            + " trim(to_char(totalicmssubst,'999999999999990.00')) as totalicmssubst"
            + "	from vnd_nfpedido nf"
            + " inner join vnd_pedvenda pv using (cdpedido)"
            + " inner join cdc_estabelecim e using (idestabelecimen)"
            + " inner join adm_estado es on (e.uf = es.uf )"
            //+ " where length(idnfe) = 44 and idnfe is not null "
            + " where tpemis in (4) and nregdpec is null and situacaonf = 'E'"
            + " order by date(dt_pedido) desc, cdpedido limit 1";


    PreparedStatement stmt = conn.prepareStatement(qry);


    ResultSet rs = stmt.executeQuery();
    HashMap map = new HashMap();

    if (rs.next()) {

        
        
        map.put("cdpedido", rs.getInt(1));
        map.put("iduf", rs.getString(2));
        
        replace = rs.getString(3);
        replace = replace.replace(".", "");
        replace = replace.replace("/", "");
        replace = replace.replace("-", "");
        
        map.put("cnpj", replace);
        
        replace = rs.getString(4);
        replace = replace.replace(".", "");
        replace = replace.replace("/", "");
        replace = replace.replace("-", "");
        
        map.put("inscricaoestad", replace);
        map.put("idnfe", rs.getString(5));
        
        replace = rs.getString(6);
        replace = replace.replace(".", "");
        replace = replace.replace("/", "");
        replace = replace.replace("-", "");
        
        map.put("cnpj_pedido", replace);
        map.put("uf", rs.getString(7));
        map.put("totalpedido", rs.getString(8));
        map.put("totalicms", rs.getString(9));
        map.put("totalicmssubst", rs.getString(10));

        
    } else {
        map.put("cdpedido", 0);
    }
    
//    System.out.println(">>>Select....: " + stmt);
    
    rs.close();
    stmt.close();

    return map;

}
public void Update_nfpedido_inut(VND_NfpedidoBean bean) throws SQLException{
   String qry = "update vnd_nfpedido set";

   if (bean.getStatus_nfe() != null){
       qry += " status_nfe = ?,";
   }
   if (bean.getObs_nfe() != null){
       qry += " obs_nfe = ?,";
   }
   if (bean.getFlag() != null){
       qry += " flag = ?,";       
   }
   if (bean.getProtocolo() != null){
       qry += " protocolo = ?,";
   }
    qry += " where cdpedido = ?::integer ";
    qry += " and cdnf::integer = ?::integer";       

    
    qry = qry.replace(", where", " where");
    
   
   PreparedStatement stmt = conn.prepareStatement(qry);

   int i = 0;
   if (bean.getStatus_nfe() != null){
       i++;
       stmt.setInt(i, bean.getStatus_nfe());
   }

   if (bean.getObs_nfe() != null){
       i++;
       stmt.setString(i, bean.getObs_nfe());
   }
   if (bean.getFlag() != null){
       i++;
       stmt.setString(i, bean.getFlag());
   }
   if (bean.getProtocolo() != null){
       i++;
       stmt.setString(i, bean.getProtocolo());
   }
   i++;
   stmt.setInt(i, bean.getCdpedido());
   i++;
   stmt.setString(i, bean.getCdnf());
   

   
   SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
   df.format(new Date(System.currentTimeMillis()));
   
   String msg = df.getCalendar().getTime() + " >>>InutilizacaoNfe....:";
//   if (StatusServico.cStat != 107){
//       msg = "Cont" + msg;
//   }
           
           
   System.out.println(msg + stmt);
   
   int retorno = stmt.executeUpdate();
   
   stmt.close();

}
//public void Update_nfpedido(VND_NfpedidoBean bean) throws SQLException{
//   String qry = "update vnd_nfpedido set status_nfe = ?, xml_nfe = ? , protocolo = ?, situacaonf = ? where cdpedido = ?";
//   
//   PreparedStatement stmt = conn.prepareStatement(qry);
//
//   stmt.setInt(1, bean.getStatus_nfe());
//   stmt.setString(2, bean.getXml_nfe());
//   stmt.setString(3, bean.getProtocolo());
//   stmt.setString(4, bean.getSituacaonf());
//   stmt.setInt(5, bean.getCdpedido());
//   
//   
//  
//   SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
//   df.format(new Date(System.currentTimeMillis()));
//   
//   String msg = df.getCalendar().getTime() + " >>>RetRecepcaoNfe....:";
//           
//   System.err.println(msg +"Pedido: "+ bean.getCdpedido());
//   
//   int retorno = stmt.executeUpdate();
//   
//   stmt.close();
//
//}
public HashMap pesquisa_nfpedido_canc() throws SQLException, Exception{
    
    String replace = "";
    String qry = "select cdpedido, cdnf, iduf, idnfe, coalesce(protocolo,split_part(obs_nfe,';',6)) as protocolo,"
            + "   e.cnpj"
            + "   from vnd_nfpedido nf"
            + "   inner join vnd_pedvenda using (cdpedido)"
            + "   inner join cdc_estabelecim e using (idestabelecimen)"
            + "   inner join adm_estado es on (e.uf = es.uf )"
            + "   where situacaonf = 'C' and status_nfe in (100)"
            + "   order by dt_pedido desc, id_nfpedido limit 1";


    PreparedStatement stmt = conn.prepareStatement(qry);

    ResultSet rs = stmt.executeQuery();
    HashMap map = new HashMap();

    if (rs.next()) {
        map.put("cdpedido", rs.getInt(1));
        map.put("cdnf", rs.getString(2));
        map.put("iduf", rs.getInt(3));
        map.put("idnfe", rs.getString(4));
        map.put("protocolo", rs.getString(5));
        
        replace = rs.getString(6);
        replace = replace.replace(".", "");
        replace = replace.replace("/", "");
        replace = replace.replace("-", "");
        
        map.put("cnpj", replace);
    } else {
        map.put("cdpedido", 0);
    }
    rs.close();
    stmt.close();

    //System.out.println(">>>Select....: " + retorno[1]);
    return map;

}
public HashMap pesquisa_nfpedido_canc_cdpedido(int cdpedido) throws SQLException, Exception{
    
    String replace = "";
    String qry = "select cdpedido, cdnf, iduf, idnfe, coalesce(protocolo,split_part(obs_nfe,';',6)) as protocolo,"
            + "   e.cnpj"
            + "   from vnd_nfpedido nf"
            + "   inner join vnd_pedvenda using (cdpedido)"
            + "   inner join cdc_estabelecim e using (idestabelecimen)"
            + "   inner join adm_estado es on (e.uf = es.uf )"
            + "   where cdpedido = ?"
            + "   order by dt_pedido desc, id_nfpedido limit 1";


    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setInt(1, cdpedido);
    ResultSet rs = stmt.executeQuery();
    HashMap map = new HashMap();

    if (rs.next()) {
        map.put("cdpedido", rs.getInt(1));
        map.put("cdnf", rs.getString(2));
        map.put("iduf", rs.getInt(3));
        map.put("idnfe", rs.getString(4));
        map.put("protocolo", rs.getString(5));
        
        replace = rs.getString(6);
        replace = replace.replace(".", "");
        replace = replace.replace("/", "");
        replace = replace.replace("-", "");
        
        map.put("cnpj", replace);
    } else {
        map.put("cdpedido", 0);
    }
    rs.close();
    stmt.close();

    //System.out.println(">>>Select....: " + retorno[1]);
    return map;

}
public void Update_nfpedido_canc(VND_NfpedidoBean bean) throws SQLException{
   String qry = "update vnd_nfpedido set ";

   
   if(bean.getStatus_nfe() != null){
       qry += " status_nfe = ?,";
   }
   if(bean.getXml_canc() != null){
       qry += " xml_canc = ?,";
   }
   if(bean.getSituacaonf() != null){
       qry += " situacaonf = ?,";
   }
   if(bean.getObs_canc_nfe() != null){
       qry += " obs_canc_nfe = ?,";
   } else {
       qry += " obs_canc_nfe = null,";
   }
   if(bean.getFlag() != null){
       qry += " flag = ?,";
   } else {
       qry += " flag = 0,";
   }
   
    qry += " where cdpedido = ?";
    qry = qry.replace(", where", " where");
    
   PreparedStatement stmt = conn.prepareStatement(qry);

   int i = 0;
   if(bean.getStatus_nfe() != null){
      i++;
      stmt.setInt(i, bean.getStatus_nfe());
   }
   if(bean.getXml_canc() != null){
      i++; 
      stmt.setString(i, bean.getXml_canc());
   }
   if(bean.getSituacaonf() != null){
      i++;
      stmt.setString(i, bean.getSituacaonf());
   }
   if(bean.getObs_canc_nfe() != null){
      i++;
      stmt.setString(i, bean.getObs_canc_nfe());
   }
   if(bean.getFlag() != null){
      i++;
      stmt.setString(i, bean.getFlag());
   }
   
   
   i++;
   stmt.setInt(i, bean.getCdpedido());

   SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
   df.format(new Date(System.currentTimeMillis()));
   
   String msg = df.getCalendar().getTime() + " >>>CancelamentoNfe....:";
           
   System.out.println(msg + stmt);
   
   int retorno = stmt.executeUpdate();
   
   stmt.close();

}
public HashMap pesquisa_nfpedido_env() throws SQLException, Exception{
    
   
    String qry = "select cdpedido, iduf, idnfe"
            + " from vnd_nfpedido nf"
            + " inner join vnd_pedvenda using (cdpedido)"
            + " inner join cdc_estabelecim e using (idestabelecimen)"
            + " inner join adm_estado es on (e.uf = es.uf )"
            + " where situacaonf in ('E') and status_nfe in (0) and obs_nfe is null"
            + " order by date(nf.data), nf.id_nfpedido, cdpedido desc, situacaonf desc limit 1";

    PreparedStatement stmt = conn.prepareStatement(qry);

    ResultSet rs = stmt.executeQuery();
    HashMap map = new HashMap();

    if (rs.next()) {
        map.put("cdpedido", rs.getInt(1));
        map.put("iduf", rs.getInt(2));
        map.put("idnfe", rs.getString(3));
        
        qry = "update vnd_nfpedido set status_nfe = null where cdpedido = ?";
        stmt = conn.prepareStatement(qry);
        stmt.setInt(1, rs.getInt(1));
        stmt.executeUpdate();
        
        DtSystem.getDate();
//        System.out.println(">>>RecepcaoNfe....:" + map.get("cdpedido").toString());
        
    } else {
        map.put("cdpedido", 0);
    }
    rs.close();
    stmt.close();
    
   
    
    
    return map;

}
public HashMap pesquisa_nfpedido_envDEPC() throws SQLException, Exception{
    
   
    String qry = "select cdpedido, iduf, idnfe"
            + " from vnd_nfpedido nf"
            + " inner join vnd_pedvenda using (cdpedido)"
            + " inner join cdc_estabelecim e using (idestabelecimen)"
            + " inner join adm_estado es on (e.uf = es.uf )"
            + " where situacaonf in ('E') and status_nfe in (124)"
            + " order by date(dt_pedido) desc, cdpedido desc, situacaonf desc limit 1";

    PreparedStatement stmt = conn.prepareStatement(qry);

    ResultSet rs = stmt.executeQuery();
    HashMap map = new HashMap();

    if (rs.next()) {
        map.put("cdpedido", rs.getInt(1));
        map.put("iduf", rs.getInt(2));
        map.put("idnfe", rs.getString(3));
        
//        qry = "update vnd_nfpedido set status_nfe = null where cdpedido = ?";
//        stmt = conn.prepareStatement(qry);
//        stmt.setInt(1, rs.getInt(1));
//        stmt.executeUpdate();
        
        DtSystem.getDate();
//        System.out.println(">>>RecepcaoNfe....:" + map.get("cdpedido").toString());
        
    } else {
        map.put("cdpedido", 0);
    }
    rs.close();
    stmt.close();
    
   
    
    
    return map;

}

public HashMap pesquisa_nfpedido_cdpedido(int cdpedido) throws SQLException, Exception{
    
   
    String qry = "select cdpedido, iduf, idnfe"
            + " from vnd_nfpedido nf"
            + " inner join vnd_pedvenda using (cdpedido)"
            + " inner join cdc_estabelecim e using (idestabelecimen)"
            + " inner join adm_estado es on (e.uf = es.uf )"
            + " where cdpedido = ?";

    PreparedStatement stmt = conn.prepareStatement(qry);

    stmt.setInt(1, cdpedido);
    
    ResultSet rs = stmt.executeQuery();
    HashMap map = new HashMap();

    if (rs.next()) {
        map.put("cdpedido", rs.getInt(1));
        map.put("iduf", rs.getInt(2));
        map.put("idnfe", rs.getString(3));
    } else {
        map.put("cdpedido", 0);
    }
    rs.close();
    stmt.close();
    
    return map;

}
public HashMap pesquisa_nfpedido_ret() throws SQLException, Exception{
    
   
    String qry = "select cdpedido, iduf, idnfe, obs_nfe, xml_nfe, flag"
            + " from vnd_nfpedido nf"
            + " inner join vnd_pedvenda using (cdpedido)"
            + " inner join cdc_estabelecim e using (idestabelecimen)"
            + " inner join adm_estado es on (e.uf = es.uf )"
            + " where situacaonf = 'E' and status_nfe in (103,104,105)"
            + " and obs_nfe is not null and flag::integer < 9"
            + " order by dt_pedido desc, status_nfe asc, flag::integer, cdpedido limit 1";

    PreparedStatement stmt = conn.prepareStatement(qry);

    ResultSet rs = stmt.executeQuery();
    HashMap map = new HashMap();

    if (rs.next()) {
        map.put("cdpedido", rs.getInt(1));
        map.put("iduf", rs.getInt(2));
        map.put("idnfe", rs.getString(3));
        map.put("obs_nfe", rs.getString(4));
        map.put("xml_nfe", rs.getString(5));
        map.put("flag", rs.getString(6));
        
//        DtSystem.getDate();
//        System.out.println(">>>RetRecepcaoNfe....:" + map.get("cdpedido").toString());
        
    } else {
        map.put("cdpedido", 0);
    }
    rs.close();
    stmt.close();
    
    //System.out.println(">>>Select....: " + retorno[1]);
    return map;

}
public HashMap pesquisa_nfpedido_ret_cdpedido(int cdpedido) throws SQLException, Exception{
    
   
    String qry = "select cdpedido, iduf, idnfe, obs_nfe, xml_nfe"
            + " from vnd_nfpedido nf"
            + " inner join vnd_pedvenda using (cdpedido)"
            + " inner join cdc_estabelecim e using (idestabelecimen)"
            + " inner join adm_estado es on (e.uf = es.uf )"
            + " where situacaonf = 'E' and status_nfe in (103,104,105)"
            + " and obs_nfe is not null"
            + " and cdpedido = ?"
            + " order by dt_pedido desc, status_nfe asc";

    PreparedStatement stmt = conn.prepareStatement(qry);
    
    stmt.setInt(1, cdpedido);

    ResultSet rs = stmt.executeQuery();
    HashMap map = new HashMap();

    if (rs.next()) {
        map.put("cdpedido", rs.getInt(1));
        map.put("iduf", rs.getInt(2));
        map.put("idnfe", rs.getString(3));
        map.put("obs_nfe", rs.getString(4));
        map.put("xml_nfe", rs.getString(5));
    } else {
        map.put("cdpedido", 0);
    }
    rs.close();
    stmt.close();
    
    //System.out.println(">>>Select....: " + retorno[1]);
    return map;

}

public String select_obs_nfe_nfpedido(VND_NfpedidoBean bean) throws SQLException, Exception{
    
   
    String qry = "select obs_nfe"
            + " from vnd_nfpedido nf"
            + " where cdpedido = ?";

    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setInt(1, bean.getCdpedido());

    ResultSet rs = stmt.executeQuery();
    
    String retorno = "";
    if (rs.next()) {
        retorno = rs.getString(1);
    }
    rs.close();
    stmt.close();

    return retorno;

}
public void Update_nfpedido_env(VND_NfpedidoBean bean) throws SQLException{
    if (bean.getCdpedido() == null){
        System.err.println("Cdpedido Nulo Update_nfpedido_env");
    }
    String qry = "update vnd_nfpedido set ";
           
           if (bean.getStatus_nfe() != null){
              qry += "status_nfe = ?,";
           }

           if (bean.getObs_nfe() != null){
              qry += "obs_nfe = ?,";
           } else {
              qry += "obs_nfe = null,";
           }
           
           if (bean.getXml_nfe() != null){
              qry += "xml_nfe = ?,";
           }
                      
           if (bean.getSituacaonf() != null){
              qry += "situacaonf = ?,";
           }
           if (bean.getProtocolo() != null){
              qry += "protocolo = ?,";
           }
           if (bean.getTpemis() != null){
              qry += "tpemis = ?,";
           }
           if (bean.getNregdpec() != null){
              qry += "nregdpec = ?,";
           }
           if (bean.getDhregdpec() != null){
              qry += "dhregdpec = ?::timestamp,";
           }
           
           if (bean.getFlag() != null){
              qry += "flag = ?,";
           }
          

           qry += " where cdpedido = ?";

           qry = qry.replace(", where", " where");
           
   PreparedStatement stmt = conn.prepareStatement(qry);

   int i =1;

   if (bean.getStatus_nfe() != null){
      stmt.setInt(i, bean.getStatus_nfe());
      i++;
   }

   if (bean.getObs_nfe() != null){
      stmt.setString(i, bean.getObs_nfe());
      i++;
   }

   if (bean.getXml_nfe() != null){
      stmt.setString(i, bean.getXml_nfe());
      i++;
   }

   if (bean.getSituacaonf() != null){
      stmt.setString(i, bean.getSituacaonf());
      i++;
   }
   if (bean.getProtocolo() != null){
      stmt.setString(i, bean.getProtocolo());
      i++;
   }
   if (bean.getTpemis() != null){
      stmt.setInt(i, bean.getTpemis());
      i++;
   }
   if (bean.getNregdpec() != null){
      stmt.setString(i, bean.getNregdpec());
      i++; 
   }
   if (bean.getDhregdpec() != null){
      stmt.setString(i, bean.getDhregdpec());
      i++; 
   } 
   if (bean.getFlag() != null){
      stmt.setString(i, bean.getFlag());
      i++;
   }
   stmt.setInt(i, bean.getCdpedido());
           
//   System.out.println(" Pedido: " + bean.getCdpedido());
   
   int retorno = stmt.executeUpdate();
   
   stmt.close();

}

public void pesquisa_nfpedido_env_ide(int cdpedido) throws SQLException, Exception{
    
   
    String qry = "Select u.iduf as cUF, lpad(pv.cdpedido::text,8,'0') as cNF, nmoperacao as natOp, nr_nota_fiscal as nnf, "
            + " substring(coalesce(dt_nota,dt_pedido)::text,0,11) as demi, substring(coalesce(dt_saida,dt_nota)::text,0,11) as dsaient,"
            + " substring(coalesce(dt_saida,dt_nota)::text,12,8) as hsaient, coalesce(trim(pv.serie), '0') as serie, trim(pv.modelo) as modelo,"
            + " substring(idnfe, 44, 1) as cDv, idestabelecimen,"
            + " coalesce(finNFe, 1) as finNFe, o.e_s as tpNf, c.cdmunicipio as cMunFG,"
            + " coalesce(pv.nf_origem,0) as refNFe, tpemis, dhregdpec"
            + " from vnd_pedvenda pv"
            + " inner join vnd_nfpedido nfp on (nfp.cdpedido=pv.cdpedido)"
            + " inner join vnd_operacao o using (cdoperacao)"
            + " inner join cdc_estabelecim e using (idestabelecimen)"
            + " inner join adm_estado u on (u.uf=e.uf)"
            + " left join adm_cidade c on (c.nmcidade ilike ('%' || trim(e.cidade) || '%') )"
            + " where pv.cdpedido = ?";

    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setInt(1, cdpedido);
    ResultSet rs = stmt.executeQuery();
    HashMap map = new HashMap();
    TNFe.InfNFe.Ide ide = new TNFe.InfNFe.Ide();

    dadosadicionais += getTotaltributos(cdpedido);
    dadosadicionais += " Pedido: " + cdpedido;
    if (rs.next()) {
        
        ide.setCUF(rs.getString("cUF").trim()); // codigo estado
        ide.setCNF(rs.getString("CNF").trim()); 
        ide.setNatOp(rs.getString("natOp").trim()); // nome da operacao
        ide.setMod(rs.getString("modelo").trim());
        ide.setCDV(rs.getString("cDv").trim());
        ide.setSerie(rs.getString("serie").trim());
        ide.setNNF(rs.getString("nnf").trim());
        ide.setDEmi(rs.getString("demi").trim());
        ide.setDSaiEnt(rs.getString("dsaient").trim());
        ide.setHSaiEnt(rs.getString("hsaient").trim());
        ide.setTpNF(rs.getString("tpNf").trim());
        ide.setCMunFG(rs.getString("cMunFG").trim());
        ide.setFinNFe(rs.getString("finNFe").trim());
        ide.setTpEmis(rs.getString("tpemis").trim());
        if (rs.getString("dhregdpec") != null){
            ide.setDhCont(rs.getString("dhregdpec").replace(" ", "T"));
            ide.setXJust("Falha de Comunicacao");
        }
        
        // verifica nfref
        TNFe.InfNFe.Ide.NFref nfref = new TNFe.InfNFe.Ide.NFref();
        if (rs.getInt("refNFe") > 0){
            int refnfe = rs.getInt("refNFe");
            int idestabelecimen = rs.getInt("idestabelecimen");
            
            qry = "select pv.cdpedido, u.iduf as cUF, pv.cdpedido as cNF, nr_nota_fiscal as nnf,"
                 + " dt_nota as demi,  coalesce(pv.serie, '0') as serie, pv.modelo,"
                 + " substring(idnfe, 44, 1) as cDv, coalesce(idNfe,'0') as idNfe,  dt_nota, cnpj,"
                 + " e.uf as uf_e,  e.regime_trib"
                 + " from vnd_pedvenda pv"
                 + " inner join vnd_nfpedido nfp on (nfp.cdpedido=pv.cdpedido)"
                 + " inner join cdc_estabelecim e using (idestabelecimen)"
                 + " inner join adm_estado u on (u.uf=e.uf)"
                 + " inner join cdc_empresa em using (idestabelecimen)"
                 + " left join adm_cidade c on (c.nmcidade ilike ('%' || trim(e.cidade) || '%'))"
                 + " where pv.nr_nota_fiscal = ?"
                 + " and idestabelecimen = ? --and (pv.modelo = '55' or pv.modelo = '7')"
                 + " limit 1";
             stmt = conn.prepareStatement(qry);
             stmt.setInt(1, refnfe);
             stmt.setInt(2, idestabelecimen);
             
             rs = stmt.executeQuery();     
             if (rs.next()) {
                if(rs.getString("idNfe").length() > 1){ 
                    nfref.setRefNFe(rs.getString("idNfe"));
                    ide.getNFref().add(nfref);
                }
             }
             
             

        }

        
        // verifica indicador de pagamento
    
        qry = "select distinct avista"
             + " from fin_formapgt f"
             + " inner join fin_condpgto cp on (cp.cdcondpgto = f.cdcondpgto)"
             + " inner join vnd_pedvenda p on (p.cdseqpgto = f.cdseqpgto)"
             + " where cdpedido = ?";
        int L_nr = 0;
        int L_indPag = 0;
        stmt = conn.prepareStatement(qry);
        stmt.setInt(1, cdpedido);
        rs = stmt.executeQuery();
        while (rs.next()) {
            L_nr = L_nr + 1;
            if (rs.getString(1).equals("F")) {
                L_indPag = 1;
            } else {
                L_indPag = 0;
            }  
        }
        if (L_nr > 1 || L_nr == 0){
              L_indPag = 2;
        }
        
        ide.setIndPag(String.valueOf(L_indPag)); // tipo pagamento 0 – pagamento à vista;1 – pagamento à prazo;2 - outros.
        
     qry = "select codigo as TpImp from adm_param"
             + " where nmparam ilike 'VND-FORMATO IMPRESSAO DANFE'";
     stmt = conn.prepareStatement(qry);
     rs = stmt.executeQuery();
     if (rs.next()) {
         ide.setTpImp(rs.getString("TpImp"));
     } else {
         ide.setTpImp(rs.getString("1"));
     }
     
     qry = "select codigo as TpAmb from adm_param"
             + "  where nmparam ilike 'VND-AMBIENTE_NFe'";
     stmt = conn.prepareStatement(qry);
     rs = stmt.executeQuery();     
     if (rs.next()) {
         ide.setTpAmb(rs.getString("TpAmb"));
     } else {
         ide.setTpAmb("1");
     }
     
     qry = "select codigo as ProcEmi from adm_param"
             + "  where nmparam ilike 'VND-PROCESSO_EMISSAO_NFe'";
     stmt = conn.prepareStatement(qry);
     rs = stmt.executeQuery();     
     if (rs.next()) {
         ide.setProcEmi(rs.getString("ProcEmi"));
     } else {
         ide.setProcEmi(rs.getString("1"));
     }
     
//     qry = "select valorparam from adm_param"
//             + "  where nmparam ilike 'VND-VERSAO_PROCESSO_EMISSAO_NFe'";
//     stmt = conn.prepareStatement(qry);
//     if (rs.next()) {
//         
//     } else {
//         
//     }
     
     
     ide.setVerProc("Sistema Sief");
        
    } 
    
    rs.close();
    stmt.close();

    NfeRecepcao.infnfe.setIde(ide);

}
public void pesquisa_nfpedido_env_emit(int cdpedido) throws SQLException, Exception{
    
    String replace = "";
    String qry = "Select e.cnpj as CNPJ, substring(trim(e.nmestabelecimen),0,60) as xNome, substring(trim(e.nmloja),0,60) as xFant,"
            + " substring(trim(e.nmlogradouro),0,60) as xLgr, trim(e.numero) as nro, e.bairro as xBairro,"
            + " c.cdmunicipio as cMun, c.nmcidade as xMun, c.uf as UF,"
            + " e.cep as CEP, '1058' as cpais, 'Brasil' as xpais, trim(e.telefones) as fone, e.inscricaoestad as IE, inscricaomunic as IM,"
            + "	lpad(em.cd_cnae_gfip::text,7,'0') as CNAE,  coalesce(e.regime_trib,3) as CRT"
            + " from vnd_pedvenda pv"
            + " inner join vnd_nfpedido nfp on (nfp.cdpedido=pv.cdpedido)"
            + " inner join vnd_operacao o using (cdoperacao)"
            + " inner join cdc_estabelecim e using (idestabelecimen)"
            + " inner join cdc_empresa em using (idestabelecimen)"
            + " inner join adm_estado u on (u.uf=e.uf)"
            + " left join adm_cidade c on (c.nmcidade ilike ('%' || trim(e.cidade) || '%') )"
            + " where pv.cdpedido = ?";

    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setInt(1, cdpedido);
    ResultSet rs = stmt.executeQuery();
    
    TNFe.InfNFe.Emit emit = new TNFe.InfNFe.Emit();

    if (rs.next()) {
        replace = rs.getString("CNPJ");
        replace = replace.replace(".", "");
        replace = replace.replace("/", "");
        replace = replace.replace("-", "");
        
        
        if (replace.toString().length() > 11){
            emit.setCNPJ(replace);
        } else {
            emit.setCPF(replace);
        }
        
        emit.setXNome(rs.getString("xNome").trim());
        emit.setXFant(rs.getString("xFant").trim());

        TEnderEmi enderemi = new TEnderEmi();
        
        enderemi.setXLgr(rs.getString("xLgr").trim());
        enderemi.setNro(rs.getString("nro").trim());

        enderemi.setXBairro(rs.getString("xBairro").trim());
        enderemi.setCMun(rs.getString("cMun").trim());
        enderemi.setXMun(rs.getString("xMun").trim());
        enderemi.setUF(TUfEmi.fromValue(rs.getString("UF")));
        enderemi.setCEP(rs.getString("CEP").trim());
        enderemi.setCPais(rs.getString("cpais").trim());
        enderemi.setXPais(rs.getString("xpais").trim());
    
        replace = rs.getString("fone");
        replace = replace.replace("(", "");
        replace = replace.replace(")", "");
        replace = replace.replace("-", "");
        replace = replace.replace(" ", "");
        
        enderemi.setFone(replace);


        emit.setEnderEmit(enderemi);

        replace = rs.getString("IE");
        replace = replace.replace(".", "");
        replace = replace.replace("-", "");
        replace = replace.replace(" ", "");
        emit.setIE(replace);
        
        emit.setIM(rs.getString("IM"));
        emit.setCNAE(rs.getString("CNAE"));
        emit.setCRT(rs.getString("CRT"));
        
        
    } 
    
    
    rs.close();
    stmt.close();
    
    NfeRecepcao.infnfe.setEmit(emit);

}
public void pesquisa_nfpedido_env_dest(int cdpedido) throws SQLException, Exception{
    
    String replace = "";
    
    String qry = "Select coalesce(d.cnpj_destino, p.cnpj_cpf) as CPF, coalesce(d.nmdestinatario, p.nmparceiro) as xNome, "
            + "   coalesce(pv.inscr_est, 'ISENTO') as IE, coalesce(d.end_destino, p.endereco) as xLgr, coalesce(coalesce(d.numero,p.numero)::text,'S/N') as nro,"
            + "   coalesce(d.complemento, p.complemento) as complemento, coalesce(d.bairro_destino, p.bairro) as xBairro,"
            + "   trim(coalesce(d.cidade_destino, p.cidade)) as xMun, coalesce(d.uf, p.uf) as UF, d.cdmunicipio as cMun, lpad(coalesce(d.cep, p.cep)::text,8,'0') as CEP,"
            + "   p.fone as fone, '1058' as cpais, 'Brasil' as xpais, case when position('@' in email) > 0 then email else '' end as email,"
            + "   pv.idparceiro, d.iddestinatario, coalesce(pv.cdvendedor, pv.idoperador_cx) as cdvendedor"
            + "   from vnd_pedvenda pv"
            + "   inner join vnd_nfpedido nfp on (nfp.cdpedido=pv.cdpedido)"
            + "   inner join par_parceiro p using (idparceiro)"
            + "   left join par_destinat d using (iddestinatario)"
            + "   left join adm_cidade cp on (cp.nmcidade ilike (trim(coalesce(d.cidade_destino, p.cidade))) and (cp.uf = d.uf or cp.uf=p.uf) )"
            + "   where pv.cdpedido = ?";

    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setInt(1, cdpedido);
    ResultSet rs = stmt.executeQuery();
    
    
    TNFe.InfNFe.Dest dest = new TNFe.InfNFe.Dest();
    
    if (rs.next()) {

        dadosadicionais += " Parceiro: " + rs.getString("idparceiro") + " Dest: " + rs.getString("iddestinatario") + " Vendedor: " + rs.getString("cdvendedor") + " **";
        
        replace = rs.getString("CPF");
        replace = replace.replace(".", "");
        replace = replace.replace("/", "");
        replace = replace.replace("-", "");
        
        if (replace.toString().length() > 11){
            dest.setCNPJ(replace);
        } else {
            dest.setCPF(replace);
        }

//        dest.setXNome("NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
        dest.setXNome(rs.getString("xNome").trim());

        TEndereco enderdest = new TEndereco();
        enderdest.setXLgr(rs.getString("xLgr").trim());
        enderdest.setNro(rs.getString("nro").trim());
        
        enderdest.setXBairro(rs.getString("xBairro").trim());
        enderdest.setCMun(rs.getString("cMun").trim());
        enderdest.setXMun(rs.getString("xMun").trim());
        enderdest.setUF(TUf.fromValue(rs.getString("UF").trim()));
        enderdest.setCEP(rs.getString("CEP").trim());
        enderdest.setCPais(rs.getString("cpais").trim());
        enderdest.setXPais(rs.getString("xpais").trim());

        if (rs.getString("fone") != null){
        replace = rs.getString("fone");
        replace = replace.replace("(", "");
        replace = replace.replace(")", "");
        replace = replace.replace("-", "");
        replace = replace.replace(" ", "");
            if(replace.length() > 6){
                enderdest.setFone(replace.trim());
            }
        }
        dest.setEnderDest(enderdest);

        replace = rs.getString("IE");
        replace = replace.replace(".", "");
        replace = replace.replace("-", "");
        replace = replace.replace(" ", "");
        
        if(replace.contains("ISENTO")) {
            replace = "ISENTO";
        }
        
        dest.setIE(replace.trim());

    //    dest.setISUF("");
        String email[] = rs.getString("email").split(";");
        for (int i = 0; i < email.length; i++) {
            if(email[i].trim().length() > 0 && email[i].trim().length() <= 61){  
                dest.setEmail(email[i].trim());
                break;
            } 
        }
    } 
    
    
    rs.close();
    stmt.close();
    
     NfeRecepcao.infnfe.setDest(dest);

}
public String remove_acento(String xml) throws SQLException{
    String retorno = "";
    String qry = "select fu_remove_acento(?);";

    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setString(1, xml);
    ResultSet rs = stmt.executeQuery();

    if (rs.next()) {
        retorno = rs.getString(1);
    }
    
    return retorno;
}
public void pesquisa_nfpedido_env_det(int cdpedido) throws SQLException, Exception{
    
    String replace = "";
    
    String qry = "Select cdalfa as cProd, cdproduto as cEAN, substring(trim(nm_p_pesquisa), 1, 60) as xProd,"
            + "   lpad(coalesce(cd_ncm, 0)::text,8,'0') as NCM,"
            + "   cdcfop as CFOP, ud as uCom, trim(to_char(qtde,'999999999999990.0999')) as qCom,"
            + "   trim(to_char(precounitario,'999999999999999999990.0999999999')) as vUnCom,"
            + "   trim(to_char(subtotal,'999999999999990.00')) as vProd,"
            + "   cdproduto as cEANTrib, "
            + "   trim(to_char(qtde,'999999999999990.0999')) as qTrib,"
            + "   ud as uTrib, "
            + "   trim(to_char(precounitario,'999999999999999999990.0999999999')) as vUnTrib,"
            + "   coalesce(vrfrete,0) as vFrete,"
            + "   coalesce(vrseguro, 0) as vSeg,"
            + "   trim(to_char(vr_desconto,'999999999999990.00')) as vDesc,"
            + "   p.origem, i.cst, trim(to_char(i.aliq_icms,'99990.00')) as pICMS,"
            + "   trim(to_char(i.vrbase_icms,'999999999999990.00')) as vBC,"
            + "   trim(to_char(i.icms_venda,'999999999999990.00')) as vICMS,"
            + "   trim(to_char(i.icms_subst,'999999999999990.00')) as vICMSST,"
            + "   coalesce(vr_despacess, 0) as vr_despacess,"
            + "   trim(to_char(i.base_subst,'999999999999990.00')) as vBCST,"
            + "   trim(to_char(i.vrisenta,'999999999999990.00')) as vIsenta,"
            + "   trim(to_char(i.vroutra,'999999999999990.00')) as vOutra,"
            + "   trim(to_char(i.perc_reducaoicms * 100,'99990.00')) as pRedBC,"
            + "   p.iva,"
            + "   trim(to_char(i.aliq_icmsst,'99990.00')) as pICMSST,"
            + "   trim(to_char(ps.aliquota_pis,'99990.00')) as pPIS,"
            + "   trim(to_char(cf.aliquota_cofins,'99990.00')) as pCOFINS,"
            + "   trim(to_char(i.valor_pis,'999999999999990.00')) as vPis,"
            + "   trim(to_char(i.valor_cofins,'999999999999990.00')) as vCofins,"
            + "   i.cst_pis, i.cst_cofins, id_itempedv, i.ipi,"
            + "   i.vr_ipi, i.base_ipi, i.cod_enq, i.cst_ipi,"
            + "   perc_red_bc_st, mod_bc_subst,"
            + "   coalesce(mod_bc,(select codigo from adm_param  where nmparam ilike 'VND_MODALIDADE_BC_ICMS')) as modBC,"
            + "   trim(to_char(i.vtottrib,'999999999999990.00')) as VTotTrib"
            + "   from vnd_itempedv i"
            + "   inner join stq_itemstq it using (id_itemstq)"
            + "   inner join prd_produto p using (cdproduto)"
            + "   inner join prd_pis ps on (p.id_pis = ps.cst_pis)"
            + "   inner join prd_cofins cf on (p.id_cofins = cf.\"cst-cofins\")"
            + "   where cdpedido = ? and situacaoitemped = 'V' "
            + "   order by id_itempedv";

    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setInt(1, cdpedido);
    ResultSet rs = stmt.executeQuery();

    int i = 1;
    while (rs.next()) {
        TNFe.InfNFe.Det det = new TNFe.InfNFe.Det();
        TNFe.InfNFe.Det.Prod prod = new TNFe.InfNFe.Det.Prod();
        det.setNItem(String.valueOf(i));
        
        prod.setCProd(rs.getString("cProd"));
        String cEAN = rs.getString("cEAN");
        if (cEAN.length() > 9){
            int lcEAN = cEAN.length();
            for (int p = lcEAN; p < 13; p++) {
                cEAN = "0" + cEAN;
            }
            prod.setCEAN(cEAN);
        } else {
            prod.setCEAN("");
        }
        prod.setXProd(rs.getString("xProd").trim());
        if(rs.getString("NCM").contentEquals("00000099")){
            prod.setNCM("99");    
        } else {
            prod.setNCM(rs.getString("NCM"));
        }    
        prod.setCFOP(rs.getString("CFOP"));
        prod.setUCom(rs.getString("uCom"));
        prod.setQCom(rs.getString("qCom"));
        prod.setVUnCom(rs.getString("vUnCom"));
        prod.setVProd(rs.getString("vProd"));
        
        
        String cEANTrib = rs.getString("cEANTrib");
        if (cEANTrib.length() > 9){
            int lcEAN = cEANTrib.length();
            for (int p = lcEAN; p < 13; p++) {
                cEANTrib = "0" + cEANTrib;
            }
            prod.setCEANTrib(cEANTrib);
        } else {
            prod.setCEANTrib("");
        }
        prod.setUTrib(rs.getString("uTrib"));
        prod.setQTrib(rs.getString("qTrib"));
        prod.setVUnTrib(rs.getString("vUnTrib"));

        prod.setIndTot("1");
        
//        prod.setVOutro(rs.getString("vOutra"));
        if (!rs.getString("vDesc").equals("0.00")){
            prod.setVDesc(rs.getString("vDesc"));
        }
        
        det.setProd(prod);
        
        TNFe.InfNFe.Det.Imposto imposto = new TNFe.InfNFe.Det.Imposto();
        TNFe.InfNFe.Det.Imposto.ICMS icms = new TNFe.InfNFe.Det.Imposto.ICMS();
        
        // IMPOSTO
        
        imposto.setVTotTrib(rs.getString("VTotTrib"));

        if (Integer.parseInt(rs.getString("cst")) == 0) {
           // L_linha := L_linha || '3100' || Barra || substring(ri.cst, 1, 1) || Barra || L_cst || Barra || L_modbc_icms;
           // L_linha := L_linha || Barra || L_vBC || Barra || L_aliq || Barra || L_vICMS;
           TNFe.InfNFe.Det.Imposto.ICMS.ICMS00 icms00 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS00();
           icms00.setOrig(rs.getString("cst").substring(0, 1));
           icms00.setCST(rs.getString("cst").substring(1, 3));
           icms00.setModBC(String.valueOf(rs.getInt("modBC")));
           icms00.setVBC(rs.getString("vbc"));
           icms00.setPICMS(rs.getString("picms"));
           icms00.setVICMS(rs.getString("vicms"));
           icms.setICMS00(icms00);
           imposto.setICMS(icms);
           det.setImposto(imposto);
        }
      
           if (Integer.parseInt(rs.getString("cst")) == 10) { 
//           /* Registro 3110 - Bloco 3110  - ST=10 - Tributos Incidentes sobre o Produto ou Servico - Grupo N - ICMS Normal e Substituicao*/
//
//           if (L_cst = '10') then
//               L_linha := L_linha || '3103' || Barra || substring(ri.cst, 1, 1) || Barra || L_cst || Barra || L_modbc_icms;
//               L_linha := L_linha || Barra || L_vBC || Barra || L_aliq || Barra || L_vICMS;
//               L_linha := L_linha || Barra || L_modbc_icms_subst || Barra || L_pMVAST || Barra || L_pRedBCST;
//               L_linha := L_linha || Barra || L_vBCST || Barra || coalesce(L_aliqST, 0) || Barra || L_vICMSST;
//           end if;
//           /* Registro 3120 - Bloco 3120  - Tributos Incidentes sobre o Produto ou Servico - Grupo N - ICMS comReducao da Base calculo */
           
               TNFe.InfNFe.Det.Imposto.ICMS.ICMS10 icms10 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS10();
               
               icms10.setOrig(rs.getString("cst").substring(0, 1));
               icms10.setCST(rs.getString("cst").substring(1, 3));
               icms10.setModBC(String.valueOf(rs.getInt("modBC")));
               icms10.setVBC(rs.getString("vbc"));
               icms10.setPICMS(rs.getString("picms"));
               icms10.setVICMS(rs.getString("vicms"));
               icms10.setModBCST("4");
               icms10.setPRedBCST("100.00");
               icms10.setVBCST(rs.getString("vBCST"));
               icms10.setPICMSST(rs.getString("pICMSST"));
               icms10.setVICMSST(rs.getString("vICMSST"));
               
               icms.setICMS10(icms10);
               imposto.setICMS(icms);
               det.setImposto(imposto);
           
           }
           if (Integer.parseInt(rs.getString("cst")) == 20) {        
//           if (L_cst = '20') then
//               L_linha := L_linha || '3106' || Barra || substring(ri.cst, 1, 1) || Barra || L_cst || Barra || L_modbc_icms;
//               L_linha := L_linha || Barra || L_pRedBC || Barra || L_vBC || Barra || L_aliq || Barra || L_vICMS;
//           end if;
               
               TNFe.InfNFe.Det.Imposto.ICMS.ICMS20 icms20 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS20();
               
               icms20.setOrig(rs.getString("cst").substring(0, 1));
               icms20.setCST(rs.getString("cst").substring(1, 3));
               icms20.setModBC(String.valueOf(rs.getInt("modBC")));
               icms20.setPRedBC(rs.getString("pRedBC"));
               icms20.setVBC(rs.getString("vbc"));
               icms20.setPICMS(rs.getString("picms"));
               icms20.setVICMS(rs.getString("vicms"));
               icms.setICMS20(icms20);
               imposto.setICMS(icms);
               det.setImposto(imposto);
        
           } 
        
//           /* Registro 3130 - Bloco 3130  - ST=30 - Isenta ou nÃ£o tributada e com cobranÃ§a do ICMS por substituiÃ§Ã£o tributÃ¡ria*/
//
           if (Integer.parseInt(rs.getString("cst")) == 30) {
//               L_linha := L_linha || '3110' || Barra || substring(ri.cst, 1, 1) || Barra || L_cst || Barra ||  L_modbc_icms_subst;
//               L_linha := L_linha || Barra || L_pMVAST || Barra || L_pRedBCST || Barra || L_vBCST ;
//               L_linha := L_linha || Barra || coalesce(L_aliqST, 0)  || Barra || L_vICMSST;
               TNFe.InfNFe.Det.Imposto.ICMS.ICMS30 icms30 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS30();
               icms30.setOrig(rs.getString("cst").substring(0, 1));
               icms30.setCST(rs.getString("cst").substring(1, 3));
               icms30.setModBCST("4");
               icms30.setPRedBCST("100.00");
               icms30.setVBCST(rs.getString("vBCST"));
               icms30.setPICMSST(rs.getString("pICMSST"));
               icms30.setVICMSST(rs.getString("vICMSST"));
               
               
               
               icms.setICMS30(icms30);
               imposto.setICMS(icms);
               det.setImposto(imposto);
               
               
           }
//           /* Registro 3140 - Bloco 3140  - ST=40 - Isenta 40 - Isenta 41 - NÃ£o tributada -50 - SuspensÃ£o*/
//
           if (Integer.parseInt(rs.getString("cst")) >= 40 && Integer.parseInt(rs.getString("cst")) <= 50) {
               //L_linha := L_linha || '3113' || Barra || substring(ri.cst, 1, 1) || Barra || L_cst;
               TNFe.InfNFe.Det.Imposto.ICMS.ICMS40 icms40 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS40();
               icms40.setOrig(rs.getString("cst").substring(0, 1));
               icms40.setCST(rs.getString("cst").substring(1, 3));
               
               icms.setICMS40(icms40);
               imposto.setICMS(icms);
               det.setImposto(imposto);
           }
//           /* Registro 3145 - Bloco 3145  - Diferimento */
//           if (L_cst = '51') then
//               L_linha := L_linha || '3116' || Barra || substring(ri.cst, 1, 1) || Barra || L_cst || Barra || L_modbc_icms;
//               L_linha := L_linha || Barra || L_pRedBC || Barra || L_vBC || Barra || L_aliq || Barra || L_vICMS;
//           end if;
//           /* Registro 3150 - Bloco 3150  - ST=60 - ICMS cobrado anteriormente por substituiÃ§Ã£o tributÃ¡ria*/
//
           if (Integer.parseInt(rs.getString("cst")) == 60) {
//               L_linha := L_linha || '3120' || Barra || substring(ri.cst, 1, 1) || Barra || L_cst;
//               L_linha := L_linha || Barra || L_vBCST ;
//               L_linha := L_linha || Barra || L_vICMSST;
               TNFe.InfNFe.Det.Imposto.ICMS.ICMS60 icms60 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS60();
               icms60.setOrig(rs.getString("cst").substring(0, 1));
               icms60.setCST(rs.getString("cst").substring(1, 3));
               icms60.setVBCSTRet(rs.getString("vBCST"));
               icms60.setVICMSSTRet(rs.getString("vICMSST"));
               icms.setICMS60(icms60);
               imposto.setICMS(icms);
               det.setImposto(imposto);
               
           }
//           /* Registro 3160 - Bloco 3160  - ST=70 - Com reduÃ§Ã£o de base de cÃ¡lculo e cobranÃ§a do ICMS por substituiÃ§Ã£o tributÃ¡ria*/
//
//           if (L_cst = '70') then
//               L_linha := L_linha || '3123' || Barra || substring(ri.cst, 1, 1) || Barra || L_cst || Barra || L_modbc_icms ;
//               L_linha := L_linha || Barra || L_pRedBC || Barra || L_vBC || Barra || L_aliq || Barra || L_vICMS;
//               L_linha := L_linha || Barra || L_modbc_icms_subst || Barra || L_pMVAST || Barra || L_pRedBCST ;
//               L_linha := L_linha || Barra || L_vBCST|| Barra || coalesce(L_aliqST, 0)  || Barra || L_vICMSST;
//           end if;
//           /* Registro 3170 - Bloco 3170  - ST=90 - Outras*/
//
//           if (L_cst = '90' ) then
//               L_linha := L_linha || '3126' || Barra || substring(ri.cst, 1, 1) || Barra || L_cst;
//           end if;
        
           // IPI

           //TNFe.InfNFe.Det.Imposto.IPI ipi = new TNFe.InfNFe.Det.Imposto.IPI();

           
         
           
//        if (ri.vr_ipi > 0.0 and ri.base_ipi > 0.0) then
//               L_linha := L_linha || '3180' || Barra || '' || Barra || '';
//               L_linha := L_linha || Barra || '' || Barra || '' || Barra || coalesce(ri.cod_enq, ' ') || chr(13) || chr(10);
//               L_vBaseIPI := ri.base_ipi;
//               L_vIPI     := ri.vr_ipi;
//               L_aliqIPI  := (ri.ipi / 100.0);
//
//               L_linha := L_linha || '3181' || Barra || coalesce(ri.cst_ipi, ' ') || Barra || L_vBaseIPI;
//               L_linha := L_linha || Barra || L_aliqIPI || Barra || L_vIPI || chr(13) || chr(10);
//           end if;
//           
//           /* Registro 3200 - Bloco 3200  - Grupo do PIS Tributado pela aliquota */
//           L_cst_pis := lpad(ri.cst_pis, 2, '0');
//           L_cst_cofins := lpad(ri.cst_cofins, 2, '0');
//           L_vBC := ri.vProd - ri.vDesc;
//           L_vPis := ri.vPis;
//           L_vCofins := ri.vCofins;
//           if (L_vBC != 0) then
//              L_aliq_pis := (L_vPIS / L_vBC) * 100;
//              L_aliq_cofins := (L_vCOFINS / L_vBC) * 100;
//           else
//              L_aliq_pis := 0;
//              L_aliq_cofins := 0;
//           end if;
           
          // PIS COFINS
           
          TNFe.InfNFe.Det.Imposto.PIS pis = new TNFe.InfNFe.Det.Imposto.PIS();
          TNFe.InfNFe.Det.Imposto.PIS.PISNT pisnt = new TNFe.InfNFe.Det.Imposto.PIS.PISNT();
          TNFe.InfNFe.Det.Imposto.PIS.PISAliq pisaliq = new TNFe.InfNFe.Det.Imposto.PIS.PISAliq();
          
          if (rs.getDouble("vpis") >0){
              pisaliq.setCST("01");
              pisaliq.setVBC(rs.getString("vProd"));
              pisaliq.setPPIS(rs.getString("pPIS"));
              pisaliq.setVPIS(rs.getString("vpis"));
              pis.setPISAliq(pisaliq);
          } else {
              pisnt.setCST("07");
              pis.setPISNT(pisnt);
          }
          
         
          imposto.setPIS(pis);
          
//           /* Provisorio para ajustar o pis- cofins*/
//           if (L_vpis >0) then
//              L_cst_Pis := '01';
//           else
//              L_cst_pis := '07';
//           end if;
          
          
          TNFe.InfNFe.Det.Imposto.COFINS cofins = new TNFe.InfNFe.Det.Imposto.COFINS();
          TNFe.InfNFe.Det.Imposto.COFINS.COFINSNT cofinsnt = new TNFe.InfNFe.Det.Imposto.COFINS.COFINSNT();
          TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq cofinsaliq = new TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq();
          if (rs.getDouble("vcofins") >0){
              cofinsaliq.setCST("01");
              cofinsaliq.setVBC(rs.getString("vProd"));
              cofinsaliq.setPCOFINS(rs.getString("pCOFINS"));
              cofinsaliq.setVCOFINS(rs.getString("vCofins"));
              cofins.setCOFINSAliq(cofinsaliq);
          } else {
              cofinsnt.setCST("07");
              
              
              cofins.setCOFINSNT(cofinsnt);
          }          
          

          imposto.setCOFINS(cofins);
          
//           if (L_vCofins >0) then
//              L_cst_cofins := '01';
//           else
//              L_cst_cofins := '07';
//           end if;
          
          
          
//           if (L_cst_pis = '01' or L_cst_pis = '02') then
//               L_linha := L_linha || '3200' || Barra || L_cst_pis || Barra || L_vBC || Barra || L_aliq_pis || Barra || L_vPIS || chr(13) || chr(10);
//           end if;
//           if (L_cst_pis = '03') then
//               /* Dados nao tratados corretamente - é por quantidade*/
//               L_linha := L_linha || '3210' || Barra || L_cst_pis || Barra || L_vBC || Barra || L_aliq_pis || Barra || L_vPIS || chr(13) || chr(10);
//           end if;
//           if (L_cst_pis = '04' or L_cst_pis ='06' or L_cst_pis ='07' or L_cst_pis ='08' or L_cst_pis ='09') then
//               L_linha := L_linha || '3220' || Barra || L_cst_pis || chr(13) || chr(10);
//           end if;
//           if (L_cst_pis = '99') then
//               L_linha := L_linha || '3230' || Barra || L_cst_pis || Barra || L_vBC || Barra || L_aliq_pis || Barra || L_vPIS || chr(13) || chr(10);
//           end if;
//                                             
//                         
//           if (L_cst_cofins = '01' or L_cst_cofins ='02') then
//               L_linha := L_linha || '3300' || Barra || L_cst_cofins || Barra || L_vBC || Barra || L_aliq_cofins || Barra || L_vCofins || chr(13) || chr(10);
//           end if;
//           if (L_cst_cofins = '03') then
//               /* Dados nao tratados corretamente - é por quantidade*/
//               L_linha := L_linha || '3310' || Barra || L_cst_cofins || Barra || L_vBC || Barra || L_aliq_cofins || Barra || L_vCofins || chr(13) || chr(10);
//           end if;
//           if (L_cst_cofins = '04' or L_cst_cofins ='06' or L_cst_cofins ='07' or L_cst_cofins ='08' or L_cst_cofins ='09') then
//               L_linha := L_linha || '3320' || Barra || L_cst_cofins || chr(13) || chr(10);
//           end if;
//           if (L_cst_cofins = '99') then
//               L_linha := L_linha || '3330' || Barra || L_cst_cofins || Barra || L_vBC || Barra || L_aliq_cofins || Barra || L_vCofins || chr(13) || chr(10);
//           end if;
//
//          end if;   
           
        
        NfeRecepcao.infnfe.getDet().add(det);
        i++;
    }
    
    rs.close();
    stmt.close();

}
public void pesquisa_nfpedido_env_trib(int cdpedido) throws SQLException, Exception{
    
    String replace = "";
    
    String qry = "Select trim(to_char(pv.totalpedido,'999999999999990.00')) as vNF,"
            + " trim(to_char(pv.baseicms,'999999999999990.00')) as vBC,"
            + " trim(to_char(pv.totalicms,'999999999999990.00')) as vICMS,"
            + " pv.baseiss,"
            + " pv.totaliss,"
            + " trim(to_char(pv.baseicmssubst,'999999999999990.00')) as vBCST,"
            + " trim(to_char(pv.totalicmssubst,'999999999999990.00')) as vST,"
            + " trim(to_char(pv.totalmercadoria,'999999999999990.00')) as vProd,"
           // + " trim(to_char(pv.totalservico,'999999999999990.00')) as vNF,"
            + " trim(to_char(pv.vr_desconto,'999999999999990.00')) as vDesc,"
            + " trim(to_char(pv.valor_pis,'999999999999990.00')) as vPIS,"
            + " trim(to_char(pv.valor_cofins,'999999999999990.00')) as vCOFINS,"
            + " pv.vrbase_ctb,"
            + " pv.vrbase_ipi,"
            + " pv.vripi,"
            + " trim(to_char(pv.vrfrete,'999999999999990.00')) as vFrete,"
            + " trim(to_char(pv.vrseguro,'999999999999990.00')) as vSeg,"
            + " trim(to_char((select sum(vtottrib) from vnd_itempedv where cdpedido = pv.cdpedido),'999999999999990.00')) as VTotTrib,"
            + " trim(to_char(pv.vroutra,'999999999999990.00')) as vOutro"
            + " from vnd_pedvenda pv"
            + " inner join vnd_nfpedido nfp on (nfp.cdpedido=pv.cdpedido)"
            + " where pv.cdpedido = ?";

    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setInt(1, cdpedido);
    ResultSet rs = stmt.executeQuery();

    TNFe.InfNFe.Total total = new TNFe.InfNFe.Total();
    TNFe.InfNFe.Total.ICMSTot icmstot = new TNFe.InfNFe.Total.ICMSTot();
    
    if (rs.next()) {
        icmstot.setVBC(rs.getString("vBC"));
        icmstot.setVICMS(rs.getString("vICMS"));
        icmstot.setVBCST(rs.getString("vBCST"));
        icmstot.setVST(rs.getString("vST"));
        icmstot.setVProd(rs.getString("vProd"));
        icmstot.setVFrete(rs.getString("vFrete"));
        icmstot.setVSeg(rs.getString("vSeg"));
        icmstot.setVDesc(rs.getString("vDesc"));
        icmstot.setVII("0.00");
        icmstot.setVIPI("0.00");
        icmstot.setVPIS(rs.getString("vPIS"));
        icmstot.setVCOFINS(rs.getString("vCOFINS"));
//        icmstot.setVOutro(rs.getString("vOutro"));
        icmstot.setVOutro("0.00");
        icmstot.setVNF(rs.getString("vNF"));
        icmstot.setVTotTrib(rs.getString("VTotTrib"));
    }
    
    rs.close();
    stmt.close();
    
    total.setICMSTot(icmstot);
    NfeRecepcao.infnfe.setTotal(total);

}
public void pesquisa_nfpedido_env_total(int cdpedido) throws SQLException, Exception{
    
    String replace = "";
    
    String qry = "Select trim(to_char(pv.totalpedido,'999999999999990.00')) as vNF,"
            + " trim(to_char(pv.baseicms,'999999999999990.00')) as vBC,"
            + " trim(to_char(pv.totalicms,'999999999999990.00')) as vICMS,"
            + " pv.baseiss,"
            + " pv.totaliss,"
            + " trim(to_char(pv.baseicmssubst,'999999999999990.00')) as vBCST,"
            + " trim(to_char(pv.totalicmssubst,'999999999999990.00')) as vST,"
            + " trim(to_char(pv.totalmercadoria,'999999999999990.00')) as vProd,"
           // + " trim(to_char(pv.totalservico,'999999999999990.00')) as vNF,"
            + " trim(to_char(pv.vr_desconto,'999999999999990.00')) as vDesc,"
            + " trim(to_char(pv.valor_pis,'999999999999990.00')) as vPIS,"
            + " trim(to_char(pv.valor_cofins,'999999999999990.00')) as vCOFINS,"
            + " pv.vrbase_ctb,"
            + " pv.vrbase_ipi,"
            + " pv.vripi,"
            + " trim(to_char(pv.vrfrete,'999999999999990.00')) as vFrete,"
            + " trim(to_char(pv.vrseguro,'999999999999990.00')) as vSeg,"
            + " trim(to_char(pv.vroutra,'999999999999990.00')) as vOutro,"
            + " trim(to_char((select sum(vtottrib) from vnd_itempedv where cdpedido = pv.cdpedido),'999999999999990.00')) as VTotTrib"
            + " from vnd_pedvenda pv"
            + " inner join vnd_nfpedido nfp on (nfp.cdpedido=pv.cdpedido)"
            + " where pv.cdpedido = ?";

    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setInt(1, cdpedido);
    ResultSet rs = stmt.executeQuery();

    TNFe.InfNFe.Total total = new TNFe.InfNFe.Total();
    TNFe.InfNFe.Total.ICMSTot icmstot = new TNFe.InfNFe.Total.ICMSTot();
    
    if (rs.next()) {
        icmstot.setVTotTrib(rs.getString("VTotTrib"));
        icmstot.setVBC(rs.getString("vBC"));
        icmstot.setVICMS(rs.getString("vICMS"));
        icmstot.setVBCST(rs.getString("vBCST"));
        icmstot.setVST(rs.getString("vST"));
        icmstot.setVProd(rs.getString("vProd"));
        icmstot.setVFrete(rs.getString("vFrete"));
        icmstot.setVSeg(rs.getString("vSeg"));
        icmstot.setVDesc(rs.getString("vDesc"));
        icmstot.setVII("0.00");
        icmstot.setVIPI("0.00");
        icmstot.setVPIS(rs.getString("vPIS"));
        icmstot.setVCOFINS(rs.getString("vCOFINS"));
//        icmstot.setVOutro(rs.getString("vOutro"));
        icmstot.setVOutro("0.00");
        icmstot.setVNF(rs.getString("vNF"));
    }
    
    rs.close();
    stmt.close();
    
    total.setICMSTot(icmstot);
    NfeRecepcao.infnfe.setTotal(total);

}
public void pesquisa_nfpedido_env_transp(int cdpedido) throws SQLException, Exception{
    
    String replace = "";
    
    String qry = "Select  coalesce(pt.nmparceiro, '') as xNome,"
            + " coalesce(pe.frete_cif_fob, '1') as ModFrete,"
            + " dt.cnpj_destino as CNPJ, "
            + " coalesce(dt.inscr_est, '') as IE,"
            + " coalesce(dt.end_destino, '') as xEnder,"
            + " coalesce(dt.cidade_destino, '') as xMun,"
            + "	coalesce(dt.uf, '') as UF_T,"
            + " idplaca as placa,"
            + " coalesce(v.uf , '') as UF_V,"
            + " coalesce(v.rntc, '') as rntc,"
            + " coalesce(pe.qtd_vol, 0) as qtd_vol,"
            + " coalesce(pe.especie, '') as especie,"
            + "	coalesce(pe.marca_merc, '') as marca_merc,"
            + " coalesce(pe.num_volumes, 0) as num_volumes,"
            + " coalesce(pe.peso_liquido, 0) as peso_liquido,"
            + " coalesce(pe.peso_bruto, 0) as peso_bruto"
            + " from vnd_pedvenda pv"
            + " inner join vnd_nfpedido nfp on (nfp.cdpedido=pv.cdpedido)"
            + " left join vnd_peso pe on (pe.cdpedido=pv.cdpedido)"
            + " left join frt_veiculos v on (v.idplaca=pe.placa)"
            + " left join par_parceiro pt on (pt.idparceiro=v.idparceiro)"
            + "	left join par_destinat dt on (dt.idparceiro=v.idparceiro and (dt.especial='1' or dt.especial is null or dt.especial is not null))"
            + " where pv.cdpedido = ? limit 1";

    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setInt(1, cdpedido);
    ResultSet rs = stmt.executeQuery();

    TNFe.InfNFe.Transp transp = new TNFe.InfNFe.Transp();
    
    if (rs.next()) {
        transp.setModFrete(rs.getString("ModFrete"));
        
        if(rs.getString("CNPJ") != null){
            TNFe.InfNFe.Transp.Transporta transportadora = new TNFe.InfNFe.Transp.Transporta();
            
            replace = rs.getString("CNPJ");
            replace = replace.replace(".", "");
            replace = replace.replace("/", "");
            replace = replace.replace("-", "");

            if (replace.length() > 11) {
                transportadora.setCNPJ(replace);
            } else {
                transportadora.setCPF(replace);
            }
            

            transportadora.setXNome(rs.getString("xNome").trim());

            replace = rs.getString("IE");
            replace = replace.replace(".", "");
            replace = replace.replace("-", "");

            if(replace.contains("ISENTO")) {
                replace = "ISENTO";
            }
            
            
            transportadora.setIE(replace);
            transportadora.setXEnder(rs.getString("xEnder").trim());
            transportadora.setXMun(rs.getString("xMun").trim());
            transportadora.setUF(TUf.valueOf(rs.getString("UF_T")));

            transp.setTransporta(transportadora);
        }
        if (rs.getString("placa") != null){
            TVeiculo veiculo = new TVeiculo();

            veiculo.setPlaca(rs.getString("placa"));
            veiculo.setUF(TUf.valueOf(rs.getString("UF_V")));

            transp.setVeicTransp(veiculo);
        }
        TNFe.InfNFe.Transp.Vol vol = new TNFe.InfNFe.Transp.Vol();
        vol.setPesoB(rs.getString("peso_bruto"));
        vol.setPesoL(rs.getString("peso_liquido"));

        transp.getVol().add(vol);
    }
    
    rs.close();
    stmt.close();
    
    NfeRecepcao.infnfe.setTransp(transp);

}
public void pesquisa_nfpedido_env_infadic(int cdpedido) throws SQLException, Exception{
    
    String replace = "";
    String L_tmp = "";

    
    TNFe.InfNFe.InfAdic infadic = new TNFe.InfNFe.InfAdic();    
    
    String qry = "select nmmensagem, coalesce(insercao1, '') as insercao1, coalesce(insercao2, '') as insercao2,"
            + " coalesce(insercao3, '') as insercao3, coalesce(insercao4, '') as insercao4, "
            + " coalesce(insercao5, '') as insercao5, coalesce(insercao6, '') as insercao6,"
            + " coalesce(insercao7, '') as insercao7, coalesce(insercao8, '') as insercao8,"
            + " qtdinsercao"
            + " from vnd_mensnota m"
            + " inner join vnd_mensagem using (cdmensagem)"
            + " where cdpedido = ?";
    
    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setInt(1, cdpedido);
    ResultSet rs = stmt.executeQuery();
    
    while (rs.next()) {
       L_tmp += rs.getString("nmmensagem").trim() + ". ";
       if (rs.getInt("qtdinsercao") > 0) {
        for (int i = 1; i < rs.getInt("qtdinsercao") + 1; i++) {
            L_tmp = L_tmp.replaceFirst("\\$", rs.getString("insercao"+i));
        }
       } else {
         L_tmp += rs.getString("nmmensagem").trim();
       }
       L_tmp = L_tmp.replace("$", "");
     }
    if (L_tmp.length() > 0){
        infadic.setInfAdFisco(L_tmp.trim());
    }    
    
    
    /* Insere as formas de pagamento exceto DUPLICATAS, como observacao */
    qry = "select coalesce(to_char(cdcondpgto, '099'), '') as cdcondpgto,"
            + " coalesce(trim(to_char(vrpago, '999990D99')), '') as vrpago,"
            + " coalesce(trim(to_char(vr_desconto, '9990D99')), '') as vr_desconto,"
            + " coalesce(trim(to_char(dt_vencto, 'dd/MM/yy')), '') as dt_vencto, nrdocumento "
            + " from fin_formapgt f"
            + " inner join fin_condpgto cp using (cdcondpgto)"
            + " where cdseqpgto = (select cdseqpgto from vnd_pedvenda where cdpedido = ?)"
            + " and cdtipodoc != coalesce((select codigo from adm_param where nmparam ilike 'TIPO_DOCUMENTO_DUPLICATAS'),4)";
    
    stmt = conn.prepareStatement(qry);
    stmt.setInt(1, cdpedido);
    rs = stmt.executeQuery();
    
    
    while (rs.next()) {
        dadosadicionais += "Cod.Pagamento: " + rs.getString("cdcondpgto") + " - Vr.Pago: " + rs.getString("vrpago") + " - Vr.Desconto: " + rs.getString("vr_desconto") + (rs.getString("dt_vencto").length() > 0 ? " - " + rs.getString("dt_vencto") : "") + " ** ";
    }
    
    infadic.setInfCpl(dadosadicionais.trim());
    
    
    String adfisco = "";
    
    rs.close();
    stmt.close();
    
    NfeRecepcao.infnfe.setInfAdic(infadic);

}

public String getTotaltributos(int cdpedido) throws SQLException{
    String ret = "";

    String qry = "select sum(vtottrib) as vtottrib," +
            " round(((sum(vtottrib) / sum(subtotal)) * 100)::numeric,2) as perc" +
            " from vnd_itempedv where cdpedido = ?";


    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setInt(1, cdpedido);
    ResultSet rs = stmt.executeQuery();

    if(rs.next()){
        ret = "Val.Aprox.Tributos R$ " + rs.getString("vtottrib") + " ("+ rs.getString("perc") +"%) Fonte: IBPT";
    }
    return ret;
}
public void pesquisa_nfpedido_env_cob(int cdpedido) throws SQLException, Exception{
    
    String replace = "";
    String L_tmp = "";
    int L_listar = 0;
    
    TNFe.InfNFe.Cobr cob = new TNFe.InfNFe.Cobr();    
    TNFe.InfNFe.Cobr.Fat fat = new TNFe.InfNFe.Cobr.Fat();    
    TNFe.InfNFe.Cobr.Dup dup = new TNFe.InfNFe.Cobr.Dup();    
    
    String qry = "select cdcondpgto, trim(to_char(f.vrpago,'999999999999990.00')) as vDup,"
            + " f.vr_desconto, to_char(dt_vencto,'YYYY-MM-DD') as dVenc,"
            + " nrdocumento as nFat,"
            + " trim(to_char(p.totalpedido,'999999999999990.00')) as Total"
            + " from fin_formapgt f"
            + " inner join fin_condpgto cp using (cdcondpgto)"
            + " inner join vnd_pedvenda p using (cdseqpgto)"
            + " where cdseqpgto = (select cdseqpgto from vnd_pedvenda where cdpedido = ?)"
            + " and cdtipodoc = (select codigo from adm_param where nmparam ilike 'TIPO_DOCUMENTO_DUPLICATAS')"
            + " order by dvenc";

    PreparedStatement stmt = conn.prepareStatement(qry);
    stmt.setInt(1, cdpedido);
    ResultSet rs = stmt.executeQuery();
     
     while (rs.next()) {
        if (L_listar == 0) {
            fat.setNFat(rs.getString("nFat"));
            fat.setVOrig(rs.getString("Total"));
            fat.setVLiq(rs.getString("Total"));
            L_listar++; 
            cob.setFat(fat);
            
            dup = new TNFe.InfNFe.Cobr.Dup();
            dup.setNDup(rs.getString("nFat"));
            dup.setDVenc(rs.getString("dVenc"));
            dup.setVDup(rs.getString("vDup"));
            
            //cob.getDup().add(dup);
         } else {
            
            dup = new TNFe.InfNFe.Cobr.Dup();
            dup.setNDup(rs.getString("nFat"));
            dup.setDVenc(rs.getString("dVenc"));
            dup.setVDup(rs.getString("vDup"));
            
        }
            cob.getDup().add(dup);
     }
    
    rs.close();
    stmt.close();
    
    NfeRecepcao.infnfe.setCobr(cob);

}
public void insert_nfpedido_rejeicao(Integer cdpedido, String rejeicao, Integer cstat) throws SQLException, Exception{
    
    String qry = "";
    PreparedStatement stmt = null;
    if (cstat == null){
        qry = "insert into vnd_nfpedido_rejeicao (rejeicao, dt_sistema, cdpedido) values (?,now(),?)";

        stmt = conn.prepareStatement(qry);
        stmt.setString(1, rejeicao);
        stmt.setInt(2, cdpedido);
    } else {
        qry = "insert into vnd_nfpedido_rejeicao (dt_sistema, cdpedido, cstat) values (now(),?,?)";

        stmt = conn.prepareStatement(qry);
        stmt.setInt(1, cdpedido);
        stmt.setInt(2, cstat);
    }
    stmt.executeUpdate();

}
public HashMap pesquisa_nfpedido_reimpressao(int idestabelecimen, int cdpedido_ini, int cdpedido_fin, ControleImpressao imp) throws SQLException, Exception{


    String qry = "select cdpedido"
            + " from vnd_nfpedido nf"
            + " inner join vnd_pedvenda using (cdpedido)"
            + " inner join cdc_estabelecim e using (idestabelecimen)"
            + " inner join adm_estado es on (e.uf = es.uf )"
            + " where situacaonf in ('N') and status_nfe in (100) and idestabelecimen = ?"
            + " and cdpedido >= ? and cdpedido <= ?"
            + " order by cdpedido";

    PreparedStatement stmt = conn.prepareStatement(qry);

    stmt.setInt(1, idestabelecimen);
    stmt.setInt(2, cdpedido_ini);
    stmt.setInt(3, cdpedido_fin);
    
    ResultSet rs = stmt.executeQuery();
    HashMap map = new HashMap();

    while (rs.next()) {
        imp.ViewPdf(null, rs.getInt("cdpedido"));
    }
    rs.close();
    stmt.close();




    return map;

}
private VND_NfpedidoBean populate (ResultSet rs) throws SQLException, Exception {
    try {
      VND_NfpedidoBean gs = new VND_NfpedidoBean();

      gs.setCdpedido( rs.getInt   ( 1) );
      gs.setCdnf( rs.getString   ( 2) );
      gs.setData( rs.getString( 3) );

      return gs;
    } catch (Exception e ) {
      throw new Exception ("Erro na leitura do VND_NfpedidoBean!");
    }
  }

}
