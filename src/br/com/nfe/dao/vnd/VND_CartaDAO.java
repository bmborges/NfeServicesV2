/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.nfe.dao.vnd;

import br.com.nfe.bean.vnd.VND_CartaBean;
import br.com.nfe.bean.vnd.VND_NfpedidoBean;
import br.com.nfe.util.Database;
import br.com.nfe.util.DtSystem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 *
 * @author supervisor
 */
public class VND_CartaDAO {
    
      java.sql.Connection conn = null;

      public VND_CartaDAO() throws Exception{
         conn = Database.getConnection();

         if (conn == null) {
            throw new Exception(getClass().getName() + ": null connection passed.");
         }
            this.conn = conn;
         }

public VND_CartaBean pesquisa_carta() throws SQLException{
    VND_CartaBean bean = null;
    String replace;

    String qry = "select distinct cdpedido from vnd_carta where status_nfe = 0 limit 1";

    PreparedStatement stmt = conn.prepareStatement(qry);

    ResultSet rs = stmt.executeQuery();
    int cdpedido = 0;
    if (rs.next()){
	    cdpedido = rs.getInt("cdpedido");
    }

    if (cdpedido > 0 ) {
    
        qry = "select cdpedido, idnfe, cnpj, c.descricao as desc, trim(cc.descricao) as descricao, iduf," 
            + "(select count(*)+1 from vnd_nfpedido_rejeicao where cdpedido = cc.cdpedido and cstat = 135) as NSeqEvento, xml_cce "
            + " from vnd_carta cc"
            + " inner join vnd_pedvenda using (cdpedido)"
            + " inner join ctb_correcao c using (cderro)"
            + "	inner join cdc_estabelecim e using (idestabelecimen)"
            + "	inner join adm_estado es on (e.uf = es.uf )"            
            + " where cdpedido = ? and protocolo is null";
  
        stmt = conn.prepareStatement(qry);
        stmt.setInt(1, cdpedido);
        System.out.println(stmt);

        rs = stmt.executeQuery();

    while (rs.next()) {
        if (rs.isFirst()){
            
            bean = new VND_CartaBean();
            
            bean.setCdpedido(rs.getInt("cdpedido"));
            
            bean.setIdnfe(rs.getString("idnfe"));
            
            replace = rs.getString("cnpj");
            replace = replace.replace(".", "");
            replace = replace.replace("/", "");
            replace = replace.replace("-", "");
        
            bean.setCnpj_cpf(replace);
            
            bean.setDescricao(rs.getString("desc").trim()+" - "+rs.getString("descricao").trim());
            
            bean.setIduf(rs.getString("iduf"));
            bean.setNSeqEvento(rs.getString("NSeqEvento"));

            bean.setXml_cce(rs.getString("xml_cce"));
            
        } else {
            bean.setDescricao(bean.getDescricao()+", "+rs.getString("desc").trim()+" - "+rs.getString("descricao").trim());
        }
      }
    }
    if (bean == null || cdpedido == 0){
        bean = new VND_CartaBean();
        bean.setCdpedido(0);
    }

    rs.close();
    stmt.close();

    //System.out.println(">>>Select....: " + retorno[1]);
    return bean;

}
public VND_CartaBean pesquisa_cartaCdpedido( int cdpedido) throws SQLException{
    VND_CartaBean bean = null;
    String replace;

    String qry;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    if (cdpedido > 0 ) {

        qry = "select cdpedido, idnfe, cnpj, c.descricao as desc, trim(cc.descricao) as descricao, iduf," +
            "(select count(*)+1 from vnd_nfpedido_rejeicao where cdpedido = cc.cdpedido and cstat = 135) as NSeqEvento, xml_cce "
            + " from vnd_carta cc"
            + " inner join vnd_pedvenda using (cdpedido)"
            + " inner join ctb_correcao c using (cderro)"
            + "	inner join cdc_estabelecim e using (idestabelecimen)"
            + "	inner join adm_estado es on (e.uf = es.uf )"
            + " where cdpedido = ? and protocolo is null";

        stmt = conn.prepareStatement(qry);
        stmt.setInt(1, cdpedido);
        System.out.println(stmt);

        rs = stmt.executeQuery();

    while (rs.next()) {
        if (rs.isFirst()){

            bean = new VND_CartaBean();

            bean.setCdpedido(rs.getInt("cdpedido"));

            bean.setIdnfe(rs.getString("idnfe"));

            replace = rs.getString("cnpj");
            replace = replace.replace(".", "");
            replace = replace.replace("/", "");
            replace = replace.replace("-", "");

            bean.setCnpj_cpf(replace);

            bean.setDescricao(rs.getString("desc").trim()+" - "+rs.getString("descricao").trim());

            bean.setIduf(rs.getString("iduf"));
            bean.setNSeqEvento(rs.getString("NSeqEvento"));
            bean.setXml_cce(rs.getString("xml_cce"));

        } else {
            bean.setDescricao(bean.getDescricao()+", "+rs.getString("desc").trim()+" - "+rs.getString("descricao").trim());
        }
      }
    }
    if (bean == null || cdpedido == 0){
        bean = new VND_CartaBean();
        bean.setCdpedido(0);
    }

    rs.close();
    stmt.close();

    //System.out.println(">>>Select....: " + retorno[1]);
    return bean;

}
public void atualiza_carta(VND_CartaBean beanCC) throws SQLException{

    String qry;
    PreparedStatement stmt = null;
    if (beanCC.getXml_cce() != null) {
        qry = "update vnd_carta set xml_cce = ?, dt_sistema = now() where cdpedido = ? " +
           "and (status_nfe not in (135) or status_nfe is null)";

        stmt = conn.prepareStatement(qry);
        stmt.setString(1, beanCC.getXml_cce());
        stmt.setInt(2, beanCC.getCdpedido());
    } else {
        qry = "update vnd_carta set status_nfe = ?, protocolo = ?, dt_sistema = now() where cdpedido = ? " +
           "and (status_nfe not in (135) or status_nfe is null)";

        stmt = conn.prepareStatement(qry);
        stmt.setInt(1, beanCC.getStatus_nfe());
        stmt.setString(2, beanCC.getProtocolo());
        stmt.setInt(3, beanCC.getCdpedido());
    }
   
       stmt.executeUpdate();
       stmt.close();
    
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
