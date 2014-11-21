/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.nfe.dao.adm;

import br.com.nfe.bean.adm.ADM_ParamBean;
import br.com.nfe.util.Database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;


/**
 *
 * @author supervisor
 */
public class ADM_ParamDAO {
    
    java.sql.Connection conn = null;
   
    
     public ADM_ParamDAO() throws Exception{
        
        conn = Database.getConnection();

         if (conn == null) {
            throw new Exception(getClass().getName() + ": null connection passed.");
         }
            this.conn = conn;
            
     }
     
public String[] pesquisa_webservice( String nmparam, String uf) throws SQLException {
   String[] retorno = new String[3];
   String qry = "select valorparam, codigo, trim(to_char(valor,'9.00')) as VersaoDados from adm_param where nmparam ilike ? and codigo = ?::integer";
   
   PreparedStatement stmt = conn.prepareStatement(qry);
   
   stmt.setString(1, nmparam);
   stmt.setString(2, uf);

   
   ResultSet rs = stmt.executeQuery();


    
   if (rs.next()) {
      retorno[0] = rs.getString(1);
      retorno[1] = String.valueOf(rs.getInt(2));
      retorno[2] = rs.getString(3);
   }

   rs.close();
   stmt.close();
   
   if (retorno.equals("")){
       retorno[0] = "Não foi possivel localizar o parametro.";
   }

   return retorno;

}

public HashMap pesquisa_impressora( String nmparam, int cdc) throws SQLException, Exception {
   HashMap map = new HashMap();
   String qry = "select valorparam, codigo, valor::integer as valor from adm_param where nmparam ilike ? and codigo = ?";
   
   PreparedStatement stmt = conn.prepareStatement(qry);
   
   stmt.setString(1, nmparam);
   stmt.setInt(2, cdc);
   
//   System.out.println(stmt);
   
   ResultSet rs = stmt.executeQuery();
 
   if (rs.next()) {
       map.put("valorparam", rs.getString("valorparam"));
       map.put("codigo", String.valueOf(rs.getInt("codigo")));
       map.put("valor", String.valueOf(rs.getInt("valor")));
   }

   rs.close();
   stmt.close();
   
//   if (map.get("valorparam") == null || map.get("valorparam").toString().length() <= 0){
//    System.err.println("Não foi localizado Impressora CDC " + cdc);
//   }
   
   return map;

}
public ADM_ParamBean getDados_impressora( String nmparam, int cdc) throws SQLException, Exception {

    String qry = "select valorparam, codigo from adm_param where nmparam ilike ? and codigo = ?";
   
    ADM_ParamBean bean = null;
    PreparedStatement stmt = conn.prepareStatement(qry);
   
    stmt.setString(1, nmparam);
    stmt.setInt(2, cdc);
   
    ResultSet rs = stmt.executeQuery();
 
    if (rs.next()) {
        bean = populate( rs );
    }

    rs.close();
    stmt.close();
   
    if (bean.getValorparam() == null || bean.getValorparam().length() <= 0){
        System.err.println("Não foi localizado Impressora CDC " + cdc);
    }
   
   return bean;

}

public ADM_ParamBean getDados_webservice( String nmparam, String uf) throws SQLException {
    
    String qry = "select idparam, nmparam, valorparam, codigo, valor, idestabelecimen from adm_param where nmparam ilike ?";

    if (uf != null){
        qry += " and codigo = ?::integer";
    }
    
    ADM_ParamBean bean = null;

    PreparedStatement stmt = conn.prepareStatement(qry);
    
    stmt.setString(1, nmparam);
    if (uf != null){
        stmt.setString(2, uf);
    }
    
    ResultSet rs = stmt.executeQuery();

    if (rs.next()) {
      bean = populate( rs );
    }

    rs.close();
    stmt.close();

    return bean;
  }
public List getDados_Nmparam( String nmparam) throws SQLException {
    List lista = new ArrayList();
    String qry = "select idparam, nmparam, valorparam, codigo, valor, idestabelecimen from adm_param where nmparam ilike ? order by idparam";

    ADM_ParamBean bean = null;

    PreparedStatement stmt = conn.prepareStatement(qry);

    stmt.setString(1, nmparam);
    ResultSet rs = stmt.executeQuery();

    while (rs.next()) {
      bean = populate( rs );
      lista.add(bean);
    }

    rs.close();
    stmt.close();
    return lista;
  }
public void gravaDados_Impressora(ADM_ParamBean bean) throws SQLException{
    String qry = "";
    PreparedStatement stmt = null;
    if (bean.getIdparam() != null){
        qry = "update adm_param set nmparam = ?, valorparam = ?, codigo = ?, valor = ? where idparam = ?";
        stmt = conn.prepareStatement(qry);

        stmt.setString(1, bean.getNmparam());
        stmt.setString(2, bean.getValorparam());
        stmt.setInt(3, bean.getCodigo());
        stmt.setDouble(4, bean.getValor());
        stmt.setInt(5, bean.getIdparam());

        stmt.executeUpdate();
    } else {
        qry = "insert into adm_param (idparam, nmparam, valorparam, codigo, valor) " +
                " values (nextval('idparam'), ?, ?, ?, ?)";
        stmt = conn.prepareStatement(qry);
        stmt.setString(1, bean.getNmparam());
        stmt.setString(2, bean.getValorparam());
        stmt.setInt(3, bean.getCodigo());
        stmt.setDouble(4, bean.getValor());

        stmt.execute();

    }

}
public void deletaDados_Impressora(ADM_ParamBean bean) throws SQLException{
    String qry = "";
    PreparedStatement stmt = null;
    qry = "delete from adm_param where idparam = ?";
    stmt = conn.prepareStatement(qry);

    stmt.setInt(1, bean.getIdparam());
    stmt.execute();


}
  private ADM_ParamBean populate(ResultSet rs) throws SQLException {
    try {
      ADM_ParamBean bean = new ADM_ParamBean();
  
      bean.setIdparam(rs.getString(1)== null ? null : rs.getInt(1));
      bean.setNmparam(rs.getString(2));
      bean.setValorparam(rs.getString(3));
      bean.setCodigo(rs.getString(4) == null ? null : rs.getInt(4));
      bean.setValor(rs.getDouble(5));      
      bean.setIdestabelecimen(rs.getString(6) == null ? null : rs.getInt(6));

      return bean;
    } catch (Exception e ) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Erro na leitura do registro!\n\n" + e.getStackTrace(),
                                    "Atencao:", JOptionPane.ERROR_MESSAGE);
      return null;
    }
  }
    
}
