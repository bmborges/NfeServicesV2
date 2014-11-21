/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.nfe.dao.cdc;

import br.com.nfe.bean.cdc.CDC_EstabelecimBean;
import br.com.nfe.util.Database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 *
 * @author supervisor
 */
public class CDC_EstabelecimDAO {
    
    private String strColunas;

    java.sql.Connection conn = null;
//    JdbcUtil j = null;

      public CDC_EstabelecimDAO() throws Exception{
//        j = new JdbcUtil();
//        conn = j.getConn();
        
        conn = Database.getConnection();

         if (conn == null) {
            throw new Exception(getClass().getName() + ": null connection passed.");
         }
            this.conn = conn;
         }
private void definirColunas() {
    
    strColunas = "idestabelecimen,"
            + "nmestabelecimen,"
            + "cnpj";

  }  // definirColunas

 private CDC_EstabelecimBean populate (ResultSet rs) throws SQLException, Exception {
    try {
      CDC_EstabelecimBean gs = new CDC_EstabelecimBean();

      gs.setIdestabelecimen( rs.getInt   ( 1) );
      gs.setNmestabelecimen( rs.getString   ( 2) );
      gs.setCnpj( rs.getString( 3) );
      

      return gs;
    } catch (Exception e ) {
      throw new Exception ("Erro na leitura do NfentradBean!");
    }
  } //== populate ===================================================================================//


public CDC_EstabelecimBean select(CDC_EstabelecimBean bean) throws SQLException, Exception{
    
    CDC_EstabelecimBean dados = null;
    definirColunas();
    if (bean.getIdestabelecimen() <= 0){
        throw new Exception ("Para consulta o Estabelecimento não pode ser 0 ou Nulo.");
    }
    String qry = "select "+ strColunas + " from cdc_estabelecim where idestabelecimen = ?";

    PreparedStatement stmt = conn.prepareStatement(qry);
    
    stmt.setInt(1, bean.getIdestabelecimen());

    ResultSet rs = stmt.executeQuery();

    if (rs.next()) {
        dados = populate( rs );
    }

    rs.close();
    stmt.close();

    return dados;

}


}
