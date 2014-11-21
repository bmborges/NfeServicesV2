/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.nfe.dao.lfs;


import br.com.nfe.bean.lfs.LFS_NfentradBean;
import br.com.nfe.util.Database;
import java.lang.Exception;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author supervisor
 */
public class LFS_NfentradDAO {
    
    public String strColunas;

    java.sql.Connection conn = null;
//    JdbcUtil j = null;

      public LFS_NfentradDAO() throws Exception{
//        j = new JdbcUtil();
//        conn = j.getConn();
        
        conn = Database.getConnection();

         if (conn == null) {
            throw new Exception(getClass().getName() + ": null connection passed.");
         }
            this.conn = conn;
         }
private void definirColunas() {
    strColunas = "cdpedidonfe,"
            + "idestabelecimen,"
            + "dtentrada,"
            + "idnfe" ;
  }  // definirColunas

 private LFS_NfentradBean populate (ResultSet rs) throws SQLException, Exception {
    try {
      LFS_NfentradBean gs = new LFS_NfentradBean();

      gs.setCdpedidonfe( rs.getInt   ( 1) );
      gs.setIdestabelecimen( rs.getInt   ( 2) );
      gs.setDtentrada( rs.getString( 3) );
      gs.setIdnfe( rs.getString( 4) );
      

      return gs;
    } catch (Exception e ) {
      throw new Exception ("Erro na leitura do NfentradBean!");
    }
  } //== populate ===================================================================================//


public LFS_NfentradBean select(LFS_NfentradBean bean) throws SQLException, Exception{
    
    LFS_NfentradBean dados = null;
    definirColunas();
    if (bean.getCdpedidonfe() <= 0){
        throw new Exception ("Para consulta o CdpedidoNfe não pode ser 0 ou Nulo.");
    }
    String qry = "select "+ strColunas + " from lfs_nfentrad where cdpedidonfe = ?";

    PreparedStatement stmt = conn.prepareStatement(qry);
    
    stmt.setInt(1, bean.getCdpedidonfe());

    ResultSet rs = stmt.executeQuery();

    if (rs.next()) {
        dados = populate( rs );
    }

    rs.close();
    stmt.close();

    return dados;

}


}
